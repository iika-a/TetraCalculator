import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class OutputPanel(winPlayer: TetraPlayer, losePlayer: TetraPlayer, originalPlayer: TetraPlayer): JPanel(GridBagLayout()) {
    private val avatarLabel = JLabel(TetraCalculatorHelper.getAvatar(winPlayer.name, 75))
    private val nameLabel = JLabel("<html><a href='' style='color:#BBBBBB;'>${winPlayer.name}</a></html>").apply {
        setLabelSettings(this)
        font = font.deriveFont(30f)
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                Desktop.getDesktop().browse(URI("https://ch.tetr.io/u/${winPlayer.name}"))
            }

            override fun mouseEntered(e: MouseEvent?) {
                text = "<html><a href='' style='color:#AAAAAA;'>${winPlayer.name}</a></html>"
            }

            override fun mouseExited(e: MouseEvent?) {
                text = "<html><a href='' style='color:#BBBBBB;'>${winPlayer.name}</a></html>"
            }
        })
    }
    private val trWinLabel = JLabel("Win TR: ${"%.2f".format(winPlayer.tr)} (+${"%.2f".format(winPlayer.tr - originalPlayer.tr)})").apply{ setLabelSettings(this) }
    private val glickoWinLabel = JLabel("Win Glicko: ${"%.2f".format(winPlayer.glicko)} ± ${"%.2f".format(winPlayer.rd)} (+${"%.2f".format(winPlayer.glicko - originalPlayer.glicko)})").apply{ setLabelSettings(this) }
    private val sigmaWinLabel = JLabel("Win Volatility: ${"%.2f".format(winPlayer.sigma)} ${TetraCalculatorHelper.getErrorText(winPlayer.sigma, 0.06)}").apply{ setLabelSettings(this) }
    private val trLossLabel = JLabel("Loss TR: ${"%.2f".format(losePlayer.tr)} (-${"%.2f".format(originalPlayer.tr - losePlayer.tr)})").apply{ setLabelSettings(this) }
    private val glickoLossLabel = JLabel("Loss Glicko: ${"%.2f".format(losePlayer.glicko)} ± ${"%.2f".format(losePlayer.rd)} (-${"%.2f".format(originalPlayer.glicko - losePlayer.glicko)})").apply{ setLabelSettings(this) }
    private val sigmaLossLabel = JLabel("Loss Volatility: ${"%.2f".format(losePlayer.sigma)} ${TetraCalculatorHelper.getErrorText(losePlayer.sigma, 0.06)}").apply{ setLabelSettings(this) }

    init {
        background = Color(0x44484A)

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

        this.add(JPanel().apply {
            add(avatarLabel)
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