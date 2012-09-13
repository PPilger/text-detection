package feature;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import validator.IValidator;


import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;

public class ContourFeatureDetector extends FeatureDetector {
	private IValidator perimeter;

	public ContourFeatureDetector(IValidator perimeter) {
		this.perimeter = perimeter;
	}

	@Override
	public int findFeatures(IplImage img, FeatureSet features) {
		List<ContourFeature> contourFeatures = new ArrayList<ContourFeature>();
		List<FeatureRule> rules = getRules();
		
		// find contours
		{
			CvMemStorage mem = cvCreateMemStorage(0);
			CvSeq contour = new CvSeq();
			IplImage temp = cvCloneImage(img);
			
			cvFindContours(temp, mem, contour, sizeof(CvContour.class),
					CV_RETR_LIST, CV_CHAIN_APPROX_NONE);
			for (; contour != null && !contour.isNull(); contour = contour.h_next()) {
				if (perimeter.isValid(contour.total())) {
					ContourFeature feature = ContourFeature.create(new CvContour(contour));
					
					boolean valid = true;
					for(FeatureRule rule : rules) {
						valid = valid && rule.isValid(feature);
					}
					if(valid) {
						contourFeatures.add(feature);
					}
				}
			}
		}
		
		//remove enclosed features
		{
			Iterator<ContourFeature> iter = contourFeatures.iterator();
			while(iter.hasNext()) {
				ContourFeature f0 = iter.next();

				for(ContourFeature f1 : contourFeatures) {
					if(f0.insideOf(f1)) {
						iter.remove();
						break;
					}
				}
			}
		}
		
		features.add(contourFeatures);
		
		return contourFeatures.size();
	}
}
