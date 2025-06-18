import java.awt.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

class MainPanel(private val leftPanel: DisplayPanel, private val rightPanel: DisplayPanel): JPanel(BorderLayout()) {
    private val calculateButton = JButton("Calculate!").apply {
        font = Font("Dubai", 0, 20)
        preferredSize = Dimension(150, 35)
    }

    init {
        background = Color(0x44484A)

        val centerPanel = JPanel(GridLayout(1, 2))
        centerPanel.add(leftPanel)
        centerPanel.add(rightPanel)
        this.add(centerPanel, BorderLayout.CENTER)
        this.add(JPanel().apply {
            background = Color(0x44484A)
            add(calculateButton)
        }, BorderLayout.SOUTH)
        calculateButton.addActionListener {
            calculate()
        }
    }

    private fun calculate() {
        val left = leftPanel.getPlayer()
        val right = rightPanel.getPlayer()
        if (left.tr == -1.0 || right.tr == -1.0) {
            val loadingFrame = JFrame("Tetra Calculation")
            loadingFrame.setSize(250, 150)
            loadingFrame.add(JPanel().apply {
                add(JLabel("Someone has not played!").apply {
                    font = Font("Dubai", 0, 20)
                    horizontalAlignment = JLabel.CENTER
                    verticalAlignment = JLabel.CENTER
                    foreground = Color(0xBBBBBB)
                    preferredSize = Dimension(300, 60)
                })

                add(JButton("OK").apply {
                    font = Font("Dubai", 0, 20)
                    preferredSize = Dimension(150, 35)
                    addActionListener {
                        loadingFrame.dispose()
                    }
                })

                background = Color(0x44484A)
            })


            loadingFrame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            loadingFrame.isResizable = false
            loadingFrame.setLocationRelativeTo(null)
            loadingFrame.isVisible = true

            return
        }

        val leftStatsIfWin = TetraRating.glicko2Update(left.glicko, left.rd, right.glicko, right.rd, 1.0, left.sigma)
        val leftStatsIfLoss = TetraRating.glicko2Update(left.glicko, left.rd, right.glicko, right.rd, 0.0, left.sigma)
        val leftTRIfWin = TetraRating.calculateTR(leftStatsIfWin.first, leftStatsIfWin.second, left.wins + 1)
        val leftTRIfLoss = TetraRating.calculateTR(leftStatsIfLoss.first, leftStatsIfLoss.second, left.wins)
        val leftPlayerIfWin = TetraPlayer(left.name, leftTRIfWin, leftStatsIfWin.first, leftStatsIfWin.second, left.wins + 1, leftStatsIfWin.third)
        val leftPlayerIfLoss = TetraPlayer(left.name, leftTRIfLoss, leftStatsIfLoss.first, leftStatsIfLoss.second, left.wins, leftStatsIfLoss.third)

        val rightStatsIfWin = TetraRating.glicko2Update(right.glicko, right.rd, left.glicko, left.rd, 1.0, right.sigma)
        val rightStatsIfLoss = TetraRating.glicko2Update(right.glicko, right.rd, left.glicko, left.rd, 0.0, right.sigma)
        val rightTRIfWin = TetraRating.calculateTR(rightStatsIfWin.first, rightStatsIfWin.second, right.wins + 1)
        val rightTRIfLoss = TetraRating.calculateTR(rightStatsIfLoss.first, rightStatsIfLoss.second, right.wins)
        val rightPlayerIfWin = TetraPlayer(right.name, rightTRIfWin, rightStatsIfWin.first, rightStatsIfWin.second, right.wins + 1, rightStatsIfWin.third)
        val rightPlayerIfLoss = TetraPlayer(right.name, rightTRIfLoss, rightStatsIfLoss.first, rightStatsIfLoss.second, right.wins, rightStatsIfLoss.third)

        val leftOutputPanel = OutputPanel(leftPlayerIfWin, leftPlayerIfLoss)
        val rightOutputPanel = OutputPanel(rightPlayerIfWin, rightPlayerIfLoss)

        val outputFrame = JFrame("Tetra Calculation")
        outputFrame.setSize(1280, 720)
        outputFrame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        outputFrame.isResizable = false
        outputFrame.setLocationRelativeTo(null)

        val outputCenterPanel = JPanel(BorderLayout()).apply {
            add(JPanel(GridLayout(1, 2)).apply {
                this.add(leftOutputPanel)
                this.add(rightOutputPanel)
            }, BorderLayout.CENTER)

            add(JPanel().apply {
                background = Color(0x44484A)
                add(JButton("OK").apply {
                    font = Font("Dubai", 0, 20)
                    preferredSize = Dimension(150, 35)
                    addActionListener {
                        outputFrame.dispose()
                    }
                })
            }, BorderLayout.SOUTH)
        }
        outputFrame.add(outputCenterPanel)
        outputFrame.isVisible = true
    }
}