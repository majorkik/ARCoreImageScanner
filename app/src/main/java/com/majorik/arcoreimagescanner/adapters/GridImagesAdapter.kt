package com.majorik.arcoreimagescanner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.majorik.arcoreimagescanner.R
import com.majorik.arcoreimagescanner.data.model.Image
import kotlinx.android.synthetic.main.item_image.view.*
import java.io.File

class GridImagesAdapter(
    private val removeBlock: (Image) -> Unit,
    private val clickListener: (Image) -> Unit
) :
    RecyclerView.Adapter<GridImagesAdapter.GridImageViewHolder>() {

    private val items: MutableList<Image> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)

        return GridImageViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GridImageViewHolder, position: Int) {
        holder.bindTo(items[position])
        holder.itemView.btn_remove.setOnClickListener {
            removeBlock(items[position])
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }

        holder.itemView.setOnClickListener {
            clickListener(items[position])
        }
    }

    fun updateImages(images: List<Image>) {
        val diffCallback = ImageDiffCallback(items, images)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(images)
        diffResult.dispatchUpdatesTo(this)
    }

    class GridImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(image: Image) {
            itemView.image.load(File(image.imagePath))
            itemView.title.text = image.title
        }
    }
}


class ImageDiffCallback(
    private val oldList: List<Image>,
    private val newList: List<Image>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]._id == newList[newItemPosition]._id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imagePath == newList[newItemPosition].imagePath
    }

}
