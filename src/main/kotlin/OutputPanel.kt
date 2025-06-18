import java.awt.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class OutputPanel(winPlayer: TetraPlayer, losePlayer: TetraPlayer, originalPlayer: TetraPlayer): JPanel(GridBagLayout()) {
    private val nameAvatarLabel = JLabel(TetraCalculatorHelper.getAvatar(winPlayer.name, 50))
    private val nameLabel = JLabel(winPlayer.name).apply { setLabelSettings(this) }
    private val trWinLabel = JLabel("Win TR: ${"%.2f".format(winPlayer.tr)} (+${"%.2f".format(winPlayer.tr - originalPlayer.tr)})").apply{ setLabelSettings(this) }
    private val glickoWinLabel = JLabel("Win Glicko: ${"%.2f".format(winPlayer.glicko)} ± ${"%.2f".format(winPlayer.rd)} (+${"%.2f".format(winPlayer.glicko - originalPlayer.glicko)})").apply{ setLabelSettings(this) }
    private val sigmaWinLabel = JLabel("Win Volatility: ${"%.2f".format(winPlayer.sigma)} ${TetraCalculatorHelper.getErrorText(winPlayer.sigma, 0.06)}").apply{ setLabelSettings(this) }
    private val trLossLabel = JLabel("Loss TR: ${"%.2f".format(losePlayer.tr)} (-${"%.2f".format(originalPlayer.tr - losePlayer.tr)})").apply{ setLabelSettings(this) }
    private val glickoLossLabel = JLabel("Loss Glicko: ${"%.2f".format(losePlayer.glicko)} ± ${"%.2f".format(losePlayer.rd)} (-${"%.2f".format(originalPlayer.glicko - losePlayer.glicko)})").apply{ setLabelSettings(this) }
    private val sigmaLossLabel = JLabel("Loss Volatility: ${"%.2f".format(losePlayer.sigma)} ${TetraCalculatorHelper.getErrorText(losePlayer.sigma, 0.06)}").apply{ setLabelSettings(this) }

    init {
        background = Color(0x44484A)

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = GridBagConstraints.RELATIVE
            anchor = GridBagConstraints.CENTER
            fill = GridBagConstraints.NONE
            insets.set(10, 10, 10, 10)
            weightx = 1.0
            weighty = 1.0
        }

        this.add(JPanel().apply {
            add(nameAvatarLabel)
            add(nameLabel)
        }, constraints)

        this.add(trWinLabel, constraints)
        this.add(glickoWinLabel, constraints)
        this.add(sigmaWinLabel, constraints)
        this.add(JSeparator(SwingConstants.HORIZONTAL).apply {
            preferredSize = Dimension(550, 5)
            foreground = Color(0xBBBBBB)
            background = foreground
        }, constraints)

        this.add(trLossLabel, constraints)
        this.add(glickoLossLabel, constraints)
        this.add(sigmaLossLabel, constraints)
    }

    private fun setLabelSettings(label: JLabel) {
        label.font = Font("Dubai", 0, 20)
        label.horizontalAlignment = JLabel.CENTER
        label.verticalAlignment = JLabel.CENTER
        label.foreground = Color(0xBBBBBB)
        label.preferredSize = Dimension(500, 25)
    }
}