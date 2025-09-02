package com.example.chatapplication.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import com.example.chatapplication.presentation.theme.enums.ScreenResolutionEnum
import com.example.chatapplication.presentation.theme.spacing.Spacing
import com.example.chatapplication.presentation.theme.spacing.SpacingDefault
import com.example.chatapplication.presentation.theme.spacing.SpacingXXHDPI

typealias Theme = MaterialTheme

val Theme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

val LocalSpacing = compositionLocalOf<Spacing> {
    error("no spacing provided")
}

object LocalScreenSpacing {
    val current: Spacing
        @Composable
        get() = when (ScreenResolutionEnum[LocalConfiguration.current.densityDpi]) {
            ScreenResolutionEnum.XHdpi -> SpacingDefault()
            ScreenResolutionEnum.XXHdpi -> SpacingDefault()
            ScreenResolutionEnum.XXXHdpi -> SpacingXXHDPI()
        }
}
