package de.berlindroid.zeapp.zeui

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.ban.autosizetextfield.AutoSizeTextField
import de.berlindroid.zeapp.R
import de.berlindroid.zeapp.zebits.composableToBitmap
import de.berlindroid.zeapp.zebits.isBinary
import de.berlindroid.zeapp.zemodels.ZeConfiguration
import de.berlindroid.zeapp.zeui.zepages.CustomPhrasePage
import de.berlindroid.zeapp.zeui.zetheme.ZeBlack
import de.berlindroid.zeapp.zeui.zetheme.ZeWhite

@Composable
fun CustomPhraseEditorDialog(
    config: ZeConfiguration.CustomPhrase,
    dismissed: () -> Unit,
    accepted: (ZeConfiguration.CustomPhrase) -> Unit,
    udpateMessage: (String) -> Unit = {},
) {
    val activity = LocalContext.current as Activity

    var randomPhrase by remember { mutableStateOf(config.phrase) }
    var image by remember { mutableStateOf(config.bitmap) }

    fun redrawComposableImage() {
        composableToBitmap(
            activity = activity,
            content = { CustomPhrasePage(randomPhrase) },
        ) {
            image = it
        }
    }

    AlertDialog(
        containerColor = ZeWhite,
        onDismissRequest = dismissed,
        confirmButton = {
            Button(
                onClick = {
                    if (image.isBinary()) {
                        accepted(ZeConfiguration.CustomPhrase(randomPhrase, image))
                    } else {
                        udpateMessage(activity.resources.getString(R.string.binary_image_needed))
                    }
                },
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = dismissed) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        title = { Text(
            color = ZeBlack,
            text = stringResource(R.string.add_your_phrase_here)
        ) },
        properties = DialogProperties(),
        text = {
            Column {
                BinaryImageEditor(
                    bitmap = image,
                    bitmapUpdated = { image = it },
                )
            }
            AutoSizeTextField(
                modifier = Modifier.fillMaxWidth(),
                value = randomPhrase,
                label = { Text(text = stringResource(R.string.random_phrase)) },
                onValueChange = { newValue ->
                    if (newValue.length <= MaxCharacters * 2) {
                        randomPhrase = newValue
                        redrawComposableImage()
                    }
                },
                placeholder = {},
                trailingIcon = {},
                supportingText = {
                    Text(text = "${randomPhrase.length}/${MaxCharacters * 2}")
                },
            )
        },
    )
}
