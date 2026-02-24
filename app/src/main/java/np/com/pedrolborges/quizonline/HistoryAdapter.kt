// app/src/main/java/np/com/pedrolborges/quizonline/HistoryAdapter.kt
package np.com.pedrolborges.quizonline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import np.com.pedrolborges.quizonline.databinding.HistoryItemRecyclerRowBinding

class HistoryAdapter(private val historyList: List<HistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    /** Deepseek - inicio
     *
     * Prompt: Crie uma classe ViewHolder para um RecyclerView que exiba itens de histórico de quizzes.
     * A classe deve:
     * - Receber um binding do tipo HistoryItemRecyclerRowBinding
     * - Ter um método bind() que popula as views com os dados de um HistoryModel
     * - Exibir quizTitle, scoreText (com prefixo "Pontuação:") e percentage (com prefixo "Acertos:" e sufixo "%")
     * - Incluir também a implementação do onCreateViewHolder com o inflate do binding
     */
    class MyViewHolder(private val binding: HistoryItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: HistoryModel) {
            binding.historyQuizTitle.text = model.quizTitle
            binding.historyScoreText.text = "Pontuação: ${model.scoreText}"
            binding.historyPercentageText.text = "Acertos: ${model.percentage}%"
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
    /** Deepseek - final */

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(historyList[position])
        }
}