package com.example.weatherproject.ui.home


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherproject.R
import com.example.weatherproject.databinding.HourlyItemBinding
import com.example.weatherproject.model.ResponseNextWeather
import com.example.weatherproject.model.ResponseNextWeather.*
import com.example.weatherproject.utils.formatDate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HourlyWeatherAdapter @Inject constructor(@ApplicationContext private val context: Context) : RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder>() {

    private lateinit var binding: HourlyItemBinding
    private var moviesList = emptyList<ItemList>()
    private var timezone : Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = HourlyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //getItem from PagingDataAdapter
        holder.bind(moviesList[position])
        //Not duplicate items
        holder.setIsRecyclable(false)
    }

    override fun getItemCount() = moviesList.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ItemList) {
            binding.apply {
                //time
                val unixTimestamp = item.dt

                timeTxt.text = unixTimestamp?.formatDate("h:mm a" , timezone)
                //image
                item.weather!![0]?.icon?.let { icon ->
                    val iconID = "image$icon"
                    val resourceId = context.resources.getIdentifier(iconID, "drawable", "com.example.weatherproject")
                    val imageResource = if (resourceId != 0) resourceId else R.drawable.image02n
                    stateIcon.setImageResource(imageResource)
                    //set temp
                }
                degree.text = item.main?.temp?.toInt().toString() + "°"


            }

        }
    }

    private var onItemClickListener: ((ResponseNextWeather) -> Unit)? = null

    fun setOnItemClickListener(listener: (ResponseNextWeather) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<ItemList> , timezonep :Int) {
        val moviesDiffUtil = FoodDiffUtils(moviesList, data)
        val diffUtils = DiffUtil.calculateDiff(moviesDiffUtil)
        moviesList = data
        diffUtils.dispatchUpdatesTo(this)
        timezone = timezonep
    }

    class FoodDiffUtils(private val oldItem: List<ItemList>, private val newItem: List<ItemList>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem.size
        }

        override fun getNewListSize(): Int {
            return newItem.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }
    }
}