package com.example.to_me_from_me

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecorderFragment : BottomSheetDialogFragment() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private lateinit var sendFragment: SendFragment // SendFragment 객체 선언
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
        countLayout.visibility = View.GONE
        startBtn.text = "시작"
        textTv.text = "녹음 버튼을 눌러 시작하세요."
        oneTv.setBackgroundResource(R.drawable.oval_shape_w)
        twoTv.setBackgroundResource(R.drawable.oval_shape_w)

        // SendFragment 초기화
        sendFragment = SendFragment()

        // 권한 요청 초기화
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

        // '시작' 클릭 시 count 레이아웃 보이기
        // 시작 -> 다음으로 변경
        startBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED ||
                    (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                            ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE.takeIf { Build.VERSION.SDK_INT <= Build.VERSION_CODES.P }
                        ).filterNotNull().toTypedArray()
                    )
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
        Toast.makeText(requireContext(), "녹음이 중지되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun startRecording() {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "audiorecordtest.mp4")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
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
                    isRecording = true
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
}
