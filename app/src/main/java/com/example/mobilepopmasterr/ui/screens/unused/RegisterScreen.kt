package com.example.mobilepopmasterr.ui.screens.unused

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview

// THIS SCREEN IS NOT USED. Keeping it in case I may reuse it
@Composable
@Deprecated("unused")
private fun RegisterScreen(
    modifier: Modifier = Modifier,
) {

    LocalFocusManager.current
    remember { FocusRequester() }
    remember { FocusRequester() }
    remember { FocusRequester() }

    var scrollable by rememberSaveable { mutableStateOf(true) }

    // TODO: figure out focus requesting on password fields, may not work cuz its in alpha?

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
        ,
        userScrollEnabled = scrollable,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        item{
            // TODO : add popmasterr logo here

            // TODO: clean this shit up, fuse this to login/signin
            //TODO: start implementing actual game functionality
            // TODO: clean this file! dont just delete it, move it somewhere cuz it's still quite cool
            /*Spacer(modifier = Modifier.height(16.dp))
            GenericInputField(
                value = usernameValue,
                onValueChange = onUsernameChange,
                label = R.string.username,
                icon = Icons.Filled.Person,
                modifier = Modifier.focusRequester(usernameFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { emailFocusRequester.requestFocus() }
                )
            )

            GenericInputField(
                value = emailValue,
                onValueChange = onEmailChange,
                label = R.string.email,
                icon = Icons.Filled.Email,
                modifier = Modifier.focusRequester(emailFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                )
            )
            HideableInputField(
                label = R.string.password,
                passwordState = passwordState,
                modifier = Modifier.focusRequester(passwordFocusRequester),
            )
            // no focus requester here cuz the outlined secure field doesnt have keyboardaction
            // we pretend it's intentional so the user doesnt just enter whatever as his confirm password
            // definitely (ffs i should've just used https://developer.android.com/develop/ui/compose/quick-guides/content/show-hide-password?hl=en )
            // even tho it uses basic secure text field and looks like ass
            HideableInputField(
                label = R.string.confirm_password,
                passwordState = confirmPasswordState,
            )

                // dogshit workaround w/ disabling scroll here but whatever
                PhoneInput(
                    checked = disableScroll,
                    onCheckedChange = { checked ->
                        disableScroll = checked
                        scrollable = !checked
                    },
                    onWarningChange = {showWarning = it}
                )
                AnimatedVisibility(showWarning) {
                    MediumWarning("We're kidding. The number you entered here will not be used for anything.", modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }

                //TODO: checkbox for ToS, probbaly needed for app store and all taht bs
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSignInClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                ) {
                    Text(
                        text = stringResource(R.string.signin_button)
                    )
                }*/
        }}
}

@Preview(name = "Light Mode", showBackground = true )
@Composable
fun LightRegisterScreenPreview() {
    RegisterScreen()
}

/*
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DarkSignInScreenPreview(){
    MobilePopmasterrTheme(darkTheme = true, dynamicColor = false) {
        SignInScreen(modifier = Modifier.fillMaxSize())
    }
}*/
