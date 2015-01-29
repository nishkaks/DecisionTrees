 package DecisionTreeBuildPack; 
/**
 * This class provides a framework for accessing a decision tree.
 * 
 */

public abstract class DecisionTree {
	/**
	 * Evaluates the learned decision tree on a test set
	 * @return the classification accuracy of the test set
	 */
	abstract public String[] classify(DataSet testSet);
	
	
	abstract public void print();
}