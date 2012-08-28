import static com.googlecode.javacv.cpp.opencv_core.cvGetReal2D;

import com.googlecode.javacv.cpp.opencv_core.CvMat;

public class Histogram {
	private double[] hist;
	private double sum;
	
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
		sum = 0;
		for (int i = 0; i < hist.length; i++) {
			sum += hist[i];
		}

		// calculate percentages
		for (int i = 0; i < hist.length; i++) {
			hist[i] = hist[i] / sum;
		}
	}

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
	
	public double get(int idx) {
		return hist[idx];
	}
	
	public int size() {
		return hist.length;
	}
}
