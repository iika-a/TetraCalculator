import org.json.JSONObject
import org.jsoup.Jsoup
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class DisplayPanel : JPanel(GridBagLayout()) {
    private val nameField = JTextField("iika", 15)
    private val nameLabel = JLabel("iika").apply { setLabelSettings(this) }

    private val imageLabel = JLabel().apply { setLabelSettings(this) }
    private val tr = JLabel("TR").apply { setLabelSettings(this) }
    private val glicko = JLabel("GLICKO±RD").apply { setLabelSettings(this) }
    private val wins = JLabel("WINS").apply { setLabelSettings(this) }

    // Trigger stat update on Enter press
    private var updateTimer: Timer? = null

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

        nameField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = scheduleUpdate()
            override fun removeUpdate(e: DocumentEvent?) = scheduleUpdate()
            override fun changedUpdate(e: DocumentEvent?) = scheduleUpdate()

            fun scheduleUpdate() {
                updateTimer?.stop()
                updateTimer = Timer(500) {
                    val name = nameField.text.trim()
                    nameLabel.text = name
                    getStats(name)
                }.apply { isRepeats = false; start() }
            }
        })

        // Do initial load
        println(nameField.text.trim())
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
            val avatarRevision = userInfo.getLong("avatar_revision")
            val avatarUrl = "https://tetr.io/user-content/avatars/$userId.jpg?v=$avatarRevision"
            println("Avatar URL: $avatarUrl")

            val image = ImageIO.read(URI.create(avatarUrl).toURL())
            val scaled = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH)
            imageLabel.icon = ImageIcon(scaled)

        } catch (e: Exception) {
            e.printStackTrace()
            tr.text = "TR: ?"
            glicko.text = "GLICKO: ?"
            wins.text = "WINS: ?"
            imageLabel.icon = null
        }
    }
}
