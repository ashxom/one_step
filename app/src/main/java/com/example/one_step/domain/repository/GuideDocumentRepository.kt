package com.example.one_step.domain.repository

import com.example.one_step.domain.model.GuideDocument

interface GuideDocumentRepository {
    suspend fun getActiveDocument(): GuideDocument?
}
