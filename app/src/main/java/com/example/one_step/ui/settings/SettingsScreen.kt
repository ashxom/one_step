package com.example.one_step.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.domain.repository.SettingsRepository
import com.example.one_step.domain.repository.GuideLocalRepository
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun SettingsScreen(
    repository: SettingsRepository? = null,
    localRepository: GuideLocalRepository? = null,
    viewModel: SettingsViewModel = viewModel(),
) {
    repository?.let(viewModel::attachRepository)
    val settings by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("설정", style = MaterialTheme.typography.headlineSmall, color = OneStepText, fontWeight = FontWeight.Bold)
        Text("나에게 맞는 한걸음 안내를 설정해 보세요.", style = MaterialTheme.typography.bodyLarge, color = OneStepTextMuted)
        errorMessage?.let { message ->
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = OneStepSurface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("글자 크기", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                Text("화면의 글자 크기를 조절합니다.", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
                Slider(
                    value = settings.fontScale,
                    onValueChange = viewModel::setFontScale,
                    valueRange = 0.85f..1.3f,
                    steps = 2,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("작게", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                    Text("보통", style = MaterialTheme.typography.bodyMedium, color = OneStepBlue)
                    Text("크게", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                }
            }
        }
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = OneStepSurface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingSwitchRow("음성 안내", "행동 설명을 음성으로 읽어 줍니다.", settings.voiceEnabled, viewModel::setVoiceEnabled)
                SettingSwitchRow("접근성 설정", "읽기 쉬운 화면 구성을 사용합니다.", settings.accessibilityEnabled, viewModel::setAccessibilityEnabled)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { viewModel.reset(localRepository) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OneStepBlueSoft, contentColor = OneStepBlue),
        ) {
            Text("설정 초기화")
        }
    }
}

@Composable
private fun SettingSwitchRow(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = OneStepText)
            Text(description, style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
