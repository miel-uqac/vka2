package com.example.klavier.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
            .background(Color.LightGray.copy(0.6f))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    var prevClickDroit = false // Besoin de ça car quand 2 doigts puis relaché les tailles des changes sont : 1221, cette variable permet d'ignorer le dernier 1
                    var isHoldSingle = false // Pour les maintients à 1 doigt
                    var isHoldDouble = false // Pour les maintients à 2 doigts

                    var firstPositionSingle = Offset.Zero //Première position pour un doigt, sert pour eviter des decallages si on bouge après un click droit
                    var lastPositionSingle = Offset.Zero
                    var firstHoldTimeSingle = 0L // Debut temporel du maintient à 1 doigt pour savoir entre click et deplacements

                    var firstPosition1 = Offset.Zero // premiere position d'un des 2 doigts pour le scroll souris
                    var firstPosition2 = Offset.Zero // premiere position de l'autre doigt pour le scroll souris
                    var lastPosition1 = Offset.Zero // pour calculer le decalage
                    var lastPosition2 = Offset.Zero
                    var firstHoldTimeDouble = 0L // Debut temporel du maintient à 2 doigts pour savoir entre click droit et deplacements

                    var lastLeftClickTime = 0L // Marque temporel du dernier click gauche pour savoir si on fait une selection glissee
                    var isLeftHolding = false

                    while (true) {
                        val event = awaitPointerEvent() //evenements de touche sur la box
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

                                if (abs(offsetDelta) > deadZone) { // Pour eviter de confondre avec un click droit
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


                        } else if (event.changes.size == 1) { // Gestuelle a 1 doigt
                            val change = event.changes[0]
                            if (prevClickDroit) {
                                prevClickDroit = false
                                lastPositionSingle = change.position
                            } else {
                                if (!isHoldSingle && change.pressed) {
                                    firstPositionSingle = change.position
                                    lastPositionSingle = change.position
                                    firstHoldTimeSingle = change.uptimeMillis
                                    isHoldSingle = true
                                } else if (isHoldSingle && !change.pressed) {
                                    if(isLeftHolding){ // Cas ou on relache un hold quand la souris etait en mode selection
                                        sendData(context.getString(R.string.id_mouse_stop_hold))
                                        isLeftHolding = false
                                    }
                                    val delta = firstPositionSingle - lastPositionSingle
                                    val deltaTime = abs(change.uptimeMillis - firstHoldTimeSingle)

                                    if (delta.getDistance() < deadZone && deltaTime < 500) {
                                        // Clic gauche
                                        sendData(context.getString(R.string.id_click_gauche))
                                        lastLeftClickTime = change.uptimeMillis
                                        change.consume()
                                    }
                                    isHoldSingle = false
                                } else if (isHoldSingle && change.pressed) {
                                    // Deplacement souris
                                    val currentPosition = change.position
                                    val delta = currentPosition - lastPositionSingle

                                    if (abs(delta.x) > deadZone || abs(delta.y) > deadZone) {
                                        
                                        // Maintient de la souris
                                        val deltaTime = abs(change.uptimeMillis - lastLeftClickTime)
                                        if(deltaTime < 500 && !isLeftHolding){
                                            sendData(context.getString(R.string.id_mouse_start_hold))
                                            isLeftHolding = true
                                        }

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
    ) {
        // Image illustrative en arrière-plan
        Image(
            painter = painterResource(id = R.drawable.souris),
            contentDescription = "Indicateur de zone souris",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.1f),
            contentScale = ContentScale.Crop
        )
    }



}