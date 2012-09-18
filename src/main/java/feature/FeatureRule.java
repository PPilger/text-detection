package feature;

/**
 * A FeatureRule validates Features. It can be used to select features with
 * special attributes and do something with them.
 * 
 * @author PilgerstorferP
 * 
 */
public interface FeatureRule {
	public boolean isValid(Feature feature);
}
