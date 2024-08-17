package com.example.unscramble.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.unscramble.data.allWords
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _uiState= MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    var userGuess by mutableStateOf("")
        private set
    private  lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }
    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }
    fun checkUserGuess() {

        if (userGuess.equals(currentWord, ignoreCase = true)) {
            _uiState.update { currentState -> currentState.copy(isGuessedWordTrue = true) }
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
            updateUserGuess("")


        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
        }
        // Reset user guess
        updateUserGuess("")
    }
        }
    private fun updateGameState(updatedScore: Int) {
        if (_uiState.value.lives>0){

            // Normal round in the game
            val new_word=pickRandomWordAndShuffle()
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    isFirstHint = false,
                    isLastHint = false,
                    isRandomHint = false,
                    isGuessedWordTrue = false,
                    currentScrambledWord = new_word,
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore,
                    currentWord = "_ ".repeat( new_word.length).trim()
                )
            }
        }
    }
    fun skipWord() {
        viewModelScope.launch {

            correctWord()

            delay(1500)

            updateGameState(_uiState.value.score)

            _uiState.update { currentstate ->currentstate.copy(lives = _uiState.value.lives-1) }
            if (_uiState.value.lives==0){_uiState.update { currentstate-> currentstate.copy(isGameOver = true) }}
            updateUserGuess("")
        }

    }

    fun correctWord(){
        _uiState.update { state -> state.copy(currentWord = currentWord) }
    }
    /*fun hint(){
        if  (_uiState.value.isHinted ==false) {
        _uiState.update { currentState -> currentState.copy(isHinted=true ,score = currentState.score-5) }
    }}*/
    fun firstChar(): String{
        return currentWord.toString().take(1)+_uiState.value.currentWord.substring(1)
    }
    fun lastChar(): String{
        return _uiState.value.currentWord.substring(0,_uiState.value.currentWord.length-1)+ currentWord.last().toString()
    }

    fun getRandomIndexAndSubstring(input: String): Pair<Int, String> {
        if (input.length < 2) {
            throw IllegalArgumentException("The string must have a length of at least 2.")
        }

        val randomIndex = Random.nextInt(1, input.length -1)

        val substring = input[randomIndex]

        return Pair(randomIndex, substring.toString())
    }
    fun hintFirstChar(){

        _uiState.update { currentState -> currentState.copy(currentWord=firstChar(), isFirstHint = true ,score = currentState.score-5) }
        }
    fun hintLastChar(){

        _uiState.update { currentState -> currentState.copy(currentWord=lastChar() , isLastHint = true,score = currentState.score-5) }
    }
    fun hintRandomChar(){
        val (index,text)=getRandomIndexAndSubstring(currentWord)
        val new_word= _uiState.value.currentWord.subSequence(startIndex = 0, endIndex = index*2).toString()+text+_uiState.value.currentWord.subSequence(index*2+1,_uiState.value.currentWord.length)


        _uiState.update { currentstate->currentstate.copy(currentWord=new_word, isRandomHint = true, score = currentstate.score-5) }
    }
}

