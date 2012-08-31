package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import image.DilateProcessor;
import image.Image;

import java.util.*;

import math.Line2D;
import math.Validator;
import math.Vector2D;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class DirectionBasedLinkingRule extends LinkingRule {
	// parameters
	private int filterSize;
	private int dilateSize;
	private int numAngles;
	private int lineWidth;
	private Validator<Double> featureRating;

	private HashMap<Feature, Histogram> hists;
	
	private IplImage img;
	private IplImage direction;
	private IplImage mask;
	private IplImage temp8u;
	private IplImage temp32f;

	private int border;
	private IplImage bigImg0;
	private IplImage bigImg1;
	private IplImage bigImg2;
	private CvMat subImg0;
	private CvMat subImg1;
	private CvMat subImg2;

	IplConvKernel[] strels;

	public DirectionBasedLinkingRule(int filterSize, int dilateSize, int numAngles, int lineWidth,
			Validator<Double> featureRating) {
		this.filterSize = filterSize;
		this.dilateSize = dilateSize;
		this.numAngles = numAngles;
		this.lineWidth = lineWidth;
		this.featureRating = featureRating;
	}
	


	@Override
	public void initialize(FeatureSet features, IplImage img) {
		IplImage input;
		IplImage max;

		// initialize images
		{
			int width = img.width();
			int height = img.height();

			this.img = img;

			input = IplImage.create(width, height, IPL_DEPTH_32F, 1);
			cvSet(input, CvScalar.ONE);
			cvMul(input, img, input, 1 / 255.0);

			direction = IplImage.create(width, height, IPL_DEPTH_8U, 1);
			cvSetZero(direction);

			max = IplImage.create(width, height, IPL_DEPTH_32F, 1);
			cvSetZero(max);

			temp8u = IplImage.create(width, height, IPL_DEPTH_8U, 1);
			temp32f = IplImage.create(width, height, IPL_DEPTH_32F, 1);
			mask = IplImage.create(width, height, IPL_DEPTH_8U, 1);

			CvRect rect = cvRect(filterSize / 2 + 1, filterSize / 2 + 1, width,
					height);

			border = filterSize / 2 + 2;
			width = width + 2 * border;
			height = height + 2 * border;
			bigImg0 = IplImage.create(width, height, IPL_DEPTH_8U, 1);
			bigImg1 = IplImage.create(width, height, IPL_DEPTH_8U, 1);
			bigImg2 = IplImage.create(width, height, IPL_DEPTH_8U, 1);
			subImg0 = CvMat.createHeader(width, height, IPL_DEPTH_8U, 1);
			subImg1 = CvMat.createHeader(width, height, IPL_DEPTH_8U, 1);
			subImg2 = CvMat.createHeader(width, height, IPL_DEPTH_8U, 1);

			cvGetSubRect(bigImg0, subImg0, rect);
			cvGetSubRect(bigImg1, subImg1, rect);
			cvGetSubRect(bigImg2, subImg2, rect);
		}

		// create filter kernels
		CvMat[] filters;
		{
			filters = new CvMat[numAngles];
			strels = new IplConvKernel[numAngles];

			double r = filterSize / 2;
			int c = (int) Math.round(r);
			for (int i = 0; i < numAngles; i++) {
				double angle = i / (double) numAngles * Math.PI;
				double sin = Math.sin(angle);
				double cos = Math.cos(angle);

				int x = (int) Math.round(cos * r);
				int y = (int) Math.round(sin * r);

				filters[i] = CvMat.create(filterSize, filterSize,
						CV_32FC1);
				cvSetZero(filters[i]);

				cvDrawLine(filters[i], cvPoint(c - x, c - y), cvPoint(c + x, c + y),
						CvScalar.ONE, lineWidth, 8, 0);

				double sum = cvSum(filters[i]).blue();
				cvScale(filters[i], filters[i], 1 / sum, 0);

				// create strel for linking
				int[] temp = new int[filterSize * filterSize];
				Line2D.draw(temp, filterSize, c - x, c - y, c + x, c + y);
				strels[i] = cvCreateStructuringElementEx(filterSize,
						filterSize, filterSize / 2, filterSize / 2,
						CV_SHAPE_CUSTOM, temp);
			}
		}

		// get the best angle for each pixel
		// the best angle is the one with the greatest value after filtering
		// the filters are stored in the array filters
		{
			IplImage filtered = temp32f;
			for (int i = 0; i < numAngles; i++) {
				CvMat filter = filters[i];

				cvFilter2D(input, filtered, filter, cvPoint(-1, -1));

				cvCmp(max, filtered, mask, CV_CMP_LT);
				cvSet(direction, cvScalarAll(i), mask);
				cvMax(max, filtered, max);
			}

			//cvShowImage("max", max);
			//cvWaitKey();
		}

		// Dilate angles to privilege better angles
		{
			IplImage temp = direction.clone();
			IplImage maxDil = max.clone();
			IplImage dil = temp32f;

			DilateProcessor processor = new DilateProcessor(dilateSize);

			cvSetZero(direction);
			for (int i = 0; i < numAngles; i++) {
				cvCmpS(temp, i, mask, CV_CMP_EQ);
				cvSetZero(dil);
				cvSet(dil, CvScalar.ONE, mask);
				cvMul(max, dil, dil, 1);

				processor.process(dil, null);
				// cvDilate(dil, dil, strels[i], 1);

				cvCmp(maxDil, dil, mask, CV_CMP_LE);
				cvSet(direction, cvScalarAll(i), mask);
				cvMax(maxDil, dil, maxDil);
			}

			// cvShowImage("max", maxDil);
			// cvWaitKey();
		}

		// debug output
		// outputDirections(img, direction, mask, temp32f, numAngles);

		hists = new HashMap<Feature, Histogram>();
		for (Feature feature : features) {
			// create a mask of the feature
			cvSetZero(mask);
			feature.fill(mask, CvScalar.WHITE);

			// set sub-images of direction and mask
			CvMat subDirection;
			CvMat subMask;
			{
				Vector2D fmin = feature.box().min();
				Vector2D fmax = feature.box().max();

				int xmin = (int) Math.min(Math.max(fmin.x, 0), img.width() - 1);
				int xmax = (int) Math.min(Math.max(fmax.x, 0), img.width() - 1);
				int ymin = (int) Math
						.min(Math.max(fmin.y, 0), img.height() - 1);
				int ymax = (int) Math
						.min(Math.max(fmax.y, 0), img.height() - 1);
				int width = xmax - xmin;
				int height = ymax - ymin;

				subDirection = CvMat.createHeader(height, width, CV_8UC1);
				subMask = CvMat.createHeader(height, width, CV_8UC1);

				CvRect rect = cvRect(xmin, ymin, width, height);

				cvGetSubRect(direction, subDirection, rect);
				cvGetSubRect(mask, subMask, rect);
			}

			// set histogram
			Histogram hist = new Histogram(subDirection, subMask, numAngles);
			/*
			 * double[] hist = new double[numAngles]; for (int i = 0; i <
			 * subDirection.rows(); i++) { for (int j = 0; j <
			 * subDirection.cols(); j++) { if (cvGetReal2D(subMask, i, j) != 0)
			 * { int val = (int) cvGetReal2D(subDirection, i, j); hist[val]++; }
			 * } }
			 * 
			 * // System.out.println(Arrays.toString(hist));
			 * 
			 * // calculate sum double sum = 0; for (int i = 0; i < hist.length;
			 * i++) { sum += hist[i]; }
			 * 
			 * // calculate percentages for (int i = 0; i < hist.length; i++) {
			 * hist[i] = hist[i] / sum; }
			 */

			hists.put(feature, hist);

			// cvShowImage("mask", mask);
			// cvWaitKey();
		}
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		Histogram hist0 = hists.get(f0);
		Histogram hist1 = hists.get(f1);

		//double angle = 0;
		int idx = 0;

		{
			double max = 0;

			for (int i = 0; i < hist0.size(); i++) {
				double val = Math.min(hist0.get(i), hist1.get(i));
				if (max < val) {
					idx = i;
					max = val;
				}
			}

			//angle = idx * Math.PI / hist0.size();
		}

		// revoke links where the features don't have the same direction
		{
			double bestVal = 0;

			for (int i = 0; i < hist0.size(); i++) {
				double val = Math.min(hist0.get(i), hist1.get(i));
				if (bestVal < val) {
					bestVal = val;
				}
			}

			if (!featureRating.isValid(bestVal)) {
				return false;
			}

			/*
			 * int max0 = hist0.max(); int max1 = hist1.max();
			 * 
			 * int diff = max0 - max1; diff = diff < hist0.size()/2 ? diff :
			 * hist0.size() - diff;
			 * 
			 * if(diff > 1) { return false; }
			 * 
			 * double val0 = hist0.get(max0); double val1 = hist1.get(max1);
			 * 
			 * if(val0 >= val1){ idx = max0; } else { idx = max1; } angle = idx
			 * * Math.PI / hist0.size();
			 */
		}

		//
		{
			LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));

			cvSetZero(bigImg0);
			f0.fill(subImg0, CvScalar.WHITE);

			cvSetZero(bigImg1);
			f1.fill(subImg1, CvScalar.WHITE);

			cvCmpS(direction, idx, temp8u, CV_CMP_EQ);
			cvAnd(subImg0, temp8u, subImg0, null);
			cvAnd(subImg1, temp8u, subImg1, null);

			CvRect rectS;
			CvRect rectB;
			{
				Vector2D min = lf.box().min();
				Vector2D max = lf.box().max();
				rectS = Image.clip(img, min, max);

				int x = (int) Math.round(min.x);
				int y = (int) Math.round(min.y);
				rectB = cvRect(x, y, rectS.width() + 2 * border, rectS.height()
						+ 2 * border);
			}

			cvSetImageROI(bigImg0, rectB);
			cvSetImageROI(bigImg1, rectB);
			cvSetImageROI(bigImg2, rectB);

			IplConvKernel strel = strels[idx];
			cvMorphologyEx(bigImg0, bigImg0, bigImg2, strel, CV_MOP_CLOSE, 1);
			cvMorphologyEx(bigImg1, bigImg1, bigImg2, strel, CV_MOP_CLOSE, 1);

			cvOr(bigImg0, bigImg1, bigImg0, null);

			cvMorphologyEx(bigImg0, bigImg0, bigImg2, strel, CV_MOP_BLACKHAT, 1);

			cvResetImageROI(bigImg0);
			cvResetImageROI(bigImg1);
			cvResetImageROI(bigImg2);

			CvMat dirSub = cvCreateMatHeader(rectS.width(), rectS.height(),
					CV_8UC1);
			CvMat maskSub = cvCreateMatHeader(rectS.width(), rectS.height(),
					CV_8UC1);
			cvGetSubRect(direction, dirSub, rectS);
			cvGetSubRect(subImg0, maskSub, rectS);

			Histogram hist = new Histogram(dirSub, maskSub, hist0.size());
			int max = hist.max();
			if (Math.abs(idx - max) > 1) {
				return false;
			}
		}

		return true;
	}

	private void outputDirections(IplImage img, IplImage direction,
			IplImage mask, IplImage temp32f, int numAngles) {
		int scale = 5;
		int width = img.width() * scale;
		int height = img.height() * scale;
		IplImage imgL = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_8U, 1);
		IplImage directionL = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_8U, 1);
		IplImage maskL = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_8U, 1);
		IplImage angle = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_32F, 1);
		cvResize(img, imgL, CV_INTER_NN);
		cvResize(direction, directionL, CV_INTER_NN);
		cvResize(mask, maskL, CV_INTER_NN);
		/*
		 * int width = 200*scale; int height = 120*scale; int px = 800, py =
		 * 600; //int px = 850, py = 650; //int px = 620, py = 580; CvRect roi =
		 * cvRect(px*scale, py*scale, width, height); cvSetImageROI(imgL, roi);
		 * cvSetImageROI(directionL, roi); cvSetImageROI(maskL, roi);
		 * cvSetImageROI(angle, roi);
		 */
		img = imgL;
		direction = directionL;
		mask = maskL;

		/*
		 * IplImage angle = temp32f;
		 */
		int size3 = 5;
		int border3 = size3 / 2 + 1;

		img = img.clone();

		// get angle-map
		cvSetZero(angle);
		for (int i = 0; i < numAngles; i++) {
			double a = i / (double) numAngles * Math.PI;

			cvCmpS(direction, i, mask, CV_CMP_EQ);
			cvSet(angle, cvScalarAll(a), mask);
		}

		// average angles over the displayed area
		// not always correct (180° == 0°)
		// cvSmooth(angle, angle, CV_BLUR, size3);

		// draw angles
		for (int y = border3; y < height - border3; y += size3) {
			for (int x = border3; x < width - border3; x += size3) {
				double a = cvGetReal2D(angle, y, x);
				CvPoint p0 = cvPoint(
						x - (int) Math.round(Math.cos(a) * border3), y
								- (int) Math.round(Math.sin(a) * border3));
				CvPoint p1 = cvPoint(
						x + (int) Math.round(Math.cos(a) * border3), y
								+ (int) Math.round(Math.sin(a) * border3));
				cvDrawLine(img, p0, p1, CvScalar.GRAY, 1, 0, 0);
			}
		}

		cvSaveImage("direction.png", img);
		// cvShowImage("img", img);
		cvWaitKey();
	}
}
