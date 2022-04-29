package com.hescul.urgent.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hescul.urgent.R
import com.hescul.urgent.navigation.HomeScreens
import com.hescul.urgent.ui.screens.home.patient.PatientViewModel
import com.hescul.urgent.ui.theme.UrgentTheme
import com.hescul.urgent.ui.versatile.InfoFieldType
import com.hescul.urgent.ui.versatile.InfoTextField
import kotlinx.coroutines.launch

@Composable
fun HomeFAB(
    onCLick: () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = false,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier,
        onClick = onCLick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.cd_addIcon)
            )
            AnimatedVisibility(
                visible = extended,
            ) {
                Text(
                    text = stringResource(id = R.string.ui_homeScreen_fabExtended),
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
        }
    }
}

@Composable
fun HomeBottomBar(
    homeNavController: NavHostController,
    screens: List<HomeScreens>,
    onScreenChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach {  screen ->
            BottomNavigationItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = stringResource(id = screen.cd)) },
                label = { Text(text = stringResource(id = screen.label)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                enabled = enabled,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.secondary,
                alwaysShowLabel = false,
                onClick = {
                    onScreenChange(screen.route)
                    homeNavController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(homeNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SubscribeSheet(
    patientViewModel: PatientViewModel,
    onSubscribeRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentPadding = 15.dp
    val titlePadding = 10.dp
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier
            .padding(
                vertical = contentPadding,
                horizontal = contentPadding * 2
            )
            .animateContentSize(tween(durationMillis = 200))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(5.dp),
                color = MaterialTheme.colors.primary.copy(0.3f)
            ) {
                Spacer(modifier = Modifier.padding(horizontal = 40.dp, vertical = 2.5.dp))
            }
            Spacer(modifier = Modifier.padding(vertical = titlePadding))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.ui_homeScreen_subscribeSheetTitle),
                    style = MaterialTheme.typography.h5,
                )
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        onSubscribeRequest()
                    },
                    enabled = patientViewModel.isSubscribeButtonEnable()
                ) {
                    Text(
                        text = stringResource(id = R.string.ui_homeScreen_subscribeButton),
                        style = MaterialTheme.typography.h6,
                    )
                }
            }
            InfoTextField(
                text = patientViewModel.deviceIdInputText,
                fieldType = InfoFieldType.DeviceIdField,
                onTextChange = patientViewModel::onDeviceIdInputTextChange,
                modifier = Modifier.fillMaxWidth(),
                isError = patientViewModel.isDeviceIdInputTextError(),
                enabled = !patientViewModel.isProgressing
            )
        }
        AnimatedVisibility(visible = patientViewModel.showDeviceIdAlreadyExistedMessage) {
            Text(
                text = stringResource(id = R.string.ui_homeScreen_deviceIdAlreadyExistedMessage),
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        }
        AnimatedVisibility(visible = !patientViewModel.showDeviceIdAlreadyExistedMessage) {
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }
        Spacer(modifier = Modifier.padding(vertical = contentPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.ui_homeScreen_attributeFieldTitle),
                style = MaterialTheme.typography.h5,
            )
            val exceedingAttributeMessage = stringResource(id = R.string.ui_homeScreen_exceedingAttributeMessage)
            TextButton(
                onClick = {
                    patientViewModel.onAddNewAttribute(exceedingAttributeMessage)
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(patientViewModel.attributeInputList.size)
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.ui_homeScreen_addAttributeButton),
                    style = MaterialTheme.typography.h6
                )
            }
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            value = patientViewModel.nameInputText,
            onValueChange = patientViewModel::onNameInputTextChange,
            label = {
                Text(text = stringResource(id = R.string.ui_homeScreen_nameFieldLabel))
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.secondary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
        Spacer(modifier = Modifier.padding(vertical = titlePadding))
        AnimatedVisibility(visible = patientViewModel.attributeWarning.isNotEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = patientViewModel.attributeWarning,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(vertical = titlePadding * 2))
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = titlePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = lazyListState
        ) {
            itemsIndexed(
                items = patientViewModel.attributeInputList,
                key = null
            ) { index, attribute ->
                val exceedingPinnedMessage = stringResource(id = R.string.ui_homeScreen_exceedingPinnedAttributeMessage)
                SubscribeSheetAttributeRow(
                    keyText = attribute.key,
                    onKeyTextChange = { attribute.key = it },
                    valueText = attribute.value,
                    onValueTextChange = { attribute.value = it },
                    pinned = attribute.pinned,
                    onPinStateChange = {
                        patientViewModel.onAttributePinStateChange(attribute, exceedingPinnedMessage)
                    },
                    onRemove = { patientViewModel.attributeInputList.removeAt(index) }
                )
                Spacer(modifier = Modifier.padding(vertical = titlePadding))
            }
        }
    }
}

@Preview
@Composable
fun PreviewSubscribeSheet() {
    val homeViewModel = PatientViewModel(LocalContext.current)
    UrgentTheme {
        Surface {
            SubscribeSheet(
                patientViewModel = homeViewModel,
                onSubscribeRequest = {},
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SubscribeSheetAttributeRow(
    keyText: String,
    onKeyTextChange: (String) -> Unit,
    valueText: String,
    onValueTextChange: (String) -> Unit,
    pinned: Boolean,
    onPinStateChange: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPinStateChange
        ) {
            if (pinned) {
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = stringResource(id = R.string.cd_filledPushPinIcon),
                    tint = MaterialTheme.colors.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PushPin,
                    contentDescription = stringResource(id = R.string.cd_outlinedPushPinIcon),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
        IconButton(
            onClick = onRemove
        ) {
            Icon(
                imageVector = Icons.Default.RemoveCircleOutline,
                contentDescription = stringResource(id = R.string.cd_removeCircleIcon),
                tint = MaterialTheme.colors.error
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 5.dp))
        TextField(
            modifier = Modifier.height(TextFieldDefaults.MinHeight).fillMaxWidth(0.4f),
            value = keyText,
            onValueChange = onKeyTextChange,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.secondary
            ),
            label = {
                Text(text = stringResource(id = R.string.ui_homeScreen_keyFieldLabel))
            },
            placeholder = {
                Text(text = stringResource(id = R.string.ui_homeScreen_keyFieldHint))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
        Spacer(modifier = Modifier.padding(horizontal = 5.dp))
        TextField(
            modifier = Modifier.height(TextFieldDefaults.MinHeight).fillMaxWidth(),
            value = valueText,
            onValueChange = onValueTextChange,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.secondary
            ),
            label = {
                Text(text = stringResource(id = R.string.ui_homeScreen_valueFieldLabel))
            },
            placeholder = {
                Text(text = stringResource(id = R.string.ui_homeScreen_valueFieldHint))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
    }
}

@Preview
@Composable
fun PreviewAttributeRow() {
    UrgentTheme {
        Surface {
            SubscribeSheetAttributeRow(
                keyText = "",
                onKeyTextChange = {},
                valueText = "",
                onValueTextChange = {},
                pinned = false,
                onPinStateChange = {},
                onRemove = {}
            )
        }
    }
}