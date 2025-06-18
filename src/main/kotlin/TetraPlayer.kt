import org.json.JSONObject
import org.jsoup.Jsoup
import java.awt.Image
import java.io.FileNotFoundException
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.ImageIcon

data class TetraPlayer(var name: String, var tr: Double, var glicko: Double, var rd: Double, var wins: Int, var sigma: Double) {
    companion object {
        fun getAvatar(name: String, sideLength: Int): ImageIcon {
            try {
                if (name.isEmpty()) throw FileNotFoundException()

                val userInfo = JSONObject(
                    Jsoup.connect("https://ch.tetr.io/api/users/$name")
                        .ignoreContentType(true)
                        .execute()
                        .body()
                ).getJSONObject("data")

                val userId = userInfo.getString("_id")

                val avatarUrl = try {
                    val avatarRevision = userInfo.getLong("avatar_revision")
                    if (avatarRevision == 0L) throw FileNotFoundException()
                    "https://tetr.io/user-content/avatars/$userId.jpg?v=$avatarRevision"
                } catch (e: Exception) {
                    "https://files.catbox.moe/wjbfg5.png"
                }

                val url = URI.create(avatarUrl).toURL()
                val connection = url.openConnection().apply {
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    setRequestProperty("Referer", "https://tetr.io/")
                }

                connection.connect()
                val image = ImageIO.read(connection.getInputStream())
                val scaled = image.getScaledInstance(sideLength, sideLength, Image.SCALE_SMOOTH)
                return ImageIcon(scaled)

            } catch (e: Exception) {
                val fallback = ImageIO.read(URI.create("https://files.catbox.moe/3dpdh6.png").toURL())
                val scaled = fallback.getScaledInstance(sideLength, sideLength, Image.SCALE_SMOOTH)
                return ImageIcon(scaled)
            }
        }
    }
}