package com.moraware.mango.messages

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelActivity
import com.moraware.mango.databinding.ActivityMessagesBinding
import com.moraware.mango.login.LoginActivity

const val MESSAGE_CHEF_ID = "MessagesActivity.Message_Chef_Id"
const val MESSAGE_CHEF_NAME = "MessagesActivity.Message_Chef_Name"
const val MESSAGE_MEAL_ID = "MessagesActivity.Message_Thread_Id"
const val MESSAGE_MEAL_IMAGE = "MessagesActivity.Message_Meal_Image"
const val MESSAGE_MEAL_NAME = "MessagesActivity.Message_Meal_Name"

class MessagesActivity: ViewModelActivity<MessagesViewModel>() {

    var mealId = ""
    var chefId = ""
    var mealImage = ""
    var chefName = ""
    var mealName = ""

    private lateinit var binding: ActivityMessagesBinding
    private var adapter: MessagingAdapter? = null

    companion object {
        val TAG: String = MessagesActivity::class.java.simpleName
        fun newInstanceFromMeal(mealId: String, mealName: String, mealImage: String, chefId: String, chefName: String): Bundle {
            return Bundle().apply {
                putString(MESSAGE_MEAL_ID, mealId)
                putString(MESSAGE_MEAL_NAME, mealName)
                putString(MESSAGE_CHEF_ID, chefId)
                putString(MESSAGE_MEAL_IMAGE, mealImage)
                putString(MESSAGE_CHEF_NAME, chefName)
            }
        }
    }

    private fun loadArguments(args: Bundle?) {
        args?.let {
            mealId = it.getString(MESSAGE_MEAL_ID) ?: ""
            chefId = it.getString(MESSAGE_CHEF_ID) ?: ""
            mealImage = it.getString(MESSAGE_MEAL_IMAGE) ?: ""
            chefName = it.getString(MESSAGE_CHEF_NAME) ?: ""
            mealName = it.getString(MESSAGE_MEAL_NAME) ?: ""
            mViewModel.initFromMeal(mealId, mealName, mealImage, chefId, chefName)

        } ?: mViewModel.initFromMessages()
    }

    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding {
        mViewModel = ViewModelProviders.of(this).get(MessagesViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages)
        binding.viewModel = mViewModel
        binding.lifecycleOwner = this
        setSupportActionBar(findViewById(R.id.toolbar))

        if(savedInstanceState != null) {
            loadArguments(savedInstanceState)
        } else {
            loadArguments(intent?.extras)
        }

        if(adapter == null) {
            adapter = MessagingAdapter(mViewModel, this)
            binding.pagerMessages.adapter = adapter
        }

        mViewModel.state.observe(this, Observer {
            binding.pagerMessages.currentItem = it
        })

        mViewModel.loginRequiredEvent.observe(this, Observer {
            startActivity(LoginActivity.newIntent(this))
        })

        return binding
    }

    override fun onStop() {
        super.onStop()
        mViewModel.stopListeningForChanges()
    }

    override fun onBackPressed() {
        if(mealId.isEmpty() && binding.pagerMessages.currentItem > 0) {
            binding.pagerMessages.setCurrentItem(0, true)
            mViewModel.resetToMessageThreads()
        } else {
            super.onBackPressed()
        }
    }
}