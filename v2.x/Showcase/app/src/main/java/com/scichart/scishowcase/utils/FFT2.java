package com.scichart.scishowcase.utils;

import com.scichart.core.model.DoubleValues;
import com.scichart.core.model.ShortValues;

// Reference: http://gerrybeauregard.wordpress.com/2011/04/01/an-fft-in-c
public class FFT2 {

    // Element for linked list in which we store the
    // input/output data. We use a linked list because
    // for sequential access it's faster than array index.
    private static class FFTElement {

        final int revTgt;       // Target position post bit-reversal

        double re = 0.0;        // Real component
        double im = 0.0;        // Imaginary component
        FFTElement next;        // Next element in linked list

        FFTElement(int revTgt) {
            this.revTgt = revTgt;
        }
    }

    private final int m_logN;        // log2 of FFT2 size
    private final int m_N;           // FFT2 size
    private final FFTElement[] m_X;  // Vector of linked list elements

    public FFT2(int logN) {
        m_logN = logN;
        m_N = 1 << m_logN;

        // Allocate elements for linked list of complex numbers and
        // specify target for bit reversal re-ordering.
        m_X = new FFTElement[m_N];
        for (int k = 0; k < m_N; k++)
            m_X[k] = new FFTElement(bitReverse(k, logN));

        // Set up "next" pointers.
        for (int k = 0; k < m_N - 1; k++)
            m_X[k].next = m_X[k + 1];
    }

    /**
     * Performs in-place complex FFT2.
     */
    public void run(ShortValues input, DoubleValues output) {
        int numFlies = m_N >> 1;    // Number of butterflies per sub-FFT2
        int span = m_N >> 1;        // Width of the butterfly
        int spacing = m_N;            // Distance between start of sub-FFTs
        int wIndexStep = 3;        // Increment for twiddle table index

        final int size = input.size();
        final short[] inputItems = input.getItemsArray();

        // Copy data into linked complex number objects
        // If it's an IFFT, we divide by N while we're at it
        FFTElement x = m_X[0];
        int k = 0;
        double scale = 1.0;
        while (x != null) {
            x.re = scale * inputItems[k];
            x.im = 0;
            x = x.next;
            k++;
        }

        // For each stage of the FFT2
        for (int stage = 0; stage < m_logN; stage++) {
            // Compute a multiplier factor for the "twiddle factors".
            // The twiddle factors are complex unit vectors spaced at
            // regular angular intervals. The angle by which the twiddle
            // factor advances depends on the FFT2 stage. In many FFT2
            // implementations the twiddle factors are cached, but because
            // array lookup is relatively slow in C#, it's just
            // as fast to compute them on the fly.
            double wAngleInc = -wIndexStep * 2.0 * Math.PI / m_N;
            double wMulRe = Math.cos(wAngleInc);
            double wMulIm = Math.sin(wAngleInc);

            for (int start = 0; start < m_N; start += spacing) {
                FFTElement xTop = m_X[start];
                FFTElement xBot = m_X[start + span];

                double wRe = 1.0;
                double wIm = 0.0;

                // For each butterfly in this stage
                for (int flyCount = 0; flyCount < numFlies; ++flyCount) {
                    // Get the top & bottom values
                    double xTopRe = xTop.re;
                    double xTopIm = xTop.im;
                    double xBotRe = xBot.re;
                    double xBotIm = xBot.im;

                    // Top branch of butterfly has addition
                    xTop.re = xTopRe + xBotRe;
                    xTop.im = xTopIm + xBotIm;

                    // Bottom branch of butterly has subtraction,
                    // followed by multiplication by twiddle factor
                    xBotRe = xTopRe - xBotRe;
                    xBotIm = xTopIm - xBotIm;
                    xBot.re = xBotRe * wRe - xBotIm * wIm;
                    xBot.im = xBotRe * wIm + xBotIm * wRe;

                    // Advance butterfly to next top & bottom positions
                    xTop = xTop.next;
                    xBot = xBot.next;

                    // Update the twiddle factor, via complex multiply
                    // by unit vector with the appropriate angle
                    // (wRe + j wIm) = (wRe + j wIm) x (wMulRe + j wMulIm)
                    double tRe = wRe;
                    wRe = wRe * wMulRe - wIm * wMulIm;
                    wIm = tRe * wMulIm + wIm * wMulRe;
                }
            }

            numFlies >>= 1;    // Divide by 2 by right shift
            span >>= 1;
            spacing >>= 1;
            wIndexStep <<= 1;    // Multiply by 2 by left shift
        }

        output.setSize(size);
        final double[] outputItems = output.getItemsArray();

        // The algorithm leaves the result in a scrambled order.
        // Unscramble while copying values from the complex
        // linked list elements back to the input/output vectors.
        x = m_X[0];
        while (x != null) {
            outputItems[x.revTgt] = calculateOutputValue(x);

            x = x.next;
        }

        // cut off half of results because FFT is symmetric
        output.setSize(size/2);
    }

    private double calculateOutputValue(FFTElement element) {
        final double magnitude = Math.sqrt(element.re * element.re + element.im * element.im);

        // convert to magnitude to dB
        return 20 * Math.log10(magnitude / m_N);
    }

    /**
     * Do bit reversal of specified number of places of an int
     * For example, 1101 bit-reversed is 1011
     *
     * @param x       Number to be bit-reverse.
     * @param numBits Number of bits in the number.
     */
    private static int bitReverse(int x, int numBits) {
        int y = 0;

        for (int i = 0; i < numBits; i++) {
            y <<= 1;
            y |= x & 0x0001;
            x >>= 1;
        }

        return y;
    }
}
