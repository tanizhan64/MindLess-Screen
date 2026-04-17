package com.mindless.screen.domain.model

data class AddictionScoreInput(
    val screen: Int,
    val social: Int,
    val unlock: Int,
    val night: Int,
    val gaming: Int,
    val session: Int
)
