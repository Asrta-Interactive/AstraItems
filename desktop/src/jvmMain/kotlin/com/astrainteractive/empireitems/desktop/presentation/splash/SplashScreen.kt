package com.astrainteractive.empireitems.desktop.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.astrainteractive.empireitems.desktop.navigation.Component
import com.astrainteractive.empireitems.desktop.navigation.Screen
import com.astrainteractive.empireitems.desktop.resources.Resources
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Dimens
import com.astrainteractive.empireitems.desktop.style.Typography
import io.kanro.compose.jetbrains.expui.control.PrimaryButton

class SplashScreen(
    val componentContext: ComponentContext,
    val navigation: StackNavigation<Screen>
) : Component {
    @Composable
    override fun render() {


        Column(
            Modifier.fillMaxSize().background(Colors.colorPrimaryVariant),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(Resources.logoHD.bitmap, "", modifier = Modifier.height(200.dp))
            Spacer(Modifier.height(Dimens.L))
            Text("Empire Items", style = Typography.H1)
            Spacer(Modifier.height(Dimens.S))
            Text(
                "This application will help you to modifty content of EmpireItems more easily!",
                style = Typography.H3
            )
            Spacer(Modifier.height(Dimens.L))
            PrimaryButton({
                navigation.replaceCurrent(Screen.FolderSelection)
            }, modifier = Modifier.padding(Dimens.S)) {
                Text("Choose folder", style = Typography.H3)
            }
        }
    }
}