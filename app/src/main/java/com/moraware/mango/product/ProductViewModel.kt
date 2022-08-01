package com.moraware.mango.product

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.moraware.domain.usecase.meals.*
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Created by Luis Palacios on 8/3/17.
 */

class ProductViewModel() : BaseViewModel(), Parcelable {

    var itemThumbnail = MutableLiveData<ItemThumbnail>().apply { value = null }
    var mealIdentifier: String = ""

    var orderId: Int = 0
    var orderStatus: Int = 0
    var orderPaymentType: Int = 0
    var orderTotal: Int = 0

    var orderAcceptedPaymentTypes: IntArray? = null
    var orderDate: Date? = null
    var userId = ""

    var messageChefEvent = SingleLiveEvent<Any>()
    var goToChefMessagesEvent = SingleLiveEvent<Unit>()
    val orderProductEvent = SingleLiveEvent<Unit>()
    val checkOrdersEvent = SingleLiveEvent<Unit>()
    val imageClickedEvent = SingleLiveEvent<Int>()
    val closeMealEvent = SingleLiveEvent<Unit>()
    val productUpdated = SingleLiveEvent<Unit>()
    val loadChefProfileEvent = SingleLiveEvent<String>()

    var canOrder = MutableLiveData<Boolean>().apply { value = false }
    var canClose = MutableLiveData<Boolean>().apply { value = false }
    var hasConfirmedOrders = MutableLiveData<Boolean>().apply { value = false }

    constructor(parcel: Parcel) : this() {
        itemThumbnail.value = parcel.readParcelable(ItemThumbnail::class.java.classLoader)
        orderId = parcel.readInt()
        orderStatus = parcel.readInt()
        orderPaymentType = parcel.readInt()
        orderTotal = parcel.readInt()
        orderAcceptedPaymentTypes = parcel.createIntArray()
        orderDate = parcel.readSerializable() as Date?
    }

    @Bindable
    fun getProduct() : ItemThumbnail? {
        return itemThumbnail.value
    }

    override fun loadData() {
        val user = runBlocking(mJob) {
            MangoApplication.getUsersBlocking()
        }

        userId = user?.uid ?: ""

        if(itemThumbnail.value == null) {
            loadProductById()
        } else {
            updateLiveData()
        }
    }

    fun loadProductById() {
        setProcessing(true)
        val useCase = GetMealsUseCase(mealId = mealIdentifier)
        mUseCaseClient.execute({it.either(::onGetMealsFailure, ::onGetMealsSuccess)}, useCase)

    }

    private fun onGetMealsSuccess(response: GetMeals) {
        if(response.meals.isNotEmpty()) {
            setProduct(ItemThumbnail(response.meals[0]))
            productUpdated.call()
        } else setErrorMessage(R.string.product_loading_error)

        setProcessing(false)
    }

    private fun onGetMealsFailure(failure: GetMealsFailure) {
        setErrorMessage(R.string.product_loading_error)
        setProcessing(false)
    }

    fun onOrderProductSelected() {
        orderProductEvent.call()
    }

    fun onImageClicked(position: Int) {
        imageClickedEvent.value = position
    }

    fun getIdentifier(): String {
        return itemThumbnail.value?.mMealId ?: ""
    }

    fun getChefId(): String {
        return itemThumbnail.value?.mChefId ?: ""
    }

    fun getMealImage(): String {
        return itemThumbnail.value?.mFeaturedImage ?: ""
    }

    fun getChefName(): String {
        return itemThumbnail.value?.mChefName ?: ""
    }

    fun getMealName(): String {
        return itemThumbnail.value?.mName ?: ""
    }

    fun updateLiveData() {
        val now = Date()
        canOrder.value = (itemThumbnail.value?.mChefId != userId) && (itemThumbnail.value?.mPatrons?.get(userId) == null)
                //&& (itemThumbnail?.mETADate?.after(now) ?: false) TODO: uncomment this after testing
        canClose.value = (itemThumbnail.value?.mChefId == userId) && (itemThumbnail.value?.mETADate?.before(now) ?: false)
        hasConfirmedOrders.value = 0 < itemThumbnail.value?.mNumberOfOrders ?: 0
    }

    fun setProduct(thumbnail: ItemThumbnail) {
        itemThumbnail.value = thumbnail
        updateLiveData()
        notifyChange()
    }

    fun setProductIdentifier(identifier: String) {
        mealIdentifier = identifier
    }

    fun onMessageChef() {
        if((itemThumbnail.value?.mChefId == userId)) goToChefMessagesEvent.call()
        else messageChefEvent.call()
    }

    fun onCheckProductOrders() {
        checkOrdersEvent.call()
    }

    fun onCloseMeal() {
        getProduct()?.mETADate?.let { time ->
            val useCase = CloseMealUseCase(getIdentifier(), getChefId(), time)
            mUseCaseClient.execute({it.either(::onCloseMealFailure, ::onCloseMealSuccess)}, useCase)
        }
    }

    private fun onCloseMealFailure(closeMealFailure: CloseMealFailure) {
        mLogger.log("Unable to close meal")
        setErrorMessage(R.string.error_closing_meal)
    }

    private fun onCloseMealSuccess(closeMeal: CloseMeal) {
        mLogger.log("Meal closed, goodbye food")
        closeMealEvent.call()
    }

    fun loadChefProfile() {
        getProduct()?.mChefId?.let { loadChefProfileEvent.value = it }
    }

    companion object CREATOR : Parcelable.Creator<ProductViewModel> {
        override fun createFromParcel(parcel: Parcel): ProductViewModel {
            return ProductViewModel(parcel)
        }

        override fun newArray(size: Int): Array<ProductViewModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(itemThumbnail.value, flags)
        parcel.writeInt(orderId)
        parcel.writeInt(orderStatus)
        parcel.writeInt(orderPaymentType)
        parcel.writeInt(orderTotal)
        parcel.writeIntArray(orderAcceptedPaymentTypes)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }
}
