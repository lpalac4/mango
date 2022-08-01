package com.moraware.mango.firebase.mappers

import com.google.firebase.firestore.GeoPoint
import com.moraware.data.entities.MealEntity

class FirebaseMealEntity: MealEntity() {
    var location: GeoPoint = GeoPoint(0.0, 0.0)
}