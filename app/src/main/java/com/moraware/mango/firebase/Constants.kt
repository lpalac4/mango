package com.moraware.mango.firebase


/**
 * Database Constants
 */

/** Users **/
const val DATABASE_ID_USERS = "users"
const val DATABASE_FIELD_USERS_CHEF = "isChef"
const val DATABASE_FIELD_UNIQUE_ID = "id"
const val DATABASE_FIELD_MY_FOLLOWERS = "followers"
const val DATABASE_FIELD_MY_FOLLOWS = "following"
const val DATABASE_FIELD_USERNAME = "username"
const val DATABASE_FIELD_BIO = "bio"
const val DATABASE_FIELD_NOTIFICATION_TOKENS = "notificationTokens"

/** Meals **/
const val DATABASE_ID_MEALS = "meals"
const val DATABASE_FIELD_MEALS_CHEF = "chefId"
const val DATABASE_FIELD_MEALS_LOCATION = "location"
const val DATABASE_FIELD_MEALS_ORDERS = "orders"
const val DATABASE_FIELD_MEALS_PATRONS_NESTED = "patrons."
const val DATABASE_FIELD_MEALS_PATRONS = "patrons"
const val DATABASE_FIELD_MEAL_ID = "mealId"
const val DATABASE_FIELD_MEALS_IMAGES = "images"
const val DATABASE_FIELD_MEALS_FEATURED_IMAGE = "featuredImage"
const val DATABASE_FIELD_MEALS_ETA = "eta"

/** Orders **/
const val DATABASE_ID_ORDERS = "orders"
const val DATABASE_FIELD_ORDERS_ACCEPTED_STATUS = "acceptedStatus"
const val DATABASE_FIELD_ORDERS_MEAL_ID = "mealId"
const val DATABASE_FIELD_ORDERS_PATRON_ID = "patronId"
const val DATABASE_FIELD_ORDERS_ORDER_ID = "orderId"

/** Messages **/
const val DATABASE_ID_MESSAGES = "messages"
const val DATABASE_FIELD_MESSAGE_IDS = "messageIds"
const val DATABASE_FIELD_MESSAGE_MEAL_ID = "mealId"
const val DATABASE_FIELD_MESSAGE_MESSAGES = "messages"
const val DATABASE_FIELD_MESSAGE_TIMESTAMP = "timestamp"

/** MessageThreads **/
const val DATABASE_ID_MESSAGE_THREADS = "message_threads"
const val DATABASE_FIELD_MESSAGE_THREAD_ID = "messageThreadIds"
const val DATABASE_FIELD_MESSAGE_THREAD_MEAL_ID = "mealId"
const val DATABASE_FIELD_MESSAGE_THREAD_TIMESTAMP = "lastTimestamp"
const val DATABASE_FIELD_MESSAGE_THREAD_PREVIEW = "messageThreadPreview"
const val DATABASE_FIELD_MESSAGE_THREAD_PARTICIPANTS = "participants"

/** Image Storage **/
const val RESIZE_POSTFIX = "_680x680"
const val MEALS_PAGE_SIZE = 25L

/** Followers **/
const val DATABASE_ID_FOLLOWERS = "followers"
const val DATABASE_FIELD_FOLLOWERS_ORIGINATOR = "originator"
const val DATABASE_FIELD_FOLLOWERS_RECIPIENT = "recipient"



