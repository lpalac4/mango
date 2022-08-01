package com.moraware.mango.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel

/**
 * Created by luis palacios on 7/29/17.
 */

abstract class BaseRecyclerviewDataBindingAdapter(viewModel: BaseViewModel, private val infScrollingOffset: Int = 1) : RecyclerView.Adapter<BaseRecyclerviewDataBindingAdapter.BaseDataBindingHolder>() {

    var mViewModel: BaseViewModel = viewModel

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BaseDataBindingHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                layoutInflater, viewType, parent, false)
        return BaseDataBindingHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseDataBindingHolder,
                                  position: Int) {
        val obj = getObjForPosition(position)
        holder.bind(obj, mViewModel)
        if(position >= itemCount - infScrollingOffset) mViewModel.loadNextPage(getLayoutIdForPosition(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    protected abstract fun getObjForPosition(position: Int): Any

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    open inner class BaseDataBindingHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(obj: Any, viewModel: BaseViewModel) {
            binding.setVariable(BR.obj, obj)
            binding.setVariable(BR.viewModel, viewModel)
            binding.executePendingBindings()
        }
    }

    class MyAdapter(private val myDataset: List<String>) :
            RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyViewHolder {
            val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_simple_list, parent, false) as TextView
            return MyViewHolder(textView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = myDataset[position]
        }

        override fun getItemCount() = myDataset.size
    }

    companion object {
        @JvmStatic
        @BindingAdapter("strings")
        fun setRecyclerViewData(view: RecyclerView, list: List<String>?) {
            if(list == null) return

            val adapter = MyAdapter(list)
            view.setHasFixedSize(true)
            view.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
            view.adapter = adapter
        }
    }
}