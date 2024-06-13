package com.example.to_me_from_me

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdjectiveFragment : BottomSheetDialogFragment(), AdjectiveButtonAdapter.OnButtonClickListener {

    private val sharedViewModel: ViewModel by activityViewModels()
    private lateinit var recyclerViews: List<RecyclerView>
    private var selectedCount = 0
    private val maxSelection = 2
    private var isAdjectiveSelected = false // 감정 형용사가 선택되었는지 여부를 저장하는 변수
    private lateinit var nextButton: Button
    private val adapters = mutableListOf<AdjectiveButtonAdapter>() // 어댑터를 리스트로 선언
    private var selectedTexts: MutableList<String> = mutableListOf() // 선택된 텍스트를 추적하기 위한 리스트 추가

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
        recyclerViews = listOf(
            view.findViewById<RecyclerView>(R.id.recyclerView1),
            view.findViewById<RecyclerView>(R.id.recyclerView2),
            view.findViewById<RecyclerView>(R.id.recyclerView3),
            view.findViewById<RecyclerView>(R.id.recyclerView4),
            view.findViewById<RecyclerView>(R.id.recyclerView5)
        )

        recyclerViews.forEachIndexed { index, recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val adapter = AdjectiveButtonAdapter(
                requireContext(),
                recyclerViews,
                getButtonDataList(index),
                ::getSelectedCount,
                ::onSelectionChanged,
                mutableListOf(), // 빈 MutableList 전달
                null, // null로 초기화된 OnButtonClickListener 전달
                index
            )
            adapters.add(adapter) // 어댑터를 리스트에 추가
            adapter.setOnButtonClickListener(this)
            recyclerView.adapter = adapter
        }

        nextButton = view.findViewById(R.id.next_btn) // nextButton 초기화
        nextButton.setOnClickListener {
            if (isAdjectiveSelected) { // 감정 형용사가 선택된 경우에만 다음 버튼을 클릭할 수 있도록 제한
                selectedTexts.clear() // 선택된 텍스트 리스트 초기화
                adapters.forEach { adapter ->
                    selectedTexts.addAll(adapter.getSelectedButtonTexts()) // 각 어댑터에서 선택된 텍스트를 리스트에 추가
                }

                // 선택된 텍스트 확인 로그 추가
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
        // 클릭된 버튼의 위치를 해당하는 리사이클러뷰에만 알리도록 함
        recyclerViews[recyclerViewIndex].adapter?.notifyItemChanged(position)

        updateAdButtons()
    }

    private fun updateAdButtons() {
        // 선택된 텍스트를 ad1, ad2 버튼에 업데이트
        selectedTexts.clear()
        adapters.forEach { adapter ->
            selectedTexts.addAll(adapter.getSelectedButtonTexts())
        }

        // ad1 버튼 설정
        val ad1Text = selectedTexts.getOrNull(0)
        if (ad1Text != null && ad1Text.isNotEmpty()) {
            ad1.text = ad1Text
            ad1.visibility = View.VISIBLE
        } else {
            ad1.visibility = View.INVISIBLE
        }

        // ad2 버튼 설정
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
        // 선택된 감정 형용사의 수에 따라 다음 버튼의 활성화 상태를 업데이트
        isAdjectiveSelected = selectedCount > 0

        // 선택된 감정 형용사의 수에 따라 다음 버튼의 배경색을 업데이트
        if (selectedCount == 2) {
            nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_main)
        } else {
            nextButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.solid_no_gray)
        }
    }

    private fun getButtonDataList(index: Int): List<ButtonData> {
        return when (index) {
            0 -> buttonDataList1()
            1 -> buttonDataList2()
            2 -> buttonDataList3()
            3 -> buttonDataList4()
            4 -> buttonDataList5()
            else -> listOf()
        }
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
