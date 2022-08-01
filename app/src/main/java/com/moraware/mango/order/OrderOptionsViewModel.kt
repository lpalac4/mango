package com.moraware.mango.order

import androidx.lifecycle.MutableLiveData
import com.moraware.domain.models.Order
import com.moraware.domain.usecase.order.*
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.base.BaseViewModel
import com.moraware.mango.featured.ItemThumbnail
import com.moraware.mango.product.ProductViewModel
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.runBlocking

class OrderOptionsViewModel : BaseViewModel() {

    private var user: RoomMangoUser? = null
    var productViewModel: ProductViewModel? = null

    var loadOrders = MutableLiveData<Boolean>().apply { value = false }
    var orders = MutableLiveData<MutableList<Order>>().apply { value = mutableListOf() }
    var isChef = MutableLiveData<Boolean>().apply { value = false }
    var chefName = MutableLiveData<String>().apply { value = "" }

    var orderSuccess = SingleLiveEvent<ItemThumbnail>()
    var singleOrderUpdated = SingleLiveEvent<String>()
    var singleOrderCancelled = SingleLiveEvent<String>()
    var allOrdersCancelled = SingleLiveEvent<ItemThumbnail>()
    var itemThumbnailChanged = SingleLiveEvent<ItemThumbnail>()

    var requestedNumberOfServings = MutableLiveData<String>().apply { value = "1" }

    override fun loadData() {
        if(loadOrders.value == true) {
            productViewModel?.getIdentifier()?.let { mealId ->
                setProcessing(true)
                val useCase = GetOrdersForMealUseCase(mealId)
                mUseCaseClient.execute( { it.either(::onRetrieveOrdersForMealFailure, :: onRetrieveOrdersForMealSuccess)}, useCase)
            }
        }
    }

    private fun onRetrieveOrdersForMealSuccess(ordersForMeal: OrdersForMeal) {
        setProcessing(false)
        orders.value = ordersForMeal.orders
    }

    private fun onRetrieveOrdersForMealFailure(getOrdersForMealFailure: GetOrdersForMealFailure) {
        setProcessing(false)
        setErrorMessage(R.string.error_retrieving_orders_for_meal)
    }

    fun setData(viewModel: ProductViewModel, checkOrders: Boolean) {
        productViewModel = viewModel
        loadOrders.value = checkOrders
        chefName.value = viewModel.getChefName()

        user = runBlocking(mJob) {
            MangoApplication.getUsersBlocking()
        }

        isChef.value = viewModel.getChefId() == user?.uid
    }

    fun processOrder() {
        if(productViewModel == null || user == null) {
            return
        }

        val orders : Int = try { requestedNumberOfServings.value?.toInt() ?: 0 } catch (exception: NumberFormatException) { 0 }

        if(orders == 0) {
            setErrorMessage(R.string.error_submitting_number_orders)
            return
        }

        productViewModel?.let { viewModel ->
            user?.let { mangoUser ->
                setProcessing(true)
                val useCase = SubmitOrderUseCase(viewModel.getIdentifier(), orders, mangoUser.uid, mangoUser.username)
                mUseCaseClient.execute({ it.either(::onSubmitOrderFailure, ::onSubmitOrderSuccess) }, useCase)
            }
        }
    }

    private fun onSubmitOrderSuccess(response: Order) {
        setProcessing(false)
        productViewModel?.itemThumbnail?.value?.apply {
            user?.uid?.let { mPatrons.put(it, false) }
        }?.let { orderSuccess.value = it }
    }

    private fun onSubmitOrderFailure(response: SubmitOrderFailure) {
        setProcessing(false)
        setErrorMessage(R.string.error_submitting_order)
    }

    fun confirmOrderRequest(order: Order) {
        if(isChef.value == true) {
            setProcessing(true)
            val useCase = ConfirmOrderUseCase(order.id, order.mealId, order.patronId, order.numberOfOrders, order.acceptedStatus)
            mUseCaseClient.execute({ it.either(::onConfirmOrderFailure, ::onConfirmOrderSuccess) }, useCase)
        }
    }

    private fun onConfirmOrderSuccess(confirmOrder: ConfirmOrder) {
        setProcessing(false)
        orders.value?.find { it.id == confirmOrder.orderId }?.let {
            it.acceptedStatus = confirmOrder.acceptedStatus
            singleOrderUpdated.value = it.id
        }

        productViewModel?.itemThumbnail?.value?.apply {
            mNumberOfOrders = mNumberOfOrders.plus(confirmOrder.numberOfOrders) }
                ?.let { itemThumbnailChanged.value = it }
    }

    private fun onConfirmOrderFailure(confirmOrderFailure: ConfirmOrderFailure) {
        setProcessing(false)
        setErrorMessage(R.string.error_confirming_order)
    }

    fun canCancel(order: Order) : Boolean {
        return !order.acceptedStatus && isChef.value == false && order.patronId == user?.uid
    }

    fun cancelOrderRequest(order: Order) {
        if(!order.acceptedStatus && isChef.value == false) {
            setProcessing(true)
            val useCase = CancelOrderUseCase(order.id, order.mealId, order.patronId, order.numberOfOrders)
            mUseCaseClient.execute({ it.either(::onCancelOrderFailure, ::onCancelOrderSuccess)}, useCase)
        }
    }

    private fun onCancelOrderSuccess(cancelOrder: CancelOrder) {
        setProcessing(false)
        orders.value?.find { it.id == cancelOrder.orderId }?.let {
            singleOrderCancelled.value = it.id
            orders.value?.remove(it)
        }

        productViewModel?.itemThumbnail?.value?.apply {
            mPatrons.remove(cancelOrder.patronId)
        }?.let { itemThumbnailChanged.value = it }

        if(orders.value?.isEmpty() == true) allOrdersCancelled.call()
    }

    private fun onCancelOrderFailure(cancelOrderFailure: CancelOrderFailure) {
        setProcessing(false)
        setErrorMessage(R.string.error_cancelling_order)
    }
}
