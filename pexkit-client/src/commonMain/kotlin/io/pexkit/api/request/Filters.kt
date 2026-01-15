package io.pexkit.api.request

public enum class Orientation(internal val value: String) {
    LANDSCAPE("landscape"),
    PORTRAIT("portrait"),
    SQUARE("square"),
}


public enum class Size(internal val value: String) {
    /** Large: 24MP */
    LARGE("large"),
    /** Medium: 12MP */
    MEDIUM("medium"),
    /** Small: 4MP */
    SMALL("small"),
}


public enum class Color(internal val value: String) {
    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    TURQUOISE("turquoise"),
    BLUE("blue"),
    VIOLET("violet"),
    PINK("pink"),
    BROWN("brown"),
    BLACK("black"),
    GRAY("gray"),
    WHITE("white"),
}


public enum class Locale(internal val value: String) {
    EN_US("en-US"),
    PT_BR("pt-BR"),
    ES_ES("es-ES"),
    CA_ES("ca-ES"),
    DE_DE("de-DE"),
    IT_IT("it-IT"),
    FR_FR("fr-FR"),
    SV_SE("sv-SE"),
    ID_ID("id-ID"),
    PL_PL("pl-PL"),
    JA_JP("ja-JP"),
    ZH_TW("zh-TW"),
    ZH_CN("zh-CN"),
    KO_KR("ko-KR"),
    TH_TH("th-TH"),
    NL_NL("nl-NL"),
    HU_HU("hu-HU"),
    VI_VN("vi-VN"),
    CS_CZ("cs-CZ"),
    DA_DK("da-DK"),
    FI_FI("fi-FI"),
    UK_UA("uk-UA"),
    EL_GR("el-GR"),
    RO_RO("ro-RO"),
    NB_NO("nb-NO"),
    SK_SK("sk-SK"),
    TR_TR("tr-TR"),
    RU_RU("ru-RU"),
}

/**
 * Media type filter for collection endpoints.
 */
public enum class MediaType(internal val value: String) {
    PHOTOS("photos"),
    VIDEOS("videos"),
}