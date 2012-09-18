package feature;

import static com.googlecode.javacv.cpp.opencv_core.cvGetReal2D;

import com.googlecode.javacv.cpp.opencv_core.CvMat;

/**
 * Represents a histogram of an image or part of an image. For each color value
 * the amount of Occurrences in the image is stored as a percentage of the total
 * amount.
 * 
 * @author PilgerstorferP
 * 
 */
public class Histogram {
	private double[] hist;

	/**
	 * Creates a new Histogram. Only pixels with a non zero mask-value are
	 * considered. All pixels in img have to have a color value in the range of
	 * [0, numColors).
	 * 
	 * @param img
	 *            a 8U1C matrix (8 bit unsigned, 1 channel)
	 * @param mask
	 *            a 8U1C matrix
	 * @param numColors
	 *            a positive number
	 */
	public Histogram(CvMat img, CvMat mask, int numColors) {
		hist = new double[numColors];

		// calculate absolute values
		for (int i = 0; i < img.rows(); i++) {
			for (int j = 0; j < img.cols(); j++) {
				if (cvGetReal2D(mask, i, j) != 0) {
					int val = (int) cvGetReal2D(img, i, j);
					hist[val]++;
				}
			}
		}

		// calculate sum
		double sum = 0;
		for (int i = 0; i < hist.length; i++) {
			sum += hist[i];
		}

		// calculate percentages
		for (int i = 0; i < hist.length; i++) {
			hist[i] = hist[i] / sum;
		}
	}

	/**
	 * @return the id (color value) of the element with the highest percentage.
	 */
	public int max() {
		int idx = 0;
		double max = 0;

		for (int i = 0; i < hist.length; i++) {
			double val = hist[i];
			if (max < val) {
				idx = i;
				max = val;
			}
		}

		return idx;
	}

	/**
	 * @param idx
	 *            the index (color value)
	 * @return the occurrence percentage of the specified color.
	 */
	public double get(int idx) {
		return hist[idx];
	}

	public int size() {
		return hist.length;
	}
}
