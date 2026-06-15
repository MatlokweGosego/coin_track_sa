package com.example.OPSC6311_PART2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.OPSC6311_PART2.databinding.ItemAchievementBinding

class AchievementAdapter(
    private var achievements: List<Achievement>
) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievements[position]

        holder.binding.tvAchievementTitle.text = "${achievement.icon} ${achievement.name}"
        holder.binding.tvAchievementDescription.text = achievement.description

        if (achievement.isUnlocked) {
            holder.binding.cardAchievement.setCardBackgroundColor(
                android.graphics.Color.parseColor("#4CAF50")
            )
            holder.binding.tvAchievementStatus.text = "✓ Unlocked"
            holder.binding.tvAchievementStatus.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
            if (achievement.dateEarned.isNotEmpty()) {
                holder.binding.tvAchievementDate.visibility = View.VISIBLE
                holder.binding.tvAchievementDate.text = "Earned: ${achievement.dateEarned}"
            }
        } else {
            holder.binding.cardAchievement.setCardBackgroundColor(
                android.graphics.Color.parseColor("#757575")
            )
            holder.binding.tvAchievementStatus.text = "🔒 Locked"
            holder.binding.tvAchievementStatus.setTextColor(android.graphics.Color.parseColor("#BDBDBD"))
            holder.binding.tvAchievementDate.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = achievements.size

    fun updateAchievements(newAchievements: List<Achievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }
}