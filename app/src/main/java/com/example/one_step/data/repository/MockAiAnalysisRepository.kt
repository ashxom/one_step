package com.example.one_step.data.repository

import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.repository.AiAnalysisRepository
import kotlinx.coroutines.delay

class MockAiAnalysisRepository : AiAnalysisRepository {
    override suspend fun analyzeDocument(text: String): AnalysisResult {
        delay(650)
        return AnalysisResult(
            title = "현장체험학습 참가 신청서",
            documentType = "학교 안내문",
            summary = "늘솔초등학교 현장체험학습 참가를 위해 보호자 동의와 준비물 확인이 필요해요.",
            actions = listOf(
                ActionItem("schedule", "일정 확인하기", "체험학습 날짜와 집합 시간을 확인해요.", 3),
                ActionItem("items", "준비물 챙기기", "안내된 준비물을 빠짐없이 준비해요.", 10),
                ActionItem("consent", "참가 동의 서명하기", "보호자 동의란을 작성하고 제출해요.", 5),
            ),
            deadline = "2026년 9월 12일",
            location = "늘솔초등학교 및 체험학습장",
            items = listOf("물병", "도시락", "편한 운동화", "우비 또는 우산"),
            cost = "참가비 10,000원",
            phone = "02-1234-5678",
            caution = "출발 10분 전까지 집합하고, 개인 복용약은 별도로 챙겨 주세요.",
        )
    }
}
