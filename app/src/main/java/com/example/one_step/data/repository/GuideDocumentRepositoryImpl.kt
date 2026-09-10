package com.example.one_step.data.repository

import com.example.one_step.domain.model.GuideDocument
import com.example.one_step.domain.repository.GuideDocumentRepository

class GuideDocumentRepositoryImpl : GuideDocumentRepository {
    override suspend fun getActiveDocument(): GuideDocument? = null
}
