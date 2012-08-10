import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class ColorRangeProcessor {
		private IplImage rImg;
		private IplImage gImg;
		private IplImage bImg;

		/*public ColorRangeProcessor(IplImage colorImg, int rMin, int rMax, int gMin, int gMax, int bMin, int bMax) {
			rImg = IplImage.create(cvSize(colorImg.width(), colorImg.height()), 8,
					1);
			gImg = IplImage.create(cvSize(colorImg.width(), colorImg.height()), 8,
					1);
			bImg = IplImage.create(cvSize(colorImg.width(), colorImg.height()), 8,
					1);
			cvSplit(colorImg, rImg, gImg, bImg, null);
			cvThreshold(rImg, rImg, rThreshold, 255, CV_THRESH_BINARY);
			cvThreshold(gImg, gImg, gThreshold, 255, CV_THRESH_BINARY);
			cvThreshold(bImg, bImg, bThreshold, 255, CV_THRESH_BINARY);
		}

		@Override
		public void process(IplImage img) {
			cvSetZero(img);
			cvSet(img, CvScalar.WHITE, rImg);
			cvSet(img, CvScalar.WHITE, gImg);
			cvSet(img, CvScalar.WHITE, bImg);
		}*/
}
