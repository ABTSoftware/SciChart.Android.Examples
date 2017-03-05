//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CubicSpline.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.splineLineSeries;

/**
 * Cubic spline interpolation.
 * Call Fit (or use the corrector constructor) to compute spline coefficients, then Eval to evaluate the spline at other X coordinates.
 * <p>
 * This is implemented based on the wikipedia article:
 * http://en.wikipedia.org/wiki/Spline_interpolation
 * I'm not sure I have the right to include a copy of the article so the equation numbers referenced in
 * comments will end up being wrong at some point.
 * <p>
 * This is not optimized, and is not MT safe.
 * This can extrapolate off the ends of the splines.
 * You must provide points in X sort order.
 * <p>
 */
public class CubicSpline {

    // N-1 spline coefficients for N points
    private double[] a;
    private double[] b;

    // Save the original x and y for Eval
    private double[] xOrig;
    private double[] yOrig;

    /**
     * Default ctor.
     */
    public CubicSpline() {
    }

    /**
     * Construct and call fit.
     *
     * @param x          Input. X coordinates to fit.
     * @param y          Input. Y coordinates to fit.
     * @param startSlope Optional slope constraint for the first point. Single.NaN means no constraint.
     * @param endSlope   Optional slope constraint for the final point. Single.NaN means no constraint.
     * @param debug      Turn on console output. Default is false.
     */
    public CubicSpline(double[] x, double[] y, double startSlope, double endSlope, boolean debug) throws Exception {
        fit(x, y, startSlope, endSlope, debug);
    }

    /**
     * Throws if fit has not been called.
     */
    private void checkAlreadyFitted() throws Exception {
        if (a == null) {
            throw new Exception("Fit must be called before you can evaluate.");
        }
    }

    private int _lastIndex = 0;

    /**
     * Find where in xOrig the specified x falls, by simultaneous traverse.
     * This allows xs to be less than x[0] and/or greater than x[n-1]. So allows extrapolation.
     * This keeps state, so requires that x be sorted and xs called in ascending order, and is not multi-thread safe.
     */
    private int getNextXIndex(double x) {
        if (x < xOrig[_lastIndex]) {
            throw new IllegalArgumentException("The X values to evaluate must be sorted.");
        }

        while ((_lastIndex < xOrig.length - 2) && (x > xOrig[_lastIndex + 1])) {
            _lastIndex++;
        }

        return _lastIndex;
    }

    /**
     * Evaluate the specified x value using the specified spline.
     *
     * @param x     The x value.
     * @param j     Which spline to use.
     * @param debug Turn on console output. Default is false.
     * @return The y value.
     */
    private double evalSpline(double x, int j, boolean debug) {
        double dx = xOrig[j + 1] - xOrig[j];
        double t = (x - xOrig[j]) / dx;
        double y = (1 - t) * yOrig[j] + t * yOrig[j + 1] + t * (1 - t) * (a[j] * (1 - t) + b[j] * t); // equation 9
        if (debug) {
            System.out.println(String.format("xs = %d, j = %d, t = %d", x, j, t));
        }
        return y;
    }

    /**
     * Fit x,y and then eval at points xs and return the corresponding y's.
     * This does the "natural spline" style for ends.
     * This can extrapolate off the ends of the splines.
     * You must provide points in X sort order.
     *
     * @param x          Input. X coordinates to fit
     * @param y          Input. Y coordinates to fit
     * @param xs         Input. X coordinates to evaluate the fitted curve at.
     * @param startSlope Optional slope constraint for the first point. Single.NaN means no constraint.
     * @param endSlope   Optional slope constraint for the final point. Single.NaN means no constraint.
     * @param debug      Turn on console output. Default is false.
     * @return The computed y values for each xs.
     */
    public double[] fitAndEval(double[] x, double[] y, double[] xs, double startSlope, double endSlope, boolean debug) throws Exception {
        fit(x, y, startSlope, endSlope, debug);
        return eval(xs, debug);
    }

    /**
     * Compute spline coefficients for the specified x,y points.
     * This does the "natural spline" style for ends.
     * This can extrapolate off the ends of the splines.
     * You must provide points in X sort order.
     *
     * @param x          Input. X coordinates to fit
     * @param y          Input. Y coordinates to fit
     * @param startSlope Optional slope constraint for the first point. Single.NaN means no constraint.
     * @param endSlope   Optional slope constraint for the final point. Single.NaN means no constraint.
     * @param debug      Turn on console output. Default is false.
     */
    public void fit(double[] x, double[] y, double startSlope, double endSlope, boolean debug) throws Exception {
        if (Double.isInfinite(startSlope) || Double.isInfinite(endSlope)) {
            throw new Exception("startSlope and endSlope cannot be infinity.");
        }

        // Save x and y for eval
        this.xOrig = x;
        this.yOrig = y;

        int n = x.length;
        double[] r = new double[n]; // the right hand side numbers: wikipedia page overloads b

        TriDiagonalMatrixF m = new TriDiagonalMatrixF(n);
        double dx1, dx2, dy1, dy2;

        // First row is different (equation 16 from the article)
        if (Double.isNaN(startSlope)) {
            dx1 = x[1] - x[0];
            m.C[0] = 1.0f / dx1;
            m.B[0] = 2.0f * m.C[0];
            r[0] = 3 * (y[1] - y[0]) / (dx1 * dx1);
        } else {
            m.B[0] = 1;
            r[0] = startSlope;
        }

        // Body rows (equation 15 from the article)
        for (int i = 1; i < n - 1; i++) {
            dx1 = x[i] - x[i - 1];
            dx2 = x[i + 1] - x[i];

            m.A[i] = 1.0f / dx1;
            m.C[i] = 1.0f / dx2;
            m.B[i] = 2.0f * (m.A[i] + m.C[i]);

            dy1 = y[i] - y[i - 1];
            dy2 = y[i + 1] - y[i];
            r[i] = 3 * (dy1 / (dx1 * dx1) + dy2 / (dx2 * dx2));
        }

        // Last row also different (equation 17 from the article)
        if (Double.isNaN(endSlope)) {
            dx1 = x[n - 1] - x[n - 2];
            dy1 = y[n - 1] - y[n - 2];
            m.A[n - 1] = 1.0f / dx1;
            m.B[n - 1] = 2.0f * m.A[n - 1];
            r[n - 1] = 3 * (dy1 / (dx1 * dx1));
        } else {
            m.B[n - 1] = 1;
            r[n - 1] = endSlope;
        }

//            if (debug) Console.WriteLine("Tri-diagonal matrix:\n{0}", m.ToDisplayString(":0.0000", "  "));
//            if (debug) Console.WriteLine("r: {0}", ArrayUtil.ToString<double>(r));

        // k is the solution to the matrix
        double[] k = m.solve(r);
//            if (debug) Console.WriteLine("k = {0}", ArrayUtil.ToString<double>(k));

        // a and b are each spline's coefficients
        this.a = new double[n - 1];
        this.b = new double[n - 1];

        for (int i = 1; i < n; i++) {
            dx1 = x[i] - x[i - 1];
            dy1 = y[i] - y[i - 1];
            a[i - 1] = k[i - 1] * dx1 - dy1; // equation 10 from the article
            b[i - 1] = -k[i] * dx1 + dy1; // equation 11 from the article
        }

//            if (debug) Console.WriteLine("a: {0}", ArrayUtil.ToString<double>(a));
//            if (debug) Console.WriteLine("b: {0}", ArrayUtil.ToString<double>(b));
    }

    /**
     * Evaluate the spline at the specified x coordinates.
     * This can extrapolate off the ends of the splines.
     * You must provide X's in ascending order.
     * The spline must already be computed before calling this, meaning you must have already called fit() or fitAndEval().
     *
     * @param x     Input. X coordinates to evaluate the fitted curve at.
     * @param debug Turn on console output. Default is false.
     * @return The computed y values for each x.
     */
    public double[] eval(double[] x, boolean debug) throws Exception {
        checkAlreadyFitted();

        int n = x.length;
        double[] y = new double[n];
        _lastIndex = 0; // Reset simultaneous traversal in case there are multiple calls

        for (int i = 0; i < n; i++) {
            // Find which spline can be used to compute this x (by simultaneous traverse)
            int j = getNextXIndex(x[i]);

            // Evaluate using j'th spline
            y[i] = evalSpline(x[i], j, debug);
        }

        return y;
    }

    /**
     * Evaluate (compute) the slope of the spline at the specified x coordinates.
     * This can extrapolate off the ends of the splines.
     * You must provide X's in ascending order.
     * The spline must already be computed before calling this, meaning you must have already called fit() or fitAndEval().
     *
     * @param x     Input. X coordinates to evaluate the fitted curve at.
     * @param debug Turn on console output. Default is false.
     * @return The computed y values for each x.
     */
    public double[] evalSlope(double[] x, boolean debug) throws Exception {
        checkAlreadyFitted();

        int n = x.length;
        double[] qPrime = new double[n];
        _lastIndex = 0; // Reset simultaneous traversal in case there are multiple calls

        for (int i = 0; i < n; i++) {
            // Find which spline can be used to compute this x (by simultaneous traverse)
            int j = getNextXIndex(x[i]);

            // Evaluate using j'th spline
            double dx = xOrig[j + 1] - xOrig[j];
            double dy = yOrig[j + 1] - yOrig[j];
            double t = (x[i] - xOrig[j]) / dx;

            // From equation 5 we could also compute q' (qp) which is the slope at this x
            qPrime[i] = dy / dx
                    + (1 - 2 * t) * (a[j] * (1 - t) + b[j] * t) / dx
                    + t * (1 - t) * (b[j] - a[j]) / dx;

            if (debug) {
                System.out.println(String.format("[%d]: xs = %d, j = %d, t = %d", i, x[i], j, t));
            }
        }

        return qPrime;
    }

    /**
     * Static all-in-one method to fit the splines and evaluate at X coordinates.
     *
     * @param x          Input. X coordinates to fit
     * @param y          Input. Y coordinates to fit
     * @param xs         Input. X coordinates to evaluate the fitted curve at.
     * @param startSlope Optional slope constraint for the first point. Single.NaN means no constraint.
     * @param endSlope   Optional slope constraint for the final point. Single.NaN means no constraint.
     * @param debug      Turn on console output. Default is false.
     * @return The computed y values for each xs.
     */
    public static double[] compute(double[] x, double[] y, double[] xs, double startSlope, double endSlope, boolean debug) throws Exception {
        CubicSpline spline = new CubicSpline();
        return spline.fitAndEval(x, y, xs, startSlope, endSlope, debug);
    }

//    /**
//     * Fit the input x,y points using a 'geometric' strategy so that y does not have to be a single-valued
//     * function of x.
//     *
//     * @param x             Input x coordinates.
//     * @param y             Input y coordinates, do not need to be a single-valued function of x.
//     * @param nOutputPoints How many output points to create.
//     *                      <param name="xs">Output (interpolated) x values.</param>
//     *                      <param name="ys">Output (interpolated) y values.</param>
//     */
//    public static void fitGeometric(double[] x, double[] y, int nOutputPoints, out double[]xs, out double[]ys) {
//        // Compute distances
//        int n = x.length;
//        double[] dists = new double[n]; // cumulative distance
//        dists[0] = 0;
//        double totalDist = 0;
//
//        for (int i = 1; i < n; i++) {
//            double dx = x[i] - x[i - 1];
//            double dy = y[i] - y[i - 1];
//            double dist = Math.sqrt(dx * dx + dy * dy);
//            totalDist += dist;
//            dists[i] = totalDist;
//        }
//
//        // Create 'times' to interpolate to
//        double dt = totalDist / (nOutputPoints - 1);
//        double[] times = new double[nOutputPoints];
//        times[0] = 0;
//
//        for (int i = 1; i < nOutputPoints; i++) {
//            times[i] = times[i - 1] + dt;
//        }
//
//        // Spline fit both x and y to times
//        CubicSpline xSpline = new CubicSpline();
//        xs = xSpline.fitAndEval(dists, x, times);
//
//        CubicSpline ySpline = new CubicSpline();
//        ys = ySpline.fitAndEval(dists, y, times);
//    }
}