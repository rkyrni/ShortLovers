package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.MainResponse
import com.app.shortlovers.core.models.MainResponseCategory
import com.app.shortlovers.core.models.MainResponseDrama
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Custom deserializer for MainResponse to handle polymorphic `data` field.
 *
 * The API returns different structures for the `data` field:
 * - Tabs like Utama, Terbaru, Populer: data is List<MainResponseCategory>
 * - Tab Semua: data is List<MainResponseDrama> directly
 *
 * This deserializer detects the structure and populates the appropriate field.
 */
class MainResponseDeserializer : JsonDeserializer<MainResponse> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): MainResponse {
        val jsonObject = json.asJsonObject

        val tabName = jsonObject.get("tab_name")?.asString
        val dataArray = jsonObject.getAsJsonArray("data")

        // Determine the type of content in data array
        if (dataArray == null || dataArray.size() == 0) {
            return MainResponse(tabName = tabName, categories = emptyList(), dramas = null)
        }

        val firstElement = dataArray.first().asJsonObject

        // Check if it's a category (has category_name field) or drama (has id field directly)
        val isCategory = firstElement.has("category_name")

        return if (isCategory) {
            // Parse as List<MainResponseCategory>
            val categoryListType = object : TypeToken<List<MainResponseCategory>>() {}.type
            val categories: List<MainResponseCategory> =
                context.deserialize(dataArray, categoryListType)
            MainResponse(tabName = tabName, categories = categories, dramas = null)
        } else {
            // Parse as List<MainResponseDrama>
            val dramaListType = object : TypeToken<List<MainResponseDrama>>() {}.type
            val dramas: List<MainResponseDrama> = context.deserialize(dataArray, dramaListType)
            MainResponse(tabName = tabName, categories = null, dramas = dramas)
        }
    }
}
