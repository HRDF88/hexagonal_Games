package com.openclassrooms.hexagonal.games.domain.model

import java.io.Serializable

/**
 * Data class representing a comment in the system.
 * This class is used to model a comment on a post, including the author's name, the content of the comment,
 * and the timestamp of when it was created.
 *
 * @param id The unique identifier of the comment.
 * @param comment The content of the comment.
 * @param author The name of the author who wrote the comment.
 * @param timestamp The timestamp of when the comment was created, represented as a `Long`.
 */
data class Comment(
    val id: String,
    val comment: String,
    val author: String,
    val timestamp: Long,
) : Serializable {


    /**
     * Default constructor required for deserialization.
     * Initializes the properties to empty or default values.
     */
    constructor() : this("", "", "", 0)
}
