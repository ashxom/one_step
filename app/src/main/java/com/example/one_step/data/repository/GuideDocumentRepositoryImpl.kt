package com.example.one_step.data.repository

import com.example.one_step.domain.model.GuideDocument
import com.example.one_step.domain.repository.GuideDocumentRepository

/** Wiring point for local/remote sources; no persistence or network calls in Issue #1. */
class GuideDocumentRepositoryImpl : GuideDocumentRepository {
    override suspend fun getActiveDocument(): GuideDocument? = null
}
