package edu.nd.pmcburne.hello.network

import retrofit2.http.GET

interface ApiService {
    @GET("placemarks.json")
    suspend fun getLocations(): List<ApiLocation>
}