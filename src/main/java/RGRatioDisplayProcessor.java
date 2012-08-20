import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class RGRatioDisplayProcessor extends ImageDisplayProcessor {
	double scale;

	public RGRatioDisplayProcessor(String title) {
		super(title);
		
		this.scale = 1;
	}

	public RGRatioDisplayProcessor(String title, int width, int height, double scale) {
		super(title, width, height);
		
		this.scale = scale;
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

		cvDiv(rImg, gImg, rgImg, 1);

		cvConvertScale(rgImg, rgImg, scale, 0);

		super.process(rgImg, null, null);
	}
	
}
