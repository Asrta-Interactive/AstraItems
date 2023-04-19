package com.astrainteractive.empireitems.desktop.presentation.folder_selection.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.astrainteractive.empireitems.desktop.presentation.folder_selection.state.FolderState
import com.astrainteractive.empireitems.desktop.style.Colors
import com.astrainteractive.empireitems.desktop.style.Typography
import java.io.File

@Composable
fun FodlerSelector(state: FolderState, onFolderSelected: (File) -> Unit, onBackClicked: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            TextButton({
                onBackClicked()
            }) {
                Icon(Icons.Filled.ChevronLeft, "", tint = Colors.colorOnPrimary)
                Text("Back ${state.parent ?: "./"}", style = Typography.H3.copy(fontWeight = FontWeight.Bold))
            }
        }
        items(state.files) {
            FileRow(it) { onFolderSelected(it) }
        }
    }
}