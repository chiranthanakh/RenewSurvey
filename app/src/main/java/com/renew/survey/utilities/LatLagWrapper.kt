package com.renew.survey.utilities

import android.os.Parcel
import android.os.Parcelable
import com.mapbox.mapboxsdk.geometry.LatLng

class LatLagWrapper(val latitude: Double, val longitude: Double) : Parcelable {
        constructor(latLng: LatLng) : this(latLng.latitude, latLng.longitude)

        constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeDouble(latitude)
            parcel.writeDouble(longitude)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<LatLagWrapper> {
            override fun createFromParcel(parcel: Parcel): LatLagWrapper {
                return LatLagWrapper(parcel)
            }

            override fun newArray(size: Int): Array<LatLagWrapper?> {
                return arrayOfNulls(size)
            }
        }
    }