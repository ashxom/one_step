package com.example.one_step.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.R
import com.example.one_step.domain.model.ActionItem
import com.example.one_step.domain.model.AnalysisResult
import com.example.one_step.domain.repository.GuideLocalRepository
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSuccess
import com.example.one_step.ui.theme.OneStepSuccessSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun GuideScreen(
    onBack: () -> Unit,
    repository: GuideLocalRepository? = null,
    viewModel: GuideViewModel = viewModel(),
) {
    repository?.let(viewModel::attachLocalRepository)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val speechController = remember(context) {
        GuideSpeechController(
            context = context,
            onPlaybackStateChanged = viewModel::setSpeaking,
            onError = viewModel::setSpeechError,
        )
    }
    DisposableEffect(speechController) {
        onDispose { speechController.shutdown() }
    }
    val stopSpeech = {
        speechController.stop()
        viewModel.setSpeaking(false)
    }
    LaunchedEffect(repository) {
        if (repository != null && state is GuideUiState.Empty) viewModel.resumeLatest()
    }
    when (val current = state) {
        GuideUiState.Empty -> GuideEmptyScreen(onBack)
        GuideUiState.Loading -> GuideLoadingScreen(onBack)
        is GuideUiState.Error -> GuideErrorScreen(current.message, onBack)
        is GuideUiState.Running -> GuideRunningScreen(
            state = current,
            onBack = { stopSpeech(); onBack() },
            onPrevious = { stopSpeech(); viewModel.previous() },
            onNext = { stopSpeech(); viewModel.next() },
            onComplete = { stopSpeech(); viewModel.completeCurrent() },
            onRetryComplete = { stopSpeech(); viewModel.retryCompleteCurrent() },
            onPause = {
                stopSpeech()
                viewModel.togglePause()
            },
            onSpeak = {
                if (current.isSpeaking) {
                    stopSpeech()
                } else {
                    val action = current.result.actions[current.currentIndex]
                    viewModel.clearSpeechError()
                    speechController.speak("${action.title}. ${action.description}")
                    viewModel.setSpeaking(true)
                }
            },
        )
        is GuideUiState.Completed -> GuideCompletedScreen(current, onBack)
    }
}

@Composable
private fun GuideHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기", tint = OneStepText)
        }
        Text("한걸음 안내", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        Spacer(Modifier.weight(1f))
        Surface(color = OneStepBlue, shape = CircleShape, modifier = Modifier.size(40.dp)) {
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "프로필",
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}

@Composable
private fun GuideRunningScreen(
    state: GuideUiState.Running,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit,
    onRetryComplete: () -> Unit,
    onPause: () -> Unit,
    onSpeak: () -> Unit,
) {
    val action = state.result.actions[state.currentIndex]
    val progress = (state.currentIndex + 1).toFloat() / state.result.actions.size
    Column(Modifier.fillMaxSize().background(OneStepBackground)) {
        GuideHeader(onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("${state.currentIndex + 1} / ${state.result.actions.size} 단계", style = MaterialTheme.typography.titleMedium, color = OneStepBlue)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp), color = OneStepBlue, trackColor = OneStepBlueMutedColor)
            if (state.isPaused) {
                Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pause, contentDescription = null, tint = OneStepBlue)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("잠시 멈췄어요", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                            Text("준비되면 계속 진행해 주세요.", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
                        }
                        TextButton(onClick = onPause) { Text("계속하기", color = OneStepBlue) }
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = OneStepSurface)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(color = OneStepBlueSoft, shape = CircleShape, modifier = Modifier.size(52.dp)) {
                        Icon(Icons.Default.SlowMotionVideo, contentDescription = null, tint = OneStepBlue, modifier = Modifier.padding(14.dp))
                    }
                    Text("지금 할 일", style = MaterialTheme.typography.bodyLarge, color = OneStepBlue, fontWeight = FontWeight.SemiBold)
                    Text(action.title, style = MaterialTheme.typography.headlineSmall, color = OneStepText, fontWeight = FontWeight.Bold)
                    Text(action.description, style = MaterialTheme.typography.bodyLarge, color = OneStepTextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = OneStepTextMuted, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("예상 ${action.estimatedMinutes}분", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
                    }
                }
            }
            Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), onClick = onSpeak) {
                Row(Modifier.padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = OneStepBlue)
                    Spacer(Modifier.width(10.dp))
                    Text(if (state.isSpeaking) "듣는 중…" else "음성으로 듣기", style = MaterialTheme.typography.bodyLarge, color = OneStepBlue, fontWeight = FontWeight.SemiBold)
                }
            }
            state.speechError?.let { message ->
                Surface(color = Color(0xFFFFEDEC), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = message,
                        color = Color(0xFFB3261E),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onPrevious, enabled = state.currentIndex > 0, modifier = Modifier.weight(1f)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("이전")
                }
                OutlinedButton(onClick = onNext, enabled = state.currentIndex < state.result.actions.lastIndex, modifier = Modifier.weight(1f)) {
                    Text("다음")
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
            Button(onClick = onComplete, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = OneStepBlue)) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (state.isSaving) "저장 중…" else "작성했어요", style = MaterialTheme.typography.titleMedium)
            }
            state.saveError?.let { message ->
                Surface(color = OneStepSuccessSoft.copy(alpha = 0.35f), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(message, style = MaterialTheme.typography.bodyMedium, color = OneStepText, modifier = Modifier.weight(1f))
                        TextButton(onClick = onRetryComplete) { Text("다시 시도", color = OneStepBlue) }
                    }
                }
            }
            OutlinedButton(onClick = onPause, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Icon(if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (state.isPaused) "계속하기" else "잠시 멈추기")
            }
            Text("완료한 행동 ${state.completedIds.size}개", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GuideCompletedScreen(state: GuideUiState.Completed, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(OneStepBackground)) {
        GuideHeader(onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Surface(color = OneStepSuccessSoft, shape = CircleShape, modifier = Modifier.size(72.dp).align(Alignment.CenterHorizontally)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OneStepSuccess, modifier = Modifier.padding(18.dp))
            }
            Text("모두 완료했어요!", style = MaterialTheme.typography.headlineSmall, color = OneStepText, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text("한걸음씩 잘 해내셨어요.", style = MaterialTheme.typography.bodyLarge, color = OneStepTextMuted, modifier = Modifier.align(Alignment.CenterHorizontally))
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = OneStepSurface), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("완료한 행동", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                    state.completedActions.forEach { action ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OneStepSuccess, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(action.title, style = MaterialTheme.typography.bodyLarge, color = OneStepText, fontWeight = FontWeight.SemiBold)
                                Text(action.description, style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                            }
                        }
                    }
                }
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = OneStepBlue)) {
                Text("한걸음 안내로 돌아가기", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun GuideEmptyScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(OneStepBackground)) {
        GuideHeader(onBack)
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("분석 결과에서 한걸음씩 시작하기를 눌러주세요.", style = MaterialTheme.typography.titleMedium, color = OneStepText, modifier = Modifier.padding(bottom = 20.dp))
            Button(onClick = onBack, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = OneStepBlue), shape = RoundedCornerShape(16.dp)) { Text("돌아가기") }
        }
    }
}

@Composable
private fun GuideLoadingScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(OneStepBackground)) {
        GuideHeader(onBack)
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.CircularProgressIndicator(color = OneStepBlue)
            Spacer(Modifier.height(16.dp))
            Text("저장된 분석 결과를 불러오는 중이에요.", style = MaterialTheme.typography.bodyLarge, color = OneStepTextMuted)
        }
    }
}

@Composable
private fun GuideErrorScreen(message: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(OneStepBackground)) {
        GuideHeader(onBack)
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, style = MaterialTheme.typography.titleMedium, color = OneStepText)
            Spacer(Modifier.height(20.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = OneStepBlue), shape = RoundedCornerShape(16.dp)) {
                Text("돌아가기")
            }
        }
    }
}

private val OneStepBlueMutedColor = Color(0xFFDBE1FF)

private fun previewResult() = AnalysisResult(
    title = "현장체험학습 참가 신청서",
    documentType = "가정통신문",
    summary = "중요한 일정과 준비물을 알기 쉽게 모았어요.",
    actions = listOf(
        ActionItem("1", "보호자 동의서 작성", "서명까지 완료해 주세요.", 5),
        ActionItem("2", "준비물 챙기기", "필요한 준비물을 확인해 주세요.", 10),
        ActionItem("3", "참가비 보내기", "스쿨뱅킹으로 참가비를 보내 주세요.", 3),
    ),
    deadline = "9월 18일(수) 까지",
    location = "본관 1층 행정실",
    items = listOf("보호자 동의서"),
    cost = "20,000원",
    phone = "042-123-4567",
    caution = null,
)

@Preview(showBackground = true)
@Composable
private fun GuideRunningPreview() {
    GuideRunningScreen(GuideUiState.Running(previewResult(), 0), {}, {}, {}, {}, {}, {}, {})
}
