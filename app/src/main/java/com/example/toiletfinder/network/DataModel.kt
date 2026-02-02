package com.example.toiletfinder.network

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

data class OverpassResponse(
    @Json(name = "elements") val elements: List<Element>
)

fun OverpassResponse.toToilets(): List<Toilet> {
    return elements.mapNotNull { element ->
        if (element.lat != null && element.lon != null) {
            Toilet(
                id = element.id,
                lat = element.lat,
                lon = element.lon,
                name = element.tags?.name ?: "Toilette inconnue",
                fee = element.tags?.fee == "yes"
            )
        } else {
            null
        }
    }
}

data class Element(
    @Json(name = "type") val type: String,
    @Json(name = "id") val id: Long,
    @Json(name = "lat") val lat: Double?,
    @Json(name = "lon") val lon: Double?,
    @Json(name = "tags") val tags: Tags?
)

data class Tags(
    @Json(name = "name") val name: String?,
    @Json(name = "toilets:wheelchair") val wheelchair: String?,
    @Json(name = "fee") val fee: String?
)

data class Toilet(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val name: String,
    val fee: Boolean,
    var userRating: Float? = null,
    var commentCount: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeString(name)
        parcel.writeByte(if (fee) 1 else 0)
        parcel.writeValue(userRating)
        parcel.writeInt(commentCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Toilet> {
        override fun createFromParcel(parcel: Parcel): Toilet {
            return Toilet(parcel)
        }

        override fun newArray(size: Int): Array<Toilet?> {
            return arrayOfNulls(size)
        }
    }
    fun distanceTo(location: Location): Float {
        val toiletLocation = Location("").apply {
            latitude = lat
            longitude = lon
        }
        return location.distanceTo(toiletLocation)
    }
}

data class Review(
    val rating: Float,
    val comment: String?
)
