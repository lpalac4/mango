package com.moraware.mango.firebase

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.moraware.data.IDataRepository
import com.moraware.data.base.BaseResponse
import com.moraware.data.base.WebServiceException
import com.moraware.data.entities.*
import com.moraware.data.interactors.Callback
import com.moraware.data.interactors.Channel
import com.moraware.data.models.*
import com.moraware.mango.firebase.mappers.FirebaseMealEntity
import com.moraware.mango.firebase.mappers.FirebaseUserMapper
import com.moraware.mango.logger.MangoLogger
import com.moraware.mango.room.RoomMangoDatabase
import com.moraware.mango.room.RoomMangoUser
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import java.net.URI
import java.util.*
import java.util.logging.Level
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext


/**
 * Created by chocollo on 7/24/17.
 */

class FirebaseRepository(val mLogger: MangoLogger, val mLocalDatabase: RoomMangoDatabase, coroutineContext: CoroutineContext) : IDataRepository {

    private val mStorageRef: StorageReference = FirebaseStorage.getInstance().reference
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseFirestore.getInstance()
    private val mExecutor = (coroutineContext as ExecutorCoroutineDispatcher).executor

    /**
     * All chefs in the ecosystem
     */
    private val mChefRef: Query
    /**
     * All meals in the ecosystem
     */
    private val mMealsRef: CollectionReference
    private var lastCachedMealDocument: DocumentSnapshot? = null // required for paging using firebase
    /**
     * All user in the ecosystem
     */
    private val mUsersRef: CollectionReference
    /**
     * All messages in the ecosystem
     */
    private val mMessagesRef: CollectionReference
    /**
     * All message threads in the ecosystem
     */
    private val mMessageThreadsRef: CollectionReference
    /**
     * All orders in the ecosystem - TODO: Not currently in use
     */
    private val mOrdersRef: CollectionReference
    /**
     * Any meals belonging to current user because of a chefId relationship
     */
    private val mMyChefMealsRef: Query
    /**
     * Any followers belonging to current user because of a followers relationship
     */
    private val mFollowersRef: CollectionReference
    /**
     * Any orders belonging to the current user because of a patron relation ship
     */
    private val mMyOrdersRef: Query

    init {
        mUsersRef = mDatabase.collection(DATABASE_ID_USERS)
        mMealsRef = mDatabase.collection(DATABASE_ID_MEALS)
        mOrdersRef = mDatabase.collection(DATABASE_ID_ORDERS)
        mMessagesRef = mDatabase.collection(DATABASE_ID_MESSAGES)
        mMessageThreadsRef = mDatabase.collection(DATABASE_ID_MESSAGE_THREADS)
        mFollowersRef = mDatabase.collection(DATABASE_ID_FOLLOWERS)

        mChefRef = mDatabase.collection(DATABASE_ID_USERS).whereEqualTo(DATABASE_FIELD_USERS_CHEF, true)
        mMyChefMealsRef = mDatabase.collection(DATABASE_ID_MEALS).whereArrayContains(DATABASE_FIELD_MEALS_CHEF, mAuth.currentUser?.uid ?: "")
        mMyOrdersRef = mDatabase.collection(DATABASE_ID_ORDERS).whereEqualTo(DATABASE_FIELD_ORDERS_PATRON_ID, mAuth.currentUser?.uid ?: "")
    }

    override fun initializeServices(settings: ApplicationServices?, debug: Boolean) {

    }

    /**
     * Start of Retrieve User
     */
    override fun retrieveUser(request: UserRequest, callback: Callback<UserResponse>) {
        mLogger.log(Level.FINE, "Retrieving user from Firebase ...")

        val user = mAuth.currentUser

        resetFirebaseVariables()

        if (user != null && !user.isAnonymous) {
            mLogger.log(Level.FINE, "Firebase user: $user...")
            getUserFromMangoDatabase(user, callback)
        } else if (user != null && user.isAnonymous) {
            mLogger.log(Level.FINE, "Anonymous Firebase user...")
            val userEntity = FirebaseUserMapper().toUserEntity(user)
            callback.onSuccess(UserResponse(userEntity))
        } else {
            mLogger.log(Level.FINE, "Failed in retrieving user from Firebase...")
            callback.onFailure(WebServiceException(null))
        }
    }

    private fun getUserFromMangoDatabase(firebaseUser: FirebaseUser, callback: Callback<UserResponse>) {
        var queryResponse = mUsersRef.whereEqualTo(DATABASE_FIELD_UNIQUE_ID, firebaseUser.uid).get()
        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener {
            if (it.isSuccessful && it.result?.documents?.isNotEmpty() == true) {
                val userEntity = it.result?.documents?.get(0)?.toObject(UserEntity::class.java)

                if (userEntity == null) callback.onFailure(WebServiceException(Exception()))
                else callback.onSuccess(UserResponse(userEntity))
            } else {
                callback.onFailure(WebServiceException(Exception()))
            }
        })
    }

    private fun resetFirebaseVariables() {
        lastCachedMealDocument = null
    }

    /**
     * End of Retrieve User
     */

    /**
     * Start of Register User.
     */
    override fun registerUser(request: RegisterUserRequest, callback: Callback<RegisterUserResponse>) {
        registerUserWithFirebase(request, object : Callback<RegisterUserResponse> {
            override fun onFailure(exception: WebServiceException) {
                callback.onFailure(exception)
            }

            override fun onSuccess(response: RegisterUserResponse) {
                if (request.anonymous()) callback.onSuccess(response)
                else addUserToMangoDatabase(response, request.imageURI, callback)
            }
        })
    }

    private fun registerUserWithFirebase(request: RegisterUserRequest, callback: Callback<RegisterUserResponse>) {
        //sign in anon
        when {
            request.anonymous() -> {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(mExecutor, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                mLogger.log("signInAnonymously:success")
                                val user = mAuth.currentUser
                                user?.let {
                                    val userEntity = FirebaseUserMapper().toUserEntity(user)

                                    val response = RegisterUserResponse(userEntity)
                                    callback.onSuccess(response)
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                mLogger.log("signInAnonymously:failure")
                                callback.onFailure(WebServiceException(null))
                            }
                        })
            }

            request.facebook() -> {
                //sign in with facebook

            }

            request.credentials() -> {
                //sign in with userArg and password
                mAuth.createUserWithEmailAndPassword(request.email, request.password)
                        .addOnCompleteListener(mExecutor, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")

                                val user = mAuth.currentUser
                                if (user != null) {
                                    val userEntity = FirebaseUserMapper().toUserEntity(user, request as CredentialsRegisterUserRequest)
                                    val response = RegisterUserResponse(userEntity)
                                    callback.onSuccess(response)
                                } else {
                                    callback.onFailure(WebServiceException(Exception()))
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                callback.onFailure(WebServiceException(Exception()))
                            }
                        })
            }
        }
    }

    private fun addUserToMangoDatabase(userResponse: RegisterUserResponse, imageUri: URI?, callback: Callback<RegisterUserResponse>) {
        if (imageUri == null) {
            mLocalDatabase.addUserBlocking(RoomMangoUser.fromEntity(userResponse.userEntity))
            callback.onSuccess(userResponse)
        } else {
            val request = SubmitImageRequest(path = imageUri, userId = userResponse.userEntity.id)
            submitImage(request, object : Callback<SubmitImageResponse> {
                override fun onFailure(exception: WebServiceException) {
                    mLocalDatabase.addUserBlocking(RoomMangoUser.fromEntity(userResponse.userEntity))
                    callback.onSuccess(userResponse)
                }

                override fun onSuccess(response: SubmitImageResponse) {
                    userResponse.userEntity.photoUrl = response.downloadUrl
                    mUsersRef.add(userResponse.userEntity).addOnCompleteListener(mExecutor, OnCompleteListener {
                        mLocalDatabase.addUserBlocking(RoomMangoUser.fromEntity(userResponse.userEntity))
                        callback.onSuccess(userResponse)
                    })
                }
            })
        }
    }

    /**
     * End of Register User
     */

    /**
     * Start of Login User
     */

    override fun loginUser(request: LoginRequest, callback: Callback<LoginResponse>) {
        when {
            request.password.isNotEmpty() -> {
                signInUserWithFirebase(request.email, request.password, callback)
            }
            request.thirdPartyId.isNotEmpty() -> {
                var queryResponse = mUsersRef.whereEqualTo(DATABASE_FIELD_UNIQUE_ID, request.thirdPartyId).get()

                queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { task ->
                    if (task.isCanceled || task.exception != null) {
                        callback.onFailure(WebServiceException(Exception()))
                    }

                    if (task.isSuccessful && task.result != null && task.result?.documents != null && task.result?.documents?.size != 0) {
                        val userEntity = task.result?.documents?.get(0)?.toObject(UserEntity::class.java)
                        Log.d(TAG, "signInWithEmail:success")

                        // User's firebase id is correct now ensure the id is the same as firebase db has on device
                        if (mAuth.currentUser?.uid.equals(request.thirdPartyId)) {
                            callback.onSuccess(LoginResponse(userEntity!!))
                        } else {
                            callback.onFailure(WebServiceException(Exception()))
                        }
                    } else {
                        callback.onFailure(WebServiceException(Exception())) //TODO: This should be a login error - prompting account creation.
                    }
                })
            }
        }
    }

    private fun signInUserWithFirebase(email: String, password: String, callback: Callback<LoginResponse>) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mExecutor, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        var queryResponse = mUsersRef.whereEqualTo(DATABASE_FIELD_UNIQUE_ID, task.result?.user?.uid).get()

                        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { userEntityDocument ->
                            if (userEntityDocument.isCanceled || userEntityDocument.exception != null) {
                                callback.onFailure(WebServiceException(Exception()))
                            }

                            if (userEntityDocument.isSuccessful && userEntityDocument.result != null && userEntityDocument.result?.documents != null && userEntityDocument.result?.documents?.size != 0) {
                                val userEntity = userEntityDocument.result?.documents?.get(0)?.toObject(UserEntity::class.java)
                                if (userEntity == null) {
                                    mLogger.log("Able to sign in to firebase with credentials but user information is not available on Mango.")
                                    callback.onFailure(WebServiceException(Exception()))
                                    return@OnCompleteListener
                                } else {
                                    Log.d(TAG, "signInWithEmail:success")
                                    mLocalDatabase.addUserBlocking(RoomMangoUser.fromEntity(userEntity))
                                    callback.onSuccess(LoginResponse(userEntity))
                                }
                            } else {
                                mAuth.signOut()
                                callback.onFailure(WebServiceException(Exception()))
                            }
                        })
                    } else {// If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        callback.onFailure(WebServiceException(Exception()))
                    }
                })
    }

    /**
     * End of Login User
     */

    /**
     * Start of Logout User
     */
    override fun logoutUser(request: LogoutRequest, callback: Callback<LogoutResponse>) {
        var queryResponse = mUsersRef.whereEqualTo(DATABASE_FIELD_UNIQUE_ID, request.id).get()

        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isCanceled || task.exception != null) {
                callback.onFailure(WebServiceException(Exception()))
            }

            if (task.isSuccessful && task.result != null && task.result?.documents != null
                    && task.result?.documents?.size != 0) {
                val userEntity = task.result?.documents?.get(0)?.toObject(UserEntity::class.java)
                if (userEntity == null) {
                    mLogger.log("Able to sign in to firebase with credentials but user information is not available on Mango.")
                    callback.onFailure(WebServiceException(Exception()))
                    return@OnCompleteListener
                } else {
                    Log.d(TAG, "signInWithEmail:success")
                    mAuth.signOut()
                    mLocalDatabase.deleteAllUserDataBlocking()
                    callback.onSuccess(LogoutResponse())
                }
            } else {
                callback.onFailure(WebServiceException(Exception()))
            }
        })
    }

    override fun changePassword(request: ChangePasswordRequest, callback: Callback<ChangePasswordResponse>) {
        val credentials = EmailAuthProvider.getCredential(request.email, request.oldPassword)
        mAuth.currentUser?.let {
            it.reauthenticate(credentials).addOnCompleteListener(mExecutor, OnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    it.updatePassword(request.newPassword).addOnCompleteListener { updatePasswordTask ->
                        if(updatePasswordTask.isSuccessful) callback.onSuccess(ChangePasswordResponse())
                        else callback.onFailure(WebServiceException(null))
                    }
                } else {
                    callback.onFailure(WebServiceException(null))
                }
            })
        }
    }

    /** End of Authentication calls **/

    private fun parseDocumentsForMeals(documents: List<DocumentSnapshot>?): MutableList<MealEntity> {
        var mealEntities = mutableListOf<MealEntity>()
        if (documents == null) return mealEntities

        for (mealDoc in documents) {
            mLogger.log(mealDoc.toString())
            val mealEntity = mealDoc.toObject(MealEntity::class.java)
            mealEntity?.let {
                val geoPoint = mealDoc.getGeoPoint("location")
                geoPoint?.let { point ->
                    mealEntity.geoLocation = GeoLocation(point.latitude, point.longitude)
                }
                mealEntity.mealId = mealDoc.id
                mealEntities.add(mealEntity)
            }
        }

        mLogger.log(Gson().toJson(mealEntities))
        return mealEntities
    }

    private fun parseSingleDocumentForMeal(document: DocumentSnapshot?): MealEntity? {
        if (document == null) return null

        val mealEntity = document.toObject(MealEntity::class.java)
        mealEntity?.let {
            val geoPoint = document.getGeoPoint("location")
            geoPoint?.let { point ->
                mealEntity.geoLocation = GeoLocation(point.latitude, point.longitude)
            }

            mealEntity.mealId = document.id
        }

        return mealEntity
    }

    private fun parseDocumentsForUsers(documents: MutableList<DocumentSnapshot>?): MutableList<UserEntity> {
        var userEntities = mutableListOf<UserEntity>()
        if (documents == null) return userEntities

        for (mealDoc in documents) {
            val userEntity = mealDoc.toObject(UserEntity::class.java)
            userEntity?.let { userEntities.add(it) }
        }

        return userEntities
    }

    override fun retrieveFeaturedMeals(request: FeaturedMealsRequest, callback: Callback<FeaturedMealsResponse>) {

        val query = lastCachedMealDocument?.let { mMealsRef.orderBy(DATABASE_FIELD_MEALS_ETA, Query.Direction.ASCENDING).startAfter(it).limit(MEALS_PAGE_SIZE) }
                ?: mMealsRef.orderBy(DATABASE_FIELD_MEALS_ETA, Query.Direction.ASCENDING).limit(MEALS_PAGE_SIZE)

        query.get().addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null) {
                    Log.d(TAG, "Current data: ${snapshot.metadata}")
                    if(snapshot.documents.isNotEmpty()) lastCachedMealDocument = snapshot.documents[snapshot.size() - 1]
                    val mealEntities = parseDocumentsForMeals(snapshot.documents)
                    callback.onSuccess(FeaturedMealsResponse(mealEntities = mealEntities))
                } else {
                    Log.d(TAG, "Current data: null")
                    callback.onFailure(WebServiceException(Exception()))
                }
            } else {
                callback.onFailure(WebServiceException(Exception()))
            }
        })
    }

    override fun submitMeal(request: SubmitMealRequest, callback: Callback<SubmitMealResponse>) {
        var mealEntity = createMealShell(request)
        mMealsRef.add(mealEntity).addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (!task.isSuccessful || task.result == null) callback.onFailure(WebServiceException(null))

            task.result?.let {
                Log.d(TAG, "SubmitMeal: Added meal successfully to Firebase, mealId: ${it.id}")
                mealEntity.mealId = it.id
                mMealsRef.document(mealEntity.mealId).update(DATABASE_FIELD_MEAL_ID, mealEntity.mealId)
                        .addOnCompleteListener(mExecutor, OnCompleteListener {
                            Log.d(TAG, "SubmitMeal: Updated meal id successfully in entity.")
                            if (task.isSuccessful) {
                                submitMultipleImages(request.imageUris, mealEntity.mealId, object : Callback<SubmitMealImagesResponse> {
                                    override fun onFailure(exception: WebServiceException) {
                                        Log.d(TAG, "SubmitMeal: Unable to upload images correctly.")
                                        callback.onFailure(exception)
                                    }

                                    override fun onSuccess(response: SubmitMealImagesResponse) {
                                        Log.d(TAG, "SubmitMeal: Meal images uploaded will update meal entities in firebase, total images: ${response.downloadUrls.size}")
                                        mealEntity.images = response.downloadUrls
                                        mealEntity.featuredImage = response.featuredUrl

                                        mDatabase.runBatch { batch ->
                                            Log.d(TAG, "SubmitMeal: Batching updates for image urls and featured images.")
                                            batch.update(mMealsRef.document(mealEntity.mealId), DATABASE_FIELD_MEALS_IMAGES, mealEntity.images)
                                            batch.update(mMealsRef.document(mealEntity.mealId), DATABASE_FIELD_MEALS_FEATURED_IMAGE, mealEntity.featuredImage)
                                        }.addOnCompleteListener(mExecutor, OnCompleteListener { updateThreadTask ->
                                            if (updateThreadTask.isSuccessful) callback.onSuccess(SubmitMealResponse(mealEntity))
                                            else callback.onFailure(WebServiceException(null))
                                        })
                                    }
                                })
                            } else {
                                Log.d(TAG, "SubmitMeal: Unable to update the mealId in Firebase")
                                callback.onFailure(WebServiceException(null))
                            }
                        })
            } ?: callback.onFailure(WebServiceException(null))
        })
    }

    private fun submitMultipleImages(imageUris: List<URI>, mealId: String, origCallback: Callback<SubmitMealImagesResponse>) {
        val callback = object : Callback<SubmitImageResponse> {
            val totalRequests = imageUris.size
            var completedCount = 0
            var listOfUrls: MutableList<String> = mutableListOf()
            var featuredUrl: String = ""

            override fun onFailure(exception: WebServiceException) {
                Log.d(TAG, "Image upload failed")
                origCallback.onFailure(WebServiceException(null))
            }

            override fun onSuccess(response: SubmitImageResponse) {
                synchronized(this) {
                    completedCount++
                    listOfUrls.add(response.downloadUrl)
                    if(response.originalPath == imageUris[0].toString()) featuredUrl = response.downloadUrl

                    Log.d(TAG, "Image upload success of $completedCount of $totalRequests")
                    if (completedCount == totalRequests) {
                        origCallback.onSuccess(SubmitMealImagesResponse(listOfUrls, featuredUrl))
                    }
                }
            }
        }

        for(i in imageUris.indices) {
            submitImage(SubmitImageRequest(path = imageUris[i], mealId = mealId, identifier = i.toString()), callback)
        }

    }

    /**
     * Because a GeoPoint part of the Firebase SDK our entity models don't have that dependency.
     */
    private fun createMealShell(request: SubmitMealRequest): MealEntity {
        return FirebaseMealEntity().apply {
            name = request.mealName
            zipCode = request.zipCode
            longDescription = request.mealDescription
            chefId = request.userId
            chefName = request.userName
            chefPhotoUrl = request.userPhotoUrl
            city = request.city
            eta = request.eta
            ingredients = request.ingredientMap
            allergens = request.allergenMap
            location = GeoPoint(request.latitude, request.longitude)
        }
    }

    override fun retrieveMeal(request: MealRequest, callback: Callback<MealResponse>) {
        var queryResponse = if(request.mealId != null) mMealsRef.whereEqualTo(DATABASE_FIELD_MEAL_ID, request.mealId)
        else mMealsRef.whereEqualTo(DATABASE_FIELD_MEALS_CHEF, request.userId)

        queryResponse.get().addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isCanceled || task.exception != null) {
                callback.onFailure(WebServiceException(Exception()))
            }

            if (task.isSuccessful && task.result != null && task.result?.documents != null
                    && task.result?.documents?.size != 0) {
                val documents = task.result?.documents ?: emptyList()
                Log.d(TAG, "Current data: $documents")
                var mealEntities = parseDocumentsForMeals(documents)

                callback.onSuccess(MealResponse(mealEntities))
            } else {
                Log.d(TAG, "Current data: null")
                callback.onFailure(WebServiceException(Exception()))
            }
        })
    }

    override fun retrieveLocalMeals(request: LocalMealsRequest, callback: Callback<LocalMealsResponse>) {
        val lesserGeoPoint = GeoPoint(request.southwest[0], request.southwest[1])
        val greaterGeoPoint = GeoPoint(request.northeast[0], request.northeast[1])

        var queryResponse = mMealsRef.whereGreaterThan(DATABASE_FIELD_MEALS_LOCATION, lesserGeoPoint).whereLessThan(DATABASE_FIELD_MEALS_LOCATION, greaterGeoPoint).get()
        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isCanceled || task.exception != null) {
                callback.onFailure(WebServiceException(Exception()))
            }

            if (task.isSuccessful && task.result != null && task.result?.documents != null
                    && task.result?.documents?.size != 0) {
                val documents = task.result?.documents ?: emptyList()
                Log.d(TAG, "Current data: $documents")
                var mealEntities = parseDocumentsForMeals(documents)

                callback.onSuccess(LocalMealsResponse(mealEntities = mealEntities))
            } else {
                Log.d(TAG, "Current data: null")
                callback.onFailure(WebServiceException(Exception()))
            }
        })
    }

    override fun closeMeal(request: CloseMealRequest, callback: Callback<CloseMealResponse>) {
        mMealsRef.document(request.mealId).get().addOnCompleteListener(mExecutor, OnCompleteListener { getMealTask ->
            if(getMealTask.isSuccessful) {
                val meal = parseSingleDocumentForMeal(getMealTask.result)

                if(meal == null || meal.chefId != request.chefId || (meal.eta?.after(Date()) == true && !request.forceDelete)) {
                    callback.onFailure(WebServiceException(null))
                } else {
                    mMealsRef.document(request.mealId).delete().addOnCompleteListener(mExecutor, OnCompleteListener { deleteMealTask ->
                        if(deleteMealTask.isSuccessful) {
                            callback.onSuccess(CloseMealResponse())
                        } else {
                            callback.onFailure(WebServiceException(null))
                        }
                    })
                }

            } else {
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    private fun createOrderEntity(request: OrderRequest) : OrderEntity {
        return OrderEntity(request.mealId, request.patronId, request.patronName, request.numberOrders, false)
    }

    override fun submitOrder(request: OrderRequest, callback: Callback<OrderResponse>) {
        var queryResponse = mMealsRef.document(request.mealId).get()
        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isSuccessful) {
                val meal = parseSingleDocumentForMeal(task.result)

                if (meal == null || meal.patrons.containsKey(request.patronId)) {
                    mLogger.log("Error retrieving my ordered meals.")
                    callback.onFailure(WebServiceException(null))
                } else {
                    mLogger.log("Retrieved meal to order: ${meal.mealId}")
                    val order = createOrderEntity(request)
                    mOrdersRef.add(order).addOnCompleteListener(mExecutor, OnCompleteListener { addOrderTask ->
                        val serverSideOrderId = addOrderTask.result?.id ?: ""
                        if (addOrderTask.isSuccessful && serverSideOrderId.isNotEmpty()) {
                            mDatabase.runBatch { batch ->
                                batch.update(mMealsRef.document(request.mealId), DATABASE_FIELD_MEALS_PATRONS_NESTED + request.patronId, order.acceptedStatus)
                                batch.update(mOrdersRef.document(serverSideOrderId), DATABASE_FIELD_ORDERS_ORDER_ID, serverSideOrderId)
                            }.addOnCompleteListener(mExecutor, OnCompleteListener { addOrderToMealTask ->
                                if (addOrderToMealTask.isSuccessful) {
                                    callback.onSuccess(OrderResponse(meal, order))
                                } else {
                                    callback.onFailure(WebServiceException(null))
                                }
                            })
                        } else {
                            callback.onFailure(WebServiceException(null))
                        }
                    })
                }
            } else {
                mLogger.log("Error retrieving my ordered meals.")
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    private fun updateOrder(request: OrderConfirmRequest, callback: Callback<OrderConfirmResponse>) {
        mOrdersRef.document(request.orderId).update(DATABASE_FIELD_ORDERS_ACCEPTED_STATUS, true).addOnCompleteListener(mExecutor, OnCompleteListener { addOrderTask ->
            if (addOrderTask.isSuccessful) {
                callback.onSuccess(OrderConfirmResponse(request.orderId, true, request.numberOfOrders))
            } else {
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    private fun deleteOrder(request: CancelOrderRequest, callback: Callback<CancelOrderResponse>) {
        mOrdersRef.document(request.orderId).delete().addOnCompleteListener(mExecutor, OnCompleteListener { addOrderTask ->
            if (addOrderTask.isSuccessful) {
                callback.onSuccess(CancelOrderResponse(request.orderId, request.patronId, request.numberOfOrders))
            } else {
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    /**
     * Assumes this is called only to set the acceptedStatus Boolean from false to true
     */
    override fun confirmOrder(request: OrderConfirmRequest, callback: Callback<OrderConfirmResponse>) {
        var queryResponse = mMealsRef.document(request.mealId).get()
        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isSuccessful) {
                val meal = parseSingleDocumentForMeal(task.result)
                val patron = meal?.patrons?.get(request.patronId)

                if (meal == null || patron == null) {
                    mLogger.log("Error retrieving my ordered meals.")
                    callback.onFailure(WebServiceException(null))
                } else {
                    mLogger.log("Retrieved meal to order: ${meal.mealId}")
                    meal.orders = (meal.orders + request.numberOfOrders)

                    mDatabase.runBatch { batch ->
                        batch.update(mMealsRef.document(request.mealId), DATABASE_FIELD_MEALS_ORDERS, meal.orders)
                        batch.update(mMealsRef.document(request.mealId), DATABASE_FIELD_MEALS_PATRONS_NESTED + request.patronId, true)
                    }.addOnCompleteListener(mExecutor, OnCompleteListener { updateThreadTask ->
                        if (updateThreadTask.isSuccessful) updateOrder(request, callback)
                        else callback.onFailure(WebServiceException(null))
                    })
                }
            } else {
                mLogger.log("Error retrieving my ordered meals.")
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    override fun retrieveOrdersForMeal(request: RetrieveOrdersForMealRequest, callback: Callback<RetrieveOrdersForMealResponse>) {
        mOrdersRef.whereEqualTo(DATABASE_FIELD_ORDERS_MEAL_ID, request.mealId).get().addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isSuccessful) {
                val docs = task.result?.documents ?: emptyList()
                val orders = mutableListOf<OrderEntity>()
                for(doc in docs) {
                    doc.toObject(OrderEntity::class.java)?.let { orders.add(it) }
                }

                callback.onSuccess(RetrieveOrdersForMealResponse(orders))
            } else {
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    override fun retrieveMyOrderedMeals(request: MyMealsRequest, callback: Callback<MyMealsResponse>) {
        mMealsRef.whereIn(DATABASE_FIELD_MEALS_PATRONS_NESTED + request.userId, listOf(true, false)).get().addOnCompleteListener(mExecutor, OnCompleteListener { task ->

            if (task.isSuccessful) {
                val meals = parseDocumentsForMeals(task.result?.documents)

                mLogger.log("Retrieved my meals, a total of ${meals.size}.")
                callback.onSuccess(MyMealsResponse(meals))
            } else {
                mLogger.log("Error retrieving my ordered meals.")
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    override fun cancelOrder(request: CancelOrderRequest, callback: Callback<CancelOrderResponse>) {
        var queryResponse = mMealsRef.document(request.mealId).get()
        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { task ->
            if (task.isSuccessful) {
                val meal = parseSingleDocumentForMeal(task.result)
                val patron = meal?.patrons?.get(request.patronId)

                if (meal == null || patron == null || patron) {
                    mLogger.log("Error retrieving my ordered meals or request already confirmed.")
                    callback.onFailure(WebServiceException(null))
                } else {
                    mLogger.log("Retrieved meal to order: ${meal.mealId}")
                    meal.orders -= request.numberOfOrders

                    mDatabase.runBatch { batch ->
                        batch.update(mMealsRef.document(request.mealId), DATABASE_FIELD_MEALS_ORDERS, meal.orders)
                        batch.update(mMealsRef.document(request.mealId), DATABASE_FIELD_MEALS_PATRONS_NESTED + request.patronId, FieldValue.delete())
                    }.addOnCompleteListener(mExecutor, OnCompleteListener { updateThreadTask ->
                        if (updateThreadTask.isSuccessful) deleteOrder(request, callback)
                        else callback.onFailure(WebServiceException(null))
                    })
                }
            } else {
                mLogger.log("Error retrieving my ordered meals.")
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    override fun retrieveMyChefMeals(request: MyChefMealsRequest, callback: Callback<MyMealsResponse>) {
        mMealsRef.whereEqualTo(DATABASE_FIELD_MEALS_CHEF, request.userId).get().addOnCompleteListener(mExecutor, OnCompleteListener { task ->

            if (task.isSuccessful) {
                val meals = parseDocumentsForMeals(task.result?.documents)

                mLogger.log("Retrieved my meals, a total of ${meals.size}.")
                callback.onSuccess(MyMealsResponse(meals))
            } else {
                mLogger.log("Error retrieving my ordered meals.")
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    private fun parseDocumentsForSocialNetworks(documents: MutableMap<String, Any>): MutableList<SocialNetworkEntity> {
        var entities = mutableListOf<SocialNetworkEntity>()

        for (socialNetworkDoc in documents.values) {
            entities.add(SocialNetworkEntity.fromMap(socialNetworkDoc as HashMap<String, String>))
        }

        return entities
    }

    override fun retrieveFollowers(request: FollowersRequest, callback: Callback<FollowersResponse>) {
        mFollowersRef.document(request.userId).get().addOnCompleteListener(mExecutor, OnCompleteListener {
            it.result?.data?.let { data ->

                val socialNetworkEntity = parseDocumentsForSocialNetworks(data)
                callback.onSuccess(FollowersResponse(socialNetworkEntity))

            } ?: callback.onFailure(WebServiceException(Exception()))
        })
    }

    override fun updateFollowers(request: UpdateFollowersRequest, callback: Callback<UpdateFollowersResponse>) {
        val updates = if(request.delete) hashMapOf<String, Any>(request.followerId to FieldValue.delete())
        else hashMapOf(request.followerId to SocialNetworkEntity(request.id, request.followerId, request.followerPhotoUrl, request.followerUsername))

        mFollowersRef.document(request.id).set(updates, SetOptions.merge()).addOnCompleteListener(mExecutor, OnCompleteListener { deleteTask ->
            val userThumbnailEntity = UserThumbnailEntity(request.followerId, request.followerPhotoUrl, request.followerUsername)
            if(deleteTask.isSuccessful) callback.onSuccess(UpdateFollowersResponse(request.delete, userThumbnailEntity))
            else callback.onFailure(WebServiceException(null))
        })
    }

    override fun retrieveProfile(request: ProfileRequest, callback: Callback<ProfileResponse>) {
        var queryResponse = mUsersRef.whereEqualTo(DATABASE_FIELD_UNIQUE_ID, request.id).get()
        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener {
            if (it.isSuccessful && it.result?.documents?.isNotEmpty() == true) {
                val userEntity = it.result?.documents?.get(0)?.toObject(UserEntity::class.java)

                when {
                    userEntity == null -> {
                        callback.onFailure(WebServiceException(Exception()))
                    }
                    request.currentUserId == request.id -> {
                        callback.onSuccess(ProfileResponse(userEntity))
                    }
                    else -> {
                        mFollowersRef.whereEqualTo(request.id + "." + DATABASE_FIELD_FOLLOWERS_ORIGINATOR, request.currentUserId).get().addOnCompleteListener(mExecutor, OnCompleteListener { isFollowingTask ->
                            userEntity.following = isFollowingTask.isSuccessful && isFollowingTask.result?.documents?.isNotEmpty() == true
                            callback.onSuccess(ProfileResponse(userEntity))
                        })
                    }
                }
            } else {
                callback.onFailure(WebServiceException(Exception()))
            }
        })
    }

    override fun updateProfile(request: UpdateProfileRequest, callback: Callback<ProfileResponse>) {
        var queryResponse = mUsersRef.whereEqualTo(DATABASE_FIELD_UNIQUE_ID, request.id).get()
        queryResponse.addOnCompleteListener(mExecutor, OnCompleteListener { retrieveProfileTask ->
            if (retrieveProfileTask.isSuccessful && retrieveProfileTask.result?.documents?.isNotEmpty() == true) {
                val document = retrieveProfileTask.result?.documents?.get(0)
                var userEntity = document?.toObject(UserEntity::class.java)

                if(document == null || userEntity == null) {
                    callback.onFailure(WebServiceException(Exception()))
                } else {
                    request.bio?.let { userEntity.bio = it }
                    request.notificationEnabled?.let {
                        if(it) userEntity.notificationTokens = request.notificationTokensToAdd ?: mutableListOf()
                        else userEntity.notificationTokens.clear()
                    }

                    mDatabase.runBatch { batch ->
                        batch.update(mUsersRef.document(document.id), DATABASE_FIELD_BIO, userEntity.bio)
                        batch.update(mUsersRef.document(document.id), DATABASE_FIELD_NOTIFICATION_TOKENS, userEntity.notificationTokens)
                    }.addOnCompleteListener(mExecutor, OnCompleteListener { updateThreadTask ->
                        if(updateThreadTask.isSuccessful) callback.onSuccess(ProfileResponse(userEntity))
                        else callback.onFailure(WebServiceException(null))
                    })
                }
            } else {
                callback.onFailure(WebServiceException(Exception()))
            }
        })
    }

    override fun favoriteMeal(request: FavoriteMealRequest, callback: Callback<FavoriteMealResponse>) {

    }

    override fun retrieveFavoriteMeals(request: FavoriteMealsRequest, callback: Callback<FavoriteMealsResponse>) {

    }

    override fun retrieveChef(request: ChefRequest, callback: Callback<ChefResponse>) {

    }

    override fun retrieveLocalChefs(request: LocalChefsRequest, callback: Callback<LocalChefsResponse>) {

    }

    class FirebaseMessageEventListener(private var channel: Channel<MessagesResponse>? = null) : EventListener<QuerySnapshot> {
        override fun onEvent(snapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && !snapshot.isEmpty) {
                Log.d(TAG, "$source data size: ${snapshot.documents.size}")
                val messagesDoc = snapshot.documents
                if (messagesDoc.isNullOrEmpty()) {
                    channel?.onFailure(WebServiceException(null))
                } else {
                    val messages = messagesDoc[0].toObject(MessagesEntity::class.java)
                    messages?.let { channel?.onUpdate(MessagesResponse(it)) }
                }
            } else {
                Log.d(TAG, "$source data: null")
                channel?.onFailure(WebServiceException(null))
            }
        }
    }

    override fun listenForMessages(request: MessagesRequest, channel: Channel<MessagesResponse>) {
        docRef = mMessagesRef.whereEqualTo(DATABASE_FIELD_MESSAGE_MEAL_ID, request.mealId)
                    .whereArrayContains(DATABASE_FIELD_MESSAGE_IDS, request.firstUser + request.secondUser)
                    .addSnapshotListener(FirebaseMessageEventListener(channel))
    }

    override fun stopListeningForMessages(callback: Callback<BaseResponse>) {
        try {
            docRef?.remove()
        } catch (e: Exception) {
            callback.onFailure(WebServiceException(null))
        } finally {
            docRef = null
        }
    }

    private fun createMessageThreadEntity(request: SubmitMessageRequest): MessageThreadEntity {
        return MessageThreadEntity(listOf(request.senderUserId, request.recipientUserId), listOf(request.senderUserId + request.recipientUserId, request.recipientUserId + request.senderUserId),
                request.mealId, request.timestamp, request.senderUserId, request.senderUserName, request.senderUserProfile,
                request.recipientUserId, request.recipientUserName, request.recipientUserProfile, request.mealName,
                request.message)
    }

    override fun submitMessage(request: SubmitMessageRequest, callback: Callback<SubmitMessageResponse>) {
        mMessageThreadsRef.whereEqualTo(DATABASE_FIELD_MESSAGE_THREAD_MEAL_ID, request.mealId)
                .whereArrayContains(DATABASE_FIELD_MESSAGE_THREAD_ID, request.senderUserId + request.recipientUserId)
                .get().addOnCompleteListener(mExecutor, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val threads = task.result?.documents
                        if (threads.isNullOrEmpty()) {
                            mMessageThreadsRef.add(createMessageThreadEntity(request)).addOnCompleteListener(mExecutor, OnCompleteListener { addThreadTask ->
                                if (addThreadTask.isSuccessful) {
                                    submitOnlyMessage(request, callback)
                                } else {
                                    callback.onFailure(WebServiceException(null))
                                }
                            })
                        } else {
                            // modify thread to latest timestamp and preview
                            val thread = threads[0]
                            mDatabase.runBatch { batch ->
                                batch.update(mMessageThreadsRef.document(thread.id), DATABASE_FIELD_MESSAGE_THREAD_TIMESTAMP, request.timestamp)
                                batch.update(mMessageThreadsRef.document(thread.id), DATABASE_FIELD_MESSAGE_THREAD_PREVIEW, request.message)
                            }.addOnCompleteListener(mExecutor, OnCompleteListener { updateThreadTask ->
                                if (updateThreadTask.isSuccessful) submitOnlyMessage(request, callback)
                                else callback.onFailure(WebServiceException(null))
                            })
                        }
                    } else {
                        callback.onFailure(WebServiceException(null))

                    }
                })
    }

    private fun createMessagesEntity(request: SubmitMessageRequest): MessagesEntity {
        return MessagesEntity(listOf(request.senderUserId + request.recipientUserId, request.recipientUserId + request.senderUserId),
                request.mealId, listOf(MessageEntity(request.message, request.timestamp, request.senderUserId, request.senderUserName, request.senderUserProfile,
                request.recipientUserId, request.recipientUserName, request.recipientUserProfile)), request.timestamp)
    }

    private fun submitOnlyMessage(request: SubmitMessageRequest, callback: Callback<SubmitMessageResponse>) {
        mMessagesRef.whereEqualTo(DATABASE_FIELD_MESSAGE_MEAL_ID, request.mealId)
                .whereArrayContains(DATABASE_FIELD_MESSAGE_IDS, request.senderUserId + request.recipientUserId).get().addOnCompleteListener(mExecutor, OnCompleteListener { retrieveMessagesTask ->
                    if (retrieveMessagesTask.isSuccessful) {
                        val messagesList = retrieveMessagesTask.result?.documents
                        if (messagesList.isNullOrEmpty()) {
                            mMessagesRef.add(createMessagesEntity(request)).addOnCompleteListener(mExecutor, OnCompleteListener { addMessageTask ->
                                if (addMessageTask.isSuccessful) callback.onSuccess(SubmitMessageResponse(request.timestamp))
                                else callback.onFailure(WebServiceException(null))
                            })
                        } else {
                            val messageEntity = MessageEntity(request.message, request.timestamp, request.senderUserId, request.senderUserName, request.senderUserProfile,
                                    request.recipientUserId, request.recipientUserName, request.recipientUserProfile)
                            val messages = messagesList[0]
                            mDatabase.runBatch { batch ->
                                batch.update(mMessagesRef.document(messages.id), DATABASE_FIELD_MESSAGE_TIMESTAMP, request.timestamp)
                                batch.update(mMessagesRef.document(messages.id), DATABASE_FIELD_MESSAGE_MESSAGES, FieldValue.arrayUnion(messageEntity))
                            }.addOnCompleteListener(mExecutor, OnCompleteListener { updateThreadTask ->
                                if (updateThreadTask.isSuccessful) callback.onSuccess(SubmitMessageResponse(request.timestamp))
                                else callback.onFailure(WebServiceException(null))
                            })
                        }
                    } else {
                        callback.onFailure(WebServiceException(null))
                    }
                })
    }

    override fun retrieveMessageThreads(request: MessageThreadsRequest, callback: Callback<MessageThreadsResponse>) {
        mMessageThreadsRef.whereArrayContains(DATABASE_FIELD_MESSAGE_THREAD_PARTICIPANTS, request.userId).get().addOnCompleteListener(mExecutor, OnCompleteListener { task ->

            if (task.isSuccessful) {
                val threads = mutableListOf<MessageThreadEntity>()
                val documents = task.result?.documents

                if (documents.isNullOrEmpty()) {
                    callback.onSuccess(MessageThreadsResponse(threads))
                } else {

                    for (threadDoc in documents) {
                        val threadEntity = threadDoc.toObject(MessageThreadEntity::class.java)
                        threadEntity?.let { threads.add(it) }
                    }

                    threads.sortBy { it.lastTimestamp }
                    mLogger.log("Retrieved my message threads, a total of ${threads.size}.")
                    callback.onSuccess(MessageThreadsResponse(threads))
                }

            } else {
                mLogger.log("Error retrieving my ordered meals.")
                callback.onFailure(WebServiceException(null))
            }
        })
    }

    override fun submitImage(request: SubmitImageRequest, callback: Callback<SubmitImageResponse>) {
        val file = Uri.parse(request.path.toString())
        var storageRef: StorageReference? = null

        var finalRefPath = ""
        when {
            request.userId.isNotEmpty() -> finalRefPath = "users_" + request.userId
            request.mealId.isNotEmpty() -> finalRefPath = "meals_" + request.mealId + "_" + request.identifier
            else -> callback.onFailure(WebServiceException(null))
        }

        storageRef = mStorageRef.child(finalRefPath)

        Log.d(TAG, "Uploading image ref: ${storageRef.path}")
        storageRef.let {
            it.putFile(file)
                    .addOnSuccessListener(mExecutor, OnSuccessListener { _ ->
                        it.downloadUrl.addOnCompleteListener(mExecutor, OnCompleteListener { downloadUrl ->
                            Log.d(TAG, "Uploaded image's url: ${downloadUrl.result}")

                            val resizedUrl = downloadUrl.result.toString().replace(finalRefPath, "$finalRefPath$RESIZE_POSTFIX")

                            val response = SubmitImageResponse(originalPath = request.path.toString(),
                                    downloadUrl = resizedUrl, userId = request.userId, mealId = request.mealId)

                            callback.onSuccess(response)
                        })
                    })
                    .addOnFailureListener(mExecutor, OnFailureListener {
                        Log.d(TAG, "Failed to upload image, exception: $it")
                        callback.onFailure(WebServiceException(null))
                    })
        }
    }

    /** End of Data storage **/

    companion object {
        val TAG: String = FirebaseRepository::class.java.simpleName
        var docRef: ListenerRegistration? = null
    }
}
