package com.example.one_step.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSuccess
import com.example.one_step.ui.theme.OneStepSuccessSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun AnalysisScreen(
    documentText: String?,
    onBack: () -> Unit,
    viewModel: AnalysisViewModel = viewModel(),
) {
    LaunchedEffect(documentText) {
        viewModel.analyze(documentText.orEmpty())
    }

    when (val state = viewModel.uiState) {
        AnalysisUiState.Idle -> AnalysisLoadingScreen()
        AnalysisUiState.Loading -> AnalysisLoadingScreen()
        is AnalysisUiState.Success -> AnalysisResultScreen(state.result, onBack)
        is AnalysisUiState.Error -> AnalysisErrorScreen(state.message, onBack) { viewModel.analyze(documentText.orEmpty()) }
    }
}

@Composable
private fun AnalysisLoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AnalysisHeader()
        Card(colors = CardDefaults.cardColors(containerColor = OneStepSurface), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(50), modifier = Modifier.size(54.dp)) {
                        Icon(Icons.Default.AutoAwesome, null, tint = OneStepBlue, modifier = Modifier.padding(14.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("AI가 내용을 꼼꼼히 분석하고 있어요", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                        Text("핵심 정보와 할 일을 정리하는 중이에요.", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                    }
                }
                LinearProgressIndicator(color = OneStepBlue, modifier = Modifier.fillMaxWidth())
                AnalysisStep("문서 글자 또렷하게 읽기", true)
                AnalysisStep("중요 일정과 준비물 이해 중", true)
                AnalysisStep("한걸음씩 할 일 카드 만들기", false)
            }
        }
    }
}

@Composable
private fun AnalysisHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("안내문 분석", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.AutoAwesome, "AI 분석", tint = OneStepBlue)
    }
}

@Composable
private fun AnalysisStep(label: String, active: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (active) {
            Icon(Icons.Default.CheckCircle, null, tint = OneStepSuccess, modifier = Modifier.size(24.dp))
        } else {
            CircularProgressIndicator(color = OneStepBlue, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = if (active) OneStepText else OneStepBlue)
    }
}

@Composable
private fun AnalysisResultScreen(result: AnalysisResult, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AnalysisHeader()
        Text(result.title, style = MaterialTheme.typography.headlineSmall, color = OneStepText)
        Text(result.documentType, style = MaterialTheme.typography.bodySmall, color = OneStepBlue)
        InfoCard("한눈에 보기", result.summary)
        InfoCard("일정", result.deadline ?: "확인 필요")
        InfoCard("장소", result.location ?: "확인 필요")
        InfoCard("준비물", result.items.joinToString(" · ").ifBlank { "확인 필요" })
        InfoCard("비용", result.cost ?: "별도 안내 없음")
        InfoCard("문의", result.phone ?: "별도 안내 없음")
        ActionCard(result.actions)
        result.caution?.let { InfoCard("주의사항", it) }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("홈으로 돌아가기") }
    }
}

@Composable
private fun InfoCard(title: String, value: String) {
    Surface(color = OneStepSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = OneStepBlue)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = OneStepText)
        }
    }
}

@Composable
private fun ActionCard(actions: List<ActionItem>) {
    Surface(color = OneStepSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("해야 할 일", style = MaterialTheme.typography.titleMedium, color = OneStepText)
            actions.forEachIndexed { index, action ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = if (index == 0) OneStepSuccessSoft else OneStepBlueSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(36.dp)) {
                        Icon(if (index == 0) Icons.Default.CheckCircle else Icons.Default.Description, null, tint = if (index == 0) OneStepSuccess else OneStepBlue, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(action.title, style = MaterialTheme.typography.bodyLarge, color = OneStepText)
                        Text("${action.description} · 약 ${action.estimatedMinutes}분", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisErrorScreen(message: String, onBack: () -> Unit, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.ErrorOutline, null, tint = OneStepBlue, modifier = Modifier.size(52.dp))
        Spacer(Modifier.size(16.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge, color = OneStepText)
        Spacer(Modifier.size(20.dp))
        Button(onClick = onRetry) { Text("다시 분석") }
        OutlinedButton(onClick = onBack) { Text("돌아가기") }
    }
}
