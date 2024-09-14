package com.example.to_me_from_me.LetterWrite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_me_from_me.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdjectiveFragment : BottomSheetDialogFragment(),
    AdjectiveButtonAdapter.OnButtonClickListener {

    private val sharedViewModel: ViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private var selectedCount = 0
    private val maxSelection = 2
    private var isAdjectiveSelected = false
    private lateinit var nextButton: Button
    private lateinit var adapter: AdjectiveButtonAdapter
    private var selectedTexts: MutableList<String> = mutableListOf()

    private lateinit var ad1: Button
    private lateinit var ad2: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_adjective, container, false)

        val textView = view.findViewById<TextView>(R.id.user_situation_tv)
        sharedViewModel.situationText.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }

        val imageView = view.findViewById<ImageView>(R.id.user_emo_iv)
        sharedViewModel.selectedImageResId.observe(viewLifecycleOwner) { resId ->
            if (resId != null) {
                imageView.setImageResource(resId)
                imageView.visibility = View.VISIBLE
            }
        }

        ad1 = view.findViewById(R.id.user_ad1)
        ad2 = view.findViewById(R.id.user_ad2)

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(requireContext(), 5, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // 모든 데이터를 병합하여 어댑터 설정
        val allButtonData = getAllButtonData()
        adapter = AdjectiveButtonAdapter(
            requireContext(),
            listOf(recyclerView),
            allButtonData,
            ::getSelectedCount,
            ::onSelectionChanged,
            mutableListOf(),
            null,
            0
        )
        adapter.setOnButtonClickListener(this)
        recyclerView.adapter = adapter

        nextButton = view.findViewById(R.id.next_btn)
        nextButton.setOnClickListener {
            if (isAdjectiveSelected) {
                selectedTexts.clear()
                selectedTexts.addAll(adapter.getSelectedButtonTexts())

                Log.d("AdjectiveFragment", "Selected Texts: $selectedTexts")

                val nextFragment = Q1Fragment()
                val bundle = Bundle()
                bundle.putStringArrayList("selectedButtonTexts", ArrayList(selectedTexts))
                nextFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // 감정 형용사가 선택되지 않았을 때의 처리를 여기에 추가할 수 있습니다.
            }
        }
        return view
    }

    override fun onButtonClick(position: Int, recyclerViewIndex: Int) {
        recyclerView.adapter?.notifyItemChanged(position)
        updateAdButtons()
    }

    private fun updateAdButtons() {
        selectedTexts.clear()
        selectedTexts.addAll(adapter.getSelectedButtonTexts())

        val ad1Text = selectedTexts.getOrNull(0)
        if (ad1Text != null && ad1Text.isNotEmpty()) {
            ad1.text = ad1Text
            ad1.visibility = View.VISIBLE
        } else {
            ad1.visibility = View.INVISIBLE
        }

        val ad2Text = selectedTexts.getOrNull(1)
        if (ad2Text != null && ad2Text.isNotEmpty()) {
            ad2.text = ad2Text
            ad2.visibility = View.VISIBLE
        } else {
            ad2.visibility = View.INVISIBLE
        }
    }

    private fun getSelectedCount(): Int {
        return selectedCount
    }

    private fun onSelectionChanged(isSelected: Boolean) {
        if (isSelected) {
            selectedCount++
        } else {
            selectedCount--
        }
        isAdjectiveSelected = selectedCount > 0

        if (selectedCount == 2) {
            nextButton.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.solid_no_main
            )
        } else {
            nextButton.background = ContextCompat.getDrawable(requireContext(),
                R.drawable.solid_no_gray
            )
        }
    }

    private fun getAllButtonData(): List<ButtonData> {
        return buttonDataList1() + buttonDataList2() + buttonDataList3() + buttonDataList4() + buttonDataList5()
    }

    private fun buttonDataList1(): List<ButtonData> {
        return listOf(
            ButtonData("감동적인"),
            ButtonData("든든한"),
            ButtonData("자랑스러운"),
            ButtonData("홀가분한"),
            ButtonData("깜짝놀란"),
            ButtonData("혼란스러운"),
            ButtonData("짜증나는"),
            ButtonData("싸늘한"),
            ButtonData("막막한"),
            ButtonData("안타까운")
        )
    }

    private fun buttonDataList2(): List<ButtonData> {
        return listOf(
            ButtonData("감사한"),
            ButtonData("만족스러운"),
            ButtonData("자신있는"),
            ButtonData("활기찬"),
            ButtonData("당황한"),
            ButtonData("답답한"),
            ButtonData("귀찮은"),
            ButtonData("지루한"),
            ButtonData("미안한"),
            ButtonData("외로운")
        )
    }

    private fun buttonDataList3(): List<ButtonData> {
        return listOf(
            ButtonData("두려운"),
            ButtonData("불안한"),
            ButtonData("재미있는"),
            ButtonData("황홀한"),
            ButtonData("기대되는"),
            ButtonData("미운"),
            ButtonData("무관심한"),
            ButtonData("피곤한"),
            ButtonData("서운한"),
            ButtonData("우울한")
        )
    }

    private fun buttonDataList4(): List<ButtonData> {
        return listOf(
            ButtonData("기쁜"),
            ButtonData("신나는"),
            ButtonData("편안한"),
            ButtonData("걱정스러운"),
            ButtonData("무서운"),
            ButtonData("분한"),
            ButtonData("부끄러운"),
            ButtonData("괴로운"),
            ButtonData("슬픈"),
            ButtonData("좌절한")
        )
    }

    private fun buttonDataList5(): List<ButtonData> {
        return listOf(
            ButtonData("놀라운"),
            ButtonData("열중한"),
            ButtonData("평화로운"),
            ButtonData("건강한"),
            ButtonData("사랑스러운"),
            ButtonData("억울한"),
            ButtonData("부러운"),
            ButtonData("그리운"),
            ButtonData("실망스러운"),
            ButtonData("후회스러운")
        )
    }
}
