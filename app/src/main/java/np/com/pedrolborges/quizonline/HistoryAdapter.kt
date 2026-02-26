package np.com.pedrolborges.quizonline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import np.com.pedrolborges.quizonline.databinding.HistoryItemRecyclerRowBinding

class HistoryAdapter(private var historyList: List<HistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: HistoryItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: HistoryModel) {
            binding.historyQuizTitle.text = model.quizTitle
            binding.historyScoreText.text = "Pontuação: ${model.scoreText}"
            binding.historyPercentageText.text = "Acertos: ${model.percentage}%"
            // Se você adicionou um campo de data no seu layout, pode colocar aqui também:
            // binding.historyDateText.text = model.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HistoryItemRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    // FUNÇÃO PARA ATUALIZAR A LISTA NA TELA
    fun updateData(newList: List<HistoryModel>) {
        historyList = newList
        notifyDataSetChanged()
    }
}