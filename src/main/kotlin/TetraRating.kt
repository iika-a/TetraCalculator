import kotlin.math.*

class TetraRating(
    private var glicko: Double,
    private var rd: Double,
    private var wins: Int
) {
    private var tr: Double = calculateTR()

    fun updateMatch(opponent: TetraRating, win: Boolean) {
        val (newGlicko, newRD) = glickoUpdate(
            r = glicko,
            rd = rd,
            rOp = opponent.glicko,
            rdOp = opponent.rd,
            result = if (win) 1 else 0
        )

        glicko = newGlicko
        rd = newRD
        if (win) wins += 1
        tr = calculateTR()
    }

    private fun glickoUpdate(r: Double, rd: Double, rOp: Double, rdOp: Double, result: Int): Pair<Double, Double> {
        val Q = ln(10.0) / 400.0
        val PI_SQUARED = PI * PI

        val g = 1 / sqrt(1 + (3 * Q * Q * rdOp * rdOp) / PI_SQUARED)
        val E = 1 / (1 + 10.0.pow(-g * (r - rOp) / 400))
        val dSquared = 1 / (Q * Q * g * g * E * (1 - E))
        val delta = (Q / (1 / (rd * rd) + 1 / dSquared)) * g * (result - E)

        val newR = r + delta
        val newRD = sqrt(1 / (1 / (rd * rd) + 1 / dSquared))

        return Pair(newR, newRD)
    }

    private fun calculateTR(): Double {
        val F = min(1.0, 0.5 + 0.5 * (wins / 18.0))
        val D = 1 + (60 - rd) / 1500.0
        val B = 1.56
        val C = 0.86
        val v = 0.87646605
        val w = 0.25

        val part1 = 22000.0 / ((1 + exp(-D * B * ((glicko - 1500) / 500))) pow (1 / (v * F)))
        val part2 = 3000.0 / ((1 + exp(-D * C * ((glicko - 2000) / 500))) pow (1 / (w * F * F)))

        return part1 + part2
    }

    private infix fun Double.pow(exponent: Double): Double = this.pow(exponent)
    fun getTR() = tr
    fun getGlicko() = glicko
    fun getRD() = rd
    fun getWins() = wins
}
