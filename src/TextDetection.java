public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Image img = new Image(
				//"C:\\Users\\PilgerstorferP\\workspace\\TextDetection\\samples\\PORTOLAN_ATLAS.jpg");
				"C:\\Users\\PilgerstorferP\\workspace\\TextDetection\\samples\\British Isles.png");//TestbildM.jpg");//

		img.addProcessor(new ThresholdProcessor(200)); // 170 TB, 200 BI, 150 PA
		img.addProcessor(new InvertProcessor());
		img.addProcessor(new ThicknessProcessor(5));
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));

		//10/90
		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000, 5000);
		FeatureLinker linker = new AngleScanFeatureLinker(1, 0.05, 0);
		
		img.findCharacters(detector, linker);
		img.save("C:\\Users\\PilgerstorferP\\workspace\\TextDetection\\TestbildOut.jpg");
	}
}
