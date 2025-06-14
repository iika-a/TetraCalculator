import kotlin.math.*

object TetraRating {

    fun glicko2Update(
        r: Double,       // your rating
        rd: Double,      // your RD
        rOp: Double,     // opponent rating
        rdOp: Double,    // opponent RD
        result: Double,   // 1.0 = win, 0.5 = draw, 0.0 = loss
        sigma: Double
    ): Pair<Double, Double> {
        val scale = 173.7178
        val tau = 0.5
        val piSq = Math.PI * Math.PI

        // Step 1: Convert to Glicko-2 scale
        val mu = (r - 1500.0) / scale
        val phi = rd / scale
        val muOp = (rOp - 1500.0) / scale
        val phiOp = rdOp / scale

        // Step 2: Compute g and E
        fun g(phi: Double): Double = 1 / sqrt(1 + 3 * phi * phi / piSq)
        val gOp = g(phiOp)
        val e = 1 / (1 + exp(-gOp * (mu - muOp)))

        // Step 3: Compute v and delta
        val v = 1 / (gOp * gOp * e * (1 - e))
        val delta = v * gOp * (result - e)

        // Step 4: Volatility update
        val a = ln(sigma * sigma)
        var m = a
        val f: (Double) -> Double = { x ->
            val ex = exp(x)
            val top = ex * (delta * delta - phi * phi - v - ex)
            val bottom = 2 * (phi * phi + v + ex) * (phi * phi + v + ex)
            top / bottom - (x - a) / (tau * tau)
        }

        var b = if (delta * delta > phi * phi + v)
            ln(delta * delta - phi * phi - v)
        else {
            var k = 1
            while (f(a - k * tau) < 0) k++
            a - k * tau
        }

        val eps = 1e-6
        var fA = f(m)
        var fB = f(b)

        while (abs(b - m) > eps) {
            val c = m + (m - b) * fA / (fB - fA)
            val fC = f(c)
            if (fC * fB < 0) {
                m = b
                fA = fB
            } else {
                fA /= 2
            }
            b = c
            fB = fC
        }

        val sigmaPrime = exp(m / 2)

        // Step 5: Update phi*
        val phiStar = sqrt(phi * phi + sigmaPrime * sigmaPrime)

        // Step 6: New phi and mu
        val phiPrime = 1 / sqrt(1 / (phiStar * phiStar) + 1 / v)
        val muPrime = mu + phiPrime * phiPrime * gOp * (result - e)

        // Step 7: Convert back to original scale
        val newR = muPrime * scale + 1500.0
        val newRD = phiPrime * scale

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

    fun estimateSigmaAfterMatch(
        rBefore: Double,
        rdBefore: Double,
        rOp: Double,
        rdOp: Double,
        result: Double,
        tau: Double = 0.5
    ): Double {
        val scale = 173.7178
        val piSquared = PI * PI
        val epsilon = 1e-6

        // Step 1: Convert ratings and RD to Glicko-2 scale
        val mu = (rBefore - 1500.0) / scale
        val phi = rdBefore / scale
        val muOp = (rOp - 1500.0) / scale
        val phiOp = rdOp / scale

        // Step 3: g(phi) and E(mu, mu_j, phi_j)
        fun g(phi: Double): Double = 1.0 / sqrt(1.0 + (3.0 * phi * phi) / piSquared)
        fun e(mu: Double, muJ: Double, phiJ: Double): Double = 1.0 / (1.0 + exp(-g(phiJ) * (mu - muJ)))

        val gPhi = g(phiOp)
        val eval = e(mu, muOp, phiOp)

        // Step 4: Estimated variance
        val v = 1.0 / (gPhi * gPhi * eval * (1.0 - eval))

        // Step 5: delta
        val delta = v * gPhi * (result - eval)

        // Step 6: iterative algorithm
        val a = ln(0.06 * 0.06)
        var m = a
        var b = if (delta * delta > phi * phi + v) ln(delta * delta - phi * phi - v) else a - 1
        var fA = f(m, delta, phi, v, a, tau)
        var fB = f(b, delta, phi, v, a, tau)

        while (abs(b - m) > epsilon) {
            val c = m + (m - b) * fA / (fB - fA)
            val fC = f(c, delta, phi, v, a, tau)

            if (fC * fB < 0) {
                m = b
                fA = fB
            } else {
                fA /= 2
            }

            b = c
            fB = fC
        }

        return exp(m / 2.0)
    }

    // Helper function for iterative sigma solve
    private fun f(x: Double, delta: Double, phi: Double, v: Double, a: Double, tau: Double): Double {
        val ex = exp(x)
        val numerator = ex * (delta * delta - phi * phi - v - ex)
        val denominator = 2.0 * (phi * phi + v + ex).pow(2.0)
        return numerator / denominator - (x - a) / (tau * tau)
    }


    private infix fun Double.powTo(exponent: Double): Double = pow(exponent)
}
