package miscellanous;

public interface Validator<T extends Number> {
	public boolean isValid(T value);
}
