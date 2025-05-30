package com.eat.jani.models.products

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val category: String,
    val nameGeo: String,
    val nameEng: String,
    val brandGeo: String,
    val imagePath: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
) : Parcelable

/*
    "category": "Carb",
    "nameGeo": "წიწიბურა",
    "nameEng": "BuckWheat",
    "brandGeo": "ალტაისკაია",
    "brandEng": "Altagracia",
    "imagePath": "/Images/CarbProducts/წიწიბურა-ალტაისკაია-სკაზკა.jpg",
    "calories": 330,
    "protein": 13,
    "carbs": 68,
    "fat": 2.5
*/
