package com.example.one_step.data.repository

import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.repository.AiAnalysisRepository
import kotlinx.coroutines.delay

class MockAiAnalysisRepository : AiAnalysisRepository {
    override suspend fun analyzeDocument(text: String): AnalysisResult {
        delay(650)
        return AnalysisResult(
            title = "현장체험학습 안내문",
            documentType = "가정통신문",
            summary = "중요한 일정과 준비물을 알기 쉽게 모았어요.",
            actions = listOf(
                ActionItem("consent", "동의서 서명", "보호자 동의서에 서명해요.", 5),
                ActionItem("payment", "참가비 확인", "참가비를 스쿨뱅킹으로 이체해요.", 3),
                ActionItem("items", "준비물 확인", "체험학습 준비물을 챙겨요.", 10),
            ),
            deadline = "9월 18일(수) 까지",
            location = "본관 1층 행정실",
            items = listOf("보호자 동의서 작성 (서명 필수)"),
            cost = "참가비 20,000원 (스쿨뱅킹 이체)",
            phone = "042-123-4567",
            caution = "안내문 내용을 제출 전에 한 번 더 확인해 주세요.",
            deadlineBadge = "D-5 남음",
            deadlineDescription = "기한 이후에는 행정 전산 마감으로 접수가 불가능해요.",
            locationDescription = "교문 통과 후 오른쪽 현관 입구 바로 옆이에요.",
            tripTitle = "가을 도시 숲 체험학습",
            targetGrade = "3학년 전체",
            phoneLabel = "교무실",
            encouragement = "한 번에 다 하지 않아도 괜찮아요. 아래 버튼을 눌러 첫 번째 단계인 동의서 서명부터 차근차근 도와드릴게요.",
        )
    }
}
