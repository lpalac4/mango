package com.moraware.mango.featured

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.moraware.domain.models.Meal
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Luis Palacios on 7/29/17.
 */

open class ItemThumbnail() : Parcelable {

    var mMealId: String = ""

    var mName: String = ""

    var mZipCode: String = ""

    var mDescription: String = ""

    var mFeaturedImage: String = ""

    var mImageUrls: List<String> = emptyList()

    var mFavorite: Boolean = false

    var mOrdered: Boolean = false

    var mVideoUrls: List<String> = emptyList()

    var mPlaceholder: String = ""

    var mChefId: String = ""

    var mChefName: String = ""

    var mChefPhotoUrl = ""

    var mOrderId: String = ""

    var mMaxOrders: Int = -1

    var mNumberOfOrders: Int = -1

    var mETADate: Date? = null

    var mIngredients: List<String> = emptyList()

    var mAllergens: List<String> = emptyList()

    var mNumberOfFavorites: Int = 0

    var mLat: Double = 0.0

    var mLng: Double = 0.0

    var mImageDescriptions: List<String> = emptyList()

    var mCity = ""

    var mPatrons: MutableMap<String, Boolean> = hashMapOf()

    var mChefAwarded: Boolean = false

    constructor(meal: Meal) : this() {
        mMealId = meal.mealId
        mName = meal.name
        mZipCode = meal.zipCode
        mDescription = meal.description
        mFeaturedImage = meal.featuredImage
        mImageUrls = meal.imageUrls
        mImageDescriptions = meal.imageDescriptions
        mFavorite = meal.favorite
        mOrdered = meal.ordered
        mVideoUrls = meal.videoUrls
        mPlaceholder = meal.placeholder
        mOrderId = meal.orderId
        mMaxOrders = meal.maxOrders
        mNumberOfOrders = meal.numberOfOrders
        mETADate = meal.eta
        mIngredients = meal.ingredients
        mAllergens = meal.allergens
        mNumberOfFavorites = meal.numberOfFavorites
        mChefId = meal.chefId
        mChefName = meal.chefName
        mChefPhotoUrl = meal.chefPhotoUrl
        mLat = meal.latitude ?: 0.0
        mLng = meal.longitude ?: 0.0
        mCity = meal.city
        mPatrons = meal.patrons
        mChefAwarded = meal.chefAwarded
    }

    protected constructor(bundle: Parcel) : this() {
        mMealId = bundle.readString() ?: ""
        mName = bundle.readString() ?: ""
        mZipCode = bundle.readString() ?: ""
        mDescription = bundle.readString() ?: ""
        mFeaturedImage = bundle.readString() ?: ""
        mImageUrls = arrayListOf<String>().apply {
            bundle.readList(this as List<String>, String::class.java.classLoader)
        }
        mFavorite = bundle.readByte().toInt() != 0
        mOrdered = bundle.readByte().toInt() != 0
        mVideoUrls = arrayListOf<String>().apply {
            bundle.readList(this as List<String>, String::class.java.classLoader)
        }
        mPlaceholder = bundle.readString() ?: ""
        mOrderId = bundle.readString() ?: "x"
        mMaxOrders = bundle.readInt()
        mNumberOfOrders = bundle.readInt()
        mETADate = Date(bundle.readLong())
        mIngredients = arrayListOf<String>().apply {
            bundle.readList(this as List<String>, String::class.java.classLoader)
        }
        mAllergens = arrayListOf<String>().apply {
            bundle.readList(this as List<String>, String::class.java.classLoader)
        }
        mNumberOfFavorites = bundle.readInt()
        mChefId = bundle.readString() ?: ""
        mChefName = bundle.readString() ?: ""
        mChefPhotoUrl = bundle.readString() ?: ""
        mLat = bundle.readDouble()
        mLng = bundle.readDouble()
        mImageDescriptions = arrayListOf<String>().apply {
            bundle.readList(this as List<String>, String::class.java.classLoader)
        }
        mCity = bundle.readString() ?: ""

        var patronsBundle = bundle.readBundle()
        @Suppress("UNCHECKED_CAST")
        mPatrons = patronsBundle?.getSerializable(PARCEL_PATRONS) as HashMap<String, Boolean>

        mChefAwarded = bundle.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(mMealId)
        dest.writeString(mName)
        dest.writeString(mZipCode)
        dest.writeString(mDescription)
        dest.writeString(mFeaturedImage)
        dest.writeList(mImageUrls)
        dest.writeByte((if (mFavorite) 1 else 0).toByte())
        dest.writeByte((if (mOrdered) 1 else 0).toByte())
        dest.writeList(mVideoUrls)
        dest.writeString(mPlaceholder)
        dest.writeString(mOrderId)
        dest.writeInt(mMaxOrders)
        dest.writeInt(mNumberOfOrders)
        dest.writeLong(mETADate?.time.let { it } ?: 0)
        dest.writeList(mIngredients)
        dest.writeList(mAllergens)
        dest.writeInt(mNumberOfFavorites)
        dest.writeString(mChefId)
        dest.writeString(mChefName)
        dest.writeString(mChefPhotoUrl)
        dest.writeDouble(mLat)
        dest.writeDouble(mLng)
        dest.writeList(mImageDescriptions)
        dest.writeString(mCity)

        var bundle = Bundle()
        bundle.putSerializable(PARCEL_PATRONS, mPatrons as HashMap)
        dest.writeBundle(bundle)
        dest.writeByte((if(mChefAwarded) 1 else 0).toByte())
    }

    companion object {
        const val PARCEL_PATRONS = "patrons"

        fun fromMeals(meals: List<Meal>?): ArrayList<ItemThumbnail> {
            val thumbnails = ArrayList<ItemThumbnail>(meals?.size ?: 0)
            meals?.let {
                for (meal in meals) {
                    thumbnails.add(ItemThumbnail(meal))
                }
            }

            return thumbnails
        }

        @JvmField
        val CREATOR: Parcelable.Creator<ItemThumbnail> = object : Parcelable.Creator<ItemThumbnail> {
            override fun createFromParcel(parcel: Parcel): ItemThumbnail {
                return ItemThumbnail(parcel)
            }

            override fun newArray(size: Int): Array<ItemThumbnail?> {
                return arrayOfNulls(size)
            }
        }
    }
}
