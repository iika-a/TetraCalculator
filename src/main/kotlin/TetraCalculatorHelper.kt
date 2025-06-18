import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.math.abs

object TetraCalculatorHelper {
    fun getErrorText(value: Double, compareTo: Double): String {
        return if (abs(value - compareTo) < 0.01)
            if (value < compareTo) "(Slightly Less)"
            else if (value > compareTo) "(Slightly More)"
            else ""
        else ""
    }

    fun setIcons(frame: JFrame) {
        val icons = listOf(
            ImageIO.read(ClassLoader.getSystemResourceAsStream("icon16.png")),
            ImageIO.read(ClassLoader.getSystemResourceAsStream("icon32.png")),
            ImageIO.read(ClassLoader.getSystemResourceAsStream("icon48.png"))
        )
        frame.iconImages = icons
    }
}