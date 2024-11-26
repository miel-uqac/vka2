package com.example.klavier.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPickerTab(
    sendData: (String) -> Unit
)
{
    val controller = rememberColorPickerController()
    var hexCode by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(Color.Transparent) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val smallHeight = screenHeight / 100
    val medHeight = screenHeight / 50
    val bigHeight = screenHeight / 20

    val smallWidth = screenWidth / 100
    val medWidth = screenWidth / 50
    val bigWidth = screenWidth / 20

    Card (
        modifier = Modifier.padding(horizontal = bigWidth, vertical = medWidth),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(0.6f) // Couleur de fond de la Card
        )
    ) {

        Row() {
            Column(
                modifier = Modifier
                    .weight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .height(bigHeight * 4)
                ) {
                    HsvColorPicker(
                        modifier = Modifier.padding(smallWidth),
                        controller = controller,
                        drawOnPosSelected = {
                            drawColorIndicator(
                                controller.selectedPoint.value,
                                controller.selectedColor.value,
                            )
                        },
                        onColorChanged = { colorEnvelope ->
                            hexCode = colorEnvelope.hexCode
                            textColor = colorEnvelope.color
                        },
                        initialColor = Color.Red,
                    )
                }

                Spacer(modifier = Modifier.height(smallWidth)) // Espacement entre les éléments

                AlphaSlider(
                    modifier = Modifier
                        .testTag("HSV_AlphaSlider")
                        .fillMaxWidth(0.8f)
                        .padding(smallWidth)
                        .height(bigHeight)
                        .align(Alignment.CenterHorizontally),
                    controller = controller,
                )

                Spacer(modifier = Modifier.height(smallWidth))

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(smallWidth)
                        .height(bigHeight),
                    controller = controller,
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(smallWidth))

                Text(
                    text = "#$hexCode",
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(medWidth),
                )

                Spacer(modifier = Modifier.height(smallWidth))

                AlphaTile(
                    modifier = Modifier
                        .size(bigHeight * 3)
                        .clip(RoundedCornerShape(6.dp))
                        .padding(smallWidth)
                        .align(Alignment.CenterHorizontally),
                    controller = controller,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    sendData("#$hexCode")
                }) {
                    Text(
                        text = "Envoyer",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }

            }
        }
    }
}