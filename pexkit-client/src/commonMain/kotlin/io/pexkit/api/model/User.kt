package io.pexkit.api.model

import kotlinx.serialization.Serializable

/**
 * Represents a user (photographer/videographer) on Pexels.
 *
 * @property id Unique identifier for the user.
 * @property name Display name of the user.
 * @property url Pexels profile URL.
 */
@Serializable
public data class User(
    val id: Long,
    val name: String,
    val url: String,
)
