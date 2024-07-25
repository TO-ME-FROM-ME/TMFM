package com.example.to_me_from_me

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class AdjectiveButtonAdapter(
    private val context: Context,
    private val recyclerViews: List<RecyclerView>,
    private val buttonDataList: List<ButtonData>,
    private val selectedCountProvider: () -> Int, // 현재 선택된 버튼의 수를 반환
    private val onSelectionChanged: (Boolean) -> Unit, // 선택 상태가 변경될 때 호출되어 selectedCount를 업데이트
    private var selectedTexts: MutableList<String> = mutableListOf(), // 선택된 텍스트를 추적하기 위한 리스트 추가
    private var onButtonClickListener: OnButtonClickListener? = null, // 클릭 리스너 추가
    private val recyclerViewIndex: Int // 리사이클러뷰의 인덱스 추가
) : RecyclerView.Adapter<AdjectiveButtonAdapter.ButtonViewHolder>() {

    // 리스너 선언
    private var listener: OnButtonClickListener? = null

    // 외부에서 리스너 설정할 수 있는 메서드 추가
    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adjective_item_button, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val buttonData = buttonDataList[position]
        holder.bind(buttonData)
        holder.updateButtonColor(buttonData.isSelected)
    }

    override fun getItemCount(): Int = buttonDataList.size

    interface OnButtonClickListener {
        fun onButtonClick(position: Int, recyclerViewIndex: Int)
    }

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: Button = itemView.findViewById(R.id.button)
        val custom_toast_container = itemView.findViewById<LinearLayout>(R.id.custom_toast_container)

        fun bind(buttonData: ButtonData) {
            button.text = buttonData.buttonText
            button.isSelected = buttonData.isSelected
            updateButtonColor(buttonData.isSelected)

            button.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val selectedCount = selectedCountProvider()
                    if (buttonData.isSelected) {
                        // 이미 선택된 버튼을 다시 클릭하여 선택 해제
                        buttonData.isSelected = false
                        selectedTexts.remove(buttonData.buttonText) // 선택 해제 시 텍스트 제거
                        onSelectionChanged(false)
                        updateButtonColor(false)
                    } else if (selectedCount < 2) {
                        // 선택된 버튼의 개수가 제한보다 적으면 선택
                        buttonData.isSelected = true
                        selectedTexts.add(buttonData.buttonText) // 선택 시 텍스트 추가
                        onSelectionChanged(true)
                        updateButtonColor(true)
                    } else {
                        // recyclerView1 위에 custom 토스트 메시지 출력
                        if (adapterPosition < recyclerViews[0].adapter?.itemCount ?: 0) {
                            val toastLayout = LayoutInflater.from(context).inflate(R.layout.toast_long, null, false)
                            val toast = Toast(context)
                            val location = IntArray(2)
                            val customToastContainer = itemView.rootView.findViewById<View>(R.id.recyclerView)

                            customToastContainer.getLocationOnScreen(location)
                            toastLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                            val yOffset = location[1] - 50 - toastLayout.measuredHeight

                            toast.view = toastLayout
                            toast.setGravity(Gravity.TOP or Gravity.END, 0, yOffset)
                            toast.show()

                            val handler = Handler(Looper.getMainLooper())
                            handler.postDelayed({
                                toast.cancel()
                            }, 700)
                        }
                    }
                    // 버튼의 선택 상태가 변경되었음을 어댑터에 알림
                    notifyItemChanged(currentPosition)
                    // 리스너 호출
                    listener?.onButtonClick(currentPosition, adapterPosition)
                }
            }
        }

        private val defaultTextColor = button.currentTextColor
        fun updateButtonColor(isSelected: Boolean) {
            if (isSelected) {
                // 선택된 경우 버튼 색상 변경
                button.setBackgroundResource(R.drawable.select_solid)
                button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
            } else {
                // 선택되지 않은 경우 기본 버튼 색상으로 변경
                button.setBackgroundResource(R.drawable.solid_stroke)
                button.setTextColor(defaultTextColor)
            }
        }
    }

    fun getSelectedButtonTexts(): List<String> {
        return selectedTexts.toList()
    }
}