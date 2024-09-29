package com.example.to_me_from_me.StatisticalReport

import android.graphics.Color
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.R
import com.example.to_me_from_me.databinding.FragmentMonthlyReportBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MonthlyReportFragment : Fragment() {
    private var _binding: FragmentMonthlyReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userScoreTv: TextView

    private lateinit var reportTv: TextView
    private lateinit var reportEmoTv: TextView
    private lateinit var scoreImage: ImageView

    private lateinit var report2Tv: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonthlyReportBinding.inflate(inflater, container, false)
        val view = binding.root


        val tipButton = view.findViewById<ImageView>(R.id.month_tip1)
        tipButton.setOnClickListener {
            val dialog = StatisticalMonthTipDialogFragment()
            dialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialog.show(parentFragmentManager, "StatisticalMonthTipDialogFragment")
        }

        val tipButton2 = view.findViewById<ImageView>(R.id.month_tip2)
        tipButton2.setOnClickListener {
            val dialog = StatisticalTipMDialogFragment()
            dialog.setStyle(
                DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
            dialog.show(parentFragmentManager, "StatisticalTipDialogFragment")
        }



        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        userScoreTv = view.findViewById(R.id.user_score_tv)

        reportTv = view.findViewById<TextView>(R.id.report_tv)
        reportEmoTv = view.findViewById<TextView>(R.id.report_emo_tv)
        scoreImage = view.findViewById<ImageView>(R.id.score_iv)

        report2Tv= view.findViewById<TextView>(R.id.report2_tv)

        loadUserScore()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun loadUserScore() {
        val user = auth.currentUser

        if (user != null) {
            // Firestore에서 사용자 문서 참조
            val userRef = firestore.collection("users").document(user.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // "totalScore" 필드를 number 타입으로 불러옴
                        val score = document.getLong("totalScore") // 혹은 getDouble() 사용
                        if (score != null) {
                            if(score<20){
                                reportTv.text = "낮은"
                                report2Tv.text = " 자존감을 갖고 있어"
                                userScoreTv.text = "${score.toString()}점"
                                reportEmoTv.text="\uD83D\uDE22"
                                scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph_01))

                            }
                           else if(score<30) {
                                reportTv.text = "보통수준"
                                report2Tv.text = "의 자존감을 갖고 있어"
                                userScoreTv.text = "${score.toString()}점"
                                reportEmoTv.text = "\uD83D\uDE03"
                                scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph_02))
                            }else{
                                reportTv.text = "매우 높은"
                                report2Tv.text = " 자존감을 갖고 있어"
                                userScoreTv.text = "${score.toString()}점"
                                reportEmoTv.text = "\uD83E\uDD70"
                                scoreImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_graph_03))
                            }
                        } else {
                            userScoreTv.text = " "
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "데이터 로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            userScoreTv.text = " "
        }
    }
}

