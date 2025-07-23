import kotlin.math.*

object TetraRating {

    fun glicko2Update(
        r: Double,       // your rating
        rd: Double,      // your RD
        rOp: Double,     // opponent rating
        rdOp: Double,    // opponent RD
        result: Double,   // 1.0 = win, 0.5 = draw, 0.0 = loss
        sigma: Double,
    ): Triple<Double, Double, Double> {
        val scale = 173.7178
        val tau = 0.5
        val piSq = Math.PI * Math.PI

        // step 1: convert to glicko-2 scale
        val mu = (r - 1500.0) / scale
        val phi = rd / scale
        val muOp = (rOp - 1500.0) / scale
        val phiOp = rdOp / scale

        // step 2: compute g and e
        fun g(phi: Double): Double = 1 / sqrt(1 + 3 * phi * phi / piSq)
        val gOp = g(phiOp)
        val e = 1 / (1 + exp(-gOp * (mu - muOp)))

        // step 3: compute v and delta
        val v = 1 / (gOp * gOp * e * (1 - e))
        val delta = v * gOp * (result - e)

        // step 4: volatility update
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

        // step 5: update phi*
        val phiStar = sqrt(phi * phi + sigmaPrime * sigmaPrime)

        // step 6: new phi and mu
        val phiPrime = 1 / sqrt(1 / (phiStar * phiStar) + 1 / v)
        val muPrime = mu + phiPrime * phiPrime * gOp * (result - e)

        // step 7: convert back to original scale
        val newR = muPrime * scale + 1500.0
        val newRD = phiPrime * scale

        if (0.260 < sigma && sigma <= 0.278) TetraCalculatorHelper.inaccurate = true

        return Triple(newR, newRD, sigmaPrime)
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

    fun estimateSigmaAndLastAnomaly(
        rBefore: Double,
        rdBefore: Double,
        rAfter: Double,
        rdAfter: Double,
        rOp: Double,
        rdOp: Double,
        result: Double
    ): Pair<Double, Int> {
        fun loss(sigma: Double): Double {
            val (glickoGuess, _) = glicko2Update(rBefore, rdBefore, rOp, rdOp, result, sigma)
            val ratingDiff = abs(glickoGuess - rAfter)
            return ratingDiff
        }

        val invGr = 2/(sqrt(5.0) + 1)

        var low = 0.000001
        var high = 1.0
        val epsilon = 1e-12

        var x1 = high - (high - low) * invGr
        var x2 = low + (high - low) * invGr
        var f1 = loss(x1)
        var f2 = loss(x2)

        while (high - low > epsilon) {
            if (f1 < f2) {
                high = x2
                x2 = x1
                f2 = f1
                x1 = high - (high - low) * invGr
                f1 = loss(x1)
            } else {
                low = x1
                x1 = x2
                f1 = f2
                x2 = low + (high - low) * invGr
                f2 = loss(x2)
            }
        }

        val guess = glicko2Update(rBefore, rdBefore, rOp, rdOp, result, (low + high) / 2.0)
        val (_, _, currentSigma) = glicko2Update(rBefore, rdBefore, rOp, rdOp, result, (low + high) / 2.0)

        val anomaly = if (abs(0.06 - currentSigma) > 0.002) {
            if (abs(sqrt(2/3.0) - rdAfter/guess.second) < 0.001) 1
            else if (abs(sqrt(5/6.0) - rdAfter/guess.second) < 0.001) 2
            else 0
        } else 0
        println(anomaly)
        return Pair(currentSigma, anomaly)
    }

    private infix fun Double.powTo(exponent: Double): Double = pow(exponent)
}