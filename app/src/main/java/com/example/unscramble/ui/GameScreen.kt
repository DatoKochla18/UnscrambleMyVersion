/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.unscramble.ui

import android.app.Activity
import androidx.compose.material3.Surface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscramble.R
import com.example.unscramble.ui.theme.UnscrambleTheme

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel()

) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.app_name),
            style = typography.titleLarge,
        )

            GameLayout(
                userGuess = gameViewModel.userGuess,
                wordCount = gameUiState.currentWordCount,
                isGuessWrong = gameUiState.isGuessedWordWrong,
                onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
                onKeyboardDone = { gameViewModel.checkUserGuess() },
                currentScrambledWord = gameUiState.currentScrambledWord,
                hintFirstChar ={ gameViewModel.hintFirstChar()},
                isFirstHint = gameUiState.isFirstHint,
                hintLastChar ={ gameViewModel.hintLastChar()},
                isLastHint = gameUiState.isLastHint,
                hintRandomChar = {gameViewModel.hintRandomChar()},
                isRandomHint = gameUiState.isRandomHint,
                isGuessTrue = gameUiState.isGuessedWordTrue,
                currentWord = gameUiState.currentWord,
                lives = gameUiState.lives,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(mediumPadding)
            )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(mediumPadding),
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {gameViewModel.checkUserGuess() }
            ) {
                Text(
                    text = stringResource(R.string.submit),
                    fontSize = 16.sp
                )
            }

            OutlinedButton(
                onClick = { gameViewModel.skipWord() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    fontSize = 16.sp
                )
            }
        }

        GameStatus(score = gameUiState.score, modifier = Modifier.padding(20.dp))
        if (gameUiState.isGameOver) {
            FinalScoreDialog(
                score = gameUiState.score,
                onPlayAgain = { gameViewModel.resetGame() }
            )
        }
    }
}

@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.score, score),
            style = typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun GameLayout(modifier: Modifier = Modifier,
               isGuessWrong: Boolean,
               wordCount: Int,
               currentScrambledWord: String,
               onUserGuessChanged: (String) -> Unit,
               onKeyboardDone: () -> Unit,
               hintFirstChar: ()-> Unit,
               isFirstHint: Boolean,
               hintLastChar: ()-> Unit,
               isLastHint: Boolean,
               hintRandomChar: ()-> Unit,
               isRandomHint: Boolean,
               userGuess: String,
               lives: Int,
               isGuessTrue: Boolean=false,
                currentWord: String
               ) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)

        ) {


            Row (modifier.fillMaxWidth(1f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row  {


                IconButton(onClick = { hintFirstChar() }, enabled = !isFirstHint) {
                    Icon(painter = painterResource(id = R.drawable.idea), contentDescription ="",
                        tint = (if (!isFirstHint)  Color.Yellow
                            else Color.LightGray ))
                }
                IconButton(onClick = { hintRandomChar() }, enabled = !isRandomHint) {
                    Icon(painter = painterResource(id = R.drawable.idea), contentDescription ="",                         tint = (if (!isRandomHint)  Color.Yellow
                    else Color.LightGray ) )
                }
                IconButton(onClick = { hintLastChar() }, enabled = !isLastHint) {
                    Icon(painter = painterResource(id = R.drawable.idea), contentDescription ="",                         tint = (if (!isLastHint)  Color.Yellow
                    else Color.LightGray ) )
                }}

                HeartWithCount(heartCount = lives, heartIcon = Icons.Filled.Favorite )
                /*Text(
                    modifier = Modifier
                        .clip(shapes.medium)
                        .background(colorScheme.surfaceTint)
                        .padding(horizontal = 10.dp),
                    text = stringResource(R.string.word_count, wordCount),
                    style = typography.titleMedium,
                    color = colorScheme.onPrimary
                )*/
            }
            Text(
                text = currentScrambledWord,
                fontSize = 45.sp,
                modifier = modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
                )
            Text(
                text = currentWord,
                style = typography.displayMedium
            )
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            var colour: Color= colorScheme.surface
            if (isGuessTrue) {colour= Color.Green}
            OutlinedTextField(
                value = userGuess,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),


                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colour,
                    unfocusedContainerColor = colour,
                    disabledContainerColor = colour,
                ),
                onValueChange = onUserGuessChanged,
                label = {        if (isGuessWrong) {
                    Text(stringResource(R.string.wrong_guess))
                } else if (isGuessTrue ) {Text(androidx.compose.ui.res.stringResource(id = R.string.correct))} else {
                    Text(stringResource(R.string.enter_your_word))
                } },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() }
                )
            )
        }
    }
}

/*
 * Creates and shows an AlertDialog with final score.
 */
@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(text = stringResource(R.string.congratulations)) },
        text = { Text(text = stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

@Composable
fun HeartWithCount(heartCount: Int, heartIcon: ImageVector) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
    ) {

        Icon(
            imageVector = heartIcon,
            contentDescription = "Heart Icon",
            modifier = Modifier
                .size(48.dp)
            ,tint=Color.Red
        )

        // Heart Count
        if (heartCount > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Red, shape = CircleShape)
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = heartCount.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    UnscrambleTheme {
        GameScreen()
    }
}

