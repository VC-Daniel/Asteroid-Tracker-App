package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Constants.DESIRED_MEDIA_TYPE
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

/** Display the image of the day from NASA and a list of overviews of asteroids for the
 * selected amount of time (7 days by default) */
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {

        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Set up the view using binding and specify the view model
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // If the current image of the day is an image display it using Picasso
        viewModel.imageOfTheDay.observe(viewLifecycleOwner, Observer { photo ->
            if (photo != null && photo.mediaType == DESIRED_MEDIA_TYPE) {
                // Using Picasso retrieve the image from the url provided by the api and display it
                Picasso.get()
                    .load(photo.url)
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .into(binding.activityMainImageOfTheDay)

                // Set the content description the value provided by the api
                binding.activityMainImageOfTheDay.contentDescription =
                    getString(R.string.nasa_picture_of_day_content_description_format, photo.title)
            }
        })

        // Display a list of overviews of the asteroid data
        binding.asteroidRecycler.adapter = AsteroidListAdapter(AsteroidListAdapter.OnClickListener {
            viewModel.displayAsteroidDetails(it)
        })

        // When an asteroid overview is touched take the user to the details page to display more
        // information about the asteroid
        viewModel.asteroidToNavigateTo.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDataComplete()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If there was an issue retrieving the latest asteroid data notify the user
        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == AsteroidApiStatus.ERROR) {
                displayNetworkError(getString(R.string.asteroid_api_description))
                viewModel.displayAsteroidApiIssueComplete()
            }
        })

        // If there was an issue retrieving the latest image of the day notify the user
        viewModel.imageStatus.observe(viewLifecycleOwner, Observer {
            if (it == AsteroidApiStatus.ERROR) {
                displayNetworkError(getString(R.string.image_api_description))
                viewModel.displayImageApiIssueComplete()
            }
        })
    }

    /** Notify the user that there has been an issue retrieving something from the network */
    private fun displayNetworkError(apiCall: String) {
        val message = getString(R.string.api_error_message_format, apiCall)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Change the data to correspond with the user's selection
        when (item.itemId) {
            R.id.show_today_menu -> viewModel.showTodaysAsteroids()
            R.id.show_week_menu -> viewModel.showWeeksAsteroids()
            R.id.show_all_menu -> viewModel.showAllAsteroids()
        }

        return true
    }
}
