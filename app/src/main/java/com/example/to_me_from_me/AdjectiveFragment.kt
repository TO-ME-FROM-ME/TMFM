package com.example.to_me_from_me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdjectiveFragment : BottomSheetDialogFragment(), AdjectiveButtonAdapter.OnButtonClickListener {

    private lateinit var recyclerViews: List<RecyclerView>
    private var selectedCount = 0
    private val maxSelection = 2
    private var isAdjectiveSelected = false // 감정 형용사가 선택되었는지 여부를 저장하는 변수

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_adjective, container, false)

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
            // 어댑터 설정
            val adapter = AdjectiveButtonAdapter(requireContext(), getButtonDataList(index), ::getSelectedCount, ::onSelectionChanged)
            adapter.setOnButtonClickListener(this)
            recyclerView.adapter = adapter
        }

        val nextButton = view.findViewById<Button>(R.id.next_btn)
        nextButton.setOnClickListener {
            if (isAdjectiveSelected) { // 감정 형용사가 선택된 경우에만 다음 버튼을 클릭할 수 있도록 제한
                val nextFragment = Q1Fragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit()
            } else {

            }
        }
        return view
    }

    override fun onButtonClick(position: Int, recyclerViewIndex: Int) {
        // 클릭된 버튼의 위치를 해당하는 리사이클러뷰에만 알리도록 함
        recyclerViews[recyclerViewIndex].adapter?.notifyItemChanged(position)
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
    }

    private fun getButtonDataList(index: Int): List<ButtonData> {
        return when(index) {
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
            ButtonData("기대되는"),
            ButtonData("사랑스러운"),
            ButtonData("재미있는"),
            ButtonData("황홀한"),
            ButtonData("두려운"),
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
            ButtonData("불안한"),
            ButtonData("억울한"),
            ButtonData("부러운"),
            ButtonData("그리운"),
            ButtonData("실망스러운"),
            ButtonData("후회스러운")
        )
    }
}
