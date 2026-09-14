package com.example.one_step.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.domain.model.GuideRecord
import com.example.one_step.domain.model.completedActionCount
import com.example.one_step.domain.model.isCompleted
import com.example.one_step.domain.repository.GuideLocalRepository
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSuccess
import com.example.one_step.ui.theme.OneStepSuccessSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    repository: GuideLocalRepository? = null,
    onRecordClick: (GuideRecord) -> Unit = {},
    viewModel: HistoryViewModel = viewModel(),
) {
    repository?.let(viewModel::attachRepository)
    val records by viewModel.records.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().background(OneStepBackground).padding(horizontal = 20.dp, vertical = 20.dp)) {
        Text("기록", style = MaterialTheme.typography.headlineSmall, color = OneStepText, fontWeight = FontWeight.Bold)
        Text("분석한 안내문과 진행 상황을 확인해 보세요.", style = MaterialTheme.typography.bodyLarge, color = OneStepTextMuted, modifier = Modifier.padding(top = 6.dp, bottom = 18.dp))
        if (records.isEmpty()) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Description, contentDescription = null, tint = OneStepBlue, modifier = Modifier.size(48.dp))
                Text("아직 분석한 안내문이 없어요.", style = MaterialTheme.typography.titleMedium, color = OneStepText, modifier = Modifier.padding(top = 14.dp))
                Text("홈에서 안내문을 분석해 보세요.", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted, modifier = Modifier.padding(top = 6.dp))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(records, key = { it.id }) { record -> HistoryCard(record, onRecordClick) }
            }
        }
    }
}

@Composable
private fun HistoryCard(record: GuideRecord, onClick: (GuideRecord) -> Unit) {
    val total = record.result.actions.size
    val progress = if (total == 0) 0f else record.completedActionCount.toFloat() / total
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(record) },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = OneStepSurface),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Surface(color = OneStepBlueSoft, shape = CircleShape, modifier = Modifier.size(42.dp)) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = OneStepBlue, modifier = Modifier.padding(10.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(record.result.title, style = MaterialTheme.typography.titleMedium, color = OneStepText, fontWeight = FontWeight.SemiBold)
                    Text(formatDate(record.analysisDate), style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
                }
                androidx.compose.material3.Surface(
                    color = if (record.isCompleted) OneStepSuccessSoft else OneStepBlueSoft,
                    shape = CircleShape,
                ) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (record.isCompleted) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OneStepSuccess, modifier = Modifier.size(16.dp))
                        Text(if (record.isCompleted) "완료" else "진행 중", style = MaterialTheme.typography.bodySmall, color = if (record.isCompleted) OneStepSuccess else OneStepBlue, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f), color = OneStepBlue, trackColor = OneStepBlueSoft)
                Spacer(Modifier.width(10.dp))
                Text("${record.completedActionCount}/$total", style = MaterialTheme.typography.bodySmall, color = OneStepBlue)
            }
        }
    }
}

private fun formatDate(timeMillis: Long): String = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN).format(Date(timeMillis))
