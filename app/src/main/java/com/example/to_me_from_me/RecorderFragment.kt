package com.example.to_me_from_me

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.IOException

class RecorderFragment : BottomSheetDialogFragment() {
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var outputFile: File
    private var isRecording: Boolean = false
    private lateinit var sendFragment: SendFragment // SendFragment 객체 선언
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recorder, container, false)

        val startBtn: Button = view.findViewById(R.id.start_btn)
        val countLayout: LinearLayout = view.findViewById(R.id.count_ll)
        val textTv: TextView = view.findViewById(R.id.text_tv)
        val oneTv: TextView = view.findViewById(R.id.one_tv)
        val twoTv: TextView = view.findViewById(R.id.two_tv)

        // 화면 초기화
        countLayout.visibility = View.GONE
        startBtn.text = "시작"
        textTv.text = "녹음 버튼을 눌러 시작하세요."
        oneTv.setBackgroundResource(R.drawable.oval_shape_w)
        twoTv.setBackgroundResource(R.drawable.oval_shape_w)


        // SendFragment 초기화
        sendFragment = SendFragment()



        // 녹음기능
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.RECORD_AUDIO] == true &&
                (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ) {
                startRecording()
            } else {
                Toast.makeText(requireContext(), "녹음 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // '시작'클릭 시 count레이아웃 보이기
        // 시작 -> 다음으로 변경
        startBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
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
//                            val fragmentManager = parentFragmentManager
//                            val transaction = fragmentManager.beginTransaction()
//                            val bottomSheetFragment = SendFragment()
//                            transaction.add(bottomSheetFragment, "SendFragment")
//                            transaction.commit()
                            sendFragment.show(parentFragmentManager, "SendFragment")
                        }
                    }
                }
            }
        }

        return view
    }


    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        Toast.makeText(requireContext(), "Recording stopped", Toast.LENGTH_SHORT).show()
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder()
        outputFile = File(requireContext().externalCacheDir!!.absolutePath + "/audiorecordtest.3gp")

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
                Toast.makeText(requireContext(), "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(requireContext(), "녹음에 실패했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
