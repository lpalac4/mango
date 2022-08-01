package com.moraware.mango.dependencyinjection

import com.moraware.data.IDataRepository
import com.moraware.data.base.BaseResponse
import com.moraware.data.interactors.Callback
import com.moraware.data.interactors.Channel
import com.moraware.data.models.*

class MockRepository: IDataRepository {
    override fun initializeServices(settings: ApplicationServices?, debug: Boolean) {
        TODO("Not yet implemented")
    }

    override fun loginUser(request: LoginRequest, callback: Callback<LoginResponse>) {
        TODO("Not yet implemented")
    }

    override fun logoutUser(request: LogoutRequest, callback: Callback<LogoutResponse>) {
        TODO("Not yet implemented")
    }

    override fun retrieveUser(request: UserRequest, callback: Callback<UserResponse>) {
        TODO("Not yet implemented")
    }

    override fun registerUser(
        request: RegisterUserRequest,
        callback: Callback<RegisterUserResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun changePassword(
        request: ChangePasswordRequest,
        callback: Callback<ChangePasswordResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun submitMeal(request: SubmitMealRequest, callback: Callback<SubmitMealResponse>) {
        TODO("Not yet implemented")
    }

    override fun retrieveMeal(request: MealRequest, callback: Callback<MealResponse>) {
        TODO("Not yet implemented")
    }

    override fun retrieveLocalMeals(
        request: LocalMealsRequest,
        callback: Callback<LocalMealsResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun favoriteMeal(
        request: FavoriteMealRequest,
        callback: Callback<FavoriteMealResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveFavoriteMeals(
        request: FavoriteMealsRequest,
        callback: Callback<FavoriteMealsResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveFeaturedMeals(
        request: FeaturedMealsRequest,
        callback: Callback<FeaturedMealsResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun closeMeal(request: CloseMealRequest, callback: Callback<CloseMealResponse>) {
        TODO("Not yet implemented")
    }

    override fun submitOrder(request: OrderRequest, callback: Callback<OrderResponse>) {
        TODO("Not yet implemented")
    }

    override fun confirmOrder(
        request: OrderConfirmRequest,
        callback: Callback<OrderConfirmResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveMyOrderedMeals(
        request: MyMealsRequest,
        callback: Callback<MyMealsResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveOrdersForMeal(
        request: RetrieveOrdersForMealRequest,
        callback: Callback<RetrieveOrdersForMealResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun cancelOrder(request: CancelOrderRequest, callback: Callback<CancelOrderResponse>) {
        TODO("Not yet implemented")
    }

    override fun retrieveChef(request: ChefRequest, callback: Callback<ChefResponse>) {
        TODO("Not yet implemented")
    }

    override fun retrieveLocalChefs(
        request: LocalChefsRequest,
        callback: Callback<LocalChefsResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveProfile(request: ProfileRequest, callback: Callback<ProfileResponse>) {
        TODO("Not yet implemented")
    }

    override fun retrieveMyChefMeals(
        request: MyChefMealsRequest,
        callback: Callback<MyMealsResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveFollowers(
        request: FollowersRequest,
        callback: Callback<FollowersResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun updateFollowers(
        request: UpdateFollowersRequest,
        callback: Callback<UpdateFollowersResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun updateProfile(request: UpdateProfileRequest, callback: Callback<ProfileResponse>) {
        TODO("Not yet implemented")
    }

    override fun submitImage(request: SubmitImageRequest, callback: Callback<SubmitImageResponse>) {
        TODO("Not yet implemented")
    }

    override fun listenForMessages(request: MessagesRequest, channel: Channel<MessagesResponse>) {
        TODO("Not yet implemented")
    }

    override fun stopListeningForMessages(callback: Callback<BaseResponse>) {
        TODO("Not yet implemented")
    }

    override fun submitMessage(
        request: SubmitMessageRequest,
        callback: Callback<SubmitMessageResponse>
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveMessageThreads(
        request: MessageThreadsRequest,
        callback: Callback<MessageThreadsResponse>
    ) {
        TODO("Not yet implemented")
    }
}