package com.moraware.domain.mappers

import com.moraware.data.entities.MealEntity
import com.moraware.data.models.FeaturedMealsResponse
import com.moraware.data.models.LocalMealsResponse
import com.moraware.data.models.MyMealsResponse
import com.moraware.domain.models.Meal

class MealMapper {
    fun transform(response: FeaturedMealsResponse, userId: String): List<Meal> {
        val meals = arrayListOf<Meal>()
        response.mealEntities.forEach {
            val meal = Meal()
            meal.mealId = it.mealId
            meal.name = it.name
            meal.zipCode = it.zipCode
            meal.description = it.longDescription
            meal.featuredImage = it.featuredImage
            meal.imageUrls = it.images
            meal.favorite = it.liked.contains(userId)
            meal.ordered = it.patrons.contains(userId)
            meal.videosAvailable = it.videos.isNotEmpty()
            meal.videoUrls = it.videos
            meal.placeholder = it.placeholder
            meal.chefId = it.chefId
            meal.chefName = it.chefName
            meal.chefPhotoUrl = it.chefPhotoUrl
            meal.city = it.city
            meal.patrons = it.patrons
            meal.orderId = it.orderId
            meal.maxOrders = it.maxOrders
            meal.eta = it.eta
            meal.ingredients = it.ingredients
            meal.allergens = it.allergens
            meal.numberOfFavorites = it.liked.size
            meal.servingsPerOrder = it.servingsPerOrder
            meal.numberOfOrders = it.orders
            meal.latitude = it.geoLocation.latitude
            meal.longitude = it.geoLocation.longitude
            meal.imageDescriptions = it.imageDescription
            meals.add(meal)
        }

        return meals
    }

    fun transform(response: MyMealsResponse, userId: String): List<Meal> {
        val meals = arrayListOf<Meal>()
        response.mealEntities.forEach {
            val meal = Meal()
            meal.mealId = it.mealId
            meal.name = it.name
            meal.zipCode = it.zipCode
            meal.description = it.longDescription
            meal.featuredImage = it.featuredImage
            meal.imageUrls = it.images
            meal.favorite = it.liked.contains(userId)
            meal.ordered = it.patrons.contains(userId)
            meal.videosAvailable = it.videos.isNotEmpty()
            meal.videoUrls = it.videos
            meal.placeholder = it.placeholder
            meal.chefId = it.chefId
            meal.chefName = it.chefName
            meal.chefPhotoUrl = it.chefPhotoUrl
            meal.city = it.city
            meal.patrons = it.patrons
            meal.orderId = it.orderId
            meal.maxOrders = it.maxOrders
            meal.eta = it.eta
            meal.ingredients = it.ingredients
            meal.allergens = it.allergens
            meal.numberOfFavorites = it.liked.size
            meal.servingsPerOrder = it.servingsPerOrder
            meal.numberOfOrders = it.orders
            meal.latitude = it.geoLocation.latitude
            meal.longitude = it.geoLocation.longitude
            meal.imageDescriptions = it.imageDescription
            meals.add(meal)
        }

        return meals
    }

    fun transform(mealEntities: List<MealEntity>, userId: String): List<Meal> {
        val meals = arrayListOf<Meal>()
        mealEntities.forEach {
            val meal = Meal()
            meal.mealId = it.mealId
            meal.name = it.name
            meal.zipCode = it.zipCode
            meal.description = it.longDescription
            meal.featuredImage = it.featuredImage
            meal.imageUrls = it.images
            meal.favorite = it.liked.contains(userId)
            meal.ordered = it.patrons.contains(userId)
            meal.videosAvailable = it.videos.isNotEmpty()
            meal.videoUrls = it.videos
            meal.placeholder = it.placeholder
            meal.chefId = it.chefId
            meal.chefName = it.chefName
            meal.chefPhotoUrl = it.chefPhotoUrl
            meal.city = it.city
            meal.patrons = it.patrons
            meal.orderId = it.orderId
            meal.maxOrders = it.maxOrders
            meal.eta = it.eta
            meal.ingredients = it.ingredients
            meal.allergens = it.allergens
            meal.numberOfFavorites = it.liked.size
            meal.servingsPerOrder = it.servingsPerOrder
            meal.numberOfOrders = it.orders
            meal.latitude = it.geoLocation.latitude
            meal.longitude = it.geoLocation.longitude
            meal.imageDescriptions = it.imageDescription
            meals.add(meal)
        }

        return meals
    }

    fun transform(entity: MealEntity, userId: String): Meal {
        val meal = Meal()
        meal.mealId = entity.mealId
        meal.name = entity.name
        meal.zipCode = entity.zipCode
        meal.description = entity.longDescription
        meal.featuredImage = entity.featuredImage
        meal.imageUrls = entity.images
        meal.favorite = entity.liked.contains(userId)
        meal.ordered = entity.patrons.contains(userId)
        meal.videosAvailable = entity.videos.isNotEmpty()
        meal.videoUrls = entity.videos
        meal.placeholder = entity.placeholder
        meal.chefId = entity.chefId
        meal.chefPhotoUrl = entity.chefPhotoUrl
        meal.chefName = entity.chefName
        meal.city = entity.city
        meal.patrons = entity.patrons
        meal.orderId = entity.orderId
        meal.maxOrders = entity.maxOrders
        meal.eta = entity.eta
        meal.ingredients = entity.ingredients
        meal.allergens = entity.allergens
        meal.numberOfFavorites = entity.liked.size
        meal.servingsPerOrder = entity.servingsPerOrder
        meal.numberOfOrders = entity.orders
        meal.latitude = entity.geoLocation.latitude
        meal.longitude = entity.geoLocation.longitude
        meal.imageDescriptions = entity.imageDescription

        return meal
    }

    fun transform(response: LocalMealsResponse, userId: String): List<Meal> {
        val meals = arrayListOf<Meal>()
        response.mealEntities.forEach {
            val meal = Meal()
            meal.mealId = it.mealId
            meal.name = it.name
            meal.zipCode = it.zipCode
            meal.description = it.longDescription
            meal.featuredImage = it.featuredImage
            meal.imageUrls = it.images
            meal.favorite = it.liked.contains(userId)
            meal.ordered = it.patrons.contains(userId)
            meal.videosAvailable = it.videos.isNotEmpty()
            meal.videoUrls = it.videos
            meal.placeholder = it.placeholder
            meal.chefId = it.chefId
            meal.chefName = it.chefName
            meal.chefPhotoUrl = it.chefPhotoUrl
            meal.city = it.city
            meal.patrons = it.patrons
            meal.orderId = it.orderId
            meal.maxOrders = it.maxOrders
            meal.eta = it.eta
            meal.ingredients = it.ingredients
            meal.allergens = it.allergens
            meal.numberOfFavorites = it.liked.size
            meal.servingsPerOrder = it.servingsPerOrder
            meal.numberOfOrders = it.orders
            meal.latitude = it.geoLocation.latitude
            meal.longitude = it.geoLocation.longitude
            meal.imageDescriptions = it.imageDescription
            meals.add(meal)
        }

        return meals
    }
}