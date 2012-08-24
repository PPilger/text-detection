import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvResizeWindow;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

public class DirectionBasedFeatureLinker extends FeatureLinker {
	private int size;
	private IplImage directionMap;
	private CvMat[] filters;
	private double[] angles;
	private int[] sums;

	public DirectionBasedFeatureLinker(int size, int numFilters, int width) {
		this.size = size;

		// int numFilters = (int) Math.ceil((2.0 * size - 2.0) / step);
		this.filters = new CvMat[numFilters];
		this.angles = new double[numFilters];
		this.sums = new int[numFilters];

		for (int i = 0; i < numFilters; i++) {
			filters[i] = CvMat.create(size, size, CV_32FC1);
			cvSetZero(filters[i]);
		}

		double r = size / 2;
		int c = (int) Math.round(r);
		for (int i = 0; i < numFilters; i++) {
			double angle = i / (double) numFilters * Math.PI;
			double sin = Math.sin(angle);
			double cos = Math.cos(angle);

			int x = (int) Math.round(cos * r);
			int y = (int) Math.round(sin * r);

			cvDrawLine(filters[i], cvPoint(c + x, c + y),
					cvPoint(c - x, c - y), CvScalar.ONE, width, 8, 0);
			angles[i] = angle;
		}
		/*
		 * int sizeM1 = size - 1; int filterIndex = 0; for (int i = 0; i < size;
		 * i += step) { CvPoint p0 = cvPoint(i, 0); CvPoint p1 = cvPoint(sizeM1
		 * - i, sizeM1); cvDrawLine(filters[filterIndex], p0, p1, CvScalar.ONE,
		 * 3, 8, 0); angles[filterIndex] = Math.atan2(sizeM1, sizeM1 - 2 * i);
		 * filterIndex++; } for (int i = 1 + (size & 0x1); i < sizeM1; i +=
		 * step) { CvPoint p0 = cvPoint(sizeM1, i); CvPoint p1 = cvPoint(0,
		 * sizeM1 - i); cvDrawLine(filters[filterIndex], p0, p1, CvScalar.ONE,
		 * 3, 8, 0); angles[filterIndex] = Math.atan2(sizeM1 - 2 * i, -sizeM1);
		 * filterIndex++; }
		 * 
		 * CvMat circle = CvMat.create(size, size, CV_32FC1);
		 * cvDrawCircle(circle, cvPoint(size / 2, size / 2), size / 2 + 1,
		 * CvScalar.WHITE, -1, 8, 0);
		 */
		for (int i = 0; i < numFilters; i++) {
			// cvMul(filters[i], circle, filters[i], 1);
			sums[i] = (int) cvSum(filters[i]).blue();
		}
	}

	@Override
	public FeatureSet linkFeatures(List<Feature> features, IplImage img) {
		IplImage input = IplImage.create(img.width(), img.height(),
				IPL_DEPTH_32F, 1);
		cvSet(input, CvScalar.ONE);
		cvMul(input, img, input, 1 / 255.0);

		directionMap = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U,
				1);
		cvSetZero(directionMap);// , cvScalarAll(Double.NaN));

		IplImage max = IplImage.create(img.width(), img.height(),
				IPL_DEPTH_32F, 1);
		cvSetZero(max);

		IplImage filled = IplImage.create(img.width(), img.height(),
				IPL_DEPTH_32F, 1);

		IplImage mask = IplImage.create(img.width(), img.height(),
				IPL_DEPTH_8U, 1);

		for (int i = 0; i < filters.length; i++) {
			CvMat filter = filters[i];
			int sum = sums[i];

			/*
			 * IplImage flarge = IplImage.create(size * 8, size * 8,
			 * IPL_DEPTH_32F, 1); cvResize(filter, flarge, CV_INTER_NN);
			 * System.out.println(i + ": " + angles[i] + " " + sum);
			 * cvShowImage("filter", flarge); cvWaitKey();
			 */

			cvFilter2D(input, filled, filter, cvPoint(-1, -1));

			cvScale(filled, filled, 1.0 / sum, 0);

			cvCmp(max, filled, mask, CV_CMP_LT);
			cvSet(directionMap, cvScalarAll(/* angle */i), mask);
			cvMax(max, filled, max);

			// cvConvertScale(ones, ones, 100, 0);// 1.0/size, 0);
			/*
			 * cvShowImage("ones", ones); //cvConvertScale(max, ones, 120000.0 /
			 * size, 0); //cvShowImage("mask", mask); cvShowImage("max", max);
			 * cvWaitKey();
			 */
		}
		// cvShowImage("map", directionMap);
		cvShowImage("max", max);
		cvWaitKey();

		/*
		 * for (int i = 0; i < filters.length; i++) { cvCmpS(directionMap, i,
		 * mask, CV_CMP_EQ); cvAnd(mask, img, mask, null); cvSetZero(filled);
		 * cvSet(filled, cvScalarAll(120000), mask); cvShowImage("map", filled);
		 * cvWaitKey(); }
		 */

		long t0 = System.currentTimeMillis();
		
		IplImage temp = directionMap.clone();
		cvSetZero(directionMap);
		IplImage maxDil = max.clone();

		int size2 = 15;
		int border = size2/2;
		
		for (int i = 0; i < filters.length; i++) {
			cvCmpS(temp, i, mask, CV_CMP_EQ);
			cvSetZero(filled);
			cvSet(filled, CvScalar.ONE, mask);
			cvMul(max, filled, filled, 1);

			IplConvKernel strel = cvCreateStructuringElementEx(border*2+1, border*2+1,
					border, border, CV_SHAPE_RECT, null);
			cvDilate(filled, filled, strel, 1);

			cvCmp(maxDil, filled, mask, CV_CMP_LE);
			cvSet(directionMap, cvScalarAll(i), mask);
			cvMax(maxDil, filled, maxDil);
		}

		cvShowImage("max", maxDil);
		cvWaitKey();
		
		long t1 = System.currentTimeMillis();
		System.out.println((t1 - t0) + "ms");

		for (int i = 0; i < filters.length; i++) {
			cvCmpS(directionMap, i, mask, CV_CMP_EQ);
			cvAnd(mask, img, mask, null);
			cvSetZero(filled);
			cvSet(filled, cvScalarAll(120000), mask);
			cvShowImage("map", filled);
			cvWaitKey();
		}

		/*
		 * int xmax = img.width() - size; int ymax = img.height() - size; for
		 * (int y = size; y < ymax; y++) { for (int x = size; x < xmax; x++) {
		 * cvSet2D(directionMap, x, y, cvScalarAll(valueAt(img, x, y))); } }
		 */

		return super.linkFeatures(features, img);
	}

	private double valueAt(IplImage img, int x0, int y0) {
		return 0;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {

		return false;
	}
}
