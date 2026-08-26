package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.example.data.local.IslamicDataSource
import com.example.data.model.Ayah
import com.example.data.model.Surah
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class QuranPlaybackState(
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurah: Surah? = null,
    val currentAyah: Ayah? = null,
    val currentAyahIndex: Int = 0,
    val totalAyahsInSurah: Int = 0,
    val selectedReciter: ReciterVoice = ReciterVoicePacks.DEFAULT_RECITER,
    val playbackProgress: Float = 0f,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val isFromAi: Boolean = false,
    val errorMessage: String? = null
)

class QuranAudioPlayerManager(private val context: Context) {

    private val TAG = "QuranAudioPlayerManager"
    private var mediaPlayer: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null
    private var loadJob: Job? = null

    private val _playbackState = MutableStateFlow(QuranPlaybackState())
    val playbackState: StateFlow<QuranPlaybackState> = _playbackState.asStateFlow()

    private var currentAyahsList: List<Ayah> = emptyList()

    fun setSurahAyahs(ayahs: List<Ayah>) {
        currentAyahsList = ayahs
    }

    fun selectReciter(reciter: ReciterVoice) {
        _playbackState.value = _playbackState.value.copy(selectedReciter = reciter)
        // If already playing, replay current ayah with new reciter voice
        val currentAyah = _playbackState.value.currentAyah
        val currentSurah = _playbackState.value.currentSurah
        if (currentAyah != null && currentSurah != null && (_playbackState.value.isPlaying || _playbackState.value.isPaused)) {
            playAyah(currentSurah, currentAyah)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackState.value = _playbackState.value.copy(playbackSpeed = speed)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.playbackParams = mediaPlayer!!.playbackParams.setSpeed(speed)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error changing playback speed", e)
        }
    }

    fun playAyah(surah: Surah, ayah: Ayah) {
        loadJob?.cancel()
        stopProgressTracker()
        releasePlayer()

        // If currentAyahsList is empty, default to this Surah's ayahs
        if (currentAyahsList.isEmpty()) {
            currentAyahsList = IslamicDataSource.getAyahsForSurah(surah)
        }

        val indexInList = currentAyahsList.indexOfFirst { 
            it.surahNumber == surah.number && it.numberInSurah == ayah.numberInSurah 
        }.let { if (it >= 0) it else (ayah.numberInSurah - 1).coerceIn(0, (surah.numberOfAyahs - 1).coerceAtLeast(0)) }

        val totalCount = if (currentAyahsList.isNotEmpty()) currentAyahsList.size else surah.numberOfAyahs

        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            isPaused = false,
            isLoading = true,
            currentSurah = surah,
            currentAyah = ayah,
            currentAyahIndex = indexInList,
            totalAyahsInSurah = totalCount,
            playbackProgress = 0f,
            currentPositionMs = 0,
            durationMs = 0,
            errorMessage = null
        )

        loadJob = coroutineScope.launch {
            val result = QuranAudioService.getAyahAudioFile(
                context = context,
                surah = surah,
                ayah = ayah,
                reciter = _playbackState.value.selectedReciter
            )

            when (result) {
                is QuranAudioResult.Success -> {
                    initAndPlayMedia(result.audioFile, surah, ayah, result.isFromAi)
                }
                is QuranAudioResult.Error -> {
                    _playbackState.value = _playbackState.value.copy(
                        isLoading = false,
                        isPlaying = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun playNextAyah() {
        val currentSurah = _playbackState.value.currentSurah ?: return
        val currentAyah = _playbackState.value.currentAyah ?: return
        val currentIndex = _playbackState.value.currentAyahIndex

        if (currentAyahsList.isNotEmpty() && currentIndex + 1 < currentAyahsList.size) {
            val nextAyah = currentAyahsList[currentIndex + 1]
            val nextSurah = if (nextAyah.surahNumber != currentSurah.number) {
                IslamicDataSource.SURAHS.find { it.number == nextAyah.surahNumber } ?: currentSurah
            } else currentSurah
            playAyah(nextSurah, nextAyah)
        } else if (currentAyah.numberInSurah < currentSurah.numberOfAyahs) {
            val nextAyah = Ayah(
                numberInSurah = currentAyah.numberInSurah + 1,
                overallNumber = currentAyah.overallNumber + 1,
                surahNumber = currentSurah.number,
                arabicText = "",
                englishTranslation = "",
                juz = currentAyah.juz,
                page = currentAyah.page
            )
            playAyah(currentSurah, nextAyah)
        } else {
            // Next Surah
            val nextSurahNumber = currentSurah.number + 1
            val nextSurah = IslamicDataSource.SURAHS.find { it.number == nextSurahNumber }
            if (nextSurah != null) {
                currentAyahsList = IslamicDataSource.getAyahsForSurah(nextSurah)
                val firstAyah = currentAyahsList.firstOrNull() ?: Ayah(
                    numberInSurah = 1,
                    overallNumber = 1,
                    surahNumber = nextSurah.number,
                    arabicText = "",
                    englishTranslation = "",
                    juz = nextSurah.juzNumber,
                    page = nextSurah.startPage
                )
                playAyah(nextSurah, firstAyah)
            } else {
                stop()
            }
        }
    }

    fun nextAyah() {
        playNextAyah()
    }

    fun playPreviousAyah() {
        val currentSurah = _playbackState.value.currentSurah ?: return
        val currentAyah = _playbackState.value.currentAyah ?: return
        val currentIndex = _playbackState.value.currentAyahIndex

        if (currentAyahsList.isNotEmpty() && currentIndex - 1 >= 0) {
            val prevAyah = currentAyahsList[currentIndex - 1]
            val prevSurah = if (prevAyah.surahNumber != currentSurah.number) {
                IslamicDataSource.SURAHS.find { it.number == prevAyah.surahNumber } ?: currentSurah
            } else currentSurah
            playAyah(prevSurah, prevAyah)
        } else if (currentAyah.numberInSurah > 1) {
            val prevAyah = Ayah(
                numberInSurah = currentAyah.numberInSurah - 1,
                overallNumber = (currentAyah.overallNumber - 1).coerceAtLeast(1),
                surahNumber = currentSurah.number,
                arabicText = "",
                englishTranslation = "",
                juz = currentAyah.juz,
                page = currentAyah.page
            )
            playAyah(currentSurah, prevAyah)
        }
    }

    fun previousAyah() {
        playPreviousAyah()
    }

    fun seekTo(progress: Float) {
        seekToFraction(progress)
    }

    fun release() {
        stop()
    }

    fun togglePlayPause() {
        val player = mediaPlayer
        if (player == null) {
            val surah = _playbackState.value.currentSurah ?: IslamicDataSource.SURAHS[0]
            val ayah = _playbackState.value.currentAyah ?: IslamicDataSource.AYAHS_BY_SURAH[1]?.first()
            if (ayah != null) {
                playAyah(surah, ayah)
            }
            return
        }

        if (player.isPlaying) {
            player.pause()
            stopProgressTracker()
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isPaused = true
            )
        } else {
            player.start()
            startProgressTracker()
            _playbackState.value = _playbackState.value.copy(
                isPlaying = true,
                isPaused = false
            )
        }
    }

    fun seekToFraction(fraction: Float) {
        val player = mediaPlayer ?: return
        try {
            val duration = player.duration
            if (duration > 0) {
                val targetMs = (fraction * duration).toInt()
                player.seekTo(targetMs)
                _playbackState.value = _playbackState.value.copy(
                    playbackProgress = fraction.coerceIn(0f, 1f),
                    currentPositionMs = targetMs
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking audio", e)
        }
    }

    fun stop() {
        loadJob?.cancel()
        stopProgressTracker()
        releasePlayer()
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            isPaused = false,
            isLoading = false,
            playbackProgress = 0f,
            currentPositionMs = 0
        )
    }

    private fun initAndPlayMedia(audioFile: File, surah: Surah, ayah: Ayah, isFromAi: Boolean) {
        try {
            releasePlayer()

            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build()
                )
                setDataSource(audioFile.absolutePath)
                prepare()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                player.playbackParams = player.playbackParams.setSpeed(_playbackState.value.playbackSpeed)
            }

            player.setOnCompletionListener {
                stopProgressTracker()
                _playbackState.value = _playbackState.value.copy(
                    isPlaying = false,
                    isPaused = false,
                    playbackProgress = 1f
                )
                // Automatically advance to the next Ayah for seamless continuous recitation
                playNextAyah()
            }

            player.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                stopProgressTracker()
                _playbackState.value = _playbackState.value.copy(
                    isPlaying = false,
                    isPaused = false,
                    isLoading = false,
                    errorMessage = "Playback error occurred. Please try again."
                )
                true
            }

            player.start()
            mediaPlayer = player

            _playbackState.value = _playbackState.value.copy(
                isPlaying = true,
                isPaused = false,
                isLoading = false,
                durationMs = player.duration,
                isFromAi = isFromAi,
                errorMessage = null
            )

            startProgressTracker()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start MediaPlayer for file ${audioFile.path}", e)
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isPaused = false,
                isLoading = false,
                errorMessage = "Unable to play audio: ${e.message}"
            )
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = coroutineScope.launch {
            while (isActive) {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    try {
                        val current = player.currentPosition
                        val duration = player.duration
                        val progress = if (duration > 0) (current.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
                        _playbackState.value = _playbackState.value.copy(
                            playbackProgress = progress,
                            currentPositionMs = current,
                            durationMs = duration
                        )
                    } catch (_: Exception) {}
                }
                delay(100L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
