//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Radix2FFT.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.data;

import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.ShortValues;
import com.scichart.core.utility.DoubleUtil;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * FFT implementation based on code from
 * http://stackoverflow.com/documentation/algorithm/8683/fast-fourier-transform/27088/radix-2-fft
 */
public class Radix2FFT {
    private final int n;
    private final int m;
    private final int mm1;

    public final int fftSize;

    private final Complex[] x;
    private final Complex[] dft;
    private final double TwoPi_N;

    private final Complex WN = new Complex();    // Wn is the exponential weighting function in the form a + jb
    private final Complex TEMP = new Complex();  // TEMP is used to save computation in the butterfly calc

    public Radix2FFT(int n) {
        this.n = n;
        this.m = (int) DoubleUtil.log(n, 2d);

        if (Math.pow(2, m) != n)
            throw new UnsupportedOperationException("n should be with power of 2");

        this.fftSize = n/2;
        this.TwoPi_N = Math.PI * 2 / n;    // constant to save computational time.  = 2*PI / N
        this.mm1 = m - 1;

        this.x = new Complex[n];
        this.dft = new Complex[n];

        for (int i = 0; i < n; i++) {
            x[i] = new Complex();
            dft[i] = new Complex();
        }
    }

    public void run(double[] re, double[] im) {
        // init input values
        for (int i = 0; i < n; i++) {
            final Complex complex = x[i];
            complex.re = re[i];
            complex.im = im[i];
        }

        // perform fft
        rad2FFT(x, dft);

        for (int i = 0; i < n; i++) {
            final Complex complex = dft[i];
            re[i] = complex.re;
            im[i] = complex.im;
        }
    }

    public void run(ShortValues input, DoubleValues output) {
        if(input.size() != n) throw new UnsupportedOperationException();

        // init input values
        final short[] itemsArray = input.getItemsArray();
        for (int i = 0; i < n; i++) {
            final Complex complex = x[i];
            complex.re = itemsArray[i];
            complex.im = 0;
        }

        // perform fft
        rad2FFT(x, dft);

        // set output
        output.setSize(fftSize);
        final double[] outputItems = output.getItemsArray();
        for (int i = 0; i < fftSize; i++) {
            outputItems[i] = calculateOutputValue(dft[i]);
        }
    }

    private double calculateOutputValue(Complex complex) {
        final double magnitude = Math.sqrt(complex.re * complex.re + complex.im * complex.im);

        // convert to magnitude to dB
        return 20 * Math.log10(magnitude / n);
    }

    private void rad2FFT(Complex[] x, Complex[] DFT) {
        int BSep;                  // BSep is memory spacing between butterflies
        int BWidth;                // BWidth is memory spacing of opposite ends of the butterfly
        int P;                     // P is number of similar Wn's to be used in that stage
        int iaddr;                 // bitmask for bit reversal
        int ii;                    // Integer bitfield for bit reversal (Decimation in Time)

        int DFTindex = 0;          // Pointer to first elements in DFT array

        // Decimation In Time - x[n] sample sorting
        for (int i = 0; i < n; i++, DFTindex++) {
            final Complex pX = x[i];        // Calculate current x[n] from index i.
            ii = 0;                         // Reset new address for DFT[n]
            iaddr = i;                      // Copy i for manipulations
            for (int l = 0; l < m; l++)     // Bit reverse i and store in ii...
            {
                if ((iaddr & 0x01) != 0)    // Detemine least significant bit
                    ii += (1 << (mm1 - l)); // Increment ii by 2^(M-1-l) if lsb was 1
                iaddr >>= 1;                // right shift iaddr to test next bit. Use logical operations for speed increase
                if (iaddr == 0)
                    break;
            }

            final Complex dft = DFT[ii];    // Calculate current DFT[n] from bit reversed index ii
            dft.re = pX.re;                 // Update the complex array with address sorted time domain signal x[n]
            dft.im = pX.im;                 // NB: Imaginary is always zero
        }

        // FFT Computation by butterfly calculation
        for (int stage = 1; stage <= m; stage++) // Loop for M stages, where 2^M = N
        {
            BSep = (int) (Math.pow(2, stage));  // Separation between butterflies = 2^stage
            P = n / BSep;                       // Similar Wn's in this stage = N/Bsep
            BWidth = BSep / 2;                  // Butterfly width (spacing between opposite points) = Separation / 2.

            for (int j = 0; j < BWidth; j++) // Loop for j calculations per butterfly
            {
                if (j != 0)              // Save on calculation if R = 0, as WN^0 = (1 + j0)
                {
                    WN.re = cos(TwoPi_N * P * j);     // Calculate Wn (Real and Imaginary)
                    WN.im = -sin(TwoPi_N * P * j);
                }

                // HiIndex is the index of the DFT array for the top value of each butterfly calc
                for (int HiIndex = j; HiIndex < n; HiIndex += BSep) // Loop for HiIndex Step BSep butterflies per stage
                {
                    final Complex pHi = DFT[HiIndex];                  // Point to higher value
                    final Complex pLo = DFT[HiIndex + BWidth];         // Point to lower value

                    if (j != 0)                            // If exponential power is not zero...
                    {
                        // Perform complex multiplication of LoValue with Wn
                        TEMP.re = (pLo.re * WN.re) - (pLo.im * WN.im);
                        TEMP.im = (pLo.re * WN.im) + (pLo.im * WN.re);

                        // Find new LoValue (complex subtraction)
                        pLo.re = pHi.re - TEMP.re;
                        pLo.im = pHi.im - TEMP.im;

                        // Find new HiValue (complex addition)
                        pHi.re = (pHi.re + TEMP.re);
                        pHi.im = (pHi.im + TEMP.im);
                    } else {
                        TEMP.re = pLo.re;
                        TEMP.im = pLo.im;

                        // Find new LoValue (complex subtraction)
                        pLo.re = pHi.re - TEMP.re;
                        pLo.im = pHi.im - TEMP.im;

                        // Find new HiValue (complex addition)
                        pHi.re = (pHi.re + TEMP.re);
                        pHi.im = (pHi.im + TEMP.im);
                    }
                }
            }
        }
    }

    private static class Complex {
        double re, im;
    }
}
