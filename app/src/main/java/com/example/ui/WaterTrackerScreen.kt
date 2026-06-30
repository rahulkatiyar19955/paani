package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WaterSettings

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerScreen(viewModel: WaterTrackerViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentIntake by viewModel.currentIntakeMl.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.logo),
                            contentDescription = "Paani Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Paani",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        // Points Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("${settings.totalPoints}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PTS", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f), fontSize = 10.sp, letterSpacing = 1.sp)
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Streak Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("🔥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${settings.currentStreak}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(0.dp)) // border-b
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(0.dp))
            ) {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { 
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 10.sp, fontWeight = if (currentRoute == "home") FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = { 
                        navController.navigate("history") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.History,
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History", fontSize = 10.sp, fontWeight = if (currentRoute == "history") FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "settings",
                    onClick = { 
                        navController.navigate("settings") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings", fontSize = 10.sp, fontWeight = if (currentRoute == "settings") FontWeight.Bold else FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Progress Area
                    CircularProgress(
                        current = currentIntake,
                        goal = settings.dailyGoalMl
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Quick Log Section
                    Text(
                        "QUICK LOG",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // slate-400
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp, bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickLogCard(
                            modifier = Modifier.weight(1f),
                            title = "Glass", 
                            amount = 250, 
                            emoji = "💧",
                            isPrimary = false,
                            onClick = { viewModel.addWater(250) }
                        )
                        QuickLogCard(
                            modifier = Modifier.weight(1f),
                            title = "Bottle", 
                            amount = 500, 
                            emoji = "🧴",
                            isPrimary = true,
                            onClick = { viewModel.addWater(500) }
                        )
                        QuickLogCard(
                            modifier = Modifier.weight(1f),
                            title = "Flask", 
                            amount = 750, 
                            emoji = "❄️",
                            isPrimary = false,
                            onClick = { viewModel.addWater(750) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        "LOG CUSTOM AMOUNT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp, bottom = 12.dp)
                    )
                    
                    var customAmountStr by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = customAmountStr,
                            onValueChange = { customAmountStr = it.filter { char -> char.isDigit() } },
                            label = { Text("Amount (ml)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Button(
                            onClick = {
                                val amount = customAmountStr.toIntOrNull()
                                if (amount != null && amount > 0) {
                                    viewModel.addWater(amount)
                                    customAmountStr = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Log", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
            
            composable("history") {
                val records by viewModel.allRecords.collectAsStateWithLifecycle()
                val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "History",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (records.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No history logs yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(records.size) { index ->
                                val record = records[index]
                                val dateStr = dateFormat.format(java.util.Date(record.timestamp))
                                val timeStr = timeFormat.format(java.util.Date(record.timestamp))
                                
                                // Group by date visually
                                val showDateHeader = index == 0 || dateFormat.format(java.util.Date(records[index - 1].timestamp)) != dateStr
                                
                                if (showDateHeader) {
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = if (index == 0) 0.dp else 16.dp, bottom = 8.dp)
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                if (record.amountMl >= 500) "🧴" else if (record.amountMl >= 250) "💧" else "🥛",
                                                fontSize = 20.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                "${record.amountMl} ml",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                timeStr,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteRecord(record.id, record.amountMl, record.timestamp) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete record",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            composable("settings") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Settings",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ConfigurationCard(settings = settings, onSettingsChanged = { viewModel.updateSettings(it) })
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
fun CircularProgress(current: Int, goal: Int) {
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "progress"
    )
    
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(208.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(208.dp)) {
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx())
                )
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$current",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "/ $goal ML",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // slate-500
                    letterSpacing = 1.sp
                )
            }
        }
        
        if (progress > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            val percent = (progress * 100).toInt()
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$percent% of daily goal reached",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun QuickLogCard(
    modifier: Modifier = Modifier,
    title: String, 
    amount: Int, 
    emoji: String,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    
    val bgColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isPrimary) Color.White else MaterialTheme.colorScheme.onBackground
    val subtitleColor = if (isPrimary) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isPrimary) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant
    
    Card(
        modifier = modifier
            .aspectRatio(0.85f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .testTag("log_${amount}"),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPrimary) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 24.sp, modifier = Modifier.padding(bottom = 4.dp))
            Text(
                "${amount}ml",
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                title,
                color = subtitleColor,
                fontSize = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationCard(settings: WaterSettings, onSettingsChanged: (WaterSettings) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isCustomActive = !listOf(15, 30, 60).contains(settings.intervalMinutes)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(32.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Notification Interval",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "Frequency of reminders",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Custom Segmented Control
            val intervals = listOf(15, 30, 60, 0) // 0 for Custom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                intervals.forEach { interval ->
                    val isSelected = (interval != 0 && settings.intervalMinutes == interval) || (interval == 0 && isCustomActive)
                    val text = if (interval == 0) "Custom" else if (interval == 60) "1h" else "${interval}m"
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .clickable {
                                if (interval != 0) {
                                    onSettingsChanged(settings.copy(intervalMinutes = interval))
                                } else {
                                    onSettingsChanged(settings.copy(intervalMinutes = 45)) // Default Custom to 45m
                                }
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            if (isCustomActive) {
                Spacer(modifier = Modifier.height(12.dp))
                var customIntervalStr by remember(settings.intervalMinutes) { mutableStateOf(settings.intervalMinutes.toString()) }
                OutlinedTextField(
                    value = customIntervalStr,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { it.isDigit() }
                        customIntervalStr = filtered
                        val minutes = filtered.toIntOrNull()
                        if (minutes != null && minutes > 0) {
                            onSettingsChanged(settings.copy(intervalMinutes = minutes))
                        }
                    },
                    label = { Text("Custom Interval (minutes)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Night Silence",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    val formatTime = { h: Int, m: Int ->
                        val amPm = if (h >= 12) "PM" else "AM"
                        val hour12 = if (h % 12 == 0) 12 else h % 12
                        val minString = m.toString().padStart(2, '0')
                        "$hour12:$minString $amPm"
                    }
                    
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            formatTime(settings.nightModeStartHour, settings.nightModeStartMin),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            onSettingsChanged(settings.copy(nightModeStartHour = h, nightModeStartMin = m))
                                        },
                                        settings.nightModeStartHour,
                                        settings.nightModeStartMin,
                                        false
                                    ).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Text(
                            " to ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            formatTime(settings.nightModeEndHour, settings.nightModeEndMin),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    android.app.TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            onSettingsChanged(settings.copy(nightModeEndHour = h, nightModeEndMin = m))
                                        },
                                        settings.nightModeEndHour,
                                        settings.nightModeEndMin,
                                        false
                                    ).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Switch(
                    checked = settings.nightModeEnabled,
                    onCheckedChange = { onSettingsChanged(settings.copy(nightModeEnabled = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Daily Goal (ml)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            var goalStr by remember(settings.dailyGoalMl) { mutableStateOf(settings.dailyGoalMl.toString()) }
            OutlinedTextField(
                value = goalStr,
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }
                    goalStr = filtered
                    val amount = filtered.toIntOrNull()
                    if (amount != null && amount > 0) {
                        onSettingsChanged(settings.copy(dailyGoalMl = amount))
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
