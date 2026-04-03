package edu.nd.pmcburne.hello.data

import android.util.Log
import edu.nd.pmcburne.hello.network.ApiService

class Repository(
    private val dao: LocationDao,
    private val api: ApiService
) {

    suspend fun syncData() {
        val apiData = api.getLocations()

        val entities = apiData.map {
            LocationEntity(
                id = it.id,
                name = it.name,
                description = it.description,
                latitude = it.visual_center.latitude,
                longitude = it.visual_center.longitude,
                tags = it.tag_list.joinToString(",")
            )
        }

        dao.insertAll(entities)
    }

    suspend fun getTags(): List<String> {
        val tagList = dao.getAll().map { it.tags }

        val splitTags = tagList
            // split tag list at comas
            .flatMap { it.split(",") }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()

        return splitTags
    }

    suspend fun getLocationsByTag(tag: String): List<LocationEntity> {
        return dao.getByTag(tag)
    }
}
