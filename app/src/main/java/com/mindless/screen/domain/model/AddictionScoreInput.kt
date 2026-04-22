package com.mindless.screen.domain.model

data class AddictionScoreInput(
    val screenScore: Int,
    val socialScore: Int,
    val unlockScore: Int,
    val nightScore: Int,
    val gamingScore: Int,
    val sessionScore: Int
)
