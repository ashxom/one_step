package com.example.one_step.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun AnalysisScreen(documentText: String?, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로 가기", tint = OneStepText) }
            Text("안내문 분석", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        }
        Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = OneStepBlue)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("안내문을 한걸음씩 정리하고 있어요", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                    Text("입력된 내용을 바탕으로 핵심 정보를 준비합니다.", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                }
            }
        }
        Text("전달된 안내문", style = MaterialTheme.typography.titleMedium, color = OneStepText)
        Surface(color = OneStepSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (documentText.isNullOrBlank()) {
                Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Description, null, tint = OneStepBlue)
                    Text("분석할 안내문이 없습니다.", color = OneStepTextMuted, modifier = Modifier.padding(top = 12.dp))
                }
            } else {
                Text(documentText, color = OneStepText, modifier = Modifier.padding(18.dp).verticalScroll(rememberScrollState()))
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("홈으로 돌아가기") }
    }
}
