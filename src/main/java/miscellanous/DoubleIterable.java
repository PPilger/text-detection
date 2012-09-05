package miscellanous;

import java.util.Iterator;

public class DoubleIterable implements Iterable<Double> {
	private double[] array;
	
	public DoubleIterable(double... items) {
		array = items;
	}

	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < array.length;
			}

			@Override
			public Double next() {
				double item = array[i];
				i++;
				return item;
			}

			@Override
			public void remove() {
			}
			
		};
	}
	
}
