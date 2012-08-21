import java.io.File;
import java.util.List;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		 britishIsles();
		//portolanAtlas();
	}

	public static void portolanAtlas() {
		Image img = new Image("samples" + File.separator
				+ "PORTOLAN_ATLAS_S.jpg");
		ImageCollection images = img.getImageCollection();
		ImageDisplay display = new ImageDisplay("output", 1200, 800);

		GrayscaleImage lines = new GrayscaleImage(img);
		{

			lines.process(new ThresholdProcessor(165)); // 170 TB, 200 BI, 150
														// PA
			lines.process(new InvertProcessor());
			lines.process(new BigObjectEraseProcessor(3));
			lines.process(new SmallObjectErasorProcessor(20));
			lines.process(new LineSegmentsProcessor(40, 50, 800));

			display.show(lines.getImage(), images.getGray());
		}

		{
			img.process(new ThresholdProcessor(165)); // 170 TB, 200 BI, 150 PA
			img.process(new InvertProcessor());
			
			display.show(images.getProcessed());
			img.process(new CloseProcessor(3));
			img.process(new BigObjectEraseProcessor(11));
			display.show(images.getProcessed());
			img.process(new SmallObjectErasorProcessor(10));
			display.show(images.getProcessed());
			img.process(new ThicknessProcessor(1, 7));
			//img.process(new CloseProcessor(3));
			display.show(images.getProcessed());
			
			img.process(new EraseProcessor(lines.getImage()));
			
			display.show(images.getProcessed());

			// img.process(new RGRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new RBRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new GBRatioDisplayProcessor("1", 1200, 800, 0.5));

			// img.process(new RGBRatioEraseProcessor(0.9, 3, 1, 3, 1, 3, 2,
			// true));
			// img.process(new ImageDisplayProcessor("1", 1200, 800));
			// img.process(new RGBRatioEraseProcessor(0.9, 3, 0.8, 3, 0.95, 3,
			// 2,
			// true));
			// img.process(new ImageDisplayProcessor("3", 1200, 800));

			img.process(new FirstDerivateEraseProcessor(120, 3));
			img.process(new SecondDerivateEraseProcessor(130, 3));

			img.process(new CloseProcessor(3));
			img.process(new ThicknessProcessor(1, 7));

			img.process(new RemoveLinesProcessor(100));

			img.process(new DilateProcessor(3));
			img.process(new CloseProcessor(3));
		}

		FeatureDetector detector = new ContourBasedFeatureDetector(15, 1000000,
				1, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);

		img.setImageDisplay(display, display);
		img.findText(detector, linker);
		img.save("Portolan Atlas.jpg");
	}

	public static void britishIsles() {
		Image img = new Image("samples" + File.separator + "British Isles.png");

		img.process(new ThresholdProcessor(207));
		img.process(new InvertProcessor());
		img.process(new ColorEraseProcessor(0, 100, 0, 50, 0, 50, 10, false));
		img.process(new ThicknessProcessor(1, 5));
		img.process(new RemoveLinesProcessor(60));
		img.process(new DilateProcessor(3));
		img.process(new CloseProcessor(3));

		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000,
				100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);

		//ImageDisplay display = new ImageDisplay("output", 1200, 800);
		//img.setImageDisplay(display, display);

		FeatureSet features = img.findText(detector, linker);
		
		features.save("Features.js");
		img.save("British Isles.png");
	}
}
