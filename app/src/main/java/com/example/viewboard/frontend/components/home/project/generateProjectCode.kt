import java.security.MessageDigest
import java.util.Locale
import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

/**
 * Generates a short project code from a Firestore document ID.
 * @param dbId The Firestore document ID.
 * @return A 5‑character code string starting with '#'.
 */
fun generateProjectCodeFromDbId(dbId: String): String {
    val base = dbId.trim()

    // SHA-256-Hash for ID
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(base.toByteArray(Charsets.UTF_8))

    var longHash = 0L
    for (i in 0 until 8) {
        longHash = (longHash shl 8) or (hashBytes[i].toLong() and 0xFF)
    }
    if (longHash < 0) longHash = -longHash

    var codePart = longHash.toString(36).uppercase(Locale.getDefault())

    codePart = when {
        codePart.length < 4 -> codePart.padStart(4, '0')
        codePart.length > 4 -> codePart.take(4)
        else -> codePart
    }

    return "#$codePart"
}

/**
 * Converts a code string (e.g. "#1A2B") into a Color.
 * @param code A string starting with '#' followed by base‑36 digits.
 * @return A Compose [Color] generated from the code.
 */
fun colorFromCode(code: String): Color {
    val clean = code.removePrefix("#")
    val value = clean.toLongOrNull(36) ?: 0L
    val hue = (value % 360).toFloat()
    val saturation = 0.5f
    val valueBright = 0.85f
    val hsv = floatArrayOf(hue, saturation, valueBright)
    val colorInt = AndroidColor.HSVToColor(hsv)
    return Color(colorInt)
}

/**
 * Derives a Color from a user's email address.
 * @param email The user's email address.
 * @return A Compose [Color] based on the email.
 */
fun colorFromEmail(email: String): Color {
    val localPart = email.substringBefore('@', email)
    val codeFragment = localPart
        .filter { it.isLetterOrDigit() }
        .padEnd(4, '0')
        .take(4)
        .uppercase()
    return colorFromCode("#$codeFragment")
}
