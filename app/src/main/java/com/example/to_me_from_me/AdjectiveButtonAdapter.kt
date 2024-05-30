package com.example.to_me_from_me

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView



class AdjectiveButtonAdapter(private val buttonDataList: List<ButtonData>) : RecyclerView.Adapter<AdjectiveButtonAdapter.ButtonViewHolder>() {

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

        fun bind(buttonData: ButtonData) {
            button.text = buttonData.buttonText
            button.isSelected = buttonData.isSelected
            button.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener?.onButtonClick(currentPosition, adapterPosition)
                    buttonData.isSelected = !buttonData.isSelected
                    updateButtonColor(buttonData.isSelected)
                }
            }
        }

        fun updateButtonColor(isSelected: Boolean) {
            if (isSelected) {
                // 선택된 경우 버튼 색상 변경
                button.setBackgroundResource(R.drawable.select_solid)
            } else {
                // 선택되지 않은 경우 기본 버튼 색상으로 변경
                button.setBackgroundResource(R.drawable.solid_stroke)
            }
        }
    }
}
