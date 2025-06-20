import com.formdev.flatlaf.FlatDarkLaf
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.*

fun main() {
    try {
        UIManager.setLookAndFeel(FlatDarkLaf())
    } catch (e: UnsupportedLookAndFeelException) {
        e.printStackTrace()
        System.err.println("Failed to initialize FlatLaf.")
    }

    val frame = JFrame("Tetra Calculator")
    frame.setSize(1280, 720)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isResizable = false
    frame.setLocationRelativeTo(null)
    TetraCalculatorHelper.setIcons(frame)

    val loadingFrame = JFrame("Tetra Calculator...")
    loadingFrame.setSize(250, 125)
    loadingFrame.add(JPanel().apply {
        background = Color(0x44484A)

        add(JLabel("Loading...").apply {
            font = Font("Dubai", 0, 20)
            horizontalAlignment = JLabel.CENTER
            verticalAlignment = JLabel.CENTER
            foreground = Color(0xBBBBBB)
            preferredSize = Dimension(300, 75)
        })
    })
    loadingFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    loadingFrame.isResizable = false
    loadingFrame.setLocationRelativeTo(null)
    loadingFrame.isVisible = true
    TetraCalculatorHelper.setIcons(loadingFrame)

    val leftPanel = DisplayPanel()
    val rightPanel = DisplayPanel()
    leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 10))
    rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 100))
    val panel = MainPanel(leftPanel, rightPanel, frame)
    loadingFrame.dispose()

    frame.add(panel)
    frame.isVisible = true
}