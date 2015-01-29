package DecisionTreeBuildPack; 
import java.util.List;
import java.util.ArrayList;

/**
 * @author Nishka
 * 
 * Holds details for one instance of data
 *
 */
public class Instance {
	
	public String label;
	public List<String> attributes = null;

	/**
	 * Add attribute values in the order of
	 * attributes as specified by the dataset
	 */
	public void addAttribute(String attr) {
		if (attributes == null) {
			attributes = new ArrayList<String>();
		}
		attributes.add(attr);
	}
	
	/**
	 * Add label value to the instance
	 */
	public void setLabel(String _label) {
		label = _label;
	}
	

}
