package com.example.to_me_from_me.LetterWrite

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.FileOutputStream
import java.io.IOException


class RecorderFragment : BottomSheetDialogFragment() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private lateinit var sendFragment: SendFragment
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var outputUri: Uri? = null

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
        startBtn.text = "시작"
        textTv.text = "따뜻한 말을 건네며 마무리해보자."
        oneTv.setBackgroundResource(R.drawable.oval_shape_w)
        twoTv.setBackgroundResource(R.drawable.oval_shape_w)
        oneTv.setTextColor(requireContext().getColor(R.color.dark))
        twoTv.setTextColor(requireContext().getColor(R.color.dark))

        sendFragment = SendFragment()

        // 권한 요청 초기화
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.RECORD_AUDIO] == true &&
                (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ) {
                // 권한이 승인되었을 때 녹음을 시작
                startRecording()
            } else {
                Toast.makeText(requireContext(), "녹음 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        startBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                // 권한 체크
                Log.d("isRecording", "isRecording: $isRecording")
                val recordAudioPermissionGranted = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                val writeExternalStoragePermissionGranted = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                } else {
                    true // Android 10 이상에서는 WRITE_EXTERNAL_STORAGE 권한 필요 없음
                }

                if (!recordAudioPermissionGranted || !writeExternalStoragePermissionGranted) {
                    // 권한이 없으면 요청
                    val permissionsToRequest = mutableListOf(Manifest.permission.RECORD_AUDIO)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
                } else {
                    // 권한이 있으면 녹음 시작
                    startRecording()
                    isRecording = true
                    handleRecordingProcess(startBtn, countLayout, textTv, oneTv, twoTv)
                }
            }
        }


        return view
    }

    private fun handleRecordingProcess(startBtn: Button, countLayout: LinearLayout, textTv: TextView, oneTv: TextView, twoTv: TextView) {
        if (startBtn.text == "시작") {
            startRecording()
            countLayout.visibility = View.VISIBLE
            startBtn.text = "다음"
            textTv.text = "잘하고 있어! 더 크게 외쳐봐!"
            oneTv.setBackgroundResource(R.drawable.oval_shape_reco)
            oneTv.setTextColor(requireContext().getColor(R.color.black))
        } else if (startBtn.text == "다음") {
            oneTv.setBackgroundResource(R.drawable.oval_shape_g)
            oneTv.setTextColor(requireContext().getColor(R.color.white))
            startRecording()
            startBtn.text = "확인"
            textTv.text = "마지막 녹음이니까 더 크게 외쳐봐!"
        } else if (startBtn.text == "확인") {
            stopRecording()
            textTv.text = "마지막 녹음 완료!\n대견해. 너무 잘했어 💙"
            twoTv.setBackgroundResource(R.drawable.oval_shape_g)
            twoTv.setTextColor(requireContext().getColor(R.color.white))
            startBtn.setOnClickListener {
                sendFragment.show(parentFragmentManager, "SendFragment")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isRecording) {
            stopRecording() // 화면 종료 시 녹음 중단
        }
    }

    private fun startRecording() {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "tmfm.mp3")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3")
            put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings")
        }

        outputUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
        val outputStream = outputUri?.let { resolver.openOutputStream(it) }

        if (outputStream != null) {
            val fileDescriptor = (outputStream as FileOutputStream).fd

            mediaRecorder = MediaRecorder()
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(fileDescriptor)
                try {
                    prepare()
                    start()
                    isRecording = true // 녹음 상태 설정
                    Toast.makeText(requireContext(), "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "녹음에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(requireContext(), "파일 저장을 위한 OutputStream을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
            } catch (e: RuntimeException) {
                //Toast.makeText(requireContext(), "녹음 중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
            release()
        }
        mediaRecorder = null
        isRecording = false
        Toast.makeText(requireContext(), "녹음이 중지되었습니다.", Toast.LENGTH_SHORT).show()
    }
}

