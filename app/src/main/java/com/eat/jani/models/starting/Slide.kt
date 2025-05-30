package com.eat.jani.models.starting

import android.os.Parcel
import android.os.Parcelable

data class Slide(val title: String?, val image: Int, val desc: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(image)
        parcel.writeString(desc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Slide> {
        override fun createFromParcel(parcel: Parcel): Slide {
            return Slide(parcel)
        }

        override fun newArray(size: Int): Array<Slide?> {
            return arrayOfNulls(size)
        }
    }
}
