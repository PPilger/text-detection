import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class RGBRatioEraseProcessor implements ImageProcessor {
	private double rgMinRatio;
	private double rgMaxRatio;
	private double rbMinRatio;
	private double rbMaxRatio;
	private double gbMinRatio;
	private double gbMaxRatio;
	private int border;
	private boolean and;

	public RGBRatioEraseProcessor(double rgMinRatio, double rgMaxRatio, double rbMinRatio, double rbMaxRatio,
			double gbMinRatio, double gbMaxRatio, int border, boolean and) {
		this.rgMinRatio = rgMinRatio;
		this.rgMaxRatio = rgMaxRatio;
		this.rbMinRatio = rbMinRatio;
		this.rbMaxRatio = rbMaxRatio;
		this.gbMinRatio = gbMinRatio;
		this.gbMaxRatio = gbMaxRatio;
		this.border = border;
		this.and = and;
	}

	@Override
	public void process(IplImage img, IplImage colorImg, IplImage temp) {
		IplImage rImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage gImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage bImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);

		cvSplit(colorImg, rImg, gImg, bImg, null);

		IplImage rgImg = IplImage
				.create(cvSize(img.width(), img.height()), IPL_DEPTH_32F, 1);
		IplImage rbImg = IplImage
				.create(cvSize(img.width(), img.height()), IPL_DEPTH_32F, 1);
		IplImage gbImg = IplImage
				.create(cvSize(img.width(), img.height()), IPL_DEPTH_32F, 1);

		cvDiv(rImg, gImg, rgImg, 1);
		cvDiv(rImg, bImg, rbImg, 1);
		cvDiv(gImg, bImg, gbImg, 1);
		
		cvInRangeS(rgImg, cvScalarAll(rgMinRatio), cvScalarAll(rgMaxRatio), rImg);
		cvInRangeS(rbImg, cvScalarAll(rbMinRatio), cvScalarAll(rbMaxRatio), gImg);
		cvInRangeS(gbImg, cvScalarAll(gbMinRatio), cvScalarAll(gbMaxRatio), bImg);

		cvSetZero(temp);

		if (and) {
			cvAnd(rImg, gImg, temp, null);
			cvAnd(temp, bImg, temp, null);
		} else {
			cvOr(rImg, gImg, temp, null);
			cvOr(temp, bImg, temp, null);
		}
		cvSet(temp, CvScalar.WHITE, temp);
		
		//cvConvertScale(gbImg, temp, 255, 0);

		new DilateProcessor(border).process(temp, null, null);

		cvSet(img, CvScalar.BLACK, temp);
	}

}
