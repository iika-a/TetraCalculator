import org.json.JSONObject
import org.jsoup.Jsoup
import javax.swing.BorderFactory
import javax.swing.JFrame

fun main() {
    val frame = JFrame("Tetra Calculator")
    frame.setSize(1280, 720)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isResizable = false
    frame.setLocationRelativeTo(null)

    val leftPanel = DisplayPanel()
    val rightPanel = DisplayPanel()
    leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 10))
    rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 100))
    val panel = MainPanel(leftPanel, rightPanel)

    frame.add(panel)
    frame.isVisible = true
}