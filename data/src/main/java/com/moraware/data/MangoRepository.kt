package com.moraware.data

import com.moraware.data.base.BaseResponse
import com.moraware.data.interactors.Callback
import com.moraware.data.interactors.Channel
import com.moraware.data.models.*
import com.moraware.data.webservices.google.GooglePlacesService
import com.moraware.data.webservices.lyft.LyftService
import com.moraware.data.webservices.uber.UberService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class MangoRepository: IDataRepository {
    override fun submitOrder(request: OrderRequest, callback: Callback<OrderResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun confirmOrder(request: OrderConfirmRequest, callback: Callback<OrderConfirmResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutUser(request: LogoutRequest, callback: Callback<LogoutResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loginUser(request: LoginRequest, callback: Callback<LoginResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveProfile(request: ProfileRequest, callback: Callback<ProfileResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveMyOrderedMeals(request: MyMealsRequest, callback: Callback<MyMealsResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveOrdersForMeal(request: RetrieveOrdersForMealRequest, callback: Callback<RetrieveOrdersForMealResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelOrder(request: CancelOrderRequest, callback: Callback<CancelOrderResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveUser(request: UserRequest, callback: Callback<UserResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerUser(request: RegisterUserRequest, callback: Callback<RegisterUserResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changePassword(request: ChangePasswordRequest, callback: Callback<ChangePasswordResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun submitMeal(request: SubmitMealRequest, callback: Callback<SubmitMealResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveMeal(request: MealRequest, callback: Callback<MealResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveLocalMeals(request: LocalMealsRequest, callback: Callback<LocalMealsResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun favoriteMeal(request: FavoriteMealRequest, callback: Callback<FavoriteMealResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveFavoriteMeals(request: FavoriteMealsRequest, callback: Callback<FavoriteMealsResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveFeaturedMeals(request: FeaturedMealsRequest, callback: Callback<FeaturedMealsResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeMeal(request: CloseMealRequest, callback: Callback<CloseMealResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveChef(request: ChefRequest, callback: Callback<ChefResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveLocalChefs(request: LocalChefsRequest, callback: Callback<LocalChefsResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveMyChefMeals(request: MyChefMealsRequest, callback: Callback<MyMealsResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveFollowers(request: FollowersRequest, callback: Callback<FollowersResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateFollowers(request: UpdateFollowersRequest, callback: Callback<UpdateFollowersResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun submitImage(request: SubmitImageRequest, callback: Callback<SubmitImageResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listenForMessages(request: MessagesRequest, channel: Channel<MessagesResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopListeningForMessages(callback: Callback<BaseResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun submitMessage(request: SubmitMessageRequest, callback: Callback<SubmitMessageResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveMessageThreads(request: MessageThreadsRequest, callback: Callback<MessageThreadsResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateProfile(request: UpdateProfileRequest, callback: Callback<ProfileResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mRepositoryScheduler = RepositoryScheduler()
    private var mEnableDebugLogging = false
    private lateinit var mSettings: ApplicationServices

    lateinit var client: OkHttpClient
    lateinit var placesService: GooglePlacesService
    lateinit var lyftService: LyftService
    lateinit var uberService: UberService

    override fun initializeServices(settings: ApplicationServices?, debug: Boolean) {
        settings?.let {
            mSettings = settings
            mEnableDebugLogging = debug
            initWebServices()
        }
    }

    private fun initWebServices() {
        client = OkHttpClient().newBuilder()
                .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
                    level = if (mEnableDebugLogging) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .build()

        val googleRetrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/place/textsearch/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        placesService = googleRetrofit.create(GooglePlacesService::class.java)

//        mSettings.taxiServices.forEach {
//            if("Lyft" == it.name){
//                val lyftRetrofit = Retrofit.Builder()
//                        .baseUrl(it.apiUrl)
//                        .client(client)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build()
//
//                lyftService = lyftRetrofit.create(LyftService::class.java)
//            }
//
//            if("Uber" == it.name){
//                val uberRetrofit = Retrofit.Builder()
//                        .baseUrl(it.apiUrl)
//                        .client(client)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build()
//
//                uberService = uberRetrofit.create(UberService::class.java)
//            }
//        }
    }

//    override fun searchLocation(request: SearchLocationRequest, callback: Callback<SearchLocationResponse>) {
//        mRepositoryScheduler.execute(Runnable {
//            val call = placesService.searchLocation(mSettings.locationAPIKey, request.address)
//            val destinations = call.execute().body()
//            val domainDestination: SearchLocationResponse? = AddressMapper().transform(destinations)
//
//            if(domainDestination != null) callback.onSuccess(domainDestination) else callback.onFailure(WebServiceException(null))
//        })
//    }
//
//    override fun searchLocationSync(request: SearchLocationRequest): SearchLocationResponse {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun searchRides(request: SearchRidesRequest, callback: Callback<SearchRidesResponse>) {
//        mRepositoryScheduler.execute(Runnable {
//            val lyftCall = lyftService.getRideEstimate(request.destinationLat.toFloat(), request.destinationLng.toFloat(), request.currentLat.toFloat(), request.currentLng.toFloat())
//            val costEstimates = lyftCall.execute().body()
//            val uberCall = uberService.getRideEstimate(request.destinationLat.toFloat(), request.destinationLng.toFloat(), request.currentLat.toFloat(), request.currentLng.toFloat())
//            val prices = uberCall.execute().body()
//            val domainDestination: SearchRidesResponse? = SearchRidesResponse(RideMapper().transform(costEstimates?.rides, prices?.rides))
//
//            if(domainDestination != null) callback.onSuccess(domainDestination) else callback.onFailure(WebServiceException(null))
//        })
//    }
}