package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.main.AsteroidListAdapter

/** Set the appropriate status icon depending on if the asteroid is potentially hazardous */
@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

/** Set the appropriate image depending on if the asteroid [isHazardous] */
@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

/** Set the content description for the [imageView] to describe the image currently being
 * displayed depending on if the asteroid [isHazardous] */
@BindingAdapter("statusImageContentDescription")
fun bindStatusImageContentDescription(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.contentDescription =
            context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
    }
}

/** Set the content description for the [isHazardousImageView] to describe the image currently
 * being displayed depending on if the asteroid [isHazardous] */
@BindingAdapter("statusIconContentDescription")
fun bindStatusIconContentDescription(isHazardousImageView: ImageView, isHazardous: Boolean) {
    val context = isHazardousImageView.context
    if (isHazardous) {
        isHazardousImageView.contentDescription =
            context.getString(R.string.potentially_hazardous_asteroid_icon)
    } else {
        isHazardousImageView.contentDescription =
            context.getString(R.string.not_hazardous_asteroid_icon)
    }
}

/** Properly format and display the asteroids [distanceFromEarth] in the provided [textView] */
@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, distanceFromEarth: Double) {
    val context = textView.context
    textView.text =
        String.format(context.getString(R.string.astronomical_unit_format), distanceFromEarth)
}

/** Properly format and display the asteroids [estimatedDiameter] in the provided [textView] */
@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, estimatedDiameter: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), estimatedDiameter)
}

/** Properly format and display the asteroids [velocity] in the provided [textView] */
@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, velocity: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), velocity)
}

/** bind the asteroid [asteroidData] to the [asteroidOverviewRecyclerView] */
@BindingAdapter("listData")
fun bindRecyclerView(asteroidOverviewRecyclerView: RecyclerView, asteroidData: List<Asteroid>?) {
    val adapter = asteroidOverviewRecyclerView.adapter as AsteroidListAdapter
    adapter.submitList(asteroidData)
}
