package com.moraware.data.models

import com.moraware.data.base.BaseRequest

open class MyMealsRequest(val userId: String): BaseRequest() {
}

class MyChefMealsRequest(chefId: String) : MyMealsRequest(chefId) {

}