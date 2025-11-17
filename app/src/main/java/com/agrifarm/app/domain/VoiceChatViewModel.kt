package com.agrifarm.app.domain

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.api.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VoiceChatViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _chatState = MutableStateFlow(VoiceChatState())
    val chatState: StateFlow<VoiceChatState> = _chatState

    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private val geminiApiKey = com.agrifarm.app.BuildConfig.GEMINI_API_KEY

    private val geminiService: GeminiApiService by lazy {
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        
        Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    init {
        initTextToSpeech()
        initSpeechRecognizer()
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                _chatState.value = _chatState.value.copy(isTtsReady = true)
            }
        }
    }

    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _chatState.value = _chatState.value.copy(isListening = true)
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _chatState.value = _chatState.value.copy(isListening = false)
            }

            override fun onError(error: Int) {
                _chatState.value = _chatState.value.copy(
                    isListening = false,
                    error = "Speech recognition error: $error"
                )
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { text ->
                    addMessage(ChatMessage(text, true))
                    sendToGemini(text)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "auto")
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _chatState.value = _chatState.value.copy(isListening = false)
    }

    private fun sendToGemini(text: String) {
        viewModelScope.launch {
            try {
                _chatState.value = _chatState.value.copy(isLoading = true)
                
                android.util.Log.d("VoiceChat", "Sending to Gemini: $text")
                android.util.Log.d("VoiceChat", "API Key: ${geminiApiKey.take(10)}...")
                android.util.Log.d("VoiceChat", "Base URL: https://generativelanguage.googleapis.com/")
                
                val request = GroqRequest(
                    messages = listOf(
                        Message("system", "You are an agricultural expert. Always respond in the same language as the user's question."),
                        Message("user", text)
                    )
                )

                android.util.Log.d("VoiceChat", "Request: $request")
                
                val response = geminiService.generateContent("Bearer $geminiApiKey", request)
                
                android.util.Log.d("VoiceChat", "Response received: $response")
                
                val aiResponse = response.choices.firstOrNull()?.message?.content ?: "No response"
                
                addMessage(ChatMessage(aiResponse, false))
                speak(aiResponse)
                
                _chatState.value = _chatState.value.copy(isLoading = false)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMsg = "HTTP ${e.code()}: $errorBody"
                android.util.Log.e("VoiceChat", "HTTP Error: $errorMsg", e)
                addMessage(ChatMessage(errorMsg, false))
                _chatState.value = _chatState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.javaClass.simpleName} - ${e.message}"
                android.util.Log.e("VoiceChat", "API Error", e)
                addMessage(ChatMessage(errorMsg, false))
                _chatState.value = _chatState.value.copy(
                    isLoading = false,
                    error = errorMsg
                )
            }
        }
    }

    private fun speak(text: String) {
        detectAndSetTTSLanguage(text)
        _chatState.value = _chatState.value.copy(isSpeaking = true)
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
        textToSpeech?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _chatState.value = _chatState.value.copy(isSpeaking = true)
            }
            override fun onDone(utteranceId: String?) {
                _chatState.value = _chatState.value.copy(isSpeaking = false)
            }
            override fun onError(utteranceId: String?) {
                _chatState.value = _chatState.value.copy(isSpeaking = false)
            }
        })
    }

    private fun detectAndSetTTSLanguage(text: String) {
        val locale = when {
            text.matches(Regex(".*[\u0900-\u097F].*")) -> Locale("hi", "IN")
            text.matches(Regex(".*[\u0A00-\u0A7F].*")) -> Locale("pa", "IN")
            text.matches(Regex(".*[\u0B80-\u0BFF].*")) -> Locale("ta", "IN")
            text.matches(Regex(".*[\u0C00-\u0C7F].*")) -> Locale("te", "IN")
            text.matches(Regex(".*[\u0980-\u09FF].*")) -> Locale("bn", "IN")
            text.matches(Regex(".*[\u0B00-\u0B7F].*")) -> Locale("or", "IN")
            text.matches(Regex(".*[\u0A80-\u0AFF].*")) -> Locale("gu", "IN")
            text.matches(Regex(".*[\u0C80-\u0CFF].*")) -> Locale("kn", "IN")
            text.matches(Regex(".*[\u0D00-\u0D7F].*")) -> Locale("ml", "IN")
            else -> Locale("en", "IN")
        }
        textToSpeech?.language = locale
    }

    private fun addMessage(message: ChatMessage) {
        _chatState.value = _chatState.value.copy(
            messages = _chatState.value.messages + message
        )
    }

    fun clearError() {
        _chatState.value = _chatState.value.copy(error = null)
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
    }
}

data class VoiceChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isListening: Boolean = false,
    val isLoading: Boolean = false,
    val isTtsReady: Boolean = false,
    val selectedLanguage: String = "en",
    val error: String? = null,
    val isSpeaking: Boolean = false
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
