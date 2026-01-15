package io.pexkit.api.request


/**
 * Filters for photo search.
 *
 * @property orientation Filter by image orientation.
 * @property size Filter by minimum image size.
 * @property color Filter by dominant color (predefined or hex code without #).
 * @property locale Locale for search query interpretation.
 */
public data class PhotoFilters(
    val orientation: Orientation? = null,
    val size: Size? = null,
    val color: String? = null,
    val locale: Locale? = null,
) {
    public companion object {
        /**
         * Creates filters with a predefined color.
         */
        public fun withColor(color: Color): PhotoFilters = PhotoFilters(color = color.value)

        /**
         * Creates filters with a hex color code.
         *
         * @param hexColor Hex color code without the # prefix (e.g., "FF5733").
         */
        public fun withHexColor(hexColor: String): PhotoFilters {
            require(hexColor.matches(Regex("^[0-9A-Fa-f]{6}$"))) {
                "hexColor must be a valid 6-digit hexadecimal string"
            }
            return PhotoFilters(color = hexColor)
        }
    }
}
