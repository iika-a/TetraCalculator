import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.FileNotFoundException
import javax.swing.*

class DisplayPanel : JPanel(GridBagLayout()) {
    private val client = OkHttpClient()
    private val nameField = JTextField("", 16).apply {
        horizontalAlignment = JTextField.CENTER
        font = font.deriveFont(20f)
        preferredSize = Dimension(10, 35)
        addFocusListener(object : java.awt.event.FocusAdapter() {
            override fun focusLost(e: FocusEvent) {
                getStats(text.lowercase())
            }
        })
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
    private val player = TetraPlayer("", -1.0, -1.0, -1.0, -1, -1.0)

    init {
        background = Color(0x44484A)

        isFocusable = true
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                requestFocusInWindow()
            }
        })

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
            this.requestFocusInWindow()
        }

        doInitialLoad()
    }

    private fun setLabelSettings(label: JLabel) {
        label.font = Font("Dubai", 0, 20)
        label.horizontalAlignment = JLabel.CENTER
        label.verticalAlignment = JLabel.CENTER
        label.foreground = Color(0xBBBBBB)
        label.preferredSize = Dimension(500, 25)
    }

    private fun getStats(name: String) {
        updateName(name)

        try {
            // league stats
            val leagueData = fetchTetrioJson("https://ch.tetr.io/api/users/$name/summaries/league")

            updateTR(leagueData.getDouble("tr"))
            updateGlicko(leagueData.getDouble("glicko"), leagueData.getDouble("rd"))
            updateWins(leagueData.getInt("gameswon"))

            val gameData = fetchTetrioJson("https://ch.tetr.io/api/users/$name/records/league/recent?limit=1")

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

            imageLabel.icon = TetraCalculatorHelper.getAvatar(name, 250)

            try {
                val player1before = player1Stats.getJSONObject(0).getDouble("glicko")
                val player1rd = player1Stats.getJSONObject(0).getDouble("rd")

                val player2before = player2Stats.getJSONObject(0).getDouble("glicko")
                val player2rd = player2Stats.getJSONObject(0).getDouble("rd")

                // determine if player1 won (based on id match)
                val result = firstEntry.getJSONObject("extras").getString("result")
                val winnerId = if (result == "dqvictory" || result == "victory") player1Id else player2Id
                val win = if (winnerId == player1Id) 1.0 else 0.0

                updateSigma(TetraRating.estimateSigmaAfterMatch(player1before, player1rd, player2before, player2rd, win))
                TetraCalculatorHelper.inaccurate = false
            } catch(e: Exception) {
                updateSigma(0.06)
                TetraCalculatorHelper.inaccurate = true
            }

        } catch (e: Exception) {
            updateTR(-1.0)
            updateGlicko(-1.0, -1.0)
            updateWins(-1)
            updateSigma(-1.0)
            imageLabel.icon = TetraCalculatorHelper.getAvatar(name, 250)
        }
    }

    private fun fetchTetrioJson(url: String): JSONObject {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .header("Referer", "https://tetr.io/")
            .build()

        val response = client.newCall(request).execute()
        val jsonText = response.body?.string() ?: throw FileNotFoundException()
        return JSONObject(jsonText).getJSONObject("data")
    }

    private fun doInitialLoad() {
        updateTR(-1.0)
        updateGlicko(-1.0, -1.0)
        updateWins(-1)
        updateSigma(-1.0)
        imageLabel.icon = TetraCalculatorHelper.getAvatar("", 250)
    }

    private fun updateName(newName: String) {
        player.name = newName
    }

    private fun updateTR(newTR: Double) {
        player.tr = newTR
        tr.text = "TR: ${"%.2f".format(newTR)}"
        tr.toolTipText = newTR.toString()
    }

    private fun updateGlicko(newGlicko: Double, newRD: Double) {
        player.glicko = newGlicko
        player.rd = newRD
        glicko.text = "Glicko: ${"%.2f".format(newGlicko)} ± ${"%.2f".format(newRD)}"
        glicko.toolTipText = "$newGlicko ± $newRD"
    }

    private fun updateWins(newWins: Int) {
        player.wins = newWins
        wins.text = "Wins: $newWins"
        wins.toolTipText = "$newWins Wins"
    }

    private fun updateSigma(newSigma: Double) {
        player.sigma = newSigma
        sigma.text = "Volatility: ${TetraCalculatorHelper.getErrorText(newSigma, 0.06)}${"%.2f".format(newSigma)}"
        sigma.toolTipText = newSigma.toString()
    }

    fun refresh() { getStats(player.name) }
    fun getPlayer() = player
}