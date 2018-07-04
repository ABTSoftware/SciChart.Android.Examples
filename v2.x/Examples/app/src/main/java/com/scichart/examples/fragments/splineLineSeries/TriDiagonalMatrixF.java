//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// TriDiagonalMatrixF.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.splineLineSeries;

/**
 * A tri-diagonal matrix has non-zero entries only on the main diagonal, the diagonal above the main (super), and the
 * diagonal below the main (sub).
 * This is based on the wikipedia article: http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm
 * The entries in the matrix on a particular row are A[i], B[i], and C[i] where i is the row index.
 * B is the main diagonal, and so for an NxN matrix B is length N and all elements are used.
 * So for row 0, the first two values are B[0] and C[0].
 * And for row N-1, the last two values are A[N-1] and B[N-1].
 * That means that A[0] is not actually on the matrix and is therefore never used, and same with C[N-1].
 */
public class TriDiagonalMatrixF {

    /**
     * The values for the sub-diagonal. A[0] is never used.
     */
    public final float[] A;

    /**
     * The values for the main diagonal.
     */
    public final float[] B;

    /**
     * The values for the super-diagonal. C[C.Length-1] is never used.
     */
    public final float[] C;

    /**
     * Construct an NxN matrix.
     */
    public TriDiagonalMatrixF(int n) {
        this.A = new float[n];
        this.B = new float[n];
        this.C = new float[n];
    }

    /**
     * Solve the system of equations this*x=d given the specified d.
     * Uses the Thomas algorithm described in the wikipedia article: http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm
     * Not optimized. Not destructive.
     *
     * @param d Right side of the equation.
     */
    public float[] solve(float[] d) {
        int n = A.length;

        if (d.length != n) {
            throw new IllegalArgumentException("The input d is not the same size as this matrix.");
        }

        // cPrime
        float[] cPrime = new float[n];
        cPrime[0] = C[0] / B[0];

        for (int i = 1; i < n; i++) {
            cPrime[i] = C[i] / (B[i] - cPrime[i - 1] * A[i]);
        }

        // dPrime
        float[] dPrime = new float[n];
        dPrime[0] = d[0] / B[0];

        for (int i = 1; i < n; i++) {
            dPrime[i] = (d[i] - dPrime[i - 1] * A[i]) / (B[i] - cPrime[i - 1] * A[i]);
        }

        // Back substitution
        float[] x = new float[n];
        x[n - 1] = dPrime[n - 1];

        for (int i = n - 2; i >= 0; i--) {
            x[i] = dPrime[i] - cPrime[i] * x[i + 1];
        }

        return x;
    }
}
