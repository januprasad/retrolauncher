package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.AppInfo
import com.example.ui.theme.MinimalistBlack
import com.example.ui.theme.MinimalistWhite
import com.example.ui.theme.MinimalistGray
import com.example.viewmodel.LauncherViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.BlendMode
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon

@Composable
fun MinimalistLauncher(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val dayOfWeek by viewModel.dayOfWeek.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val apps by viewModel.installedApps.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MinimalistBlack)
    ) {
        // Stylized Wave/Rock Background Detail on the right
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width * 0.85f, 0f)
                // Pockmarked rocky edge
                lineTo(size.width * 0.8f, size.height * 0.1f)
                lineTo(size.width * 0.85f, size.height * 0.2f)
                lineTo(size.width * 0.75f, size.height * 0.35f)
                lineTo(size.width * 0.9f, size.height * 0.5f)
                lineTo(size.width * 0.7f, size.height * 0.65f)
                lineTo(size.width * 0.8f, size.height * 0.8f)
                lineTo(size.width * 0.6f, size.height * 0.95f)
                lineTo(size.width * 0.65f, size.height)
                lineTo(size.width, size.height)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(
                path = path,
                color = Color(0xFF151515)
            )
            
            // Subtle highlight line on the edge
            drawPath(
                path = path,
                color = Color(0xFF333333),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // Left Column: Day and Date
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.45f)
            ) {
                // Vertical "IT'S SUNDAY" - Forced into a single line without clipping
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp
                        )) {
                            append("IT'S ")
                        }
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            letterSpacing = 2.sp
                        )) {
                            append(dayOfWeek)
                        }
                    },
                    color = MinimalistWhite,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        // Use wrapContentWidth with unbounded=true to prevent clipping 
                        // before the rotation is applied.
                        .wrapContentWidth(Alignment.Start, unbounded = true)
                        .graphicsLayer {
                            rotationZ = -90f
                        },
                    maxLines = 1,
                    softWrap = false
                )
                
                // Small metadata above the vertical text
                Text(
                    text = "T LAUNCHER",
                    color = MinimalistGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(bottom = 60.dp, start = 20.dp, end = 10.dp)
                )

                // Date at the bottom left
                Text(
                    text = currentDate,
                    fontWeight = FontWeight.ExtraLight,
                    fontFamily = FontFamily.SansSerif,
                    color = MinimalistWhite,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(bottom = 60.dp, start = 20.dp, end = 10.dp)
                )
            }

            // Right Column: App List
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.55f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 120.dp, bottom = 32.dp)
            ) {
                items(apps) { app ->
                    AppListItem(app = app, onClick = { viewModel.launchApp(app.packageName) })
                }
                
                item {
                    SettingsItem(onClick = { viewModel.launchApp("com.android.settings") })
                }
            }
        }
    }
}

@Composable
fun SettingsItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MinimalistWhite
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = "Settings",
                color = MinimalistWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "com.android.settings",
                color = MinimalistGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun AppListItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Original Colored Icon
        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            app.icon?.let { drawable ->
                Image(
                    bitmap = drawable.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } ?: Box(modifier = Modifier.size(40.dp).background(MinimalistGray))
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = app.label,
                color = MinimalistWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )
            Text(
                text = app.packageName,
                color = MinimalistGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
