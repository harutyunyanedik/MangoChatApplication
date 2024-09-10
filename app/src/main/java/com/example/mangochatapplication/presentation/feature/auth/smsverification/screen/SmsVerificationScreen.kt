package com.example.mangochatapplication.presentation.feature.auth.smsverification.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mangochatapplication.common.utils.safeLet
import com.example.mangochatapplication.presentation.feature.auth.smsverification.SmsVerificationViewModel
import com.example.mangochatapplication.presentation.navigation.routes.Screens
import com.example.mangochatapplication.presentation.shared.views.LoadingButton
import com.example.mangochatapplication.presentation.shared.views.PinView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SmsVerificationScreen(viewModel: SmsVerificationViewModel = koinViewModel(), navController: NavHostController?, phone: String?, countryCode: String?) {
    val state = viewModel.smsVerificationState.collectAsState().value

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        phone?.let {
            viewModel.addIntent(SmsVerificationScreenIntent.Initial("$countryCode $it".trim()))
            viewModel.addIntent(SmsVerificationScreenIntent.SendCode("$countryCode $it".trim()))
        }
    }

    @Composable
    fun VerificationCodeSentText() {
        Text(
            text = "Sms \nVerification \nCode is sent",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight(700),
                color = MaterialTheme.colorScheme.onPrimary
            ),
        )
    }

    @Composable
    fun VerifyButton() {
        LoadingButton(
            isLoading = state.isLoading == true,
            onClick = {
                safeLet(state.phone, state.pinValue) { phoneNumber, pinValue ->
                    viewModel.addIntent(SmsVerificationScreenIntent.Verify(phoneNumber, pinValue))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text(text = "Verify")
        }
    }

    @Composable
    fun ResendButton() {
        Button(
            enabled = state.resendTime == null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            onClick = {
                safeLet(phone, countryCode) { phoneNumber, countryCode ->
                    viewModel.addIntent(SmsVerificationScreenIntent.SendCode("$countryCode $phoneNumber".trim()))
                }
            }
        ) {
            val text = if (state.resendTime == null) "Resend Code" else "Resend code in ${state.resendTime}"
            Text(text = text)
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            VerificationCodeSentText()
            Spacer(modifier = Modifier.height(32.dp))
            PinView(
                length = 6,
                onValueChanged = {
                    viewModel.addIntent(SmsVerificationScreenIntent.UpdatePinValue(it))
                },
                isCursorVisible = true,
                value = state.pinValue ?: ""
            )
        }
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
        ) {
            VerifyButton()
            ResendButton()
        }
    }

    SideEffect {
        scope.launch {
            viewModel.smsVerificationEffects.collectLatest {
                when (it) {
                    is SmsVerificationScreenEffect.CodeSent -> {
                        var time = 60
                        repeat(time) {
                            viewModel.addIntent(SmsVerificationScreenIntent.UpdateResendTimer(time--))
                            delay(1000)
                        }
                        viewModel.addIntent(SmsVerificationScreenIntent.UpdateResendTimer(null))
                    }

                    is SmsVerificationScreenEffect.Verified -> {
                        when {
                            it.data != null && it.error == null -> {
                                val screen = if (it.data.isUserExists == true) Screens.Chat else Screens.Registration
                                navController?.navigate(screen.route)
                            }

                            it.error != null -> {
                                // todo remove
                                navController?.navigate(Screens.Registration.withArgs(viewModel.smsVerificationState.value.phone))
                                Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun SmsVerificationScreenPreview() {
    SmsVerificationScreen(phone = "", countryCode = "", navController = null)
}