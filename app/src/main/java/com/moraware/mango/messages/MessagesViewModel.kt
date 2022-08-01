package com.moraware.mango.messages

import androidx.lifecycle.MutableLiveData
import com.moraware.domain.models.Message
import com.moraware.domain.models.MessageThread
import com.moraware.domain.usecase.messages.*
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking
import java.util.*

class MessagesViewModel: BaseViewModel() {

    companion object {
        const val DISPLAYING_MESSAGE_THREADS = 0
        const val DISPLAYING_MESSAGES = 1
    }

    var initialized = false
    var mealId: String = ""
    var mealName = ""

    var currentUserId: String = ""
    var currentUserName: String = ""
    var currentUserProfile: String = ""

    var otherUserId: String = ""
    var otherUserName: String = ""
    var otherUserProfile: String = ""

    var inputMessage = MutableLiveData<String>().apply{ value = "" }

    var selectedThread: MessageThread? = null

    var messages: MutableLiveData<Messages> = MutableLiveData()
    var messageThreads: MutableLiveData<MessageThreads> = MutableLiveData()

    var state: MutableLiveData<Int> = MutableLiveData()

    var noResults = MutableLiveData<Boolean>()
    var displayingMessages = MutableLiveData<Boolean>()
    var loginRequiredEvent = SingleLiveEvent<Any>()

    init {
        val user = runBlocking {
            MangoApplication.getUsersBlocking()
        }

        currentUserId = user?.uid ?: ""
        currentUserProfile = user?.photoUrl ?: ""
        currentUserName = user?.username ?: ""
    }

    fun initFromMeal(mealId: String, mealName: String, mealImage: String, chefId: String, chefName: String) {
        if(initialized) return

        initialized = true
        setState(DISPLAYING_MESSAGES)
        this.mealId = mealId
        this.mealName = mealName
        otherUserId = chefId
        otherUserProfile = mealImage
        otherUserName = chefName
    }

    fun initFromMessages() {
        if(initialized) return

        initialized = true
        setState(DISPLAYING_MESSAGE_THREADS)
    }

    override fun loadData() {
        if(currentUserId.isEmpty()) {
            setErrorMessage(R.string.error_loading_messages)
            loginRequiredEvent.call()
            return
        }

        setProcessing(true)

        if(selectedThread != null) {
            val currentUserInitiatedThread = currentUserId == selectedThread?.senderId
            if(currentUserInitiatedThread) {
                otherUserId = selectedThread?.recipientId ?: ""
                otherUserName = selectedThread?.recipientName ?: ""
                otherUserProfile = selectedThread?.recipientProfile ?: ""
            } else {
                otherUserId = selectedThread?.senderId ?: ""
                otherUserName = selectedThread?.senderName ?: ""
                otherUserProfile = selectedThread?.senderProfile ?: ""
            }
        }

        if(mealId.isNotEmpty()) {
            setState(DISPLAYING_MESSAGES)
            val useCase = GetMessagesUseCase(mealId, currentUserId, otherUserId)
            mUseCaseClient.execute({ it.either(::onGetMessagesFailure, ::onGetMessagesSuccess) }, useCase)
        } else {
            setState(DISPLAYING_MESSAGE_THREADS)
            val useCase = GetMessageThreadsUseCase(currentUserId)
            mUseCaseClient.execute({ it.either(::onGetMessageThreadsFailure, ::onGetMessageThreadsSuccess) }, useCase)
        }
    }

    fun otherUserIsSender(message: Message): Boolean {
        return message.senderId == otherUserId
    }

    fun userIsSender(message: Message): Boolean {
        return message.senderId == currentUserId
    }

    fun otherUserName(message: MessageThread) : String {
        return if(currentUserId == message.senderId) message.recipientName else message.senderName
    }

    fun otherUserProfileUrl(message: MessageThread): String {
        return if(currentUserId == message.senderId) message.recipientProfile else message.senderProfile
    }

    fun onGetMessagesSuccess(response: Messages) {
        setProcessing(false)
        messages.value = response
        setNoResults()
    }

    fun onGetMessagesFailure(failure: GetMessagesFailure) {
        setProcessing(false)
    }

    fun onGetMessageThreadsSuccess(response: MessageThreads) {
        setProcessing(false)
        messageThreads.value = response
        setNoResults()
    }

    fun onGetMessageThreadsFailure(failure: GetMessageThreadsFailure) {
        setProcessing(false)
    }

    fun setState(newState: Int) {
        if(newState != state.value) {
            state.value = newState
        }

        val displayMsg = DISPLAYING_MESSAGES == newState
        if(displayingMessages.value != displayMsg) {
            displayingMessages.value = displayMsg
        }
    }

    fun setNoResults() {
        var condition = DISPLAYING_MESSAGE_THREADS == state.value
                && messageThreads.value?.messageThreads?.isEmpty() ?: false

        if(condition != noResults.value) {
            noResults.value = condition
        }
    }

    fun messageThreadSelected(messageThread: MessageThread) {
        selectedThread = messageThread
        mealId = messageThread.mealId
        mealName = messageThread.mealName
        loadData()
    }

    fun resetToMessageThreads() {
        setState(DISPLAYING_MESSAGE_THREADS)
        otherUserName = ""
        otherUserProfile = ""
        otherUserId = ""
        selectedThread = null
        mealId = ""
        loadData()
    }

    fun sendMessage() {
        if(inputMessage.value?.isEmpty() == true) return

        val timestamp = Date()
        val useCase = SubmitMessageUseCase(mealId, currentUserId, currentUserName, currentUserProfile,
                otherUserId, otherUserName, otherUserProfile, mealName, inputMessage.value ?: "", timestamp.time)
        mUseCaseClient.execute({ it.either(::onSubmitMessageFailure, ::onSubmitMessage) }, useCase)
        messages.value?.messages?.add(Message(inputMessage.value ?: "", timestamp.time, currentUserId, currentUserName, currentUserProfile, otherUserId, otherUserName, otherUserProfile))
        inputMessage.value = ""
    }

    fun onSubmitMessage(response: SubmitMessage) {
        messages.value?.messages?.find { it.timestamp == response.timestamp }?.uploaded = true
    }

    fun onSubmitMessageFailure(failure: SubmitMessageFailure) {
        messages.value?.messages?.find { it.timestamp == failure.timestamp }?.error = true
    }

    fun stopListeningForChanges() {
        val useCase = StopListeningForMessages()
        mUseCaseClient.execute({ it.either(::onStopListeningFailure, ::onStopListening) }, useCase)
    }

    fun onStopListening(success: StopListening) {
        mLogger.log("Stopped listening for messages.")
    }

    fun onStopListeningFailure(failure: StopListeningFailure) {
        mLogger.log("Unable to stop listening for messages. Uh oh")
    }
}