package np.com.pedrolborges.quizonline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import np.com.pedrolborges.quizonline.databinding.RankingItemRowBinding

class RankingAdapter(private var rankingList: List<RankingModel>) :
    RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    class RankingViewHolder(val binding: RankingItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: RankingModel, position: Int) {
            // A posição na lista começa no 0, então somamos 1 para mostrar "1º, 2º, 3º"
            binding.rankingPositionText.text = "${position + 1}º"
            binding.rankingNameText.text = model.name
            binding.rankingScoreText.text = "${model.totalScore} pts"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = RankingItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(rankingList[position], position)
    }

    override fun getItemCount(): Int = rankingList.size

    fun updateData(newList: List<RankingModel>) {
        rankingList = newList
        notifyDataSetChanged()
    }
}