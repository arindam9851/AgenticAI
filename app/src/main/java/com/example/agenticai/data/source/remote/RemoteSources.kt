package com.example.agenticai.data.source.remote


import com.example.agenticai.data.model.ProductDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreProductSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getProducts(): List<Pair<String, ProductDto>> {
        val snapshot = firestore.collection("products").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val dto = doc.toObject(ProductDto::class.java)
            if (dto != null) Pair(doc.id, dto) else null
        }
    }
}

@Singleton
class RemoteConfigSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    suspend fun init() {
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(settings).await()
        remoteConfig.fetchAndActivate().await()
    }

    fun getGroqApiKey(): String =
        remoteConfig.getString("groq_api_key")
}