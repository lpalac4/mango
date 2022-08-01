package com.moraware.mango.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.moraware.domain.models.Message
import com.moraware.domain.models.MessageThread
import com.moraware.domain.usecase.messages.MessageThreads
import com.moraware.domain.usecase.messages.Messages
import com.moraware.mango.R
import com.moraware.mango.databinding.BaseRecyclerviewDataBindingAdapter
import com.moraware.mango.messages.MessagesViewModel.Companion.DISPLAYING_MESSAGES
import com.moraware.mango.messages.MessagesViewModel.Companion.DISPLAYING_MESSAGE_THREADS

class MessagingAdapter(val viewModel: MessagesViewModel, val lifecycleOwner: LifecycleOwner) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val recyclerview = inflater.inflate(R.layout.recyclerview_empty_reusable, container, false) as RecyclerView
        recyclerview.layoutManager = LinearLayoutManager(container.context)

        when(position) {
            DISPLAYING_MESSAGE_THREADS -> {
                recyclerview.adapter = MessageThreadItemAdapter(recyclerview, lifecycleOwner, viewModel)
            }
            DISPLAYING_MESSAGES -> {
                recyclerview.adapter = MessageItemAdapter(recyclerview, lifecycleOwner, viewModel)
            }
        }

        container.addView(recyclerview)
        return recyclerview
    }

    class MessageItemAdapter(val recyclerView: RecyclerView, lifecycleOwner: LifecycleOwner, viewModel: MessagesViewModel) : BaseRecyclerviewDataBindingAdapter(viewModel) {

        private var messages : MutableList<Message> = viewModel.messages.value?.messages ?: mutableListOf()

        init {
            viewModel.messages.observe(lifecycleOwner, Observer {
                updateModels(it)
            })
        }

        private fun updateModels(messages: Messages) {
            this.messages.clear()
            this.messages.addAll(messages.messages)
            notifyDataSetChanged()
            recyclerView.scrollToPosition(messages.messages.size - 1)
        }

        override fun getObjForPosition(position: Int): Any {
            return messages[position]
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.adapter_message
        }

        override fun getItemCount(): Int {
            return messages.size
        }
    }

    class MessageThreadItemAdapter(val recyclerView: RecyclerView, lifecycleOwner: LifecycleOwner, viewModel: MessagesViewModel) : BaseRecyclerviewDataBindingAdapter(viewModel) {

        private var messageThreads : MutableList<MessageThread> = viewModel.messageThreads.value?.messageThreads ?: mutableListOf()

        init {
            viewModel.messageThreads.observe(lifecycleOwner, Observer {
                updateModels(it)
            })
        }

        private fun updateModels(messagesThreads: MessageThreads) {
            this.messageThreads.clear()
            this.messageThreads.addAll(messagesThreads.messageThreads)
            notifyDataSetChanged()
            recyclerView.scrollToPosition(messagesThreads.messageThreads.size - 1)
        }

        override fun getObjForPosition(position: Int): Any {
            return messageThreads[position]
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.adapter_message_thread
        }

        override fun getItemCount(): Int {
            return messageThreads.size
        }
    }
}