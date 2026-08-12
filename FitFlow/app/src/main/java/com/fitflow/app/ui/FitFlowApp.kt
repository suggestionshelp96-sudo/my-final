package com.fitflow.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitflow.app.FitViewModel
import com.fitflow.app.data.UserSettings
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.math.min

private val Orange = Color(0xFFF46B2C)
private val Ink = Color(0xFF1C2635)

@Composable fun FitFlowApp(vm: FitViewModel, requestTracking: () -> Unit) {
    val settings by vm.settings.collectAsState()
    val dark = settings.theme != "Orange"
    MaterialTheme(colorScheme = if (dark) androidx.compose.material3.darkColorScheme(primary = when(settings.theme) { "Blue" -> Color(0xFF32A7FF); "Red" -> Color(0xFFFF5252); else -> Orange }) else androidx.compose.material3.lightColorScheme(primary = Orange, background = Color(0xFFFFF9F5), surface = Color.White)) {
        val nav = rememberNavController(); var tab by remember { mutableIntStateOf(0) }
        val routes = listOf("home", "steps", "workout", "habits", "body")
        Scaffold(bottomBar = { NavigationBar { listOf(Icons.Default.Home to "Home", Icons.AutoMirrored.Filled.ShowChart to "Steps", Icons.Default.DirectionsRun to "Workout", Icons.Default.Favorite to "Habits", Icons.Default.AccessibilityNew to "Body").forEachIndexed { i, pair -> NavigationBarItem(selected = tab == i, onClick = { tab = i; nav.navigate(routes[i]) { launchSingleTop = true } }, icon = { Icon(pair.first, pair.second) }, label = { Text(pair.second) }) } } }) { padding ->
            NavHost(nav, "home", Modifier.padding(padding)) { composable("home") { Home(settings, requestTracking, { nav.navigate("settings") }) }; composable("steps") { TrackerPage("Step tracker", "Daily, weekly and monthly movement history", "Start secure sensor tracking", requestTracking) }; composable("workout") { TrackerPage("Workout", "Timer, activity log and calorie estimates", "Start a workout", {}) }; composable("habits") { HabitsPage(vm) }; composable("body") { BodyExplorer() }; composable("settings") { Settings(settings, vm::saveSettings) } }
        }
    }
}

@Composable private fun Home(settings: UserSettings, requestTracking: () -> Unit, openSettings: () -> Unit) { LazyColumn(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { item { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Hi, ${settings.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Welcome back to FitFlow") }; Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Orange.copy(.12f))) { Text("7 day\nstreak", Modifier.padding(12.dp), color = Orange, fontWeight = FontWeight.Bold) }; IconButton(openSettings) { Icon(Icons.Default.Settings, "Settings") } } }; item { StepRing(6480, settings.stepsGoal) }; item { Text("Today at a glance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }; item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { Metric("Calories", "326 kcal", Icons.Default.DirectionsRun, Modifier.weight(1f)); Metric("Distance", "4.8 km", Icons.AutoMirrored.Filled.ShowChart, Modifier.weight(1f)) } }; item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { Metric("Hydration", "1.4 / 2.5 L", Icons.Default.LocalDrink, Modifier.weight(1f)); Metric("Sleep", "7h 24m", Icons.Default.Favorite, Modifier.weight(1f)) } }; item { Goal("Water goal", 0.56f); Goal("Sleep goal", 0.92f); Goal("Workout goal", 0.4f) }; item { AdBanner() }; item { TextButton(requestTracking) { Text("Enable step tracking") } } } }
@Composable private fun StepRing(steps: Int, goal: Int) { val p = min(steps.toFloat() / goal, 1f); val progress by animateFloatAsState(p, label = "steps"); Box(Modifier.fillMaxWidth().height(290.dp), Alignment.Center) { Canvas(Modifier.size(240.dp)) { drawArc(Orange.copy(.12f), -90f, 360f, false, style = Stroke(22.dp.toPx(), cap = StrokeCap.Round)); drawArc(Orange, -90f, progress * 360, false, style = Stroke(22.dp.toPx(), cap = StrokeCap.Round)) }; Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.DirectionsRun, null, tint = Orange, modifier = Modifier.size(40.dp)); Text("$steps", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black); Text("of $goal steps") } } }
@Composable private fun AdBanner() { val context = LocalContext.current; AndroidView(factory = { AdView(context).apply { adUnitId = AdConfig.BANNER_AD_UNIT_ID; setAdSize(AdSize.BANNER); loadAd(AdRequest.Builder().build()) } }, modifier = Modifier.fillMaxWidth().height(52.dp)) }
@Composable private fun Metric(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) { Card(modifier, shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(4.dp)) { Column(Modifier.padding(16.dp)) { Icon(icon, null, tint = Orange); Spacer(Modifier.height(12.dp)); Text(value, fontWeight = FontWeight.Bold); Text(title, color = Color.Gray, style = MaterialTheme.typography.labelMedium) } } }
@Composable private fun Goal(title: String, progress: Float) { Column { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(title, fontWeight = FontWeight.SemiBold); Text("${(progress * 100).toInt()}%", color = Orange) }; Spacer(Modifier.height(6.dp)); Box(Modifier.fillMaxWidth().height(9.dp).clip(RoundedCornerShape(20.dp)).background(Orange.copy(.13f))) { Box(Modifier.fillMaxWidth(progress).height(9.dp).background(Orange)) } } }
@Composable private fun TrackerPage(title: String, description: String, action: String, onAction: () -> Unit) { Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text(description); Card { Column(Modifier.padding(22.dp)) { Text("Your activity stays on-device", fontWeight = FontWeight.Bold); TextButton(onAction) { Text(action) } } }; Text("Reports", style = MaterialTheme.typography.titleLarge); Text("Day   Week   Month\n\nBest day: 10,840 steps\nCurrent streak: 7 days\nGoal completion: 72%") } }
@Composable private fun HabitsPage(vm: FitViewModel) { val habits by vm.habits.collectAsState(); var add by remember { mutableStateOf(false) }; Column(Modifier.fillMaxSize().padding(24.dp)) { Text("Habits", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Build a steady routine, one day at a time."); Spacer(Modifier.height(18.dp)); habits.forEach { Card(Modifier.fillMaxWidth().padding(vertical = 5.dp)) { Text("${it.name}  -  ${it.streak} day streak", Modifier.padding(18.dp)) } }; TextButton({ vm.addHabit(if (add) "Morning stretch" else "Drink water"); add = !add }) { Text("+ Create habit") } } }
@Composable private fun BodyExplorer() { var selected by remember { mutableStateOf("Heart") }; val parts = listOf("Brain", "Eyes", "Ears", "Heart", "Lungs", "Liver", "Kidneys", "Stomach", "Muscles", "Bones", "Legs", "Feet"); Column(Modifier.fillMaxSize().padding(24.dp)) { Text("Body explorer", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Offline health learning guide"); Spacer(Modifier.height(18.dp)); Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(20.dp)) { Icon(Icons.Default.AccessibilityNew, null, tint = Orange, modifier = Modifier.size(140.dp).align(Alignment.CenterHorizontally)); Text(selected, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("Main function: supports your daily movement and wellbeing. Health tip: stay active, eat a balanced diet, and speak with a qualified clinician for symptoms.") } }; Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { parts.take(4).forEach { Text(it, Modifier.clickable { selected = it }.padding(6.dp), color = if (it == selected) Orange else MaterialTheme.colorScheme.onBackground) } }; Text("Tap a body area to explore its function, prevention tips, common concerns, and facts.", Modifier.padding(top = 12.dp)) } }
@Composable private fun Settings(settings: UserSettings, save: (UserSettings) -> Unit) { val context = LocalContext.current; Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); SettingOption("Theme: ${settings.theme}") { val next = when(settings.theme) { "Orange" -> "Dark"; "Dark" -> "Blue"; "Blue" -> "Red"; else -> "Orange" }; save(settings.copy(theme = next)) }; SettingOption("Language: ${settings.language}") { val next = when(settings.language) { "English" -> "Hindi"; "Hindi" -> "Gujarati"; else -> "English" }; save(settings.copy(language = next)) }; SettingOption("Goals: ${settings.stepsGoal} steps, ${settings.waterGoal} ml water") {}; Text("Follow FitFlow", fontWeight = FontWeight.Bold); Row { listOf("Facebook" to "https://facebook.com", "Instagram" to "https://instagram.com", "X" to "https://x.com").forEach { (name, url) -> TextButton({ context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }) { Text(name) } } }; SettingOption("Privacy policy") { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/fitflow/privacy"))) }; SettingOption("About FitFlow") {}; Text("FitFlow 1.0.0\nYour wellness data is stored locally on your device.", color = Color.Gray) } }
@Composable private fun SettingOption(label: String, action: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = action), shape = RoundedCornerShape(16.dp)) { Row(Modifier.padding(17.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, Modifier.weight(1f)); Icon(Icons.Default.MoreHoriz, null) } } }
