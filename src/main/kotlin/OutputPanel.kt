import java.awt.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class OutputPanel(winPlayer: TetraPlayer, losePlayer: TetraPlayer): JPanel(GridBagLayout()) {
    private val nameAvatarLabel = JLabel(TetraPlayer.getAvatar(winPlayer.name, 50))
    private val nameLabel = JLabel(winPlayer.name).apply { setLabelSettings(this) }
    private val trWinLabel = JLabel("Win TR: ${winPlayer.tr}").apply{ setLabelSettings(this) }
    private val glickoWinLabel = JLabel("Win Glicko: ${winPlayer.glicko} ± ${winPlayer.rd}").apply{ setLabelSettings(this) }
    private val sigmaWinLabel = JLabel("Win Volatility: ${winPlayer.sigma}").apply{ setLabelSettings(this) }
    private val trLossLabel = JLabel("Loss TR: ${losePlayer.tr}").apply{ setLabelSettings(this) }
    private val glickoLossLabel = JLabel("Loss Glicko: ${losePlayer.glicko} ± ${losePlayer.rd}").apply{ setLabelSettings(this) }
    private val sigmaLossLabel = JLabel("Loss Volatility: ${losePlayer.sigma}").apply{ setLabelSettings(this) }

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