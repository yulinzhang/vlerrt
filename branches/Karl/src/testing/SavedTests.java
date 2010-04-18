package testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nodeTypes.RRTWorld;
import nodeTypes.EpsilonNode.UpdateScheme;
import rrt.Search;
import rrt.Stats;
import search.RRTstats;


public class SavedTests {
	
	public class ResultSet {
		ResultSetData data = new ResultSetData();
	}
	
	public static final String sep = "\t";
	
	public static void main(String[] args) throws Exception {

		screenShotTests(args);
		//basicTest(args);
		//testEpsilonChangeSchemes(args,1000);
		//testMultRestartInDepth(args);
		//testVariedBaseLengths(args);

	}
	
	private static void screenShotTests(String[] args) throws Exception {
		Testing test;
		String world;
		UpdateScheme inc = UpdateScheme.Mult;
		UpdateScheme dec = UpdateScheme.Restart;
		double incFactor = 2;
		double decFactor = .1;
		
		for(int i = 0; i < args.length; i++){
			world = args[i];
			
			test = new Testing(50,20, 10, 0, null, new RRTWorld(world), true, 
					inc, incFactor, dec, decFactor); //
			
			test.execNRuns(1,Search.Algorithm.RRT,true);
			test.execNRuns(1,Search.Algorithm.VLRRT,true);
			test.execNRuns(1,Search.Algorithm.DVLRRT,true);
			test.printStats("stats_" + world.substring(7) + "_O");
		}
		
		for(int i = 0; i < args.length; i++){
			world = args[i];
			
			test = new Testing(50,20, 10, 0, null, new RRTWorld(world), false, 
					inc, incFactor, dec, decFactor); //
			
			test.execNRuns(1,Search.Algorithm.RRT,true);
			test.execNRuns(1,Search.Algorithm.VLRRT,true);
			test.execNRuns(1,Search.Algorithm.DVLRRT,true);
			test.printStats("stats_" + world.substring(7));
		}
	}
	
	private static void basicTest(String[] args) throws Exception {
		Testing test;
		String world;
		
		for(int i = 0; i < args.length; i++){
			world = args[i];
			
			test = new Testing(25,20, 10, 0, null, new RRTWorld(world)); //
			
			test.execNRuns(100,Search.Algorithm.RRT);
			test.execNRuns(100,Search.Algorithm.VLRRT);
			test.execNRuns(100,Search.Algorithm.DVLRRT);
			test.printStats("stats_" + world.substring(7));
		}
		
		
	}
	
	private static void testEpsilonChangeSchemes(String[] worlds, int iterations) throws Exception {
		

		Map<UpdateScheme,List<Double>> tests = constructTests();
		List<Search.Algorithm> algs = new ArrayList<Search.Algorithm>();
		algs.add(Search.Algorithm.RRT);
		algs.add(Search.Algorithm.VLRRT);
		algs.add(Search.Algorithm.DVLRRT);	
		int baseLength = 10;
		int numTests = iterations;

		ResultSetData results;
		String prefix = "changeSchemeTests";
		
		File f = new File(prefix + System.currentTimeMillis());

		

		
		try {

			FileWriter fwr = new FileWriter(f);
			
				fwr.write("Algorithm" + sep + "IncScheme" + sep + "incFactor" + sep + "decScheme" + sep + "decFactor");
				for (int w = 0; w < worlds.length; w++) fwr.write(sep + worlds[w].substring(7) + sep + sep);
				fwr.write("\n");

				fwr.write("" + Search.Algorithm.RRT + sep + 0 + sep + 0 + sep + 0 + sep + 0); //header

				for (int w = 0; w < worlds.length; w++) { //not optimized
					results = testScheme(numTests, baseLength, Search.Algorithm.RRT, worlds[w], false, null, 0, null, 0); 

					fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
				}
	
				fwr.write("\n");
				
//				fwr.write("" + RRTsearch.Algorithm.RRT + "_O" + sep + 0 + sep + 0 + sep + 0 + sep + 0); //header
//
//				for (int w = 0; w < worlds.length; w++) { //optimized
//					results = testScheme(numTests, baseLength, RRTsearch.Algorithm.RRT, worlds[w], true, null, 0, null, 0); 
//					fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
//				}
//	
//				fwr.write("\n");
				
				

			
			for (UpdateScheme incScheme : tests.keySet()) {
				for (UpdateScheme decScheme : tests.keySet()) {
					for (double incFactor : tests.get(incScheme)) {
						for (double decFactor : tests.get(decScheme)) {
							for (Search.Algorithm alg : algs) { //line for each alg/change scheme combination
								fwr.write("" + alg + sep + incScheme + sep + incFactor + sep + decScheme + sep + decFactor); //header
								for (int w = 0; w < worlds.length; w++) {
									results = testScheme(numTests, baseLength, alg, worlds[w], false, incScheme, incFactor, decScheme, decFactor);
									//fwr.write(sep + results);
									fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
									System.out.println(results);
								}
								fwr.write("\n");
								
//								fwr.write("" + alg + "_0" + sep + incScheme + sep + incFactor + sep + decScheme + sep + decFactor); //header
//								for (int w = 0; w < worlds.length; w++) {
//									results = testScheme(numTests, baseLength, alg, worlds[w], true, incScheme, incFactor, decScheme, decFactor);
//									//fwr.write(sep + results);
//									fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
//									System.out.println(results);
//								}
//								fwr.write("\n");
							}
						}
					}
				}
			}
			fwr.close();
		} catch (FileNotFoundException e) {
			System.err.println("Bad file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error writing to file.");
		}

	}
	
private static void testMultRestartInDepth(String[] worlds) throws Exception {
		
		List<Search.Algorithm> algs = new ArrayList<Search.Algorithm>();
		algs.add(Search.Algorithm.VLRRT);
		algs.add(Search.Algorithm.DVLRRT);	
		int numTests = 10000;
		int baseLength = 10;

		ResultSetData results;
		String prefix = "changeSchemeTests" + numTests + "_";
		
		File f = new File(prefix + System.currentTimeMillis());

		try {

			FileWriter fwr = new FileWriter(f);
			
			

			fwr.write("Algorithm" + sep + "IncScheme" + sep + "incFactor" + sep + "decScheme" + sep + "decFactor");
			for (int w = 0; w < worlds.length; w++) fwr.write(sep + worlds[w].substring(7) + sep + sep);
			fwr.write("\n");

			fwr.write("" + Search.Algorithm.RRT + sep + 0 + sep + 0 + sep + 0 + sep + 0); //header

			for (int w = 0; w < worlds.length; w++) { //not optimized
				results = testScheme(numTests, baseLength, Search.Algorithm.RRT, worlds[w], false, null, 0, null, 0); 
				fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
			}
	
			fwr.write("\n");
				
			fwr.write("" + Search.Algorithm.RRT + "_O" + sep + 0 + sep + 0 + sep + 0 + sep + 0); //header

			for (int w = 0; w < worlds.length; w++) { //optimized
				results = testScheme(numTests, baseLength, Search.Algorithm.RRT, worlds[w], true, null, 0, null, 0); 
				fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
			}
	
			fwr.write("\n");
			
			UpdateScheme incScheme = UpdateScheme.Mult;
			UpdateScheme decScheme = UpdateScheme.Restart;
			double decFactor = 1;  //doesn't matter

			
			for (double incFactor = 2; incFactor < 5; incFactor++) {
				for (Search.Algorithm alg : algs) { //line for each alg/change scheme combination
					fwr.write("" + alg + sep + incScheme + sep + incFactor + sep + decScheme + sep + decFactor); //header
					for (int w = 0; w < worlds.length; w++) {
						results = testScheme(numTests, baseLength, alg, worlds[w], false, incScheme, incFactor, decScheme, decFactor);

						fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
						System.out.println(results);
					}
					fwr.write("\n");
					
					fwr.write("" + alg + "_0" + sep + incScheme + sep + incFactor + sep + decScheme + sep + decFactor); //header
					for (int w = 0; w < worlds.length; w++) {
						results = testScheme(numTests, baseLength, alg, worlds[w], true, incScheme, incFactor, decScheme, decFactor);
						//fwr.write(sep + results);
						fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
						System.out.println(results);
					}
					fwr.write("\n");
				}
			}
	
			fwr.close();
		} catch (FileNotFoundException e) {
			System.err.println("Bad file.");
		} catch (IOException e) {
			System.err.println("Error writing to file.");
		}

	}
	
private static void testVariedBaseLengths(String[] worlds) throws Exception {
	
	List<Search.Algorithm> algs = new ArrayList<Search.Algorithm>();
	algs.add(Search.Algorithm.VLRRT);
	algs.add(Search.Algorithm.DVLRRT);	
	int numTests = 100;

	ResultSetData results;
	String prefix = "changeSchemeTests" + numTests + "_";
	
	File f = new File(prefix + System.currentTimeMillis());

	try {

		FileWriter fwr = new FileWriter(f);
		
		

		fwr.write("Algorithm" + sep + "BaseLength" + sep + "Mult incFactor");
		for (int w = 0; w < worlds.length; w++) fwr.write(sep + worlds[w].substring(7) + sep + "dist" + sep + "time");
		fwr.write("\n");

		for (int baseLength = 2; baseLength <= 10; baseLength = baseLength + 2 ) {
			fwr.write("" + Search.Algorithm.RRT + sep + baseLength); //header
			for (int w = 0; w < worlds.length; w++) { //not optimized
				results = testScheme(numTests, baseLength, Search.Algorithm.RRT, worlds[w], false, null, 0, null, 0); 
				fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
				System.out.println(results);
			}
			fwr.write("\n");
		}

		
		UpdateScheme incScheme = UpdateScheme.Mult;
		UpdateScheme decScheme = UpdateScheme.Restart;
		double decFactor = 1;  //doesn't matter

		for (double incFactor = 2; incFactor < 5; incFactor++) {
			for (int baseLength = 2; baseLength <= 10; baseLength = baseLength +2) {
				for (Search.Algorithm alg : algs) { //line for each alg/change scheme combination
					fwr.write("" + alg + sep + baseLength + sep + incFactor); //header
					for (int w = 0; w < worlds.length; w++) {
						results = testScheme(numTests, baseLength, alg, worlds[w], false, incScheme, incFactor, decScheme, decFactor);

						fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
						System.out.println(results);
					}
					fwr.write("\n");
				
				}
			}
		}

		fwr.close();
	} catch (FileNotFoundException e) {
		System.err.println("Bad file.");
	} catch (IOException e) {
		System.err.println("Error writing to file.");
	}

}

	private static ResultSetData testScheme(int numRuns, int baseLength, Search.Algorithm alg, String world, boolean optimize, 
			UpdateScheme inc, double incFactor, UpdateScheme dec, double decFactor) throws Exception {

		Testing test = new Testing(20,baseLength,0,null,new RRTWorld(world), optimize ,inc,incFactor,dec,decFactor);
		test.execNRuns(numRuns, alg);

		ResultSetData data = getData(test.getStats());
		return data;		
	}
	
	
	private static ResultSetData getData(List<RRTstats> theData) {
		double successes = 0;
		double failures = 0;
		double distance = 0;  //only when successful
		long time = 0;  //only when successful
		double nodes = 0;  //only when successful
		double coverage = 0;  //when not successful
		
		for (Stats s : theData) {
			if (s.isGoalFound()) {
				successes++;
				distance = distance + s.getgDistance();
				time = time + s.getElapsedTime();
				nodes = nodes + s.getnNodes();
			} else {
				failures++;
				coverage = coverage + s.getTreeCoverage();
			}
		}
		
		ResultSetData ret = new ResultSetData();
		ret.aveCoverage = (coverage)/(failures);
		ret.aveNodes = (nodes)/(successes);
		ret.aveTime = (time)/(successes);
		ret.avePathDist = (distance)/(successes);
		ret.successRate = (successes)/(theData.size());
		
		return ret;
	}
	
	private static Map<UpdateScheme,List<Double>> constructTests() {
		Map<UpdateScheme,List<Double>> ret = new HashMap<UpdateScheme,List<Double>>();
		
		List<Double> linearFactors = new ArrayList<Double>();
		for(double factor = .1;  factor < 2; factor += .2) linearFactors.add(factor);
		//linearFactors.add(.1);
		ret.put(UpdateScheme.Linear, linearFactors);
		
		List<Double> multFactors = new ArrayList<Double>();
		for(double factor = 2;  factor < 5; factor += 1) multFactors.add(factor);
		//multFactors.add(2.0);
		ret.put(UpdateScheme.Mult, multFactors);
		
		List<Double> restartFactors = new ArrayList<Double>();
		restartFactors.add(1.0);
		ret.put(UpdateScheme.Restart, restartFactors);
		
		return ret;
		
		
		
	}
	
	public static void tests(String[] worlds) throws Exception {
		Map<UpdateScheme,List<Double>> tests = constructTests();
		List<Search.Algorithm> algs = new ArrayList<Search.Algorithm>();

		algs.add(Search.Algorithm.VLRRT);
		algs.add(Search.Algorithm.DVLRRT);	

		ResultSetData results;
		String prefix = "mega_testage";
		
		File f = new File(prefix + System.currentTimeMillis());
		try {

			FileWriter fwr = new FileWriter(f);
			fwr.write("Algorithm" + sep + "IncScheme" + sep + "incFactor" + sep + "decScheme" + sep + "decFactor" +
					  sep + "World" + sep + "SuccRate" + sep + "avePath" + sep + "aveTime\n");
			fwr.write("RRT"  + sep + "" + sep + sep + sep); //header
			for (int w = 0; w < worlds.length; w++) {
				results = testScheme(100,10,Search.Algorithm.RRT,worlds[w], false, null, 0, null, 0);
				fwr.write(sep + worlds[w] + sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
			}
			fwr.write("\n");
			
			for (UpdateScheme incScheme : tests.keySet()) {
			for (UpdateScheme decScheme : tests.keySet()) {
				for (double incFactor : tests.get(incScheme)) {
					for (double decFactor : tests.get(decScheme)) {
						for (Search.Algorithm alg : algs) { //line for each alg/change scheme combination
							fwr.write("" + alg + sep + "" + incScheme + sep + incFactor + sep + decScheme + sep + decFactor); //header
							for (int w = 0; w < worlds.length; w++) {
								results = testScheme(100,10,alg, worlds[w], false, incScheme, incFactor, decScheme, decFactor);
								//fwr.write(sep + results);
								fwr.write(sep + worlds[w] + sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
								//System.out.println(results);
							}
							fwr.write("\n");
						}
					}
				}
			}
			}
			fwr.close();
		} catch (FileNotFoundException e) {
			System.err.println("Bad file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error writing to file.");
		}
	}
	
	
}
