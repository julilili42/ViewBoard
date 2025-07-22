import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor


/**
 * Generiert einen vierstelligen Base36‑Code aus Projektname und Datum‑String.
 *
 * @param projectName der Name des Projekts
 * @param creationDate ein ISO‑Datum im Format "YYYY-MM-DD"
 * @return Code wie "#1A2B"
 */
/**
 * Generiert aus einer Datenbank-ID einen kurzen, eindeutigen Projektcode.
 *
 * @param dbId Die Datenbank-ID (z. B. Firebase-Document-ID).
 * @return Ein 4‑stelliges Base36‑Token, immer mit „#“ vorangestellt.
 */
fun generateProjectCodeFromDbId(dbId: String): String {
    // 1) Basis-String: ID (ohne weitere Zusätze)
    val base = dbId.trim()

    // 2) SHA-256-Hash über die ID
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(base.toByteArray(Charsets.UTF_8))

    // 3) Aus den ersten 8 Bytes einen positiven Long bauen
    var longHash = 0L
    for (i in 0 until 8) {
        longHash = (longHash shl 8) or (hashBytes[i].toLong() and 0xFF)
    }
    if (longHash < 0) longHash = -longHash

    // 4) In Base36 umwandeln und uppercase
    var codePart = longHash.toString(36).uppercase(Locale.getDefault())

    // 5) Auf genau 4 Zeichen bringen: pad oder cut
    codePart = when {
        codePart.length < 4 -> codePart.padStart(4, '0')
        codePart.length > 4 -> codePart.take(4)
        else                 -> codePart
    }

    // 6) Fertigen Code mit Präfix zurückgeben
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

fun colorFromEmail(email: String): Color {
    // Local‑Part extrahieren (alles vor '@'), oder fallback auf die ganze Adresse
    val localPart = email.substringBefore('@', email)
    // Hex‑Prefix dran hängen, damit colorFromCode sauber läuft („1A2B“ → „#1A2B“)
    // Hier nutzen wir einfach die ersten 4 Zeichen als Code, oder padded, falls kürzer
    val codeFragment = localPart
        .filter { it.isLetterOrDigit() }
        .padEnd(4, '0')
        .take(4)
        .uppercase()
    return colorFromCode("#$codeFragment")
}
