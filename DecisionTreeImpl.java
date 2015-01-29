package DecisionTreeBuildPack; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The implementation details of the class DecisionTree
 */
public class DecisionTreeImpl extends DecisionTree {
	
	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// this is void purposefully
	}
	
	private DecTreeNode rootNode;
	private static String[] overallMajorityLabel;

	// list of remaining questions 
	private ArrayList<Integer> remainingQuestions = new ArrayList<Integer>();
	
	private double log2 = Math.log10(2);
	

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train 
	 * 			the training set
	 */
	 public DecisionTreeImpl(DataSet train) {
		
		overallMajorityLabel = majorityLabel(train);
		rootNode = new DecTreeNode("", "", "ROOT", false);
		rootNode.displayText = "ROOT";

		
		// populate list of remaining features 
		int numAttr = DTLearn.arffTrainData.getNumberOfAttributes();
		for (int i = 0; i < numAttr - 1; i++ ) { 
			remainingQuestions.add(i);
		}		
		
		//internalNodesList = new ArrayList<Integer>();
		//internalNodesList.add(rootNode.nodeRefNum);
		buildTree(train,remainingQuestions, rootNode,overallMajorityLabel);	
		rootNode.displayText = ""; // just to be consistent with the given output
	}	
	
	
	private String[] majorityLabel(DataSet data) {
		
		int m = data.instances.size();
		int countClassOne = 0;
		int countClassTwo = 0;
		String[] returnVal = new String[4];
		
		for(int i=0; i<m; i++) {
			if (data.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[0])) {
				countClassOne++;
			}
			else if (data.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[1])) {
				countClassTwo++;
			}
		}
		
		if (countClassOne >= countClassTwo) {
			returnVal[0] = DTLearn.arffTrainData.getAttributeData("class")[0];
		}
		else {
			returnVal[0] = DTLearn.arffTrainData.getAttributeData("class")[1];
		}
		
		if (countClassOne == 0 || countClassTwo == 0) {
			returnVal[1] = "Y";
		}
		else {
			returnVal[1] = "N";
		}
		
		returnVal[2] = Integer.toString(countClassOne);
		returnVal[3] = Integer.toString(countClassTwo);
		
		return returnVal;	
	}
	
	private void buildTree(DataSet examples,ArrayList<Integer> questions, DecTreeNode currNode, String[] parentLabel) {	

		String[] labels = new String[2];
		labels = majorityLabel(examples);
		
		if (examples.instances.size() < DTLearn.inputM) { // there are fewer than m training instances reaching the node
			currNode.terminal = true;
			currNode.label = labels[0];  
			currNode.displayText = currNode.displayText + " [" + labels[2] +" " + labels[3] + "]";
			return;
		}

	
		currNode.label = labels[0];
		if (labels[1].equals("Y")) { // all of the training instances reaching the node belong to the same class
			currNode.terminal = true;		
			currNode.displayText = currNode.displayText + " [" + labels[2] +" " + labels[3] + "]";
			return;
		} 
		else {

			Object[] returnVal = bestQuestion(examples,questions); // returns best question index and threshold in case of numeric features
			int q = Integer.parseInt(returnVal[0].toString()); // best question index
			
			if (q == -99) {  // equals("NOMOREQUESTIONS") || q.equals("NOINFORMATIONGAIN") || q.equals("NOCANDIDATESPLITS")) { 
				currNode.terminal = true;   
				return;
			}
			
			String questionDesc = DTLearn.arffTrainData.getAttributeName(q);
			currNode.attribute = Integer.toString(q);
			currNode.displayText = currNode.displayText + " [" + labels[2] +" " + labels[3] + "]";
			
			//System.out.println("Nishka = best question = "+questionDesc);
			

			if (DTLearn.arffTrainData.getAttributeType(questionDesc).equals("nominal")) {
				int n = DTLearn.arffTrainData.getAttributeData(questionDesc).length;
				ArrayList<Integer> newQuestions = newCopyQuestions(questions,q);
				for(int i=0;i <n ; i++) {

					DecTreeNode childNode = new DecTreeNode("", "", DTLearn.arffTrainData.getAttributeData(questionDesc)[i], false);
					DataSet newExamples = newCopyExamples(examples,DTLearn.arffTrainData.getAttributeData(questionDesc)[i],q);
					childNode.displayText = questionDesc + " = " + DTLearn.arffTrainData.getAttributeData(questionDesc)[i] ;

					currNode.addChild(childNode);
					buildTree(newExamples,newQuestions,childNode,labels);

				}				
			}
			else { // numeric
				Double threshold = Double.parseDouble(returnVal[1].toString());
				currNode.threshold = threshold;
				DecTreeNode childNode1 = new DecTreeNode("", "", "LEFT", false);
				childNode1.displayText = questionDesc + " <= " + String.format("%.6f", threshold) ;
				ArrayList<DataSet> newExamples = newCopyExamplesNumeric(examples,q,threshold);
				currNode.addChild(childNode1);
				buildTree(newExamples.get(0),questions,childNode1,labels);
				
				DecTreeNode childNode2 = new DecTreeNode("", "", "RIGHT", false);
				childNode2.displayText = questionDesc + " > " + String.format("%.6f", threshold) ;
				currNode.addChild(childNode2);
				buildTree(newExamples.get(1),questions,childNode2,labels);
			}
				
		}
	}
	
	public ArrayList<DataSet> newCopyExamplesNumeric(DataSet examples,int q,double threshold) {

		int m = examples.instances.size();
		ArrayList<DataSet> returnSet = new ArrayList<DataSet>();
		List<Instance> instanceList1 = new ArrayList<Instance>();
		List<Instance> instanceList2 = new ArrayList<Instance>();

		for (int i = 0; i < m; i++) {
			if (Double.parseDouble(examples.instances.get(i).attributes.get(q)) <= threshold) { // LEFT <=
				Instance newInstance = new Instance();
				for(int j = 0; j < examples.instances.get(i).attributes.size() ; j ++)
					newInstance.addAttribute(examples.instances.get(i).attributes.get(j));
				newInstance.setLabel(examples.instances.get(i).label);				
				instanceList1.add(newInstance);
			}
			else { // RIGHT >
				Instance newInstance = new Instance();
				for(int j = 0; j < examples.instances.get(i).attributes.size() ; j ++)
					newInstance.addAttribute(examples.instances.get(i).attributes.get(j));
				newInstance.setLabel(examples.instances.get(i).label);				
				instanceList2.add(newInstance);
			}
		}
		
		DataSet dataset1 = new DataSet(instanceList1);
		DataSet dataset2 = new DataSet(instanceList2);
		returnSet.add(dataset1);
		returnSet.add(dataset2);

		return returnSet;

	}
	
	public DataSet newCopyExamples(DataSet examples,String featureValue,int q) {

		int m = examples.instances.size();
		DataSet returnSet = new DataSet();
		ArrayList<Instance> instanceList = new ArrayList<Instance>();

		for (int i = 0; i < m; i++) {
			if (examples.instances.get(i).attributes.get(q).equals(featureValue)) {
				Instance newInstance = new Instance();
				for(int j = 0; j < examples.instances.get(i).attributes.size() ; j ++)
					newInstance.addAttribute(examples.instances.get(i).attributes.get(j));
				newInstance.setLabel(examples.instances.get(i).label);				
				instanceList.add(newInstance);
			}			
		}

		returnSet.instances = instanceList;		
		return returnSet;

	}	
	
	// maximizing the information gain
	private Object[] bestQuestion(DataSet examples, ArrayList<Integer> questions) {
		int m = questions.size();
//		if (m==0) { // wont come here. Would always have numeric features
//			return -99;
//		}
		
		Object[] returnObj = new Object[2]; // return the best question index and threshold in case of numerical feature
		double entropy = calcEntropy(examples);		
		returnObj[0] = questions.get(0);
		double infoGain; // Maximum possible entropy is 1 because there are only two outcomes (1 bit)
		double bestInfoGain = -99;
		for (int i = 0; i <m; i++){
			Object[] condEntropy_Thresh = calcConditionalEntropy(examples, questions.get(i)); // return the best conditional entropy and threshold in case of numerical feature
			infoGain = entropy - Double.parseDouble(condEntropy_Thresh[0].toString());
			if (infoGain > bestInfoGain) {
				bestInfoGain = infoGain;
				returnObj[0] = questions.get(i);
				returnObj[1] = condEntropy_Thresh[1];
				
				
			}
		}
		
		if (bestInfoGain <= 0) {
			returnObj[0] = -99;  //no feature has positive information gain
		}
		
		//System.out.print(returnObj[0] +" - "+returnObj[1] + "\n");
		return returnObj;
	}
	
	private double calcEntropy(DataSet examples) {
		int m = examples.instances.size();
		double[] counts = new double[2];
		for (int i = 0; i<m;i++) {
			if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[0])) {
				counts[0]+=1;
			}
			else if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[1])) {
				counts[1]+=1;
			}
		}
		
		double p1 = counts[0]/m;
		double p2 = counts[1]/m;
		
		return entropyCalcHelper(p1,p2);
	}
		
	
	
	private Object[] calcConditionalEntropy(DataSet examples,int question) {		

		Object[] condEntropy_Thresh = new Object[2];
		

		String questionDesc = DTLearn.arffTrainData.getAttributeName(question);
		int m = examples.instances.size();
		
		double[][] counts;
		int n;
		
		String attrType = DTLearn.arffTrainData.getAttributeType(questionDesc);
		
		if (attrType.equals("nominal")) {
			n = DTLearn.arffTrainData.getAttributeData(questionDesc).length;
			double conditionalEntropy = 0;
			counts = new double[n][3];
			for (int i = 0; i<m;i++) {
				for (int j = 0; j < n; j++) {
					if (examples.instances.get(i).attributes.get(question).equals(DTLearn.arffTrainData.getAttributeData(questionDesc)[j])) {
						counts[j][0]+=1; 
						if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[0])) {
							counts[j][1]+=1;
						}
						else if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[1])) {
							counts[j][2]+=1;
						}
					}			   
				}		

			}
			for (int j = 0; j < n; j++) {
				if (counts[j][0] != 0) {
					double p0 = counts[j][0]/m;
					double p1 = counts[j][1]/counts[j][0];
					double p2 = counts[j][2]/counts[j][0];				

					conditionalEntropy +=  p0 * entropyCalcHelper(p1,p2);
				}
			}
			
			condEntropy_Thresh[0] = conditionalEntropy;
		}
		
		else { // numeric features
			ArrayList<Double> candidateSplits = getCandidateSplits(examples,question);
			int numSplits = candidateSplits.size();
			//System.out.println("question:"+questionDesc+"  numsplit:"+numSplits); // TOREMOVE 
			//for (int k = 0; k<numSplits; k++) {
				//System.out.print(candidateSplits.get(k)+"\t");
				
			//}
			//System.out.println("");
			//System.out.println("--------------------");
			
			if (numSplits ==0) { // no candidate splits
				condEntropy_Thresh[0] = 100;
				return condEntropy_Thresh;
			}
			
			n = 2;
			double bestCondEntropy = 100;
			double bestThresh = candidateSplits.get(0);
			for (int k = 0; k<numSplits; k++) {
				double conditionalEntropy = 0;
				counts = new double[2][3];
				for (int i = 0; i<m;i++) {
					if (Double.parseDouble(examples.instances.get(i).attributes.get(question)) <= candidateSplits.get(k)) {
						counts[0][0]+=1; 
						if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[0])) {
							counts[0][1]+=1;
						}
						else if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[1])) {
							counts[0][2]+=1;
						}
					}
					else {
						counts[1][0]+=1; 
						if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[0])) {
							counts[1][1]+=1;
						}
						else if (examples.instances.get(i).label.equals(DTLearn.arffTrainData.getAttributeData("class")[1])) {
							counts[1][2]+=1;
						}
					}
				}
				
				for (int j = 0; j < n; j++) {
					if (counts[j][0] != 0) {
						double p0 = counts[j][0]/m;
						double p1 = counts[j][1]/counts[j][0];
						double p2 = counts[j][2]/counts[j][0];				

						conditionalEntropy +=  p0 * entropyCalcHelper(p1,p2);
					}
				}
				
				//System.out.println("Nishka = numeric = "+questionDesc+" cond ent "+ conditionalEntropy + " thresh ="  + candidateSplits.get(k));
				if (conditionalEntropy < bestCondEntropy) {  // minimizing cond entropy here
					bestCondEntropy = conditionalEntropy;
					bestThresh = candidateSplits.get(k);
				}
			}
			condEntropy_Thresh[0] = bestCondEntropy;
			condEntropy_Thresh[1] = bestThresh;
		}
		
		//System.out.println("Nishka = question = "+questionDesc+" cond ent "+ condEntropy_Thresh[0] + " thresh ="  + condEntropy_Thresh[1]);
		
		return condEntropy_Thresh;
	}
	
	private ArrayList<Double> getCandidateSplits(DataSet examples,int question) {
		int m = examples.instances.size();
		ArrayList<Double> candidateSplits = new ArrayList<Double>();
		
		Map<Double,String> featureValues = new HashMap<Double,String>();
		for (int i=0; i<m; i++) {
			//String temp = featureValues.get(Double.parseDouble(examples.instances.get(i).attributes.get(question)));
			if (featureValues.containsKey(Double.parseDouble(examples.instances.get(i).attributes.get(question))) &&
					!featureValues.get(Double.parseDouble(examples.instances.get(i).attributes.get(question))).equals(examples.instances.get(i).label) ) {
				featureValues.put(Double.parseDouble(examples.instances.get(i).attributes.get(question)), "DIFFERENT");
			}
			else {
				featureValues.put(Double.parseDouble(examples.instances.get(i).attributes.get(question)), examples.instances.get(i).label);
			}
			
			
		//	System.out.print(Double.parseDouble(examples.instances.get(i).attributes.get(question))+"-\t"+examples.instances.get(i).label+"\t");
		}
		
		//System.out.println("");
		//System.out.println("Sort Tree Map");
		
		Map<Double,String> sortedFeatureValues = new TreeMap<Double,String>(featureValues);
		
		String oldLabel = "T";
		String newLabel;
		Double oldVal = 1.0;
		Double newVal;
		int i = 0;
		
		for (Map.Entry<Double, String> entry: sortedFeatureValues.entrySet()) {
			
			if (i == 0) {
				oldLabel = entry.getValue();
				oldVal = entry.getKey();
			}
			
			newLabel = entry.getValue();
			newVal = entry.getKey();
			
//			if (!newLabel.equals(oldLabel) && i != 0 ) {
//				candidateSplits.add((oldVal + newVal)/2);
//			}
			
			//System.out.print(entry.getKey()+"-\t"+entry.getValue()+"\t");
			
			if (!newLabel.equals(oldLabel) || oldLabel.equals("DIFFERENT") && i!=0) {
//				if (Math.abs(oldVal-newVal) < 0.000001) {
//					newLabel = String.copyValueOf("DIFFERENT".toCharArray());
//				}
//				else {
					candidateSplits.add((oldVal + newVal)/2);
//				}
			}
			
			oldLabel = String.copyValueOf(newLabel.toCharArray());
			oldVal = newVal;
			i++;
		}

//		System.out.println("-----------------------");
//		System.out.println("-----------------------");
	
		return candidateSplits;
	}
	
	private double entropyCalcHelper(double p1, double p2) {
		double logp1;
		double logp2;
		if (p1 == 0) {
			logp1 = 0;
		}
		else {
			logp1 = Math.log10(p1)/log2;
		}
		if (p2 == 0) {
			logp2 = 0;
		}
		else {
			logp2 = Math.log10(p2)/log2;
		}
		
		return -(p1 * logp1) - (p2 * logp2);
	}
	

	private ArrayList<Integer> newCopyQuestions(ArrayList<Integer> sourceList, int qnum) {
		ArrayList<Integer> targetList = new ArrayList<Integer>();
		for(Integer copyString: sourceList) {
			if (qnum != copyString){
				targetList.add(copyString);
			}
		}
		return targetList;
	}


	@Override
	public String[] classify(DataSet test) {
		int m = test.instances.size();
		String[] resultSet = new String[m];
		for (int i = 0; i<m; i++){
			resultSet[i] = classifyInstance(test.instances.get(i));
		}
		
		return resultSet;
	}
	
	public String classifyInstance(Instance instance) {
		DecTreeNode currNode = rootNode;
		
		// stop when you reach a terminal node
		while(!currNode.terminal){
			
			int questionIndex = Integer.parseInt(currNode.attribute);
			String questionDesc = DTLearn.arffTrainData.getAttributeName(questionIndex);
			
		
			int numChildren = currNode.children.size();
			for (int i=0;i<numChildren;i++) {
				DecTreeNode childNode = currNode.children.get(i);
				String instanceAttrVal;
				
				// special case for numeric variables
				if (DTLearn.arffTrainData.getAttributeType(questionDesc).equals("numeric")) {
					double contInstanceAttrVal = Double.parseDouble(instance.attributes.get(questionIndex));
					if (contInstanceAttrVal <= currNode.threshold) {
						instanceAttrVal = "LEFT";
					}
					else {
						instanceAttrVal = "RIGHT";
					}
				}
				else {
					instanceAttrVal = instance.attributes.get(questionIndex);
				}
				
				if (childNode.parentAttributeValue.equals(instanceAttrVal)) {
					currNode = childNode;					
					break;
				}
			}		
		}
		
		return currNode.label;
	}


	@Override
	public void print() {
		rootNode.print(-1);
		
	}
	
	
	
	
}