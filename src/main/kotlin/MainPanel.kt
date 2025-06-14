import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class MainPanel(private val leftPanel: DisplayPanel, private val rightPanel: DisplayPanel): JPanel(BorderLayout()) {
    private val calculateButton = JButton("Calculate!")
    private val resultLabel = JLabel().apply {
        font = font.deriveFont(18f)
        horizontalAlignment = JLabel.CENTER
        verticalAlignment = JLabel.CENTER
    }

    init {
        this.add(leftPanel, BorderLayout.WEST)
        this.add(rightPanel, BorderLayout.EAST)
        this.add(calculateButton, BorderLayout.SOUTH)
        this.add(resultLabel, BorderLayout.NORTH)
        calculateButton.addActionListener {
            calculateButton.isEnabled = false

            javax.swing.Timer(5000) {
                calculateButton.isEnabled = true
                (it.source as javax.swing.Timer).stop()
            }.start()

            calculate()
        }
    }

    private fun calculate() {
        val yourName = leftPanel.getPlayerName()
        val theirName = rightPanel.getPlayerName()
        val yourGlicko = leftPanel.getGlicko()
        val yourRD = leftPanel.getRD()
        val theirGlicko = rightPanel.getGlicko()
        val theirRD = rightPanel.getRD()

        if (yourGlicko != -1.0 && theirGlicko != -1.0) {
            val yourStatsIfWin = TetraRating.glickoUpdate(yourGlicko, yourRD, theirGlicko, theirRD, 1)
            val yourStatsIfLoss = TetraRating.glickoUpdate(yourGlicko, yourRD, theirGlicko, theirRD, 0)
            val yourTRIfWin = TetraRating.calculateTR(yourStatsIfWin.first, yourStatsIfWin.second, leftPanel.getWins() + 1)
            val yourTRIfLoss = TetraRating.calculateTR(yourStatsIfLoss.first, yourStatsIfLoss.second, leftPanel.getWins())

            val theirStatsIfWin = TetraRating.glickoUpdate(theirGlicko, theirRD, yourGlicko, yourRD, 1)
            val theirStatsIfLoss = TetraRating.glickoUpdate(theirGlicko, theirRD, yourGlicko, yourRD, 0)
            val theirTRIfWin = TetraRating.calculateTR(theirStatsIfWin.first, theirStatsIfWin.second, rightPanel.getWins() + 1)
            val theirTRIfLoss = TetraRating.calculateTR(theirStatsIfLoss.first, theirStatsIfLoss.second, rightPanel.getWins())

            resultLabel.text = "$yourName:[WIN: $yourTRIfWin | LOSS: $yourTRIfLoss]  ||  $theirName:[WIN: $theirTRIfWin | LOSS: $theirTRIfLoss]"
        }
        else resultLabel.text = "Someone has not played!"
    }
}