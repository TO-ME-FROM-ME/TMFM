package com.example.to_me_from_me

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException

class RecorderActivity : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private lateinit var outputFile: File
    private var isRecording: Boolean = false

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)

        val startBtn: Button = findViewById(R.id.start_btn)
        val countLayout: LinearLayout = findViewById(R.id.count_ll)
        val textTv: TextView = findViewById(R.id.text_tv)
        val oneTv: TextView = findViewById(R.id.one_tv)
        val twoTv: TextView = findViewById(R.id.two_tv)

        // 화면 초기화
        countLayout.visibility = View.GONE
        startBtn.text = "시작"
        textTv.text = "녹음 버튼을 눌러 시작하세요."
        oneTv.setBackgroundResource(R.drawable.oval_shape_w)
        twoTv.setBackgroundResource(R.drawable.oval_shape_w)

        // 녹음기능
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.RECORD_AUDIO] == true &&
                (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ) {
                startRecording()
            } else {
                Toast.makeText(this, "녹음 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // '시작'클릭 시 count레이아웃 보이기
        // 시작 -> 다음으로 변경
        startBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                } else {
                    if (startBtn.text == "시작") {
                        startRecording()
                        countLayout.visibility = View.VISIBLE
                        startBtn.text = "다음"
                        textTv.text = "잘하고 있어! 더 크게 외쳐봐!"
                        oneTv.setBackgroundResource(R.drawable.oval_shape_g)
                    } else if (startBtn.text == "다음") {
                        startRecording()
                        startBtn.text = "확인"
                        textTv.text = "좋아! 마지막으로 외쳐봐!"
                        twoTv.setBackgroundResource(R.drawable.oval_shape_g)
                        startBtn.setOnClickListener{
                            val fragmentManager = supportFragmentManager
                            val transaction = fragmentManager.beginTransaction()
                            val bottomSheetFragment = SendFragment()
                            transaction.add(bottomSheetFragment, "SendFragment")
                            transaction.commit()
                        }
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder()
        outputFile = File(externalCacheDir!!.absolutePath + "/audiorecordtest.3gp")

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
                Toast.makeText(this@RecorderActivity, "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@RecorderActivity, "녹음에 실패했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
