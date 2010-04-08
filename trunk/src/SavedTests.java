import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class SavedTests {
	
	public static final String sep = "\t";
	
	public static void main(String[] args) throws Exception {
		basicTest(args);
		//testEpsilonChangeSchemes(args);
	}
	
	private static void basicTest(String[] args) throws Exception {
		Testing test;
		String world;
		
		for(int i = 0; i < args.length; i++){
			world = args[i];
			
			test = new Testing(20, 10, 0, null, new RRTWorld(world)); //
			
			test.execNRuns(100,RRTsearch.Algorithm.RRT);
			test.execNRuns(100,RRTsearch.Algorithm.VLRRT);
			test.execNRuns(100,RRTsearch.Algorithm.DVLRRT);
			test.printStats("stats_" + world.substring(7),true);
		}
		
		
	}
	
	private static void testEpsilonChangeSchemes(String[] worlds) throws Exception {
		
		Map<VLRRTnode.changeEpsilonScheme,List<Double>> tests = constructTests();
		List<RRTsearch.Algorithm> algs = new ArrayList<RRTsearch.Algorithm>();
		algs.add(RRTsearch.Algorithm.VLRRT);
		algs.add(RRTsearch.Algorithm.DVLRRT);
		double percent;
		
		
		File f = new File("changeSchemeTests");
		try {
			FileWriter fwr = new FileWriter(f + "_base");
			fwr.write("Algorithm \t World \t IncScheme \t incFactor \t decScheme \t decFactor \t % success \n");
			percent = testScheme(RRTsearch.Algorithm.RRT, worlds[0], null, 0, null, 0);
			fwr.write("" + RRTsearch.Algorithm.RRT + sep + worlds[0] + sep + 0 + sep + 0 + sep + 
					0 + sep + 0 + sep + percent + "\n");
			//System.out.println(percent);
			percent = testScheme(RRTsearch.Algorithm.RRT, worlds[1], null, 0, null, 0);
			fwr.write("" + RRTsearch.Algorithm.RRT + sep + worlds[1] + sep + 0 + sep + 0 + sep + 
					0 + sep + 0 + sep + percent + "\n");
			//System.out.println(percent);
			fwr.close();
			
			for (RRTsearch.Algorithm alg : algs) {
				for (int w = 0; w < worlds.length; w++) {
					for (VLRRTnode.changeEpsilonScheme incScheme : tests.keySet()) {
						for (VLRRTnode.changeEpsilonScheme decScheme : tests.keySet()) {
							fwr = new FileWriter(f + "_" + incScheme + "_" + decScheme + "_" + alg + w);
							fwr.write("Algorithm \t World \t IncScheme \t incFactor \t decScheme \t decFactor \t % success \n");
							percent = testScheme(RRTsearch.Algorithm.RRT, worlds[0], null, 0, null, 0);
							
							for (double incFactor : tests.get(incScheme)) {
								for (double decFactor : tests.get(decScheme)) {
									percent = testScheme(alg, worlds[w], incScheme, incFactor, decScheme, decFactor);
									fwr.write("" + alg + sep + worlds[w] + sep + incScheme + sep + incFactor + sep + 
											decScheme + sep + decFactor + sep + percent + "\n");
									//System.out.println(percent);
								}
							}
							fwr.close();
						}
					}
				}
			}
		
		
		} catch (FileNotFoundException e) {
			System.err.println("Bad file.");
		} catch (IOException e) {
			System.err.println("Error writing to file.");
		}
		
		
		
	}
	
	
	private static double testScheme(RRTsearch.Algorithm alg, String world, VLRRTnode.changeEpsilonScheme inc, double incFactor, 
											VLRRTnode.changeEpsilonScheme dec, double decFactor) throws Exception {
		Testing test = new Testing(20,10,0,null,new RRTWorld(world),inc,incFactor,dec,decFactor);
		test.execNRuns(100, alg);
		double percent = getPercentSucceeded(test.getStats());
		return percent;		
	}
	
	
	private static double getPercentSucceeded(List<Stats> theData) {
		double successes = 0;
		
		for (Stats s : theData) if (s.isGoalFound()) successes++;
		
		return (successes)/(theData.size());
	}
	
	private static Map<VLRRTnode.changeEpsilonScheme,List<Double>> constructTests() {
		Map<VLRRTnode.changeEpsilonScheme,List<Double>> ret = new HashMap<VLRRTnode.changeEpsilonScheme,List<Double>>();
		
		List<Double> linearFactors = new ArrayList<Double>();
		for(double factor = .1;  factor < 2; factor += .2) linearFactors.add(factor);
		//linearFactors.add(.1);
		ret.put(VLRRTnode.changeEpsilonScheme.Linear, linearFactors);
		
		List<Double> multFactors = new ArrayList<Double>();
		for(double factor = 2;  factor < 5; factor += 1) multFactors.add(factor);
		//multFactors.add(2.0);
		ret.put(VLRRTnode.changeEpsilonScheme.Mult, multFactors);
		
		List<Double> restartFactors = new ArrayList<Double>();
		restartFactors.add(1.0);
		ret.put(VLRRTnode.changeEpsilonScheme.Restart, restartFactors);
		
		return ret;
		
		
		
		
		
	}
	
	
}
