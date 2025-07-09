package com.example.mobilepopmasterr.ui.screens.unused

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilepopmasterr.getPopulationFromCoordinates
import com.example.mobilepopmasterr.ui.Rectangle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch


// this whole file exists just for the joke I make in registration lmao
// TODO: see if network and all that shit is done correctly
// TODO : overall clean up this messy af code wtf is this shit.
// wrote this for a meme before doing most of this project
// TODO: add error handling for the map stuff if something fucks up

@Composable
fun PhoneInput(
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onWarningChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
){
    // For dropdown menu
    var expanded by rememberSaveable { mutableStateOf(false) }
    var phoneNumberStart by rememberSaveable { mutableStateOf("") }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    var visible by rememberSaveable { mutableStateOf(false) }
    var population: String? by rememberSaveable { mutableStateOf("") }


    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(80.dp)
            .padding(0.dp, 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        //couldnt get all the heights to line up n shit but in the end whatever idc
        OutlinedTextField(
            value = phoneNumberStart,
            onValueChange = { phoneNumberStart = it },
            modifier = Modifier
                .width(110.dp),
            label = { Text(text = "Country code" )},
            trailingIcon = {
                Icon(
                    icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        // TODO : make this dropdown a .foreach w/ numbers in /data/
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("+1 USA") },
                onClick = { phoneNumberStart = "+1"; expanded = false }
            )
            DropdownMenuItem(
                text = { Text("+44 UK") },
                onClick = { phoneNumberStart = "+44"; expanded = false }
            )
            DropdownMenuItem(
                text = { Text("+49 Germany") },
                onClick = { phoneNumberStart = "+49"; expanded = false }
            )
            DropdownMenuItem(
                text = { Text("+370 Lithuania") },
                onClick = { phoneNumberStart = "+370"; expanded = false }
            )
        }

        OutlinedTextField(
            value = population ?: "NULL",
//                onClick = { visible = !visible },
            onValueChange = { },
            label = { Text(text = "Select phone number")},
            readOnly = true,
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    visible = true
                }
            },
        )
    }
    val defaultLocation = LatLng(0.0, 0.0)
    val firstMarkerState = rememberMarkerState(position = defaultLocation)
    val secondMarkerState = rememberMarkerState(position = defaultLocation)

    val rectangle = Rectangle(firstMarkerState.position, secondMarkerState.position)

    AnimatedVisibility(visible) {

        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Text(
                text = "Please select an area that has the population equivalent to your phone number",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
            // TODO: https://stackoverflow.com/questions/70836603/how-to-scroll-zoom-a-map-inside-a-vertically-scrolling-in-jetpack-compose
            // fuck ts tho
            //https://stackoverflow.com/questions/71379665/how-to-scroll-to-a-certain-item-in-a-columnmodifier-verticalscroll
            MapWithRectangle(
                rectangle, firstMarkerState, secondMarkerState, modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(8.dp)
            )
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    coroutineScope.launch {
                        population = getPopulationFromCoordinates(rectangle); visible = false
                    }
                    onWarningChange(true)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary, // background color
                    contentColor = MaterialTheme.colorScheme.onPrimary  // text/icon color
                ),
            ) {
                Text(
                    text = "Get population",
    //                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
    //                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
    //                        modifier = modifier,
    //                        color = MaterialTheme.colorScheme.primary
                )
            }

            // this is a stupid ass workdaround, but I don't quite give a shit!
            var checked by rememberSaveable { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.Start
            ){
                Checkbox(
                    checked = checked,
                    onCheckedChange = {onCheckedChange(!checked) ; checked = !checked},
                    modifier = Modifier.padding(8.dp)
                )
                TextButton(
                    onClick = {onCheckedChange(!checked); checked = !checked}
                ) {
                    Text(
                        text = "Disable page scroll (Makes map selection easier)",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
}
}

@Preview
@Composable
fun PhoneInputPreview() {
    PhoneInput()
}