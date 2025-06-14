import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel

class MainPanel(leftPanel: DisplayPanel, rightPanel: DisplayPanel): JPanel(BorderLayout()) {
    private val calculateButton = JButton("Calculate!")

    init {
        this.add(leftPanel, BorderLayout.WEST)
        this.add(rightPanel, BorderLayout.EAST)
        this.add(calculateButton, BorderLayout.SOUTH)
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
        println("meow")
    }
}