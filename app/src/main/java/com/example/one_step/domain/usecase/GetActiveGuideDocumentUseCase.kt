package com.example.one_step.domain.usecase

import com.example.one_step.domain.model.GuideDocument
import com.example.one_step.domain.repository.GuideDocumentRepository

class GetActiveGuideDocumentUseCase(private val repository: GuideDocumentRepository) {
    suspend operator fun invoke(): GuideDocument? = repository.getActiveDocument()
}
