package image;

import static com.googlecode.javacv.cpp.opencv_core.cvDrawLine;
import static com.googlecode.javacv.cpp.opencv_core.cvGetReal2D;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvSetZero;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;

import java.util.Iterator;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Histogram implements Iterable<Integer> {
	private int[] hist;
	private IplImage img;
	private IplImage totalImg;

	public Histogram(IplImage img) {
		hist = new int[256];

		// calculate absolute values
		for (int i = 0; i < img.height(); i++) {
			for (int j = 0; j < img.width(); j++) {
				int val = (int) cvGetReal2D(img, i, j);
				hist[val]++;
			}
		}
	}

	public Histogram(IplImage img, IplImage mask) {
		hist = new int[256];

		// calculate absolute values
		for (int i = 0; i < img.height(); i++) {
			for (int j = 0; j < img.width(); j++) {
				if (cvGetReal2D(mask, i, j) != 0) {
					int val = (int) cvGetReal2D(img, i, j);
					hist[val]++;
				}
			}
		}
	}

	public IplImage asImage() {
		if (img == null) {
			int max = hist[0];
			for (int i = 1; i < hist.length; i++) {
				max = max >= hist[i] ? max : hist[i];
			}

			int height = 100;
			img = IplImage.create(cvSize(hist.length, height), 8, 1);

			cvSetZero(img);
			for (int x = 0; x < hist.length; x++) {
				int val = (int) Math.round(hist[x] * (height / (double) max));
				cvDrawLine(img, cvPoint(x, height), cvPoint(x, height - val),
						CvScalar.WHITE, 1, 1, 0);
			}
		}
		return img;
	}

	public IplImage asTotalImage() {
		if (totalImg == null) {
			int sum = hist[0];
			for (int i = 1; i < hist.length; i++) {
				sum += hist[i];
			}

			int height = 512;
			totalImg = IplImage.create(cvSize(hist.length, height), 8, 1);

			cvSetZero(totalImg);
			for (int x = 0; x < hist.length; x++) {
				int val = (int) Math.round(hist[x] * (height / (double) sum));
				cvDrawLine(totalImg, cvPoint(x, height),
						cvPoint(x, height - val), CvScalar.WHITE, 1, 1, 0);
			}
		}
		return totalImg;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < hist.length;
			}

			@Override
			public Integer next() {
				int val = hist[i];
				i++;
				return val;
			}

			@Override
			public void remove() {
			}
		};
	}
}
