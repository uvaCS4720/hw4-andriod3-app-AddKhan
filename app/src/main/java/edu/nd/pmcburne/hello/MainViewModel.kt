package edu.nd.pmcburne.hello

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import edu.nd.pmcburne.hello.data.*
import edu.nd.pmcburne.hello.network.ApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {

    var selectedTag by mutableStateOf("core")
    var tags by mutableStateOf(listOf<String>())
    var locations by mutableStateOf(listOf<LocationEntity>())

    private lateinit var repository: Repository

    fun init(context: Context) {
        if (::repository.isInitialized) return

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cs.virginia.edu/~wxt4gm/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        repository = Repository(db.locationDao(), api)

        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val fetched = repository.syncData()

            tags = repository.getTags()
            //Log.d("MainViewModel", "Available tags: $tags")

            loadLocations()
        }
    }

    fun loadLocations() {
        viewModelScope.launch {
            locations = repository.getLocationsByTag(selectedTag)
        }
    }

    fun onTagSelected(tag: String) {
        selectedTag = tag
        loadLocations()
    }
}
