package com.moraware.mango.createmeal

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.moraware.mango.R
import com.moraware.mango.databinding.AdapterCreateMealPhotoItemBinding
import java.net.URI
import java.util.*


class CreateMealPhotoAdapter(var viewModel: CreateMealViewModel): RecyclerView.Adapter<CreateMealPhotoAdapter.CreateMealPhotoHolder>(){

    class PhotoItem(var deletable: Boolean = false, var featured: Boolean = false, var androidUri: Uri? = null, var javaURI: URI? = null)
    private var mItems : ArrayList<PhotoItem> = arrayListOf(
            PhotoItem(deletable = false, featured = true),
            PhotoItem(deletable = false, featured = false),
            PhotoItem(deletable = false, featured = false),
            PhotoItem(deletable = false, featured = false),
            PhotoItem(deletable = false, featured = false),
            PhotoItem(deletable = false, featured = false))

    var touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback( ItemTouchHelper.UP or ItemTouchHelper.DOWN
            or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition

            onItemMove(fromPos, toPos)

            return fromPos != toPos
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    })

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < mItems.size && toPosition < mItems.size) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(mItems, i, i + 1)
                    viewModel.swap(mItems[i].javaURI, mItems[i+1].javaURI)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(mItems, i, i - 1)
                    viewModel.swap(mItems[i].javaURI, mItems[i-1].javaURI)
                }
            }

            notifyItemMoved(fromPosition, toPosition)
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun setPhotoImageView(item: PhotoItem, uri: Uri, javaUri: URI) {
        item.androidUri = uri
        item.deletable = true
        item.javaURI = javaUri
        notifyItemChanged(mItems.indexOf(item))
    }

    fun deletePhoto(item: PhotoItem) {
        item.androidUri = null
        item.deletable = false
        notifyItemChanged(mItems.indexOf(item))
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CreateMealPhotoHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<AdapterCreateMealPhotoItemBinding>(
                layoutInflater, viewType, parent, false)
        return CreateMealPhotoHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateMealPhotoHolder,
                                  position: Int) {
        val obj = mItems[position]
        holder.bind(obj, viewModel)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.adapter_create_meal_photo_item
    }

    inner class CreateMealPhotoHolder(private val binding: AdapterCreateMealPhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val TIMEOUT = 200

        @SuppressLint("ClickableViewAccessibility")
        fun bind(obj: PhotoItem, viewModel: CreateMealViewModel) {
            binding.createMealPhotoImage0.setOnTouchListener { view, event ->
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
                        val clickDuration: Long = event.eventTime - event.downTime
                        if (clickDuration < TIMEOUT) {
                            view?.performClick()
                        } else {
                            touchHelper.startDrag(this@CreateMealPhotoHolder)
                        }
                    }
                }

                true
            }
            binding.setVariable(BR.obj, obj)
            binding.setVariable(BR.viewModel, viewModel)
            binding.executePendingBindings()
        }
    }
}