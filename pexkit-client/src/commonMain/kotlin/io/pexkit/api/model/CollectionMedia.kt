package io.pexkit.api.model

/**
 * Represents a media item within a collection.
 *
 * A collection can contain both photos and videos. Use [asPhoto] or [asVideo]
 * to access type-specific properties, or pattern match on [type].
 */
public sealed interface CollectionMedia {
    /** Unique identifier. */
    public val id: Long
    /** Media width in pixels. */
    public val width: Int
    /** Media height in pixels. */
    public val height: Int
    /** Pexels page URL. */
    public val url: String
    /** The type of media. */
    public val type: Type

    /**
     * A photo in a collection.
     */
    public data class PhotoMedia(
        override val id: Long,
        override val width: Int,
        override val height: Int,
        override val url: String,
        val photographer: String,
        val photographerUrl: String,
        val photographerId: Long,
        val avgColor: String,
        val src: PhotoSource,
        val alt: String,
        val liked: Boolean,
    ) : CollectionMedia {
        override val type: Type = Type.PHOTO
    }

    /**
     * A video in a collection.
     */
    public data class VideoMedia(
        override val id: Long,
        override val width: Int,
        override val height: Int,
        override val url: String,
        val image: String,
        val fullRes: String?,
        val tags: List<String>,
        val duration: Int,
        val user: User,
        val videoFiles: List<VideoFile>,
        val videoPictures: List<VideoPicture>,
    ) : CollectionMedia {
        override val type: Type = Type.VIDEO
    }

    /**
     * An unknown media type in a collection.
     *
     * This is returned when the API returns a media type that is not recognized.
     * This prevents data loss when the API introduces new media types.
     *
     * @property originalType The original type string from the API.
     */
    public data class Unknown(
        override val id: Long,
        override val width: Int,
        override val height: Int,
        override val url: String,
        val originalType: String,
    ) : CollectionMedia {
        override val type: Type = Type.UNKNOWN
    }

    /**
     * Media type enum.
     */
    public enum class Type {
        PHOTO,
        VIDEO,
        UNKNOWN,
    }
}

/**
 * Returns this media as a [CollectionMedia.PhotoMedia] if it is a photo, null otherwise.
 */
public fun CollectionMedia.asPhoto(): CollectionMedia.PhotoMedia? = this as? CollectionMedia.PhotoMedia

/**
 * Returns this media as a [CollectionMedia.VideoMedia] if it is a video, null otherwise.
 */
public fun CollectionMedia.asVideo(): CollectionMedia.VideoMedia? = this as? CollectionMedia.VideoMedia

/**
 * Returns this media as a [CollectionMedia.Unknown] if it is an unknown type, null otherwise.
 */
public fun CollectionMedia.asUnknown(): CollectionMedia.Unknown? = this as? CollectionMedia.Unknown
