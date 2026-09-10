package com.example.one_step.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.R
import com.example.one_step.navigation.AppDestination
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueBright
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSuccess
import com.example.one_step.ui.theme.OneStepSuccessSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val state = viewModel.uiState
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OneStepBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        HomeTopBar()
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            HeroCard()
            IngestionSection(onCameraClick = { onNavigate(AppDestination.Camera.route) })
            ActiveDocumentSection(
                state = state,
                onResume = { onNavigate(AppDestination.Guide.route) },
            )
            HowItWorksCard()
            TrustMessage()
        }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(76.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(color = OneStepBlue, shape = RoundedCornerShape(10.dp), modifier = Modifier.size(38.dp)) {
            Image(
                painter = painterResource(R.drawable.mascot_home),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.padding(4.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Text("한걸음", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        Spacer(Modifier.weight(1f))
        Surface(color = OneStepBlue, shape = CircleShape, modifier = Modifier.size(40.dp)) {
            Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "프로필",
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}

@Composable
private fun HeroCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = OneStepSurface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.clip(CircleShape).background(OneStepBlueSoft).padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(OneStepBlue))
                    Spacer(Modifier.width(4.dp))
                    Text("AI 쉬운 안내 도우미", style = MaterialTheme.typography.bodySmall, color = OneStepBlue)
                }
                Spacer(Modifier.height(14.dp))
                Text("어려운 안내문도\n이제 쉽게,", style = MaterialTheme.typography.headlineSmall, color = OneStepText)
                Row {
                    Text("한걸음씩", style = MaterialTheme.typography.headlineSmall, color = OneStepBlue, fontWeight = FontWeight.Bold)
                    Text(" 해봐요.", style = MaterialTheme.typography.headlineSmall, color = OneStepText)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "복잡한 서류, 공문서를 알기 쉬운 단계로\n풀어드려요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OneStepTextMuted,
                )
            }
            Spacer(Modifier.width(12.dp))
            Surface(color = OneStepBlueBright, shape = RoundedCornerShape(20.dp), modifier = Modifier.size(92.dp)) {
                Image(
                    painter = painterResource(R.drawable.mascot_home),
                    contentDescription = "한걸음 도우미",
                    modifier = Modifier.padding(10.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun IngestionSection(onCameraClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("새로운 안내문 넣기", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted, modifier = Modifier.padding(horizontal = 4.dp))
        Surface(
            color = OneStepBlue,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth().height(128.dp).clickable(onClick = onCameraClick),
        ) {
            Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color.White.copy(alpha = .2f), shape = RoundedCornerShape(16.dp), modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(16.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("안내문 촬영하기", style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text("카메라로 비추면 바로 쉬워져요", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = .8f))
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            QuickActionCard("파일 보관함", "저장된 사진이나 pdf 불러오기", Icons.Default.FolderOpen, OneStepBlueSoft, Modifier.weight(1f))
            QuickActionCard("직접 입력하기", "글자 적거나 붙여넣기", Icons.Default.EditNote, OneStepSuccessSoft, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionCard(title: String, description: String, icon: ImageVector, accent: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(156.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OneStepSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Surface(color = accent, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(48.dp)) {
                Icon(icon, null, tint = OneStepBlue, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = OneStepText)
            Text(description, style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted, maxLines = 2)
        }
    }
}

@Composable
private fun ActiveDocumentSection(state: HomeUiState, onResume: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("◉ 진행 중인 안내문", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
            Text("1개 남음", style = MaterialTheme.typography.bodySmall, color = OneStepBlue)
        }
        Card(colors = CardDefaults.cardColors(containerColor = OneStepSurface), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(14.dp), modifier = Modifier.size(52.dp)) {
                        Icon(Icons.Default.School, null, tint = OneStepBlue, modifier = Modifier.padding(13.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(state.activeDocumentTitle, style = MaterialTheme.typography.titleMedium, color = OneStepText, maxLines = 1)
                        Text(state.activeDocumentSource, style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                    }
                    Text("${state.completedSteps}/${state.totalSteps} 완료", style = MaterialTheme.typography.bodySmall, color = OneStepBlue, modifier = Modifier.clip(CircleShape).background(OneStepBlueSoft).padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("마지막 단계: 참가 동의 서명하기", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                    Text("66%", style = MaterialTheme.typography.bodySmall, color = OneStepBlue)
                }
                LinearProgressIndicator(progress = { 0.66f }, color = OneStepBlue, trackColor = OneStepBlueSoft, modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StepChip("✓ 1. 일정 확인", OneStepSuccessSoft, OneStepSuccess)
                    StepChip("✓ 2. 준비물 체크", OneStepSuccessSoft, OneStepSuccess)
                    StepChip("● 3. 동의 서명", OneStepBlueSoft, OneStepBlue)
                }
                Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onResume)) {
                    Row(Modifier.padding(vertical = 15.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("이어서 작성하기", style = MaterialTheme.typography.labelLarge, color = OneStepBlue)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = OneStepBlue, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StepChip(label: String, color: Color, textColor: Color) {
    Text(label, style = MaterialTheme.typography.bodySmall, color = textColor, maxLines = 1, modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(color).padding(horizontal = 8.dp, vertical = 7.dp))
}

@Composable
private fun HowItWorksCard() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFF2F3FF)).padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(color = Color.White, shape = RoundedCornerShape(16.dp), modifier = Modifier.size(60.dp), shadowElevation = 2.dp) {
            Icon(Icons.Default.Lightbulb, null, tint = Color(0xFF9A6800), modifier = Modifier.padding(14.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text("사진만 찍으면 3단계로 끝!", style = MaterialTheme.typography.titleMedium, color = OneStepText)
            Text("어려운 단어는 쉬운 말로, 해야 할 일은\n체크리스트로 딱딱 짚어드려요.", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
        }
    }
}

@Composable
private fun TrustMessage() {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(color = OneStepBlueSoft, shape = CircleShape, modifier = Modifier.size(32.dp)) {
            Text("♡", modifier = Modifier.padding(top = 4.dp), color = OneStepBlue, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(8.dp))
        Text("모든 사람이 정보를 쉽게 이해하고\n자신 있게 행동할 수 있도록", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
    }
}
