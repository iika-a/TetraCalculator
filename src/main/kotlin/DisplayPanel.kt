import org.json.JSONObject
import org.jsoup.Jsoup
import java.awt.*
import java.io.FileNotFoundException
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.*

class DisplayPanel : JPanel(GridBagLayout()) {
    private val nameField = JTextField("iika", 15).apply {
        horizontalAlignment = JTextField.CENTER
        font = font.deriveFont(20f)
        preferredSize = Dimension(10, 35)
    }

    private val imageLabel = JLabel().apply {
        setLabelSettings(this)
        border = BorderFactory.createLineBorder(Color(0x313335), 3)
        preferredSize = Dimension(250, 250)
    }
    private val tr = JLabel("TR").apply { setLabelSettings(this) }
    private val glicko = JLabel("GLICKO±RD").apply { setLabelSettings(this) }
    private val wins = JLabel("WINS").apply { setLabelSettings(this) }
    private val sigma = JLabel("VOLATILITY").apply { setLabelSettings(this) }

    init {
        background = Color(0x3d4042)
        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = GridBagConstraints.RELATIVE
            anchor = GridBagConstraints.CENTER
            fill = GridBagConstraints.NONE
            insets.set(10, 10, 10, 10)
            weightx = 1.0
            weighty = 1.0
        }

        add(nameField, constraints)
        add(imageLabel, constraints)
        add(tr, constraints)
        add(glicko, constraints)
        add(wins, constraints)
        add(sigma, constraints)

        nameField.addActionListener {
            val name = nameField.text.lowercase()
            getStats(name)
        }

        // do initial load
        getStats(nameField.text.lowercase())
    }

    private fun setLabelSettings(label: JLabel) {
        label.font = Font("Dubai", 0, 20)
        label.horizontalAlignment = JLabel.CENTER
        label.verticalAlignment = JLabel.CENTER
        label.foreground = Color(0xBBBBBB)
        label.preferredSize = Dimension(500, 25)
    }

    private fun getStats(name: String) {
        try {
            // league stats
            val leagueData = JSONObject(
                Jsoup.connect("https://ch.tetr.io/api/users/$name/summaries/league")
                    .ignoreContentType(true)
                    .execute()
                    .body()
            ).getJSONObject("data")

            tr.text = "TR: ${leagueData.getDouble("tr")}"
            glicko.text = "Glicko: ${leagueData.getDouble("glicko")} ± ${leagueData.getDouble("rd")}"
            wins.text = "Wins: ${leagueData.getInt("gameswon")}"

            val gameData = JSONObject(
                Jsoup.connect("https://ch.tetr.io/api/users/$name/records/league/recent?limit=5")
                    .ignoreContentType(true)
                    .execute()
                    .body()
            ).getJSONObject("data")

            val entries = gameData.getJSONArray("entries")
            val firstEntry = entries.getJSONObject(0)

            val leaderboard = firstEntry.getJSONObject("results").getJSONArray("leaderboard")

            // get usernames and ids
            val player1Obj = leaderboard.getJSONObject(0)
            val player2Obj = leaderboard.getJSONObject(1)


            val player1Id = player1Obj.getString("id")
            val player2Id = player2Obj.getString("id")

            val league = firstEntry.getJSONObject("extras").getJSONObject("league")

            val player1Stats = league.getJSONArray(player1Id)
            val player2Stats = league.getJSONArray(player2Id)

            val sigmaValue: Double

            imageLabel.icon = getAvatar(name)

            try {
                val player1before = player1Stats.getJSONObject(0).getDouble("glicko")
                val player1rd = player1Stats.getJSONObject(0).getDouble("rd")

                val player2before = player2Stats.getJSONObject(0).getDouble("glicko")
                val player2rd = player2Stats.getJSONObject(0).getDouble("rd")

                // determine if player1 won (based on id match)
                val result = firstEntry.getJSONObject("extras").getString("result")
                val winnerId = if (result == "dqvictory" || result == "victory") player1Id else player2Id
                val win = if (winnerId == player1Id) 1.0 else 0.0

                sigmaValue = TetraRating.estimateSigmaAfterMatch(player1before, player1rd, player2before, player2rd, win)
                sigma.text = "Volatility: $sigmaValue"
            } catch(e: Exception) {
                sigma.text = "Volatility: 0.06"
            }

        } catch (e: Exception) {
            tr.text = "TR: -1.0"
            glicko.text = "Glicko: -1.0 ± -1.0"
            wins.text = "Wins: -1.0"
            sigma.text = "Volatility: -1.0"
            imageLabel.icon = getAvatar(name)
        }
    }

    private fun getAvatar(name: String): ImageIcon {
        try {
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
                if (avatarRevision == 0.toLong()) throw FileNotFoundException()
                avatarUrl = "https://tetr.io/user-content/avatars/$userId.jpg?v=$avatarRevision"
            } catch (e: Exception) {
                avatarUrl = "https://files.catbox.moe/wjbfg5.png"
            }


            val image = ImageIO.read(URI.create(avatarUrl).toURL())
            val scaled = image.getScaledInstance(250, 250, Image.SCALE_SMOOTH)
            return ImageIcon(scaled)
        } catch(e: Exception) {
            val image = ImageIO.read(URI.create("https://files.catbox.moe/3dpdh6.png").toURL())
            val scaled = image.getScaledInstance(250, 250, Image.SCALE_SMOOTH)
            return ImageIcon(scaled)
        }
    }

    fun getPlayerName() = nameField.text.lowercase()
    @Suppress("unused")
    fun getTR() = tr.text.substring(4).toDouble()
    fun getGlicko() = glicko.text.substring(8).split(" ± ")[0].toDouble()
    fun getRD() = glicko.text.substring(8).split(" ± ")[1].toDouble()
    fun getWins() = wins.text.substring(6).toInt()
    fun getSigma() = sigma.text.substring(12).toDouble()
}
