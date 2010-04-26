package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

class PlotFactory {
	private static XYDataset createDataset(String n, double[][] d) {
		XYSeries xyseries = new XYSeries(n);

		for(double[] a:d)
			xyseries.add(a[0], a[1]);

		return new XYSeriesCollection(xyseries);
	}

	private static XYDataset createDataset(String[] labels, double[][] d[]) {
		if(labels.length != d.length)
			throw new RuntimeException("Table Labels.size <> data.size");

		XYSeriesCollection r = new XYSeriesCollection();
		XYSeries s;
		for(int i=0;i<labels.length;++i){
			s = new XYSeries(labels[i]);

			for(double[] a:d[i])
				s.add(a[0], a[1]);

			r.addSeries(s);
		}

		return r;
	}

	public static JPanel createBarChart(String title,String x,String y,double[][] d) {
		return new ChartPanel(ChartFactory.createXYLineChart(title, x, y,
				createDataset("",d), PlotOrientation.VERTICAL, false, true, false));
	}

	public static JPanel createBarChart(String title,String x,String y,String[] labels,double[][] ...d) {
		return new ChartPanel(ChartFactory.createXYLineChart(title, x, y,
				createDataset(labels,d), PlotOrientation.VERTICAL, true, true, false));
	}
}


@SuppressWarnings("serial")
class Dataset extends AbstractIntervalXYDataset implements IntervalXYDataset {
	private Double xStart[];
	private Double xEnd[];
	private Double yValues[];
	private String name;

	public Dataset(String name,double[][] d) {
		this.name=name;
		xStart = new Double[d.length];
		xEnd = new Double[d.length];
		yValues = new Double[d.length];

		for(int i=0;i<d.length;++i){
			xStart[i] =  d[i][0];
			xEnd[i] =    d[i][0]+1.0;
			yValues[i] = d[i][1];
		}
	}

	public int getSeriesCount() { return 1; }
	
	public Comparable<?> getSeriesKey(int i) { return name; }
	
	public int getItemCount(int i)        { return yValues.length; }
	public Number getX     (int i, int j) { return xStart[j]; }
	public Number getY     (int i, int j) { return yValues[j]; }
	public Number getStartX(int i, int j) { return xStart[j]; }
	public Number getEndX  (int i, int j) { return xEnd[j]; }
	public Number getStartY(int i, int j) { return yValues[j]; }
	public Number getEndY  (int i, int j) { return yValues[j]; }

	//    public void addChangeListener(DatasetChangeListener datasetchangelistener) {}
	//    public void removeChangeListener(DatasetChangeListener datasetchangelistener) {}

	public static JPanel createBarChart(String title,String x,String y,String series,double[][] d) {
		return new ChartPanel(ChartFactory.createXYBarChart(title,x, false,y,
				new Dataset(series,d),PlotOrientation.VERTICAL, false, true, false));
	}
}

@SuppressWarnings("serial")
public class StatsGUI extends JFrame {

	public StatsGUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,400);

		JTabbedPane tabs = new JTabbedPane();

		//table
		Object[][] rows = { {0,1} , {2,3}};
		Object[] columns = {"column1","column2"};
		JTable table = new JTable(rows, columns);
		table.setAutoscrolls(true);
		tabs.addTab("table",new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

		///
		double[][] points = { {1,2,3,4} , {3,4,5,5}};
		tabs.addTab("bar",
				Dataset.createBarChart(
						"asd",
						"aqwd",
						"qwe",
						"qwe",
						points));

		Random rd = new Random();
		tabs.addTab("plot", PlotFactory.createBarChart(
				"title", "x-label", "y-label",
				new String[]{"a","b"},

				new double[][]{
						{1,rd.nextDouble()}, // (x,y)
						{2,rd.nextDouble()},
						{3,rd.nextDouble()},
						{4,rd.nextDouble()},
						{5,rd.nextDouble()}
				},

				new double[][]{
						{1,rd.nextDouble()},
						{2,rd.nextDouble()},
						{3,rd.nextDouble()},
						{4,rd.nextDouble()},
						{5,rd.nextDouble()}
				}
		));
		add(tabs);
	}
	
	public StatsGUI(String st) throws Exception{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,400);
		final int MAX = 100;
		Scanner sc = new Scanner(new File(st));
		sc.nextLine(); //ignore first line.
		
		XYSeriesCollection wc = new XYSeriesCollection();
		XYSeriesCollection nwc = new XYSeriesCollection();
		
		while( sc.hasNext() ){
			XYSeries s = new XYSeries("?");
			for(int j=0;j<MAX;++j){
				s.setKey( sc.next() );
				s.add(sc.nextInt(), sc.nextDouble());
			}
			sc.next(); sc.next(); sc.next();
			
			
//			s = new XYSeries( s.getKey()+" average");
			double average = sc.nextDouble();
			s.setKey( s.getKey()+" "+average );
//			s.add(average, 50e6);
//			s.add(average, 0);
//			r.addSeries(s);
		
			if( (s.getKey()+"").contains("_WC") )
				wc.addSeries(s);
			else
				nwc.addSeries(s);
		
		}
		
		JTabbedPane tabs = new JTabbedPane();

		tabs.addTab("_W",
			new ChartPanel(ChartFactory.createXYLineChart(
					st, "iteration #", "time (ns)",
					wc, PlotOrientation.VERTICAL, true, true, false) )
		);

		tabs.addTab("_NWC",
			new ChartPanel(ChartFactory.createXYLineChart(
					st, "iteration #", "time (ns)",
					nwc, PlotOrientation.VERTICAL, true, true, false) )
		);
		
		add(tabs);
	}

	public static void main(String[] args) throws Exception{

//		JFrame f = new StatsGUI("batcher_80_RRTpaper-world.txt");
//		JFrame f = new StatsGUI("batcher_634_RRTpaper-world");
		JFrame f = new StatsGUI(
//				"batcher_wp15_424(false)_cluttered"
				"batcher_wp15_972(true)_cluttered"
//				"batcher_wp15_663(false)_proposal-world"

//				"batcher_wp15_328(true)_RRTpaper-world"
//								"batcher_wp15_827(true)_proposal-world"
//				"batcher_wp15_524(false)_RRTpaper-world"
//				"batcher_wp15_894(false)_RRTpaper-world"
//				"batcher_wp15_717(true)_cluttered"
//				"batcher_wp15_50(true)_RRTpaper-world"
		);
		f.setVisible(true);
	}
}
