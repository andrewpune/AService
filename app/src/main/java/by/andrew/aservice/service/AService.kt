package by.andrew.aservice.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.ImageView
import by.andrew.aservice.R
import java.io.File
import java.io.IOException


class AService : AccessibilityService() {
 var recorder : MediaRecorder? = null
 lateinit var fileToSave:File
 lateinit var mLayout:FrameLayout

    override fun onServiceConnected() {
        super.onServiceConnected()
        val dir = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val fileName = "$dir/last.3gp"
        Log.d("!!!fileName",fileName)
        fileToSave = File(fileName)

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayout = FrameLayout(this)
        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.TOP
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.button_layout,mLayout,true)
        wm.addView(mLayout, lp)

        val btnStart = mLayout.findViewById<ImageView>(R.id.btnStart)
        val btnStop = mLayout.findViewById<ImageView>(R.id.btnStop)

        btnStart.setOnClickListener { startRecording() }
        btnStop.setOnClickListener { stopRecording() }
        startRecording()

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
      Log.d("!!!","Event: ${event?.toString()}")
    }

    override fun onInterrupt() {

    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder?.setOutputFile(fileToSave)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
        recorder?.setAudioEncodingBitRate(48000)
        recorder?.setAudioSamplingRate(16000)
        try {
            recorder?.prepare()
        } catch (e: IOException) {
            Log.e("!!!", "prepare() failed")
        }
        recorder?.start()
        Log.d("!!!","startRecording")
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        Log.d("!!!","stopRecording")
    }

    override fun onUnbind(intent: Intent?): Boolean {
//        Log.d("!!!", "onUnbind")
        stopRecording()
        return super.onUnbind(intent)

    }
}