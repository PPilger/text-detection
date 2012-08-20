

public class TempDisplayProcessor extends ImageDisplay implements ImageProcessor {

	public TempDisplayProcessor(String title) {
		super(title);
	}

	public TempDisplayProcessor(String title, int width, int height) {
		super(title, width, height);
	}

	@Override
	public void process(ImageCollection images) {
		super.show(images.getTemp());
	}
}
