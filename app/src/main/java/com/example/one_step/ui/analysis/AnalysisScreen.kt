package com.example.one_step.ui.analysis

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.R
import com.example.one_step.domain.model.AnalysisResult
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
fun AnalysisScreen(
    documentText: String?,
    onBack: () -> Unit,
    onStartGuide: () -> Unit,
    viewModel: AnalysisViewModel = viewModel(),
) {
    LaunchedEffect(documentText) {
        viewModel.analyze(documentText.orEmpty())
    }

    when (val state = viewModel.uiState) {
        AnalysisUiState.Idle -> AnalysisLoadingScreen(onBack)
        AnalysisUiState.Loading -> AnalysisLoadingScreen(onBack)
        is AnalysisUiState.Success -> AnalysisSummaryScreen(state.result, documentText, onBack, onStartGuide)
        is AnalysisUiState.Error -> AnalysisErrorScreen(state.message, onBack) { viewModel.analyze(documentText.orEmpty()) }
    }
}

@Composable
private fun AnalysisLoadingScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AnalysisHeader("한걸음 안내", onBack)
        Card(colors = CardDefaults.cardColors(containerColor = OneStepSurface), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = OneStepBlueSoft, shape = CircleShape, modifier = Modifier.size(54.dp)) {
                        Icon(Icons.Default.AutoAwesome, null, tint = OneStepBlue, modifier = Modifier.padding(14.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("AI가 내용을 꼼꼼히 분석하고 있어요", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                        Text("핵심 정보와 할 일을 정리하는 중이에요.", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                    }
                }
                LinearProgressIndicator(color = OneStepBlue, modifier = Modifier.fillMaxWidth())
                SummaryStep("문서 글자 또렷하게 읽기", true)
                SummaryStep("중요 일정과 준비물 이해 중", true)
                SummaryStep("한걸음씩 할 일 카드 만들기", false)
            }
        }
    }
}

@Composable
private fun AnalysisHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로 가기", tint = OneStepText) }
        Text(title, style = MaterialTheme.typography.titleLarge, color = OneStepText)
        Spacer(Modifier.weight(1f))
        Surface(color = OneStepBlue, shape = CircleShape, modifier = Modifier.size(40.dp)) {
            Image(painter = painterResource(R.drawable.ic_profile), contentDescription = "프로필", modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
private fun SummaryStep(label: String, completed: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (completed) {
            Icon(Icons.Default.CheckCircle, null, tint = OneStepSuccess, modifier = Modifier.size(24.dp))
        } else {
            CircularProgressIndicator(color = OneStepBlue, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = if (completed) OneStepText else OneStepBlue)
    }
}

@Composable
private fun AnalysisSummaryScreen(
    result: AnalysisResult,
    documentText: String?,
    onBack: () -> Unit,
    onStartGuide: () -> Unit,
) {
    var showOriginal by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        containerColor = OneStepBackground,
        topBar = { AnalysisHeaderBar(onBack) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(innerPadding).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SummaryStatusRow(onOriginalClick = { showOriginal = true })
            DocumentHero(result)
            ExperienceImageCard(result.tripTitle ?: result.title, result.targetGrade)
            AudioSummaryButton()
            DeadlineCard(result.deadline, result.deadlineBadge, result.deadlineDescription)
            LocationCard(result.location, result.locationDescription)
            ItemsCard(result.items, result.cost)
            PhoneCard(result.phone, result.phoneLabel)
            EncouragementCard(result.encouragement)
            StartGuideButton(onStartGuide)
            AuxiliaryActions()
        }
    }
    if (showOriginal) {
        AlertDialog(
            onDismissRequest = { showOriginal = false },
            title = { Text("원문 보기") },
            text = {
                Text(
                    documentText?.takeIf { it.isNotBlank() } ?: "원문이 없습니다.",
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    color = OneStepText,
                )
            },
            confirmButton = { TextButton(onClick = { showOriginal = false }) { Text("닫기") } },
        )
    }
}

@Composable
private fun SummaryStatusRow(onOriginalClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Surface(color = OneStepSuccessSoft, shape = CircleShape) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = OneStepSuccess, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("AI 분석 완료", style = MaterialTheme.typography.bodySmall, color = OneStepSuccess, fontWeight = FontWeight.SemiBold)
            }
        }
        Surface(color = OneStepBlueSoft, shape = CircleShape, modifier = Modifier.clickable(onClick = onOriginalClick)) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, null, tint = OneStepBlue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("원문 보기", color = OneStepBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DocumentHero(result: AnalysisResult) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(8.dp)) {
                Text(result.documentType, fontSize = 13.sp, color = OneStepBlue, modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp))
            }
            Spacer(Modifier.width(10.dp))
            Text(result.title, fontSize = 13.sp, color = OneStepTextMuted, maxLines = 1)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                val title = androidx.compose.ui.text.buildAnnotatedString {
                    append("이 안내문에서 해야 할 일은\n")
                    withStyle(androidx.compose.ui.text.SpanStyle(color = OneStepBlue, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("총 ${result.actions.size}가지")
                    }
                    append("예요!")
                }
                Text(title, style = TextStyle(fontSize = 26.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold), color = OneStepText)
                Spacer(Modifier.height(8.dp))
                Text(result.summary, fontSize = 16.sp, color = OneStepTextMuted)
            }
            Surface(color = OneStepBlueBright, shape = RoundedCornerShape(16.dp), modifier = Modifier.size(56.dp)) {
                Image(painter = painterResource(R.drawable.mascot_face), contentDescription = "한걸음 마스코트", modifier = Modifier.padding(12.dp))
            }
        }
    }
}

@Composable
private fun ExperienceImageCard(title: String, targetGrade: String?) {
    Box(Modifier.fillMaxWidth().height(144.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFDCECF1))) {
        Image(
            painter = painterResource(R.drawable.trip_school),
            contentDescription = "체험학습 이미지",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC2E3445)))))
        Row(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 1)
            Surface(color = Color.White.copy(alpha = .25f), shape = CircleShape) {
                Text(targetGrade ?: "전체", color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
            }
        }
    }
}

@Composable
private fun AudioSummaryButton() {
    Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = OneStepBlue)
            Spacer(Modifier.width(10.dp))
            Text("소리로 요약 들어보기", fontSize = 14.sp, color = OneStepBlue, modifier = Modifier.weight(1f))
            Icon(Icons.Default.PlayArrow, null, tint = OneStepTextMuted, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun DeadlineCard(deadline: String?, badge: String?, description: String?) {
    SummaryCard(title = "제출 기한", icon = Icons.Default.CalendarMonth, iconBackground = OneStepBlueSoft, modifier = Modifier.heightIn(min = 129.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text(formatDeadline(deadline), fontSize = 21.sp, lineHeight = 26.sp, color = Color(0xFFBA1A1A), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(description ?: "마감일을 확인해 주세요.", fontSize = 12.sp, color = OneStepTextMuted)
            }
            Surface(color = Color(0xFFFFDAD6), shape = CircleShape) {
                Text(badge ?: "날짜 확인", fontSize = 13.sp, maxLines = 1, softWrap = false, color = Color(0xFF93000A), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun LocationCard(location: String?, description: String?) {
    val context = LocalContext.current
    SummaryCard(title = "제출할 곳", icon = Icons.Default.LocationOn, containerColor = OneStepSurface, iconBackground = OneStepSuccessSoft, iconTint = OneStepSuccess, modifier = Modifier.heightIn(min = 120.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Text(location ?: "확인 필요", fontSize = 19.sp, color = OneStepText)
            Text(
                "지도 확인 ›",
                fontSize = 13.sp,
                color = OneStepBlue,
                modifier = Modifier.clickable {
                    location?.takeIf { it.isNotBlank() }?.let { query ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(query)}"))
                        if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                    }
                },
            )
        }
        Text(description ?: "제출 장소를 확인해 주세요.", fontSize = 13.sp, color = OneStepTextMuted)
    }
}

@Composable
private fun ItemsCard(items: List<String>, cost: String?) {
    SummaryCard(
        title = "꼭 챙길 것",
        icon = Icons.AutoMirrored.Filled.Rule,
        iconBackground = Color(0xFFFFE3B3),
        iconTint = Color(0xFF996100),
        modifier = Modifier.heightIn(min = 136.dp),
        headerTrailing = {
            Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(6.dp)) {
                Text("${items.size + if (cost.isNullOrBlank()) 0 else 1}가지", fontSize = 13.sp, color = OneStepBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        },
    ) {
        items.forEach { item -> BulletItem(item) }
        cost?.takeIf { it.isNotBlank() }?.let { BulletItem(it, emphasizeAmount = true) }
    }
}

@Composable
private fun BulletItem(value: String, emphasizeAmount: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(top = 3.dp), verticalAlignment = Alignment.Top) {
        Text("•", style = MaterialTheme.typography.titleMedium, color = OneStepBlue, modifier = Modifier.width(20.dp))
        val text = if (emphasizeAmount) {
            androidx.compose.ui.text.buildAnnotatedString {
                val amount = Regex("[0-9,]+원").find(value)
                if (amount == null) append(value) else {
                    append(value.substring(0, amount.range.first))
                    withStyle(androidx.compose.ui.text.SpanStyle(color = OneStepBlue, fontWeight = FontWeight.Bold)) { append(amount.value) }
                    append(value.substring(amount.range.last + 1))
                }
            }
        } else androidx.compose.ui.text.AnnotatedString(value)
        Text(text, fontSize = 18.sp, color = OneStepText)
    }
}

@Composable
private fun PhoneCard(phone: String?, phoneLabel: String?) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = OneStepSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 78.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Phone, null, tint = OneStepBlue, modifier = Modifier.padding(7.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("문의 전화", fontSize = 13.sp, color = OneStepTextMuted)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(phone ?: "확인 필요", fontSize = 16.sp, color = OneStepText, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Text("(${phoneLabel ?: "연락처"})", fontSize = 10.sp, color = OneStepTextMuted)
                }
            }
            Surface(color = OneStepBlueSoft, shape = CircleShape, modifier = Modifier.clickable {
                phone?.takeIf { it.isNotBlank() }?.let {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.filter(Char::isDigit)}"))
                    if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                }
            }) {
                Row(Modifier.height(36.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, null, tint = OneStepBlue, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("전화 연결", color = OneStepBlue, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun EncouragementCard(encouragement: String?) {
    Surface(color = Color(0xFFF2F3FF), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Lightbulb, null, tint = OneStepBlue, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("걱정하지 마세요!", fontSize = 14.sp, color = OneStepText, fontWeight = FontWeight.SemiBold)
                Text(encouragement ?: "한 번에 다 하지 않아도 괜찮아요. 아래 버튼을 눌러 차근차근 도와드릴게요.", fontSize = 13.sp, lineHeight = 18.sp, color = OneStepTextMuted)
            }
        }
    }
}

@Composable
private fun AuxiliaryActions() {
    Row(Modifier.fillMaxWidth().padding(top = 0.dp, bottom = 12.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = OneStepTextMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text("어려운 단어 풀이", color = OneStepTextMuted, style = MaterialTheme.typography.bodyMedium)
        Text("•", color = OneStepBlueSoft, modifier = Modifier.padding(horizontal = 14.dp))
        Icon(Icons.Default.EditNote, contentDescription = null, tint = OneStepTextMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text("정보 수정 요청", color = OneStepTextMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StartGuideButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text("한 걸음씩 시작하기", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(10.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(22.dp))
    }
}

private fun formatDeadline(value: String?): String {
    if (value.isNullOrBlank()) return "확인 필요"
    val match = Regex("(\\d{4})[-.년 ]\\s*(\\d{1,2})[-.월 ]\\s*(\\d{1,2})").find(value)
    return if (match != null) "${match.groupValues[2]}월 ${match.groupValues[3]}일 까지" else value
}


@Composable
private fun AnalysisHeaderBar(onBack: () -> Unit) {
    Surface(color = OneStepBackground, shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로 가기", tint = OneStepText) }
            Text("한걸음 안내", fontSize = 18.sp, color = OneStepText, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Surface(color = OneStepBlue, shape = CircleShape, modifier = Modifier.size(32.dp)) {
                Image(painter = painterResource(R.drawable.ic_profile), contentDescription = "프로필", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color = OneStepSurface,
    iconBackground: Color = OneStepBlueSoft,
    iconTint: Color = OneStepBlue,
    modifier: Modifier = Modifier,
    headerTrailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = containerColor), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = iconBackground, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(32.dp)) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.padding(7.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 14.sp, color = OneStepText)
                Spacer(Modifier.weight(1f))
                headerTrailing?.invoke()
            }
            content()
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

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun AnalysisLoadingPreview() {
    AnalysisLoadingScreen(onBack = {})
}
