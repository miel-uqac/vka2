package com.example.klavier.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.klavier.R
import kotlin.math.abs

@Composable
fun TouchPad(
    sensibility: Float,
    sendData: (String) -> Unit
) {

    val context = LocalContext.current

    // PARAMETERS
    val deadZone = 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.DarkGray)
            .fillMaxHeight(0.6f)
            .background(Color.LightGray.copy(0.6f)) // FOR DEBUG
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    var prevClickDroit = false //Besoin de ça car quand 2 doigts puis relaché les tailles des changes sont : 1221, cette variable permet d'ignorer le dernier 1
                    var isHoldSingle = false
                    var isHoldDouble = false

                    var firstPositionSingle = Offset.Zero // pour un doigt
                    var lastPositionSingle = Offset.Zero
                    var firstHoldTimeSingle = 0L

                    var firstPosition1 = Offset.Zero // pour deux doigts
                    var firstPosition2 = Offset.Zero
                    var lastPosition1 = Offset.Zero
                    var lastPosition2 = Offset.Zero
                    var firstHoldTimeDouble = 0L


                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.size == 2) {
                            // Gestion à 2 doigts
                            val change1 = event.changes[0]
                            val change2 = event.changes[1]

                            if (!isHoldDouble && change1.pressed && change2.pressed) {
                                // Premier appui
                                firstPosition1 = change1.position
                                firstPosition2 = change2.position
                                lastPosition1 = change1.position
                                lastPosition2 = change2.position
                                firstHoldTimeDouble = change1.uptimeMillis
                                isHoldDouble = true
                            } else if (isHoldDouble && (!change1.pressed || !change2.pressed)) {
                                // Détection clic droit
                                val delta1 = firstPosition1 - lastPosition1
                                val delta2 = firstPosition2 - lastPosition2
                                val deltaTime = abs(change1.uptimeMillis - firstHoldTimeDouble)

                                if (delta1.getDistance() <= deadZone && delta2.getDistance() <= deadZone && deltaTime < 500) {
                                    // Clic droit
                                    sendData(context.getString(R.string.id_click_droit))
                                    change1.consume()
                                    change2.consume()
                                    prevClickDroit = true
                                    lastPositionSingle = if (change1.pressed) change1.position else change2.position
                                }
                                isHoldDouble = false
                            } else if (isHoldDouble && change1.pressed && change2.pressed) {
                                // Détection défilement
                                val currentPosition1 = change1.position
                                val currentPosition2 = change2.position
                                val offsetDelta = (currentPosition1.y + currentPosition2.y) / 2 -
                                        (lastPosition1.y + lastPosition2.y) / 2

                                if (abs(offsetDelta) > deadZone) {
                                    val scrollDirection = if (offsetDelta > 0) -1 else 1
                                    val scrollCommand =
                                        context.getString(R.string.id_mouse_scroll) + ":H:$scrollDirection"
                                    sendData(scrollCommand)
                                    change1.consume()
                                    change2.consume()
                                }

                                // Mettre à jour la dernière position
                                lastPosition1 = currentPosition1
                                lastPosition2 = currentPosition2
                            }


                        } else if (event.changes.size == 1) {
                            val change = event.changes[0]
                            if (prevClickDroit) {
                                prevClickDroit = false
                                lastPositionSingle = change.position
                            } else {
                                // Gestion à un doigt
                                if (!isHoldSingle && change.pressed) {
                                    firstPositionSingle = change.position
                                    lastPositionSingle = change.position
                                    firstHoldTimeSingle = change.uptimeMillis
                                    isHoldSingle = true
                                } else if (isHoldSingle && !change.pressed) {
                                    val delta = firstPositionSingle - lastPositionSingle
                                    val deltaTime = abs(change.uptimeMillis - firstHoldTimeSingle)

                                    if (delta.getDistance() < deadZone && deltaTime < 500) {
                                        // Clic gauche
                                        sendData(context.getString(R.string.id_click_gauche))
                                        change.consume()
                                    }
                                    isHoldSingle = false
                                } else if (isHoldSingle && change.pressed) {
                                    val currentPosition = change.position
                                    val delta = currentPosition - lastPositionSingle

                                    if (abs(delta.x) > deadZone || abs(delta.y) > deadZone) {
                                        val moveCommand =
                                            context.getString(R.string.id_mouse_move) +
                                                    ":${(sensibility * delta.x).toInt()}:${(sensibility * delta.y).toInt()}"
                                        sendData(moveCommand)
                                        change.consume()
                                    }

                                    lastPositionSingle = currentPosition
                                }
                            }
                        }
                    }
                }
            }
    )



}