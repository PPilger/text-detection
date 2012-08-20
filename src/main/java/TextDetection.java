import java.io.File;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Image img = new Image("samples" + File.separator + "PORTOLAN_ATLAS.jpg");
		img.addProcessor(new ColorToGrayProcessor());
		img.addProcessor(new ThresholdProcessor(165)); // 170 TB, 200 BI, 150 PA
		img.addProcessor(new InvertProcessor());
		
		//img.addProcessor(new ColorEraseProcessor(70, 130, 70, 110, 80, 100, 10, true));
		//img.addProcessor(new ColorEraseProcessor(70, 110, 70, 100, 80, 90, 10, true));
		
		img.addProcessor(new RGRatioDisplayProcessor("1", 1200, 800, 0.5));
		img.addProcessor(new RBRatioDisplayProcessor("1", 1200, 800, 0.5));
		img.addProcessor(new GBRatioDisplayProcessor("1", 1200, 800, 0.5));
		

		img.addProcessor(new RGBRatioEraseProcessor(0.9, 3, 1, 3, 1, 3, 2, true));
		//img.addProcessor(new ImageDisplayProcessor("3"));
		

		img.addProcessor(new RGBRatioEraseProcessor(0.9, 2, 0, 1000, 0, 1000, 2, true));
		img.addProcessor(new TempDisplayProcessor("3", 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0.9, 2, 0, 1000, 0, 1000, 2, true));
		img.addProcessor(new TempDisplayProcessor("3", 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0.9, 2, 0, 1000, 0, 1000, 2, true));
		img.addProcessor(new TempDisplayProcessor("3", 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0.9, 2, 0, 1000, 0, 1000, 2, true));
		img.addProcessor(new TempDisplayProcessor("3", 1200, 800));
		
		img.addProcessor(new RGBRatioEraseProcessor(0, 1000, 0, 1000, 0, 1000, 2, true));
		
		//img.addProcessor(new ColorEraseProcessor(0, 50, 0, 50, 0, 100, 10));
		//img.addProcessor(new ColorEraseProcessor(0, 50, 0, 50, 0, 100, 10));
		
		
		img.addProcessor(new ThicknessProcessor(1, 5));
		//img.addProcessor(new RemoveLinesProcessor(60));
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));

		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000, 100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);
		
				//"PORTOLAN_ATLAS.jpg");
				//TestbildM.jpg");//

		/*Image img = new Image("samples" + File.separator + "British Isles.png");
		img.addProcessor(new ColorToGrayProcessor());
		img.addProcessor(new ThresholdProcessor(207)); // 170 TB, 200 BI, 150 PA
		img.addProcessor(new InvertProcessor());
		img.addProcessor(new ColorEraseProcessor(0, 50, 0, 50, 0, 100, 10, false));
		img.addProcessor(new ThicknessProcessor(1, 5));
		img.addProcessor(new RemoveLinesProcessor(60));
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));
		
		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000, 100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);*/
		
		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		img.setImageDisplay(display, display, display);
		img.findText(detector, linker);
		img.save("TestbildOut.jpg");
	}
}
