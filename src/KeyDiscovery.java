import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import smile.Network;
import smile.SMILEException;

//import smile.Network;



//import org.eclipse.recommenders.jayes.BayesNet;
//import org.eclipse.recommenders.jayes.BayesNode;
//import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeAlgorithm;

//import weka.classifiers.bayes.net.BayesNetGenerator;


public class KeyDiscovery {
	
	public static Lattice lt;
	
	public static ColumnCombination maxEGCC = null;
	
	public static ColumnCombination uniqueKeyAns = null;
	
	public static boolean allPrunedFlag = true;
	
	public static float threshold = (float) 0.9;
	
	public static Hashtable<String, Float> goldenStd = new Hashtable<String, Float>();
	
	public static Hashtable<ArrayList<String>, Float> goldenStdDistribution = new Hashtable<ArrayList<String>, Float>();
	
	public static Hashtable<String, Integer> discoveredKeys = new Hashtable<String, Integer>();
	
	public static Network bayesNet;
	
	public static TreeNode treeRoot;
	
	public static Hashtable<String, Float> confMeasure = new Hashtable<String, Float>();
	
	/*public static String[][] table = new String[][]{
			  { "A", "B", "C", "D" },
			  { "1", "2", "4", "1" },
			  { "2", "2", "5", "3" },
			  { "3", "2", "4", "3" }
			  //{ "1", "2", "4", "3" }
			};*/
	
	public static String[] keys = new String[] { "AB" };
	
	public static String[][] keyIncOrder = new String[][]{ { "A" }, { "AB" }, { "ABC" }, { "ABCD" }, {"ABCDE"}, { "ABCDEF" }, 
		 										{"ABCDEFG"}, {"ABCDEFGH"} };
	
	public static int[] randMax = new int[] {4};//1200, 35, 11, 6, 5, 4, 3, 3};
	
	public static String[][] table;
	
	public static void generatePowerLattice(String[] columns)
	{
		
		int count = (int) Math.pow(2, columns.length);
		String combination;
		for (int i = 1; i < count; i ++)
		{
			String bitString = Integer.toBinaryString(i);
			while (bitString.length() < columns.length) {
		        bitString = "0" + bitString;
			}
			BitSet bs = BitSet.valueOf(new long[] { i });
			combination = getColumnCombination(bitString, columns);
			
			ColumnCombination cc = new ColumnCombination(combination, bs);
			int level = combination.length() - 1;
			lt.levelStructure[level].add(cc);
			
		}
		
	}
	
	public static void generatePowerLatticeBayesianNodes(String[] columns)
	{
		int count = (int) Math.pow(2, columns.length);
		String combination;
		bayesNet = new Network();
		for (int i = 1; i < count; i ++)
		{
			String bitString = Integer.toBinaryString(i);
			while (bitString.length() < columns.length) {
		        bitString = "0" + bitString;
			}
			BitSet bs = BitSet.valueOf(new long[] { i });
			combination = getColumnCombination(bitString, columns);
			
			ColumnCombination cc = new ColumnCombination(combination, bs);
			int level = combination.length() - 1;
			lt.levelStructure[level].add(cc);
			
			// Creating node "{combination}" and setting/adding outcomes:
			int nodeSuccess = bayesNet.addNode(Network.NodeType.Cpt, combination);
			bayesNet.setOutcomeId(combination, 0, "T");
			bayesNet.setOutcomeId(combination, 1, "F");
			
			//BayesNode nd = bayesNet.createNode(combination);
			//nd.addOutcomes("T", "F");
				
		}
		
		//Add parent-child relation
		buildBayesianNetwork();
	}

	public static void buildBayesianNetwork()
	{
		for(int i = 1; i < table[0].length; i++)
		{
			for(int k = 0; k < lt.levelStructure[i].size(); k++)
			{
				ColumnCombination curr = lt.levelStructure[i].get(k);
				int level = curr.getBitSet().cardinality() - 2;
				if (level < 0) continue;
				ArrayList<ColumnCombination> levelNodes = lt.levelStructure[level];
				//ArrayList<BayesNode> parentsList = new ArrayList<BayesNode>();
				//BayesNode currNd = bayesNet.getNode(curr.getColName());
				for (int j = 0; j < levelNodes.size(); j++) {
					
					int size = curr.getBitSet().size();
					BitSet tempBs = new BitSet();
					tempBs.or(curr.getBitSet());
					tempBs.flip(0, size);
					
					BitSet nodeBs = new BitSet();
					nodeBs.or(levelNodes.get(j).getBitSet());
					nodeBs.and(tempBs);
					if(nodeBs.cardinality() == 0)
					{
						String parent = lt.levelStructure[level].get(j).getColName();
						bayesNet.addArc(parent, curr.getColName());
						//parentsList.add(bayesNet.getNode(parent));
					}
				}
				//icurrNd.setParents(parentsList);
				//System.out.println(bayesNet.getNode(curr.getColName()).getParents().size());
			}
		}
		
		setCPTForBayesian();
		bayesNet.setBayesianAlgorithm(Network.BayesianAlgorithmType.Lauritzen);
	}
	
	public static String getColumnCombination(String binary, String[] columns)
	{
		String combination = "";
		for (int i = 0; i < binary.length(); i++)
		{
			if(binary.charAt(i) == '1')
				combination += columns[i];
		}
		return combination;
	}
	
	public static int getUniqueCount(ColumnCombination cc)
	{
		Hashtable<String, Integer> map = new Hashtable<String, Integer>();
		String columns = cc.getBitSet().toString();
		String delims = "[{,} ]+";
		String[] index = columns.split(delims);
		for (int i = 1; i < table.length; i++)
		{
			String mapKey = "";
			for (int j = index.length - 1; j >= 1; j--)
			{
				int colId = table[0].length - Integer.parseInt(index[j]) - 1;
				 mapKey += table[i][colId];
			}
			map.put(mapKey, 1);
		}
		
		return map.size();
	}
	
	public static void computeEGForLattice() 
	{
		uniqueKeyAns = maxEGCC;
		maxEGCC = null;
		allPrunedFlag = true;
		float maxEG = 0;
		for(int i = 0; i < table[0].length-1; i++)
		{
			for(int j = 0; j < lt.levelStructure[i].size(); j++)
			{
				if(!lt.levelStructure[i].get(j).isPruned())
				{
					float nbOfSubsets = lt.getNbOfSubsets(i);
					float nbOfSupersets = lt.getNbOfSupersets(lt.levelStructure[i].get(j), i);
					float probabOfYes = (float) getUniqueCount(lt.levelStructure[i].get(j)) / (float) (table.length - 1);
					float probabOfNo = 1 - probabOfYes;
					float EG = (nbOfSupersets)*probabOfYes + (nbOfSubsets)*probabOfNo;
					if(EG > maxEG)
					{
						maxEG = EG;
						maxEGCC = lt.levelStructure[i].get(j);
					}
					lt.levelStructure[i].get(j).setExpectedGain(EG);
					allPrunedFlag = false;
					//System.out.println("For: "+ lt.levelStructure[i].get(j).getColName() + " " + nbOfSubsets + " " + nbOfSupersets + " uniques: " + getUniqueCount(lt.levelStructure[i].get(j)) + " EG= " + lt.levelStructure[i].get(j).getExpectedGain() );
				}
			}
		}
	}
	
	public static void setCPTForBayesian()
	{
		for(int lvl = 0; lvl < table[0].length; lvl++)
		{
			for(int j = 0; j < lt.levelStructure[lvl].size(); j++)
			{
				double[] dist;
				ColumnCombination curr = lt.levelStructure[lvl].get(j);
				String colName = curr.getColName();
				if(lvl == 0)
				{
					float probabOfYes = (float) getUniqueCount(lt.levelStructure[lvl].get(j)) / (float) (table.length - 1);
					float probabOfNo = 1 - probabOfYes;
					dist = new double[] {probabOfYes,probabOfNo};
				}else
				{
					int combinations = (int)Math.pow(2,(lvl+1));
					dist = new double[combinations*2];
					for(int i = 0; i < combinations*2 - 2; i+=2)
					{
						dist[i] = 1;
						dist[i+1] = 0;
					}
					double[][] colDist = getDistinctiveness(colName);
					dist[combinations*2-2] = colDist[0][0];
					dist[combinations*2-1] = colDist[0][1];
				}
				
				//bayesNet.getNode(colName);
				//String[] parents = bayesNet.getParentIds(colName);
				//System.out.println(parents.length);
				bayesNet.setNodeDefinition(colName, dist);
				//nodeInd.setProbabilities(dist);
				double[] dist2 = bayesNet.getNodeDefinition(colName);
				System.out.println(colName +" "+dist2[dist2.length -2]+" "+dist2[dist2.length-1]);
			}
		}
	}
	
	public static double[][] getDistinctiveness(String colName)
	{
		double[][] dist = new double[1][2];
		double currColDistinctiveness;
		String[] parentsArr;
		ArrayList<Double> parentsDistinctiveness = new ArrayList<Double>();
		int nodeId;
		nodeId = bayesNet.getNode(colName);
		parentsArr = bayesNet.getParentIds(nodeId);
		//parentsArr = nodeId.getParents();
		for(int i = 0; i < parentsArr.length; i++)
		{
			String parent = parentsArr[i];
			if(parent != null)
			{
				double[] currNodeDist = bayesNet.getNodeDefinition(parent);
				parentsDistinctiveness.add(currNodeDist[currNodeDist.length-2]);
				//System.out.println(bayesNet.getNode(parentsArr.get(i).getName()));
			}
			
		}
		currColDistinctiveness = computeDistinctiveness(parentsDistinctiveness);
		dist[0][0] = currColDistinctiveness;
		dist[0][1] = 1-currColDistinctiveness;
		
		return dist;
	}
	
	public static double computeDistinctiveness(ArrayList<Double> parentsDistinctiveness)
	{
		double D = 0;
		int count = (int) Math.pow(2, parentsDistinctiveness.size());
		
		for (int i = 1; i < count; i ++)
		{
			int nbSetBit = 0;
			double product = 1;
			String bitString = Integer.toBinaryString(i);
			while (bitString.length() < parentsDistinctiveness.size()) {
		        bitString = "0" + bitString;
			}
			
			for(int j = 0; j < bitString.length(); j++)
			{
				if(bitString.charAt(j) == '1')
				{
					product = product*parentsDistinctiveness.get(j);
					nbSetBit++;
				}
			}
			
			if(nbSetBit % 2 == 0)
				D = D - product;
			else
				D = D + product;
		}
		return D;
	}
	
	public static double generateGoldenSetDist(Hashtable<String, Integer> sigma)
	{
		

//		bayesNet.clearAllEvidence();
//		bayesNet.updateBeliefs();
//		bayesNet.setEvidence("B", "T");
//		bayesNet.setEvidence("AB", "T");
//		System.out.println(bayesNet.isEvidence("AB"));
//		bayesNet.updateBeliefs();
//		double[] values2 = bayesNet.getNodeValue("ABC");
//		System.out.println(values2);
		
		
		double jpt = 1;
		int count = (int) Math.pow(2, table[0].length) - 1;
		long possible = (long) Math.pow(2, count);
		String[] nodes = new String[count];
		int l = count-1;
		double avgFmeasure = 0;
		for(int j = 0; j < lt.levelStructure.length; j++)
		{
			ArrayList<ColumnCombination> level = lt.levelStructure[j];
			for(int k = 0; k < level.size(); k++)
			{
				nodes[l] = level.get(k).getColName();
				l--;
			}
		}
		
		//Print SIGMA
		//System.out.print("SIGMA: ");
		Iterator<Entry<String, Integer>> it3 = sigma.entrySet().iterator();
		while(it3.hasNext()) 
		{
			Map.Entry<String, Integer> e = (Map.Entry<String, Integer>)it3.next();
			//System.out.print(e.getKey()+" ");
		}
		//System.out.println();
		
		for(int i = 0; i < possible; i++)
		{
			String bitString = Integer.toBinaryString(i);
			while (bitString.length() < count) {
		        bitString =  "0" + bitString ;
			}
			
			//System.out.print("EVIDENCE: ");
			int trueEvidence; // will be used to calculate recall.
			for(int j = bitString.length()-1; j >= 0; j--)
			{
				trueEvidence = 0;
				// First set the evidences from sigma.
				Iterator<Entry<String, Integer>> it = sigma.entrySet().iterator();
				while(it.hasNext()) 
				{
					Map.Entry<String, Integer> e = (Map.Entry<String, Integer>)it.next();
					bayesNet.setEvidence((String) e.getKey(), "T");
					trueEvidence++;
//					ArrayList<String> superSet = lt.getSuperset((String) e.getKey());
//					for(String node : superSet)
//					{
//						bayesNet.setEvidence(node, "T");
//						trueEvidence++;
//					}
					
				}
				
				//Set all remaining evidences.
				for(int k = bitString.length()-1; k > j; k--)
				{
					//Avoid ones that are already set.
					if(!bayesNet.isEvidence(nodes[k]))
					{
						if(bitString.charAt(k) == '1')
						{
							bayesNet.setEvidence(nodes[k], "T");
							trueEvidence++; // count true evidences in the last round only
						}
						else
						{
							if(!bayesNet.isEvidence(nodes[k]))
								bayesNet.setEvidence(nodes[k], "F");
						}
					}
				}
				
				bayesNet.updateBeliefs();
				double[] values = bayesNet.getNodeValue(nodes[j]);
				if(bayesNet.isEvidence(nodes[j]))
				{
					if(bayesNet.getEvidenceId(nodes[j]).compareTo("T") == 0)
					{
						jpt = jpt*values[0];
						//System.out.print(nodes[j]+"=T ");
					}else
					{
						jpt = jpt*values[1];
						//System.out.print(nodes[j]+"=T ");
					}
				} else
				{
					jpt = jpt*values[1 - Integer.parseInt(bitString.charAt(j)+"")];
					//if(bitString.charAt(j) == '1')
						//System.out.print(nodes[j]+"=T ");
					//else if(bitString.charAt(j) == '0')
						//System.out.print(nodes[j]+"=F ");
				}
				
				//Calculate F-measure at end
				if(j == 0) //j == 0 is last because j is decreasing
				{
					int matchCount = 0;
					Iterator<Entry<String, Integer>> it2 = sigma.entrySet().iterator();
					while(it2.hasNext()) 
					{
						Map.Entry<String, Integer> e = (Map.Entry<String, Integer>)it2.next();
						if(bayesNet.isEvidence((String) e.getKey()))
						{
							matchCount++;
						}
//						ArrayList<String> superSet = lt.getSuperset((String) e.getKey());
//						for(String node : superSet)
//						{
//							if(bayesNet.isEvidence(node))
//							{
//								matchCount++;
//							}
//						}
						
					}
					double precision;
					if(sigma.size() != 0)
						precision = matchCount/sigma.size();
					else
						precision = 0;
					double recall = (double) matchCount/trueEvidence;
					double Fmeasure = 2*precision*recall/(precision + recall);
					avgFmeasure += Fmeasure;
					//System.out.println("precision: "+precision+ " recall: "+recall+" Fmeasure: "+Fmeasure);
				}
				bayesNet.clearAllEvidence();
			}
			
			//System.out.println("Joint Probability: "+jpt);
			bayesNet.clearAllEvidence();
			jpt = 1;
		}
		avgFmeasure = avgFmeasure/possible;
		
		return avgFmeasure;

	}
	
	public static void setInitialKeySet()
	{
		for(int lvl = 0; lvl < table[0].length; lvl++)
		{
			for(int j = 0; j < lt.levelStructure[lvl].size(); j++)
			{
				ColumnCombination curr = lt.levelStructure[lvl].get(j);
				float probabOfYes = (float) getUniqueCount(curr) / (float) (table.length - 1);
				boolean subsetPresent = false;
				if(probabOfYes == 1)
				{
					ArrayList<String> subset = lt.getSubset(curr.getColName());
					for(String set : subset)
					{
						if(discoveredKeys.containsKey(set))
							subsetPresent = true;
					}
					if(!subsetPresent)
						discoveredKeys.put(curr.getColName(), 1);
					
				}
			}
		}
	}
	
	public static double getInfrence(String node, String obv)
	{
		bayesNet.clearAllEvidence();
		bayesNet.setEvidence(node, obv);
		bayesNet.updateBeliefs();
		double[] values = bayesNet.getNodeValue(node);
		bayesNet.clearAllEvidence();
		if(obv.compareTo("T") == 1)
			return values[0];
		else
			return values[1];
	}
	
	public static Hashtable<String, Integer> getNewSigma(Hashtable<String, Integer> oldSigma, String colName, String obv)
	{
		Hashtable<String, Integer> newSigma = new Hashtable<String, Integer>();
		
		Iterator<Entry<String, Integer>> it = oldSigma.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry e = (Map.Entry)it.next();
			newSigma.put((String) e.getKey(), (Integer) e.getValue());
		}
		
		if(obv.compareTo("T") == 0)
		{
			newSigma.put(colName, 1);
			ArrayList<String> superSet = lt.getSuperset(colName);
			
			for(String node : superSet)
			{
				if(newSigma.containsKey(node))
				{
					newSigma.remove(node);
				}
			}
			
		} else if(obv.compareTo("F") == 0)
		{
			newSigma.remove(colName);
			ArrayList<String> superSet = lt.getSubset(colName);
			
			for(String node : superSet)
			{
				if(newSigma.containsKey(node))
				{
					newSigma.remove(node);
				}
			}
		}
		
		return newSigma;
	}
	
	public static double expectedQ(String colName)
	{
		double eq = 0;
		
		double prT = getInfrence(colName, "T");
		double prF = getInfrence(colName, "F");
		
		Hashtable<String, Integer> newSigmaT = getNewSigma(discoveredKeys, colName, "T"); //update discoveredKeys after knowing the answer from user
		Hashtable<String, Integer> newSigmaF = getNewSigma(discoveredKeys, colName, "F"); //do this in the main loop.
		
		double exAccuT = generateGoldenSetDist(newSigmaT);
		double exAccuF = generateGoldenSetDist(newSigmaF);
		
		eq = prT*exAccuT + prF*exAccuF;
		
		return eq;
	}
	
	public static void computeConfMeasure() 
	{
		for(int i = 0; i < table[0].length; i++)
		{
			for(int j = 0; j < lt.levelStructure[i].size(); j++)
			{
				float uniquePercent = (float) getUniqueCount(lt.levelStructure[i].get(j)) / (float) (table.length - 1);
				
				confMeasure.put(lt.levelStructure[i].get(j).getColName(), uniquePercent);
				System.out.println("CONF MEASURE: "+lt.levelStructure[i].get(j).getColName()+" " +confMeasure.get(lt.levelStructure[i].get(j).getColName()));
			}
		}
	}
	
	
	
	
	
	public static void naive() {

//		goldenStd.put("AC", 1);
//		goldenStd.put("AD", 1);
//		goldenStd.put("ABC", 1);
//		goldenStd.put("ACD", 1);
//		goldenStd.put("ABD", 1);
//		goldenStd.put("ABCD", 1);
		
//		goldenStd.put("C", 1);
//		goldenStd.put("AC", 1);
//		goldenStd.put("AD", 1);
//		goldenStd.put("BC", 1);
//		goldenStd.put("CD", 1);
//		goldenStd.put("AD", 1);
//		goldenStd.put("ABC", 1);
//		goldenStd.put("BCD", 1);
//		goldenStd.put("ACE", 1);
//		goldenStd.put("ABD", 1);
//		goldenStd.put("ADE", 1);
//		goldenStd.put("ABDE", 1);
//		goldenStd.put("ACDE", 1);
//		goldenStd.put("ABCE", 1);
//		goldenStd.put("ABCDE", 1);
		//int numQAvg = 0;
		
		PrintWriter writer;
		try {
			writer = new PrintWriter("results.txt", "UTF-8");

		for(int run = 0; run < keyIncOrder.length; run++)
		{
			System.out.println("KEY LENGTH " + keyIncOrder[run][0]);
			writer.println("KEY LENGTH " + keyIncOrder[run][0]);
			for(int randNum = randMax[0]; randNum < 10; randNum+=2)
			{
				DataGenerator dg = new DataGenerator(keys, randNum);
				table = dg.table;
				for(int i = 0; i < keys.length; i++)
				{
					goldenStd.put(keys[i], (float)1);
				}
				
				int numQ = 0;
				lt = new Lattice(table[0].length);
				generatePowerLattice(table[0]);
				computeEGForLattice();
				
				while(!allPrunedFlag)
				{
					//Simulate user answers by verifying them against goldenStd
					maxEGCC.setPruned(true);
					if(goldenStd.containsKey(maxEGCC.getColName()))
					{
						int level = maxEGCC.getColName().length() - 1;
						lt.pruneSuperset(maxEGCC, level);
						discoveredKeys.put(maxEGCC.getColName(), 1);
					} else
					{
						lt.pruneSubsets(maxEGCC);
					}
					numQ++;
					computeEGForLattice();
				}
				
				Enumeration<String> discKeys = discoveredKeys.keys();
				//while(discKeys.hasMoreElements())
					//System.out.println("#####Discovered key is " + discKeys.nextElement());
				
				System.out.println(randNum+ " " + numQ);
				writer.println(randNum+ " " + numQ);
				
				//numQAvg += numQ;
				/*for(int i = 0; i < table[0].length; i++)
				{
					for(int j = 0; j < lt.levelStructure[i].size(); j++)
					{
						System.out.print(lt.levelStructure[i].get(j).isPruned() + " ");
						System.out.println(lt.levelStructure[i].get(j).getColName());
					}
				}*/
			}
		}
		writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("--------AVERAGE NUMBER OF QUESTIONS ASKED " + numQAvg/10);
		
	}
	
	public static void bayesian() {

		PrintWriter writer;
		try {
			writer = new PrintWriter("results.txt", "UTF-8");

		for(int run = 0; run < keyIncOrder.length; run++)
		{
			System.out.println("KEY LENGTH " + keyIncOrder[run][0]);
			writer.println("KEY LENGTH " + keyIncOrder[run][0]);
			for(int randNum = randMax[0]; randNum < 10; randNum+=2)
			{
				DataGenerator dg = new DataGenerator(keys, randNum);
				//bayesNet = new Network();
				table = dg.table;
				for(int i = 0; i < keys.length; i++)
				{
					goldenStd.put(keys[i], (float)1);
				}
				
				int numQ = 0;
				lt = new Lattice(table[0].length);
				generatePowerLatticeBayesianNodes(table[0]);
				
				//generateGoldenSetDist();
				
				setInitialKeySet();
				
				for(int lvl = 0; lvl < table[0].length-1; lvl++)
				{
					for(int j = 0; j < lt.levelStructure[lvl].size(); j++)
					{
						ColumnCombination curr = lt.levelStructure[lvl].get(j);
						double eq = expectedQ(curr.getColName());
						System.out.println("EQ of "+curr.getColName()+" is "+ eq);
					}
				}
				
				while(!allPrunedFlag)
				{
					//Simulate user answers by verifying them against goldenStd
					maxEGCC.setPruned(true);
					if(goldenStd.containsKey(maxEGCC.getColName()))
					{
						int level = maxEGCC.getColName().length() - 1;
						lt.pruneSuperset(maxEGCC, level);
						discoveredKeys.put(maxEGCC.getColName(), 1);
					} else
					{
						lt.pruneSubsets(maxEGCC);
					}
					numQ++;
					//computeEGForLattice();
				}
				
				Enumeration<String> discKeys = discoveredKeys.keys();
				//while(discKeys.hasMoreElements())
					//System.out.println("#####Discovered key is " + discKeys.nextElement());
				
				System.out.println(randNum+ " " + numQ);
				writer.println(randNum+ " " + numQ);
				
				//numQAvg += numQ;
				/*for(int i = 0; i < table[0].length; i++)
				{
					for(int j = 0; j < lt.levelStructure[i].size(); j++)
					{
						System.out.print(lt.levelStructure[i].get(j).isPruned() + " ");
						System.out.println(lt.levelStructure[i].get(j).getColName());
					}
				}*/
			}
		}
		writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("--------AVERAGE NUMBER OF QUESTIONS ASKED " + numQAvg/10);
		
	}
	
	public static void levelWise()
	{
		DataGenerator dg = new DataGenerator(keys, randMax[0]);
		table = dg.table;
		
		lt = new Lattice(table[0].length);
		generatePowerLattice(table[0]);
		computeConfMeasure();
//		generateGSTree();
		
//		computeGoldenSet();
//		
		Enumeration<String> items = goldenStd.keys();

		while(items.hasMoreElements())
		{
			String elem = items.nextElement();
			System.out.println(elem + " " +goldenStd.get(elem));
		}
		
		System.out.println("**** Size of golden Set: "+ goldenStd.size());
	
	}

	public static void main(String[] args) {
		
		bayesian();
		//naive();
		//levelWise();
		
	}
	
	
	
	
//	public static void generateGSTree()
//	{
//		ArrayList<String> goldenSet = new ArrayList<String>();
//		Hashtable<String, Integer> completeSet = new Hashtable<String, Integer>();
//		
//		for(int i = 0; i < table[0].length; i++)
//		{
//			for(int j = 0; j < lt.levelStructure[i].size(); j++)
//			{
//				completeSet.put(lt.levelStructure[i].get(j).getColName(), 0);
//			}
//		}
//		
//		treeRoot = new TreeNode(goldenSet, completeSet);
//		
//		makeTreeRecursive(treeRoot);
//	}
	
//	public static void makeTreeRecursive(TreeNode nd)
//	{
//		
//		boolean terminate = true;
//		Enumeration<String> items = nd.getCompleteSet().keys();
//		String elem = "";
//		while(items.hasMoreElements())
//		{
//			elem = items.nextElement();
//			if(nd.getCompleteSet().get(elem) == 0)
//			{
//				terminate = false;
//				break;
//			}
//		}
//		
//		if(terminate)
//		{
//			System.out.println(nd.getGoldenSet());
//			System.out.println(nd.getCurrentProbabilityProduct());
//			goldenStdDistribution.put(nd.getGoldenSet(), nd.getCurrentProbabilityProduct());
//			return;
//		}
//		
//		float leftProb = confMeasure.get(elem);
//		float rightProb = 1 - leftProb;
//		
//		//populate goldenSet and completeSet for left recurssion.
//		ArrayList<String> leftGoldenSet = new ArrayList<String>();
//		for(int i = 0; i < nd.getGoldenSet().size(); i++)
//		{
//			leftGoldenSet.add(nd.getGoldenSet().get(i));
//		}
//		leftGoldenSet.add(elem);
//		
//		Hashtable<String, Integer> leftCompleteSet = new Hashtable<String, Integer>();
//		Enumeration<String> leftItems = nd.getCompleteSet().keys();
//		while(leftItems.hasMoreElements())
//		{
//			String leftElem = leftItems.nextElement();
//			leftCompleteSet.put(leftElem, nd.getCompleteSet().get(leftElem));
//		}
//		leftCompleteSet.put(elem, 1);
//		ArrayList<String> newEntries = lt.getSuperset(elem);
//		
//		for(int i = 0; i < newEntries.size(); i++)
//		{
//			leftCompleteSet.put(newEntries.get(i), 1);
//		}
//		
//		TreeNode leftChild = new TreeNode(leftGoldenSet, leftCompleteSet);
//		leftChild.setCurrentProbabilityProduct(nd.getCurrentProbabilityProduct()*leftProb);
//		nd.setLeftChild(leftChild);
//		makeTreeRecursive(leftChild);
//		
//		//populate goldenSet and completeSet for right recurssion.
//		ArrayList<String> rightGoldenSet = new ArrayList<String>();
//		for(int i = 0; i < nd.getGoldenSet().size(); i++)
//		{
//			rightGoldenSet.add(nd.getGoldenSet().get(i));
//		}
//		rightGoldenSet.add(elem);
//		
//		Hashtable<String, Integer> rightCompleteSet = new Hashtable<String, Integer>();
//		Enumeration<String> rightItems = nd.getCompleteSet().keys();
//		while(rightItems.hasMoreElements())
//		{
//			String rightElem = rightItems.nextElement();
//			rightCompleteSet.put(rightElem, nd.getCompleteSet().get(rightElem));
//		}
//		rightCompleteSet.put(elem, 1);
//		
//		TreeNode rightChild = new TreeNode(rightGoldenSet, rightCompleteSet);
//		rightChild.setCurrentProbabilityProduct(nd.getCurrentProbabilityProduct()*rightProb);
//		nd.setRightChild(rightChild);
//		makeTreeRecursive(rightChild);
//		
//	}
}
