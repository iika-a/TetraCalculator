import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class OutputPanel(winPlayer: TetraPlayer, lossPlayer: TetraPlayer, originalPlayer: TetraPlayer): JPanel(GridBagLayout()) {
    private val avatarLabel = JLabel(TetraCalculatorHelper.getAvatar(winPlayer.name, 75))
    private val nameLabel = JLabel("<html><a href='' style='color:#BBBBBB; text-decoration:none;'>${winPlayer.name}</a></html>").apply {
        setLabelSettings(this, "")
        font = font.deriveFont(30f)
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                Desktop.getDesktop().browse(URI("https://ch.tetr.io/u/${winPlayer.name}"))
            }

            override fun mouseEntered(e: MouseEvent?) {
                text = "<html><a href='' style='color:#999999; text-decoration:none;'>${winPlayer.name}</a></html>"
            }

            override fun mouseExited(e: MouseEvent?) {
                text = "<html><a href='' style='color:#BBBBBB; text-decoration:none;'>${winPlayer.name}</a></html>"
            }
        })
    }
    private val trWinLabel = JLabel("<html>TR: ${"%,.2f".format(winPlayer.tr)} ${getChangeText(winPlayer.tr, originalPlayer.tr)}</html>").apply{ setLabelSettings(this, winPlayer.tr.toString())}
    private val glickoWinLabel = JLabel("<html>Glicko: ${"%,.2f".format(winPlayer.glicko)} ± ${"%,.2f".format(winPlayer.rd)} ${getChangeText(winPlayer.glicko, originalPlayer.glicko)}</html>").apply{ setLabelSettings(this, "${winPlayer.glicko} ± ${winPlayer.rd}") }
    private val sigmaWinLabel = JLabel("Volatility: ${TetraCalculatorHelper.getErrorText(winPlayer.sigma, 0.06)}${"%,.2f".format(winPlayer.sigma)}").apply{ setLabelSettings(this, winPlayer.sigma.toString()) }
    private val trLossLabel = JLabel("<html>TR: ${"%,.2f".format(lossPlayer.tr)} ${getChangeText(lossPlayer.tr, originalPlayer.tr)}</html>").apply{ setLabelSettings(this, lossPlayer.tr.toString()) }
    private val glickoLossLabel = JLabel("<html>Glicko: ${"%,.2f".format(lossPlayer.glicko)} ± ${"%,.2f".format(lossPlayer.rd)} ${getChangeText(lossPlayer.glicko, originalPlayer.glicko)}</html>").apply{ setLabelSettings(this, "${lossPlayer.glicko} ± ${lossPlayer.rd}") }
    private val sigmaLossLabel = JLabel("Volatility: ${TetraCalculatorHelper.getErrorText(lossPlayer.sigma, 0.06)}${"%,.2f".format(lossPlayer.sigma)}").apply{ setLabelSettings(this, lossPlayer.sigma.toString()) }

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

        val winPanel = JPanel(GridBagLayout()).apply {
            background = Color(0x44484A)
            border = TitledBorder("Win Stats").apply {
                titleFont = Font("Dubai", 0, 18)
            }
            add(trWinLabel, constraints)
            add(glickoWinLabel, constraints)
            add(sigmaWinLabel, constraints)
        }

        val lossPanel = JPanel(GridBagLayout()).apply {
            background = Color(0x44484A)
            border = TitledBorder("Loss Stats").apply {
                titleFont = Font("Dubai", 0, 18)
            }
            add(trLossLabel)
            add(glickoLossLabel, constraints)
            add(sigmaLossLabel, constraints)
        }

        this.add(winPanel, constraints)
        this.add(lossPanel, constraints)
    }

    private fun getChangeText(value1: Double, value2: Double): String {
        val difference = value1 - value2
        val sign: String
        val color: String
        if (difference > 0) {
            sign = "+"
            color = "B2FBA5"
        } else {
            sign = ""
            color = "FF746C"
        }

        val text = "<a href='' style='color:#$color; text-decoration:none;'>($sign${"%.2f".format(difference)})</a>"
        return text
    }

    private fun setLabelSettings(label: JLabel, toolTip: String) {
        label.font = Font("Dubai", 0, 20)
        label.horizontalAlignment = JLabel.CENTER
        label.verticalAlignment = JLabel.CENTER
        label.foreground = Color(0xBBBBBB)
        label.preferredSize = Dimension(500, 25)
        if (toolTip.isEmpty()) return
        else label.toolTipText = toolTip
    }
}