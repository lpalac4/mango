package com.moraware.data

import com.moraware.data.base.BaseResponse
import com.moraware.data.interactors.Callback
import com.moraware.data.interactors.Channel
import com.moraware.data.models.*

interface IDataRepository {

    fun initializeServices(settings: ApplicationServices?, debug: Boolean)

    fun loginUser(request: LoginRequest, callback: Callback<LoginResponse>)
    fun logoutUser(request: LogoutRequest, callback: Callback<LogoutResponse>)
    fun retrieveUser(request: UserRequest, callback: Callback<UserResponse>)
    fun registerUser(request: RegisterUserRequest, callback: Callback<RegisterUserResponse>)
    fun changePassword(request: ChangePasswordRequest, callback: Callback<ChangePasswordResponse>)

    fun submitMeal(request: SubmitMealRequest, callback: Callback<SubmitMealResponse>)
    fun retrieveMeal(request: MealRequest, callback: Callback<MealResponse>)
    fun retrieveLocalMeals(request: LocalMealsRequest, callback: Callback<LocalMealsResponse>)
    fun favoriteMeal(request: FavoriteMealRequest, callback: Callback<FavoriteMealResponse>)
    fun retrieveFavoriteMeals(request: FavoriteMealsRequest, callback: Callback<FavoriteMealsResponse>)
    fun retrieveFeaturedMeals(request: FeaturedMealsRequest, callback: Callback<FeaturedMealsResponse>)
    fun closeMeal(request: CloseMealRequest, callback: Callback<CloseMealResponse>)

    fun submitOrder(request: OrderRequest, callback: Callback<OrderResponse>)
    fun confirmOrder(request: OrderConfirmRequest, callback: Callback<OrderConfirmResponse>)
    fun retrieveMyOrderedMeals(request: MyMealsRequest, callback: Callback<MyMealsResponse>)
    fun retrieveOrdersForMeal(request: RetrieveOrdersForMealRequest, callback: Callback<RetrieveOrdersForMealResponse>)
    fun cancelOrder(request: CancelOrderRequest, callback: Callback<CancelOrderResponse>)

    fun retrieveChef(request: ChefRequest, callback: Callback<ChefResponse>)
    fun retrieveLocalChefs(request: LocalChefsRequest, callback: Callback<LocalChefsResponse>)
    fun retrieveProfile(request: ProfileRequest, callback: Callback<ProfileResponse>)
    fun retrieveMyChefMeals(request: MyChefMealsRequest, callback: Callback<MyMealsResponse>)
    fun retrieveFollowers(request: FollowersRequest, callback: Callback<FollowersResponse>)
    fun updateFollowers(request: UpdateFollowersRequest, callback: Callback<UpdateFollowersResponse>)
    fun updateProfile(request: UpdateProfileRequest, callback: Callback<ProfileResponse>)

    fun submitImage(request: SubmitImageRequest, callback: Callback<SubmitImageResponse>)

    fun listenForMessages(request: MessagesRequest, channel: Channel<MessagesResponse>)
    fun stopListeningForMessages(callback: Callback<BaseResponse>)
    fun submitMessage(request: SubmitMessageRequest, callback: Callback<SubmitMessageResponse>)
    fun retrieveMessageThreads(request: MessageThreadsRequest, callback: Callback<MessageThreadsResponse>)
}