import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;

public class ContourBasedFeatureDetector implements FeatureDetector {
	private int minPerimeter;
	private int maxPerimeter;
	private int minArea;
	private int maxArea;

	public ContourBasedFeatureDetector(int minPerimeter, int maxPerimeter, int minArea, int maxArea) {
		this.minPerimeter = minPerimeter;
		this.maxPerimeter = maxPerimeter;
		this.minArea = minArea;
		this.maxArea = maxArea;
	}

	@Override
	public List<Feature> findFeatures(IplImage img) {
		List<ContourFeature> contourFeatures = new ArrayList<ContourFeature>();
		
		// find contours
		{
			CvMemStorage mem = CvMemStorage.create();
			CvSeq contour = new CvSeq();
			IplImage temp = cvCloneImage(img);
			
			cvFindContours(temp, mem, contour, sizeof(CvContour.class),
					CV_RETR_LIST, CV_CHAIN_APPROX_NONE);
			for (; contour != null; contour = contour.h_next()) {
				if (minPerimeter < contour.total()
						&& contour.total() < maxPerimeter) {
					
					ContourFeature feature = ContourFeature.create(new CvContour(contour));
					contourFeatures.add(feature);
				}
			}
		}
		
		//remove enclosed features
		{
			Iterator<ContourFeature> iter = contourFeatures.iterator();
			while(iter.hasNext()) {
				ContourFeature f0 = iter.next();
				
				boolean inside = false;
				for(ContourFeature f1 : contourFeatures) {
					if(f0.insideOf(f1)) {
						inside = true;
						break;
					}
				}
				
				if(inside || maxArea <= f0.area() || f0.area() <= minArea) {
					iter.remove();
				}
			}
		}

		return new ArrayList<Feature>(contourFeatures);
	}
}
