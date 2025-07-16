import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor


fun generateProjectCode(
    projectName: String,
    creationInstant: Instant = Instant.now()
): String {
    // 1) Basis-String: Name + ISO-Zeit
    val timestamp = creationInstant
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ISO_INSTANT)
    val base = projectName.trim() + "|" + timestamp

    // 2) SHA-256-Hash
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(base.toByteArray(Charsets.UTF_8))

    // 3) Erste 8 Bytes → Long (positiv)
    var longHash = 0L
    for (i in 0 until 8) {
        longHash = (longHash shl 8) or (hashBytes[i].toLong() and 0xFF)
    }
    if (longHash < 0) longHash = -longHash

    // 4) Base36-String (uppercase)
    var codePart = longHash.toString(36).uppercase(Locale.getDefault())

    // 5) Auf genau 4 Zeichen bringen
    codePart = when {
        codePart.length < 4 -> codePart.padStart(4, '0')
        codePart.length > 4 -> codePart.take(4)
        else                 -> codePart
    }

    // 6) fertiger Code
    return "#$codePart"
}



/**
 * Wandelt einen Base‑36‑Code "#XXXX" in eine Compose‑Color um.
 * – Hue aus dem Zahlenwert modulo 360
 * – feste Sättigung & Helligkeit für gute Lesbarkeit
 */
fun colorFromCode(code: String): Color {
    // 1) "#1A2B" → "1A2B"
    val clean = code.removePrefix("#")
    // 2) Parse als Base36‑Long (maximal: 36⁴–1)
    val value = clean.toLongOrNull(36) ?: 0L
    // 3) Hue [0,360)
    val hue = (value % 360).toFloat()
    // 4) Wähle S und V (Sättigung, Helligkeit) so, dass die Farbe nicht zu dunkel oder grell ist
    val saturation = 0.5f    // 50% gesättigt
    val valueBright = 0.85f  // 85% hell
    // 5) Android‑Hilfsfunktion wandelt HSV → ARGB‑Int
    val hsv = floatArrayOf(hue, saturation, valueBright)
    val colorInt = AndroidColor.HSVToColor(hsv)
    // 6) Wrap in Compose Color
    return Color(colorInt)
}
