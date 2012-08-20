import java.io.File;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		//britishIsles();
		portolanAtlas();
	}

	public static void portolanAtlas() {
		Image img = new Image("samples" + File.separator + "PORTOLAN_ATLAS_S.jpg");
		img.addProcessor(new ThresholdProcessor(165)); // 170 TB, 200 BI, 150 PA
		img.addProcessor(new InvertProcessor());
		
		//img.addProcessor(new RGRatioDisplayProcessor("1", 1200, 800, 0.5));
		//img.addProcessor(new RBRatioDisplayProcessor("1", 1200, 800, 0.5));
		//img.addProcessor(new GBRatioDisplayProcessor("1", 1200, 800, 0.5));
		
		//img.addProcessor(new RGBRatioEraseProcessor(0.9, 3, 1, 3, 1, 3, 2, true));
		//img.addProcessor(new ImageDisplayProcessor("1", 1200, 800));
		//img.addProcessor(new RGBRatioEraseProcessor(0.9, 3, 0.8, 3, 0.95, 3, 2, true));
		//img.addProcessor(new ImageDisplayProcessor("3", 1200, 800));

		int i = 10;

		img.addProcessor(new ImageDisplayProcessor("color", 1200, 800));
		img.addProcessor(new FirstDerivateEraseProcessor(120, 3));
		img.addProcessor(new SecondDerivateEraseProcessor(130, 3));

		//img.addProcessor(new ImageDisplayProcessor("rgRatio", 1200, 800, 0.5));
		//img.addProcessor(new ImageDisplayProcessor("rbRatio", 1200, 800, 0.5));
		//img.addProcessor(new ImageDisplayProcessor("gbRatio", 1200, 800, 0.5));

		img.addProcessor(new CloseProcessor(3));
		img.addProcessor(new ThicknessProcessor(1, 7));
		//img.addProcessor(new ImageDisplayProcessor("processed", "a"+i++, 1200, 800));

		//img.addProcessor(new ImageDisplayProcessor("gray", "a"+i++, 1200, 800));
		img.addProcessor(new RemoveLinesProcessor(70));
		img.addProcessor(new ImageDisplayProcessor("temp", "a"+i++, 1200, 800));
		img.addProcessor(new ImageDisplayProcessor("processed", "a"+i++, 1200, 800));
		
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));
		img.addProcessor(new ImageDisplayProcessor("processed", "a"+i++, 1200, 800));

		

		FeatureDetector detector = new ContourBasedFeatureDetector(15, 1000000, 1, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);

		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		img.setImageDisplay(null, display, display);
		img.findText(detector, linker);
		img.save("Portolan Atlas.jpg");
	}
	
	public static void britishIsles() {
		Image img = new Image("samples" + File.separator + "British Isles.png");
		img.addProcessor(new ThresholdProcessor(207));
		img.addProcessor(new InvertProcessor());
		img.addProcessor(new ColorEraseProcessor(0, 100, 0, 50, 0, 50, 10, false));
		img.addProcessor(new ThicknessProcessor(1, 5));
		img.addProcessor(new RemoveLinesProcessor(60));
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));
		
		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000, 100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);
		
		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		//img.setImageDisplay(display, display, display);
		img.findText(detector, linker);
		img.save("British Isles.png");
	}
}
