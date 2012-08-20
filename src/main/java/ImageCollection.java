import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class ImageCollection {
	private Map<String, IplImage> images;
	
	public ImageCollection(IplImage color) {
		images = new HashMap<String, IplImage>();
		
		IplImage img = IplImage.create(color.cvSize(), IPL_DEPTH_8U, 1);
		
		images.put("color", color);
		images.put("processed", img);
		images.put("temp", cvCloneImage(img));
		
		IplImage red = cvCloneImage(img);
		IplImage green = cvCloneImage(img);
		IplImage blue = cvCloneImage(img);
		
		cvSplit(color, blue, green, red, null);

		images.put("red", red);
		images.put("green", green);
		images.put("blue", blue);

		IplImage rgRatio = IplImage.create(img.cvSize(), IPL_DEPTH_32F, 1);
		IplImage rbRatio = cvCloneImage(rgRatio);
		IplImage gbRatio = cvCloneImage(rgRatio);

		cvDiv(red, green, rgRatio, 1);
		cvDiv(red, blue, rbRatio, 1);
		cvDiv(green, blue, gbRatio, 1);

		images.put("rgRatio", rgRatio);
		images.put("rbRatio", rbRatio);
		images.put("gbRatio", gbRatio);
	}
	
	public IplImage get(String name) {
		return images.get(name);
	}

	public IplImage getColor() {
		return images.get("color");
	}

	public IplImage getProcessed() {
		return images.get("processed");
	}

	public IplImage getTemp() {
		return images.get("temp");
	}

	public IplImage getRed() {
		return images.get("red");
	}

	public IplImage getGreen() {
		return images.get("green");
	}

	public IplImage getBlue() {
		return images.get("blue");
	}

	public IplImage getRGRatio() {
		return images.get("rgRatio");
	}

	public IplImage getRBRatio() {
		return images.get("rbRatio");
	}

	public IplImage getGBRatio() {
		return images.get("gbRatio");
	}
}
