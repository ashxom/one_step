package com.example.one_step.domain.repository

import com.example.one_step.domain.model.GuideDocument

/** Contract for future Room/DataStore and Retrofit-backed document sources. */
interface GuideDocumentRepository {
    suspend fun getActiveDocument(): GuideDocument?
}
