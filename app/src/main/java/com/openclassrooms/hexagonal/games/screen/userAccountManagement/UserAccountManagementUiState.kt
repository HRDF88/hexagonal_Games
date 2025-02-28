package com.openclassrooms.hexagonal.games.screen.userAccountManagement

data class UserAccountManagementUiState (
    val error : Int? = null,
    val loading : Boolean = false,
    val state : Boolean = false

)