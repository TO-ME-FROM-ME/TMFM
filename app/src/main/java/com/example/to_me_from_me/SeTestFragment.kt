package com.example.to_me_from_me


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SeTestFragment : Fragment() {

    private val questions = listOf(
        "나는 다른 사람만큼 가치 있는 사람이야.",
        "나는 내가 좋은 성품을 가졌다고 생각해.",
        "대체로 나는 내가 실패했다고 느껴.",
        "나는 다른 사람들과 일을 잘 할 수 있어.",
        "나는 내가 자랑할만한 게 별로 없어.",
        "나는 내 자신에 대해 긍정적인 태도를 지녀.",
        "나는 내 자신에 대해 대체로 만족해.",
        "나는 스스로를 조금 더 존중했으면 좋겠어.",
        "가끔 나는 내가 쓸모없다고 느껴.",
        "때로 나는 아무 능력 없는 사람이라 생각해."
    )

    // 현재 질문 인덱스
    private var currentQuestionIndex = 0

    private lateinit var questiontv: TextView
    private lateinit var sAgreebtn: Button
    private lateinit var agreebtn: Button
    private lateinit var disagreebtn: Button
    private lateinit var sDisagreebtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setest_q, container, false)

        questiontv = view.findViewById(R.id.q_text)
        sAgreebtn = view.findViewById(R.id.s_agree)
        agreebtn = view.findViewById(R.id.agree)
        disagreebtn = view.findViewById(R.id.disagree)
        sDisagreebtn = view.findViewById(R.id.s_disagree)

        questiontv.text = questions[currentQuestionIndex]

        val buttonClickListener = View.OnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                questiontv.text = questions[currentQuestionIndex]
            } else {
                navigateToSeFinFragment()
            }
        }

        fun resetButtonBackgrounds() {
            sAgreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)
            agreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)
            disagreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)
            sDisagreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)
        }

        sAgreebtn.setOnClickListener {
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            sAgreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        }

        agreebtn.setOnClickListener {
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            agreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        }

        disagreebtn.setOnClickListener {
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            disagreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        }

        sDisagreebtn.setOnClickListener {
            buttonClickListener.onClick(it)
            resetButtonBackgrounds()
            sDisagreebtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        }

        return view
    }

    private fun navigateToSeFinFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, SeFinFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}

