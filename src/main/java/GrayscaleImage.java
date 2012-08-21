import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.*;


public class GrayscaleImage {
	private IplImage img;
	private IplImage temp;
	
	public GrayscaleImage(IplImage img) {
		this.img = img;
		this.temp = cvCloneImage(img);
	}

	public GrayscaleImage(Image img) {
		this.img = img.getImageCollection().getGray();
		this.temp = cvCloneImage(this.img);
	}
	
	public IplImage getImage() {
		return img;
	}
	
	public IplImage getTemp() {
		return temp;
	}
	
	public void process(SingleImageProcessor processor) {
		processor.process(img, temp);
	}
}
