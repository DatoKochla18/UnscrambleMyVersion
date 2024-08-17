package com.example.unscramble.ui

data class GameUiState(
    val currentScrambledWord: String="",
    val isGuessedWordWrong: Boolean = false,
    val isGuessedWordTrue: Boolean = false,
    val score: Int = 0,
    val currentWordCount: Int = 1,
    val isGameOver: Boolean = false,
    val isFirstHint: Boolean = false,
    val isLastHint: Boolean = false,
    val isRandomHint: Boolean = false,
    val currentWord: String="_ ".repeat( currentScrambledWord.length).trim(),
    val lives: Int=3,
)
