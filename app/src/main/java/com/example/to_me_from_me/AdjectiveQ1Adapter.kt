package com.example.to_me_from_me

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class AdjectiveQ1Adapter(
    private val context: Context,
    private val buttonDataList: List<ButtonData>,
    private val onButtonClickListener: (ButtonData) -> Unit
) : RecyclerView.Adapter<AdjectiveQ1Adapter.ButtonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.q1_adjective_layout, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val buttonData = buttonDataList[position]
        holder.bind(buttonData)
        holder.itemView.setOnClickListener {
            onButtonClickListener(buttonData)
        }
    }

    override fun getItemCount(): Int = buttonDataList.size

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: Button = itemView.findViewById(R.id.button)

        val buttonBg = ContextCompat.getDrawable(itemView.context, R.drawable.solid_no_main2)

        fun bind(buttonData: ButtonData) {
            button.text = buttonData.buttonText
            button.setTextColor(Color.BLACK)
            buttonBg?.let {
                button.background = it // 배경 이미지 설정
            }
        }
    }
}
