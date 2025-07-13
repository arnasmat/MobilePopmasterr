package com.example.mobilepopmasterr.network

import com.example.mobilepopmasterr.baseUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.example.mobilepopmasterr.ui.Rectangle
import com.google.android.gms.maps.model.LatLng


// This function is not currently used, it was used in the future to let users "create" their own rectangles and check their population.
suspend fun getPopulationFromCoordinates(rectangle: Rectangle): String? =
    withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val north = maxOf(rectangle.pos1.latitude, rectangle.pos2.latitude)
        val south = minOf(rectangle.pos1.latitude, rectangle.pos2.latitude)
        val east = maxOf(rectangle.pos1.longitude, rectangle.pos2.longitude)
        val west = minOf(rectangle.pos1.longitude, rectangle.pos2.longitude)

        val coordinatesJson = "[$west, $north, $east, $south]"

        val json = """
        {
            "coordinates": $coordinatesJson
        }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$baseUrl/get_population_from_coordinates")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Unexpected code $response")
            }

            response.body.string().let {
                JSONObject(it).getString("population")
            }
        }
    }

suspend fun getRectangleAndPopulation(): Pair<Rectangle, String?> = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$baseUrl/get_rectangle_and_population")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Unexpected code $response")

            val responseBody = response.body.string()
            val result = run {
                val jsonObject = JSONObject(responseBody)
                val cx1 = jsonObject.getDouble("cx1")
                val cy1 = jsonObject.getDouble("cy1")
                val cx2 = jsonObject.getDouble("cx2")
                val cy2 = jsonObject.getDouble("cy2")
                val population = jsonObject.optString("population")

                val rectangle = Rectangle(
                    pos1 = LatLng(cy1, cx1),
                    pos2 = LatLng(cy2, cx2)
                )
                Pair(rectangle, population)
            }
            return@withContext result
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // rethrowing it, note: must be handled if this is called
        throw Exception(e.message)
    }
}