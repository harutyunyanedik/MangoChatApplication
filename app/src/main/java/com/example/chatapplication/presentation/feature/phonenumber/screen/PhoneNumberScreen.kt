package com.example.chatapplication.presentation.feature.phonenumber.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.chatapplication.common.utils.EMPTY_STRING
import com.example.chatapplication.presentation.feature.phonenumber.PhoneNumberScreenViewModel
import com.example.chatapplication.presentation.navigation.routes.Screens
import com.example.chatapplication.presentation.shared.views.PhoneNumberView
import com.example.chatapplication.presentation.theme.Theme
import com.example.chatapplication.presentation.theme.spacing
import com.example.interviewalphab.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max
import kotlin.math.min

@Composable
fun PhoneNumberScreen(viewModel: PhoneNumberScreenViewModel = koinViewModel(), navController: NavHostController?) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = viewModel.phoneNumberScreenState.collectAsStateWithLifecycle().value
    val scope = rememberCoroutineScope()

    val items = remember {
        mutableStateOf(listOf<String>())
    }

    LaunchedEffect(Unit) {
        val list: MutableList<String> = mutableListOf()
        for (i in 0..31) {
            list.add(i.toString())
        }
        items.value = list
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(Theme.spacing.medium)
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.global_whats_your_phone_number),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight(700),
                    color = MaterialTheme.colorScheme.onPrimary
                ),
            )
            Spacer(modifier = Modifier.height(32.dp))
//            GridViewAdvancedArrangement(items.value)

            PhoneNumberView(
                selectedCountry = state.selectedCountry,
                phoneNumber = state.phoneNumber ?: EMPTY_STRING,
                errorText = state.error,
                onPhoneNumberChanged = {
                    viewModel.addIntent(PhoneNumberScreenIntent.PhoneNumberChanged(it))
                }, onCountryChanged = {
                    viewModel.addIntent(PhoneNumberScreenIntent.CountryChanged(it))
                })
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            onClick = {
                viewModel.addIntent(PhoneNumberScreenIntent.Validate)
            }
        ) {
            Text(text = stringResource(id = R.string.global_continue))
        }
    }

    SideEffect {
        scope.launch {
            viewModel.phoneNumberScreenEffect.collectLatest {
                when (it) {
                    is PhoneNumberScreenEffect.Validated -> {
                        if (it.error == null) {
                            val phoneNumber = viewModel.phoneNumberScreenState.value.phoneNumber
                            val countryCode = viewModel.phoneNumberScreenState.value.selectedCountry.code
                            navController?.navigate(Screens.SmsVerificationScreen.withArgs(phoneNumber, countryCode))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestScreen() {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { size = it.size }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4A90E2),
                        Color(0xFF50E3C2),
                        Color.Transparent
                    ),
                    center = Offset(size.width * offsetX, size.height * offsetY),
                    radius = 3000f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp, top = 120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "First Text",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Second Text",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.flag_armenia),
            contentDescription = "Bottom Icon",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(48.dp)
        )
    }
}

@Preview
@Composable
fun TestScreenPreview() {
    TestScreen()
}
