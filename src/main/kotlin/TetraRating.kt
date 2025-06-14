import kotlin.math.*

object TetraRating {

    fun glickoUpdate(
        r: Double,
        rd: Double,
        rOp: Double,
        rdOp: Double,
        result: Int
    ): Pair<Double, Double> {
        val q = ln(10.0) / 400.0
        val piSquared = PI * PI

        val g = 1 / sqrt(1 + (3 * q * q * rdOp * rdOp) / piSquared)
        val e = 1 / (1 + 10.0.pow(-g * (r - rOp) / 400))
        val dSquared = 1 / (q * q * g * g * e * (1 - e))
        val delta = (q / (1 / (rd * rd) + 1 / dSquared)) * g * (result - e)

        val newR = r + delta
        val newRD = sqrt(1 / (1 / (rd * rd) + 1 / dSquared))

        return Pair(newR, newRD)
    }

    fun calculateTR(glicko: Double, rd: Double, wins: Int): Double {
        val f = min(1.0, 0.5 + 0.5 * (wins / 18.0))
        val d = 1 + (60 - rd) / 1500.0
        val b = 1.56
        val c = 0.86
        val v = 0.87646605
        val w = 0.25

        val part1 = 22000.0 / ((1 + exp(-d * b * ((glicko - 1500) / 500))) powTo (1 / (v * f)))
        val part2 = 3000.0 / ((1 + exp(-d * c * ((glicko - 2000) / 500))) powTo (1 / (w * f * f)))

        return part1 + part2
    }

    private infix fun Double.powTo(exponent: Double): Double = pow(exponent)
}
