import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.awt.Image
import java.io.FileNotFoundException
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import kotlin.math.abs

object TetraCalculatorHelper {
    private val client = OkHttpClient()

    fun getErrorText(value: Double, compareTo: Double): String {
        return if (abs(value - compareTo) < 0.001)
            if (value < compareTo) "Slightly less than "
            else if (value > compareTo) "Slightly more than "
            else ""
        else ""
    }

    fun setIcons(frame: JFrame) {
        val icons = listOf(
            ImageIO.read(ClassLoader.getSystemResourceAsStream("icon16.png")),
            ImageIO.read(ClassLoader.getSystemResourceAsStream("icon32.png")),
            ImageIO.read(ClassLoader.getSystemResourceAsStream("icon48.png"))
        )
        frame.iconImages = icons
    }

    fun getAvatar(name: String, sideLength: Int): ImageIcon {
        try {
            if (name.isEmpty()) throw FileNotFoundException()

            // step 1: get user info from tetrio api
            val request = Request.Builder()
                .url("https://ch.tetr.io/api/users/$name")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Referer", "https://tetr.io/")
                .build()

            val response = client.newCall(request).execute()
            val jsonText = response.body?.string() ?: throw FileNotFoundException()
            val userInfo = JSONObject(jsonText).getJSONObject("data")
            val userId = userInfo.getString("_id")

            // step 2: build avatar url or fallback
            val avatarUrl = try {
                val avatarRevision = userInfo.getLong("avatar_revision")
                if (avatarRevision == 0L) throw FileNotFoundException()
                "https://tetr.io/user-content/avatars/$userId.jpg?v=$avatarRevision"
            } catch (_: Exception) {
                "https://files.catbox.moe/wjbfg5.png"
            }

            // step 3: load and scale avatar image
            val avatarRequest = Request.Builder()
                .url(avatarUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .header("Referer", "https://tetr.io/")
                .build()

            val avatarResponse = client.newCall(avatarRequest).execute()
            val avatarStream = avatarResponse.body?.byteStream() ?: throw FileNotFoundException()
            val image = ImageIO.read(avatarStream)
            val scaled = image.getScaledInstance(sideLength, sideLength, Image.SCALE_SMOOTH)

            return ImageIcon(scaled)

        } catch (e: Exception) {
            // fallback image
            val fallback = ImageIO.read(URI.create("https://files.catbox.moe/3dpdh6.png").toURL())
            val scaled = fallback.getScaledInstance(sideLength, sideLength, Image.SCALE_SMOOTH)
            return ImageIcon(scaled)
        }
    }
}