/**
 *
 * From CS3
 */
package com.flixclusive.feature.mobile.provider.settings.util

import com.flixclusive.core.util.exception.safeCall

private val flagEmojiRegex = Regex("[\uD83C\uDDE6-\uD83C\uDDFF]{2}")
internal fun getFlagFromLanguageCode(code: String?): String? {
    if (code?.equals("multiple", true) == true)
        return "\uD83C\uDF10"

    if (code.isNullOrBlank() || code.length < 2)
        return "❓"

    return safeCall {
        val flagEmoji = countryCodeToEmojiFlag(flags[code])
            ?: countryCodeToEmojiFlag(code.uppercase())
            ?: return "❓"

        return if (flagEmojiRegex.matches(flagEmoji)) flagEmoji else "❓"
    }
}

private fun countryCodeToEmojiFlag(flagAscii: String?): String? {
    if (flagAscii.isNullOrBlank() || flagAscii.length < 2) return null

    return safeCall {
        val offset = 0x1F1E6 - 0x41
        val firstChar: Int = Character.codePointAt(flagAscii, 0) + offset
        val secondChar: Int = Character.codePointAt(flagAscii, 1) + offset

        (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))
    }
}

private val flags = mapOf(
    "af" to "ZA",
    "agq" to "CM",
    "ajp" to "SY",
    "ak" to "GH",
    "am" to "ET",
    "ar" to "AE",
    "ars" to "SA",
    "as" to "IN",
    "asa" to "TZ",
    "az" to "AZ",
    "bas" to "CM",
    "be" to "BY",
    "bem" to "ZM",
    "bez" to "IT",
    "bg" to "BG",
    "bm" to "ML",
    "bn" to "BD",
    "bo" to "CN",
    "br" to "FR",
    "brx" to "IN",
    "bs" to "BA",
    "ca" to "ES",
    "cgg" to "UG",
    "chr" to "US",
    "cs" to "CZ",
    "cy" to "GB",
    "da" to "DK",
    "dav" to "KE",
    "de" to "DE",
    "dje" to "NE",
    "dua" to "CM",
    "dyo" to "SN",
    "ebu" to "KE",
    "ee" to "GH",
    "en" to "GB",
    "el" to "GR",
    "es" to "ES",
    "et" to "EE",
    "eu" to "ES",
    "ewo" to "CM",
    "fa" to "IR",
    "fil" to "PH",
    "fr" to "FR",
    "ga" to "IE",
    "gl" to "ES",
    "gsw" to "CH",
    "gu" to "IN",
    "guz" to "KE",
    "gv" to "GB",
    "ha" to "NG",
    "haw" to "US",
    "he" to "IL",
    "hi" to "IN",
    "ff" to "CN",
    "fi" to "FI",
    "fo" to "FO",
    "hr" to "HR",
    "hu" to "HU",
    "hy" to "AM",
    "id" to "ID",
    "ig" to "NG",
    "ii" to "CN",
    "is" to "IS",
    "it" to "IT",
    "ita" to "IT",
    "ja" to "JP",
    "jmc" to "TZ",
    "ka" to "GE",
    "kab" to "DZ",
    "ki" to "KE",
    "kam" to "KE",
    "mer" to "KE",
    "kde" to "TZ",
    "kea" to "CV",
    "khq" to "ML",
    "kk" to "KZ",
    "kl" to "GL",
    "kln" to "KE",
    "km" to "KH",
    "kn" to "IN",
    "ko" to "KR",
    "kok" to "IN",
    "ksb" to "TZ",
    "ksf" to "CM",
    "kw" to "GB",
    "lag" to "TZ",
    "lg" to "UG",
    "ln" to "CG",
    "lt" to "LT",
    "lu" to "CD",
    "lv" to "LV",
    "lat" to "LV",
    "luo" to "KE",
    "luy" to "KE",
    "mas" to "TZ",
    "mfe" to "MU",
    "mg" to "MG",
    "mgh" to "MZ",
    "ml" to "IN",
    "mk" to "MK",
    "mr" to "IN",
    "ms" to "MY",
    "mt" to "MT",
    "mua" to "CM",
    "my" to "MM",
    "naq" to "NA",
    "nb" to "NO",
    "no" to "NO",
    "nn" to "NO",
    "nd" to "ZW",
    "ne" to "NP",
    "nl" to "NL",
    "nmg" to "CM",
    "nus" to "SD",
    "nyn" to "UG",
    "om" to "ET",
    "or" to "IN",
    "pa" to "PK",
    "pl" to "PL",
    "ps" to "AF",
    "pt" to "PT",
    "pt-pt" to "PT",
    "pt-br" to "BR",
    "rm" to "CH",
    "rn" to "BI",
    "ro" to "RO",
    "ru" to "RU",
    "rw" to "RW",
    "rof" to "TZ",
    "rwk" to "TZ",
    "saq" to "KE",
    "sbp" to "TZ",
    "seh" to "MZ",
    "ses" to "ML",
    "sg" to "CF",
    "shi" to "MA",
    "si" to "LK",
    "sk" to "SK",
    "sl" to "SI",
    "sn" to "ZW",
    "so" to "SO",
    "sq" to "AL",
    "sr" to "RS",
    "sv" to "SE",
    "sw" to "TZ",
    "swc" to "CD",
    "ta" to "IN",
    "te" to "IN",
    "teo" to "UG",
    "th" to "TH",
    "ti" to "ET",
    "to" to "TO",
    "tr" to "TR",
    "twq" to "NE",
    "tzm" to "MA",
    "uk" to "UA",
    "ur" to "PK",
    "uz" to "UZ",
    "vai" to "LR",
    "vi" to "VN",
    "vun" to "TZ",
    "xog" to "UG",
    "yav" to "CM",
    "yo" to "NG",
    "zh" to "CN",
    "zu" to "ZA",
    "tl" to "PH",
)