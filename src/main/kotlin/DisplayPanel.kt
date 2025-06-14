import org.json.JSONObject
import org.jsoup.Jsoup
import java.awt.*
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.*

class DisplayPanel : JPanel(GridBagLayout()) {
    private val nameField = JTextField("iika", 15)
    private val nameLabel = JLabel("iika").apply { setLabelSettings(this) }

    private val imageLabel = JLabel().apply { setLabelSettings(this) }
    private val tr = JLabel("TR").apply { setLabelSettings(this) }
    private val glicko = JLabel("GLICKO±RD").apply { setLabelSettings(this) }
    private val wins = JLabel("WINS").apply { setLabelSettings(this) }

    init {
        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = GridBagConstraints.RELATIVE
            insets.set(10, 10, 10, 10)
            fill = GridBagConstraints.BOTH
        }

        add(nameField, constraints)
        add(nameLabel, constraints)
        add(imageLabel, constraints)
        add(tr, constraints)
        add(glicko, constraints)
        add(wins, constraints)

        nameField.addActionListener {
            val name = nameField.text.trim()
            nameLabel.text = name
            getStats(name)
        }

        // Do initial load
        getStats(nameField.text.trim())
    }

    private fun setLabelSettings(label: JLabel) {
        label.font = label.font.deriveFont(20f)
        label.horizontalAlignment = JLabel.CENTER
        label.verticalAlignment = JLabel.CENTER
    }

    private fun getStats(name: String) {
        try {
            // League stats
            val leagueData = JSONObject(
                Jsoup.connect("https://ch.tetr.io/api/users/$name/summaries/league")
                    .ignoreContentType(true)
                    .execute()
                    .body()
            ).getJSONObject("data")

            tr.text = "TR: ${leagueData.getDouble("tr")}"
            glicko.text = "GLICKO: ${leagueData.getDouble("glicko")} ± ${leagueData.getDouble("rd")}"
            wins.text = "WINS: ${leagueData.getInt("gameswon")}"

            // Avatar
            val userInfo = JSONObject(
                Jsoup.connect("https://ch.tetr.io/api/users/$name")
                    .ignoreContentType(true)
                    .execute()
                    .body()
            ).getJSONObject("data")

            val userId = userInfo.getString("_id")
            var avatarUrl: String
            try {
                val avatarRevision = userInfo.getLong("avatar_revision")
                avatarUrl = "https://tetr.io/user-content/avatars/$userId.jpg?v=$avatarRevision"
            } catch (e: Exception) {
                avatarUrl = "https://files.catbox.moe/w43hs8.png"
            }


            val image = ImageIO.read(URI.create(avatarUrl).toURL())
            val scaled = image.getScaledInstance(250, 250, Image.SCALE_SMOOTH)
            imageLabel.icon = ImageIcon(scaled)

        } catch (e: Exception) {
            e.printStackTrace()
            tr.text = "TR: ?"
            glicko.text = "GLICKO: ?"
            wins.text = "WINS: ?"
            imageLabel.icon = null
        }
    }

    fun getPlayerName() = nameLabel.text!!
    @Suppress("unused")
    fun getTR() = tr.text.substring(4).toDouble()
    fun getGlicko() = glicko.text.substring(8).split(" ± ")[0].toDouble()
    fun getRD() = glicko.text.substring(8).split(" ± ")[1].toDouble()
    fun getWins() = wins.text.substring(6).toInt()
}
