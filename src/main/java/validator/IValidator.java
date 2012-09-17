package validator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface IValidator {
	/**
	 * Checks if value is valid or not.
	 * 
	 * @param value
	 * @return true if value is valid, false otherwise
	 */
	public boolean isValid(int value);

	/**
	 * Checks every pixel of src for validity. All valid pixels are set to
	 * WHITE, the invalid are set to black. src remains unchanged, while dst
	 * contains the result.
	 * 
	 * src and dst need to have the same amount of channels.
	 * 
	 * @param src
	 * @param dst
	 *            needs to be of type 8U or 8S
	 */
	public void validate(IplImage src, IplImage dst);
}
