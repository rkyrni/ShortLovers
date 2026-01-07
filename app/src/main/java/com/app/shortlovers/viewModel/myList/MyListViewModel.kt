package com.app.shortlovers.viewModel.myList

import androidx.lifecycle.ViewModel
import com.app.shortlovers.core.models.MovieItem

class MyListViewModel : ViewModel() {
    val movies =
        listOf(
            MovieItem("The Midnight Sun", 8.2, 2023, "https://.../midnight_sun.jpg"),
            MovieItem("Echoes of the Past", 7.9, 2022, "https://.../echoes_past.jpg"),
            MovieItem("Crimson Tide", 8.5, 2021, "https://.../crimson_tide.jpg"),
            MovieItem("Whispers of the Wind", 7.7, 2020, "https://.../whispers_wind.jpg"),
            MovieItem("The Silent Witness", 8.0, 2019, "https://.../silent_witness.jpg"),
            MovieItem("Beneath the Surface", 7.5, 2018, "https://.../beneath_surface.jpg")
        )

    init {}
}
