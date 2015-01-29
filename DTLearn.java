package DecisionTreeBuildPack; 
import java.io.IOException;

import java.util.List;

public class DTLearn {
	
	static ArffFile arffTrainData;
	static ArffFile arffTestData;
	static int inputM;

	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("usage: java -jar dt-learn.jar <train-set-file> <test-set-file> m");
			System.exit(-1);
		}

		try {
			// Load Arff file
			arffTrainData = ArffFile.load(args[0]);
			arffTestData = ArffFile.load(args[1]);
			inputM = Integer.parseInt(args[2]);

			if (inputM < 0) {
				System.out.println("Error: m must be a positive number");
				System.exit(-1);
			}
			
			//System.out.println(DTLearn.arffTrainData.getAttributeData("'cp'").length);
			//System.out.println(arffTrainData.getAttributeName(arffTrainData.getNumberOfAttributes()-1));
			//System.out.println(arffTrainData.dump());
			
		
			
			// Create training data in DataSet format
			DataSet trainSet = createDataSet(arffTrainData.getData());
			

			// Create decision tree
			DecisionTree tree = new DecisionTreeImpl(trainSet);
			// print the tree and calculate accuracy
			tree.print();
			
			// Create test data in DataSet format
			DataSet testSet = createDataSet(arffTestData.getData());
			
			// Test accuracy
			double testAccuracy = calcTestAccuracy(testSet, tree.classify(testSet));
			
			System.out.println("Prediction accuracy on the test set is: " 
					+ String.format("%.5f", testAccuracy));
			
		
			
//          // Code used for plotting part 2 of the homework			
//			double[] trainpercent = {0.05,0.1,0.2,0.5,1};
//			int n = trainSet.instances.size();
//			for (double percent : trainpercent) {
//				int trainSubSize = (int) (n * percent);
//				double minimum = 100;
//				double maximum = -100;
//				double average = 0;
//				
//				for (int i=0; i<10;i++) {
//					Collections.shuffle(trainSet.instances);
//					
//					DataSet trainSubSet = new DataSet();
//					trainSubSet.instances = trainSet.instances.subList(0, trainSubSize);
//					
//					DecisionTree tree = new DecisionTreeImpl(trainSubSet);
//					double testAccuracy = calcTestAccuracy(testSet, tree.classify(testSet));
//					
//					average+= testAccuracy;
//					
//					if (testAccuracy < minimum) {
//						minimum = testAccuracy;
//					}
//					
//					if (testAccuracy > maximum) {
//						maximum = testAccuracy;
//					}
//					
//					//System.out.println("n = "+trainSet.instances.size()+" trainSubSize="+trainSubSet.instances.size()+" testAccuracy = "+testAccuracy);
//				}
//				average/=10.0;
//				System.out.println("percent = "+percent+" average="+String.format("%.3f",average)+" minimum"+String.format("%.3f",minimum)+" maximum"+String.format("%.3f",maximum));
//			
//			}
			
		} catch (ArffFileParseError e) {
			System.out.println("Couldn't parse ARFF file.");
		} catch (IOException e) {
			System.out.println("File IO Exception.");
		}		








	}

	/**
	 * Converts from data format of ArffFile to DataSet format.
	 * 
	 */
	private static DataSet createDataSet(List<Object[]> data) {
		
		DataSet set = new DataSet();
		
		for (int i = 0; i < data.size(); i++) {
			Object[] datarow = data.get(i);
			set.addInstance(datarow);
		}
		
		return set;
	}
	
	/**
	 * Calculate predication accuracy on the test set.
	 */
	private static double calcTestAccuracy(DataSet test, String[] results) {
		
		if(results == null) {
			 System.out.println("Error in calculating accuracy: " +
			 		"You must implement the classify method");
			 System.exit(-1);
		}
		
		List<Instance> testInsList = test.instances;
		if(testInsList.size() == 0) {
			System.out.println("Error: Size of test set is 0");
			System.exit(-1);
		}
		if(testInsList.size() > results.length) {
			System.out.println("Error: The number of predictions is inconsistant " +
					"with the number of instances in test set, please check it");
			System.exit(-1);
		}
		
		System.out.println("");
		System.out.println(" Predicted     Actual");
		System.out.println("==========   ========");
		int correct = 0, total = testInsList.size();
		for(int i = 0; i < testInsList.size(); i ++) {
			System.out.println(String.format("%10s %10s",results[i], testInsList.get(i).label));
			if(testInsList.get(i).label.equals(results[i]))
				correct ++;			
		}
			

		System.out.println("");
		System.out.println("Number of correctly classified test instances: " + correct);
		System.out.println("Total number of instances in the test set: "+total);
		
		return (correct * 1.0 / total);
	}




}
