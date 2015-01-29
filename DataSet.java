package DecisionTreeBuildPack; 
import java.util.ArrayList;
import java.util.List;

/**
 * This class organizes the information of a data set into simple structures.
 * 
 */

public class DataSet {
	
	public List<Instance> instances = null; // ordered list of instances
	
	public DataSet() {
		// blank
	}
	
	public DataSet(List<Instance> instances) {
		this.instances = instances;
	}
	
	/**
	 * Add instance to collection.
	 */
	public void addInstance(Object[] datarow) {
		if (instances == null) {
			instances = new ArrayList<Instance>();
		}
		
		Instance instance = new Instance();
		
		for (int i =0; i < datarow.length - 1 ; i++) {
			instance.addAttribute(datarow[i].toString());
		}
		instance.setLabel(datarow[datarow.length -1].toString());
		
		instances.add(instance);
		
	}

}
