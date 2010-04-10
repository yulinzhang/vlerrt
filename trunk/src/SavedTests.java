import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedTests {
	
	public class ResultSet {
		ResultSetData data = new ResultSetData();
	}
	
	public static final String sep = "\t";
	
	public static void main(String[] args) throws Exception {
		//screenShotTests(args);
		//basicTest(args);
		testEpsilonChangeSchemes(args);
	}
	
	private static void screenShotTests(String[] args) throws Exception {
		Testing test;
		String world;
		VLRRTnode.changeEpsilonScheme inc = VLRRTnode.changeEpsilonScheme.Mult;
		VLRRTnode.changeEpsilonScheme dec = VLRRTnode.changeEpsilonScheme.Restart;
		double incFactor = 2;
		double decFactor = .1;
		
		for(int i = 0; i < args.length; i++){
			world = args[i];
			
			test = new Testing(50,20, 10, 0, null, new RRTWorld(world),
					inc, incFactor, dec, decFactor); //
			
			test.execNRuns(1,RRTsearch.Algorithm.RRT,true);
			test.execNRuns(1,RRTsearch.Algorithm.VLRRT,true);
			test.execNRuns(1,RRTsearch.Algorithm.DVLRRT,true);
			test.printStats("stats_" + world.substring(7),true);
		}
	}
	
	private static void basicTest(String[] args) throws Exception {
		Testing test;
		String world;
		
		for(int i = 0; i < args.length; i++){
			world = args[i];
			
			test = new Testing(25,20, 10, 0, null, new RRTWorld(world)); //
			
			test.execNRuns(100,RRTsearch.Algorithm.RRT);
			test.execNRuns(100,RRTsearch.Algorithm.VLRRT);
			test.execNRuns(100,RRTsearch.Algorithm.DVLRRT);
			test.printStats("stats_" + world.substring(7),true);
		}
		
		
	}
	
	private static void testEpsilonChangeSchemes(String[] worlds) throws Exception {
		
		//List<FileWriter> files = new ArrayList<FileWriter>();
		//Map<FileWriter,ResultSetData> fwToRes = new HashMap<FileWriter, ResultSetData>();
		Map<VLRRTnode.changeEpsilonScheme,List<Double>> tests = constructTests();
		List<RRTsearch.Algorithm> algs = new ArrayList<RRTsearch.Algorithm>();
		algs.add(RRTsearch.Algorithm.VLRRT);
		algs.add(RRTsearch.Algorithm.DVLRRT);	
		//double results;
		ResultSetData results;
		String prefix = "changeSchemeTests";
		
		File f = new File(prefix + System.currentTimeMillis());
//		File success = new File(prefix + "_success" + System.currentTimeMillis());
//		File dist = new File(prefix + "_distance" + System.currentTimeMillis());
//		File time = new File(prefix + "_time" + System.currentTimeMillis());
//		File coverage = new File(prefix + "_coverage" + System.currentTimeMillis());
//		File nodes = new File(prefix + "_nodes" + System.currentTimeMillis());
		
		
		try {
//			FileWriter successFwr = new FileWriter(success);
//			FileWriter distFwr = new FileWriter(dist);
//			FileWriter timeFwr = new FileWriter(time);
//			FileWriter coverageFwr = new FileWriter(coverage);
//			FileWriter nodesFwr = new FileWriter(nodes);
//			files.add(successFwr);
//			files.add(distFwr);
//			files.add(timeFwr);
//			files.add(coverageFwr);
//			files.add(nodesFwr);
			FileWriter fwr = new FileWriter(f);
			
			
			//header
			//for (FileWriter fwr : files) {
				fwr.write("Algorithm" + sep + "IncScheme" + sep + "incFactor" + sep + "decScheme" + sep + "decFactor");
				for (int w = 0; w < worlds.length; w++) fwr.write(sep + worlds[w].substring(7) + sep + sep);
				fwr.write("\n");
			//}
			//basic RRT
			//for (FileWriter fwr : files) {
				fwr.write("" + RRTsearch.Algorithm.RRT + sep + 0 + sep + 0 + sep + 0 + sep + 0); //header
			//}
				for (int w = 0; w < worlds.length; w++) {
					results = testScheme(RRTsearch.Algorithm.RRT, worlds[w], null, 0, null, 0); 
					fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
				}
			//for (FileWriter fwr : files) {	
				fwr.write("\n");
			//}
			
			for (VLRRTnode.changeEpsilonScheme incScheme : tests.keySet()) {
				for (VLRRTnode.changeEpsilonScheme decScheme : tests.keySet()) {
					for (double incFactor : tests.get(incScheme)) {
						for (double decFactor : tests.get(decScheme)) {
							for (RRTsearch.Algorithm alg : algs) { //line for each alg/change scheme combination
								fwr.write("" + alg + sep + incScheme + sep + incFactor + sep + decScheme + sep + decFactor); //header
								for (int w = 0; w < worlds.length; w++) {
									results = testScheme(alg, worlds[w], incScheme, incFactor, decScheme, decFactor);
									//fwr.write(sep + results);
									fwr.write(sep + results.successRate + sep + results.avePathDist + sep + results.aveTime);
									System.out.println(results);
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
		} catch (IOException e) {
			System.err.println("Error writing to file.");
		}

	}
	
//	private static double testScheme(RRTsearch.Algorithm alg, String world, VLRRTnode.changeEpsilonScheme inc, double incFactor, 
//		VLRRTnode.changeEpsilonScheme dec, double decFactor) throws Exception {
//		Testing test = new Testing(20,10,0,null,new RRTWorld(world),inc,incFactor,dec,decFactor);
//		test.execNRuns(100, alg);
//		double percent = getPercentSuccess(test.getStats());
//		return percent;
//	}
//	
//	private static double getPercentSuccess(List<Stats> theData) {
//		double successes = 0;
//		for (Stats s : theData) {
//			if (s.isGoalFound()) successes++;
//		}
//		
//		return (successes)/(theData.size());
//	}
	
	private static ResultSetData testScheme(RRTsearch.Algorithm alg, String world, VLRRTnode.changeEpsilonScheme inc, double incFactor, 
											VLRRTnode.changeEpsilonScheme dec, double decFactor) throws Exception {
		Testing test = new Testing(20,10,0,null,new RRTWorld(world),inc,incFactor,dec,decFactor);
		test.execNRuns(100, alg);
		ResultSetData data = getData(test.getStats());
		return data;		
	}
	
	
	private static ResultSetData getData(List<Stats> theData) {
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
	
	private static Map<VLRRTnode.changeEpsilonScheme,List<Double>> constructTests() {
		Map<VLRRTnode.changeEpsilonScheme,List<Double>> ret = new HashMap<VLRRTnode.changeEpsilonScheme,List<Double>>();
		
		List<Double> linearFactors = new ArrayList<Double>();
		//for(double factor = .1;  factor < 2; factor += .2) linearFactors.add(factor);
		linearFactors.add(.1);
		ret.put(VLRRTnode.changeEpsilonScheme.Linear, linearFactors);
		
		List<Double> multFactors = new ArrayList<Double>();
		//for(double factor = 2;  factor < 5; factor += 1) multFactors.add(factor);
		multFactors.add(2.0);
		ret.put(VLRRTnode.changeEpsilonScheme.Mult, multFactors);
		
		List<Double> restartFactors = new ArrayList<Double>();
		restartFactors.add(1.0);
		ret.put(VLRRTnode.changeEpsilonScheme.Restart, restartFactors);
		
		return ret;
		
		
		
		
		
	}
	
	
}
