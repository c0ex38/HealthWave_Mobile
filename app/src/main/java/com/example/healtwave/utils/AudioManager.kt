package com.example.healtwave.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError
    
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun getFrequencyUrl(frequencyId: Int): String {
        return when (frequencyId) {
            1 -> "http://192.168.0.11:8000/media/frequencies/396hz/396_Hz_Tone_Sound_Solfeggio_Frequency_OnlineSound.net.mp3"
            2 -> "http://192.168.0.11:8000/media/frequencies/417hz/417_Hz_Tone_Sound_Solfeggio_Frequency_OnlineSound.net.mp3"
            3 -> "http://192.168.0.11:8000/media/frequencies/528hz/528_Hz_Tone_Sound_Solfeggio_Frequency_OnlineSound.net.mp3"
            4 -> "http://192.168.0.11:8000/media/frequencies/639hz/639_Hz_Tone_Sound_Solfeggio_Frequency_OnlineSound.net.mp3"
            5 -> "http://192.168.0.11:8000/media/frequencies/741hz/741_Hz_Tone_Sound_Solfeggio_Frequency_OnlineSound.net.mp3"
            6 -> "http://192.168.0.11:8000/media/frequencies/852hz/852_Hz_Tone_Sound_Solfeggio_Frequency_OnlineSound.net.mp3"
            else -> "http://192.168.0.11:8000/media/frequencies/852hz/852_Hz_Tone_Sound_Solfeggio_Frequency_OnlineSound.net.mp3"
        }
    }

    fun prepareAudio(frequencyId: Int, onPrepared: () -> Unit = {}) {
        try {
            _isLoading.value = true
            _hasError.value = false
            _errorMessage.value = ""
            
            releasePlayer()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(getFrequencyUrl(frequencyId))
                setOnPreparedListener {
                    _isLoading.value = false
                    Log.d("AudioManager", "MP3 hazırlandı: ${getFrequencyUrl(frequencyId)}")
                    onPrepared()
                }
                setOnErrorListener { _, what, extra ->
                    _isLoading.value = false
                    _hasError.value = true
                    _errorMessage.value = "Ses dosyası yüklenemedi (Hata: $what)"
                    Log.e("AudioManager", "MediaPlayer hatası: what=$what, extra=$extra")
                    false
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    Log.d("AudioManager", "MP3 tamamlandı")
                }
                isLooping = true
                prepareAsync()
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _hasError.value = true
            _errorMessage.value = "Bağlantı hatası: ${e.message}"
            Log.e("AudioManager", "MP3 hazırlama hatası: ${e.message}")
        }
    }

    fun start() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                try {
                    player.start()
                    _isPlaying.value = true
                    _hasError.value = false
                    Log.d("AudioManager", "MP3 çalmaya başladı")
                } catch (e: Exception) {
                    _hasError.value = true
                    _errorMessage.value = "Çalma hatası: ${e.message}"
                    Log.e("AudioManager", "MP3 çalma hatası: ${e.message}")
                }
            }
        }
    }

    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                try {
                    player.pause()
                    _isPlaying.value = false
                    Log.d("AudioManager", "MP3 duraklatıldı")
                } catch (e: Exception) {
                    Log.e("AudioManager", "MP3 duraklama hatası: ${e.message}")
                }
            }
        }
    }

    fun stop() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                _isPlaying.value = false
                Log.d("AudioManager", "MP3 durduruldu")
            } catch (e: Exception) {
                Log.e("AudioManager", "MP3 durdurma hatası: ${e.message}")
            }
        }
    }

    fun releasePlayer() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
                _isPlaying.value = false
                Log.d("AudioManager", "MediaPlayer serbest bırakıldı")
            } catch (e: Exception) {
                Log.e("AudioManager", "MediaPlayer serbest bırakma hatası: ${e.message}")
            }
        }
        mediaPlayer = null
    }

    fun retry(frequencyId: Int, onPrepared: () -> Unit = {}) {
        Log.d("AudioManager", "Yeniden deneniyor...")
        prepareAudio(frequencyId, onPrepared)
    }
}
