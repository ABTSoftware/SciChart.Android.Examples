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
    public double[] A;

    /**
     * The values for the main diagonal.
     */
    public double[] B;

    /**
     * The values for the super-diagonal. C[C.Length-1] is never used.
     */
    public double[] C;

    /**
     * Returns the width and height of this matrix.
     */
    public int N() {
        return (A != null ? A.length : 0);
    }

    /**
     * Gets a value from the passed in row and column.
     */
    public double getCell(int row, int col) {
        int di = row - col;

        if (di == 0) {
            return B[row];
        } else if (di == -1) {
            return C[row];
        } else if (di == 1) {
            return A[row];
        } else return 0;
    }

    /**
     * Sets a value into the passed in row and column.
     * Throws an exception if you try to set any not on the super, main, or sub diagonals.
     */
    public void setCell(int row, int col, double value) {
        int di = row - col;

        if (di == 0) {
            B[row] = value;
        } else if (di == -1) {
            C[row] = value;
        } else if (di == 1) {
            A[row] = value;
        } else {
            throw new IllegalArgumentException("Only the main, super, and sub diagonals can be set.");
        }
    }

    /**
     * Construct an NxN matrix.
     */
    public TriDiagonalMatrixF(int n) {
        this.A = new double[n];
        this.B = new double[n];
        this.C = new double[n];
    }

    public String toDisplayString() {
        if (this.N() > 0) {
            StringBuilder s = new StringBuilder();
            for (int r = 0; r < N(); r++) {

                for (int c = 0; c < N(); c++) {
                    s.append(getCell(r, c));
                    if (c < N() - 1) s.append(", ");
                }

                s.append("\n");
            }

            return s.toString();
        } else {
            return "0x0 Matrix";
        }
    }

    /**
     * Solve the system of equations this*x=d given the specified d.
     * Uses the Thomas algorithm described in the wikipedia article: http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm
     * Not optimized. Not destructive.
     *
     * @param d Right side of the equation.
     */
    public double[] solve(double[] d) {
        int n = this.N();

        if (d.length != n) {
            throw new IllegalArgumentException("The input d is not the same size as this matrix.");
        }

        // cPrime
        double[] cPrime = new double[n];
        cPrime[0] = C[0] / B[0];

        for (int i = 1; i < n; i++) {
            cPrime[i] = C[i] / (B[i] - cPrime[i - 1] * A[i]);
        }

        // dPrime
        double[] dPrime = new double[n];
        dPrime[0] = d[0] / B[0];

        for (int i = 1; i < n; i++) {
            dPrime[i] = (d[i] - dPrime[i - 1] * A[i]) / (B[i] - cPrime[i - 1] * A[i]);
        }

        // Back substitution
        double[] x = new double[n];
        x[n - 1] = dPrime[n - 1];

        for (int i = n - 2; i >= 0; i--) {
            x[i] = dPrime[i] - cPrime[i] * x[i + 1];
        }

        return x;
    }
}
