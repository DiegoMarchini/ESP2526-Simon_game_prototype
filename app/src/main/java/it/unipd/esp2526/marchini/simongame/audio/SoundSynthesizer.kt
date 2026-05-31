package it.unipd.esp2526.marchini.simongame.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlin.math.PI
import kotlin.math.sin


class SoundSynthesizer {

    val mTag = this::class.simpleName
    private val sampleRate = 44100 // frequenza di campionamento (44.1 kHz)
    private val tracks = mutableListOf<AudioTrack>() // lista delle tracce audio
    private val frequencies = listOf(261.63, 293.66, 329.63, 349.23, 392.00, 440.00) // frequenze dei 6 toni (uno per button)

    // creo le tracce da riprodurre e le inserisco in una lista
    init {
        frequencies.forEach {
            freq -> tracks.add(createTracks(freq))
            Log.d(mTag, "istanziato il tono $freq")
        }
    }

    // funzione chiamata all'inizializzazione dell'oggetto, crea le tracce audio e le inserisce nei relativi buffer
    private fun createTracks(frequency : Double) : AudioTrack{
        val durationMs = 500 // durata in ms della traccia
        val nSamples = (durationMs * sampleRate) / 1000 // numero di campioni per ogni traccia
        val samples = FloatArray(nSamples) // array contenente i campioni generati con la forma d'onda

        // genero i sample con una forma d'onda
        for(i in 0 until nSamples){
            samples[i] = sin(2.0 * PI * i * frequency / sampleRate).toFloat()
        }
        // creazione della traccia usando i sample generati sopra
        val track = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME) // indico che l'audio è utilizzato per un gioco
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // indico che il contenuto è un effetto sonoro
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_FLOAT) // uso 32 bit anzichè 16 bit
                .setSampleRate(sampleRate) // imposto la frequenza di campionamento
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO) // indico che l'output è a canale singolo (mono)
                .build())
            .setBufferSizeInBytes(samples.size * 4) // ogni sample è codificato con 32 bit (4 byte)
            .setTransferMode(AudioTrack.MODE_STATIC) // l'audio viene caricato per intero in memoria una sola volta
            .build()

        // caricando una sola volta le tracce audio mi assicuro, tramite WRITE_BLOCKING, che le tracce vengano salvate nei buffer con successo (senza interruzioni)
        track.write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING)

        return track
    }

    // funzione chiamata per riprodurre la traccia dato l'id del button premuto
    fun playTone(buttonId : Int?){
        if(buttonId == null)return
        tracks[buttonId].stop()
        tracks[buttonId].reloadStaticData()
        tracks[buttonId].play()
    }
    fun stopAllTones(){
        tracks.forEach { { it.stop()} }
    }
    // funzione per rilasciare le risorse impegnate per la creazione e riproduzione delle tracce audio
    fun release(){
        tracks.forEach { it.release() }
        tracks.clear()
    }
}