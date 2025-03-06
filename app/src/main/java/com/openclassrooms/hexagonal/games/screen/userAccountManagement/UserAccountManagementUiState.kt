package com.openclassrooms.hexagonal.games.screen.userAccountManagement

/**
 * A data class representing the UI state for user account management operations.
 * This class holds the state information that reflects the current status of user account actions
 * such as logging out and deleting the user account.
 *
 * @param error Optional error resource ID. Holds an error message resource ID if an error occurs during an operation.
 * @param loading Boolean indicating whether an operation is in progress (e.g., loading state).
 * @param state Boolean representing the state of the user account. It can be used to track if the user is logged in or not,
 *              or to track the success state of operations like account deletion.
 */
data class UserAccountManagementUiState(
    val error: Int? = null,
    val loading: Boolean = false,
    val state: Boolean = false

)