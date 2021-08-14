package Evolutionary_Algorithm;

import java.awt.geom.Rectangle2D;
import java.io.*;


import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
//import org.w3c.dom.DOMImplementation;
//import org.w3c.dom.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class Visualisation {

	

	/**
	 * Auf der Basis von
	 * <a href ="https://www.tutorialspoint.com/sqlite/sqlite_java.htm">SQLite
	 * Tutorial</a>, <a href
	 * ="https://www.mathematik.hu-berlin.de/~ccafm/teachingBasic/allg/JAVA_Pakete/JFreeChart/JFreeChart-Tutorial.html">JFreeChart
	 * Tutorial</a> Diese Klasse dient der Visualisierung vorhandener Daten einer
	 * Datenbank, die vorher duch Main_1 oder Main_2 entstanden sind! Informationen
	 * �ber das ResultSet: <a href
	 * ="https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html">ResultSetInfos</a>
	 */

		private static XYSeries[] series;
		private static XYSeriesCollection dataset;
		private static XYDotRenderer dot;
		private static NumberAxis yax;
		private static NumberAxis xax;
		private static ApplicationFrame punkteframe;
		private static XYPlot plot;
		private static JFreeChart chart;
		private static ChartPanel chartPanel;
		private static String path = Main.getPath();
		private static String chartpath = Main.getChartPath();

		// Maximal ermittelte Generationen einer Version
		private static int max_gen;

		/**
		 * F�hrt plot-fitness aus.
		 * 
		 * @param tn       Tabelle
		 * @param database Datenbank
		 */
		public static void visu(String tn, String database) {
			System.out.println(path + database + ".db");
			Connection c = null;
			Statement stmt = null;

			// "A ResultSet object is a table of data representing a database result set,
			// which is usually generated by executing a statement that queries the
			// database"
			// by https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
			ResultSet rs = null;

			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection(path + database + ".db");
				c.setAutoCommit(false);
				stmt = c.createStatement();

				plot_fitness(tn, rs, stmt);

				stmt.close();
				c.commit();
				c.close();
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
		}

		/**
		 * F�hrt plot_avergae_fitness aus.
		 * 
		 * @param tn       Tabelle (Vorraussetzung f�r die Anwendung dieser Methode ist,
		 *                 dass es maxv Tabellen gibt, die alle mit dem angegebenen
		 *                 String tn beginnen und jeweils die Versionsnummer von 0 bis
		 *                 (maxv-1) am Ende ausweisen. tn wird ohne Versionsnummer
		 *                 angegeben, da diese innerhalb einer for-Schleife angehongen
		 *                 wird.
		 * @param database Datenbank
		 * @param maxv     Maximal durchgef�hrte Versionen aus denen nun Mittelwerte
		 *                 gebildet werden sollen.
		 * @param maxgen   Maximal durchgef�hrte Generationen
		 */
		public static void visu2(String tn, String database, int maxv, int maxgen) {

			Connection c = null;
			Statement stmt = null;

			// "A ResultSet object is a table of data representing a database result set,
			// which is usually generated by executing a statement that queries the
			// database"
			// by https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
			ResultSet rs = null;

			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection(path + database + ".db");
				c.setAutoCommit(false);
				stmt = c.createStatement();

				plot_average_fitness(tn, rs, stmt, maxv, maxgen);

				stmt.close();
				c.commit();
				c.close();

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
		}

		/**
		 * Die Methode erstellt ein Diagramm, mit dem man die Anzahl der Zuordnungen des
		 * Individuums mit den meisten Zuordnungen verfolgen kann. Dadurch soll die
		 * "Beliebtheit" visualisiert werden. Wenn die Population zu konvergieren droht,
		 * wird sich die Anzahl der Zuordnungen des beliebtesten Individuums erh�hen.
		 * Sollte die Anzahl konstant bleiben, wird an einer weiteren Linie, die den
		 * Median der �hnlichkeitsanteile miteinbezieht ersichtlich, ob die zugeordneten
		 * Individuen dem Individuum immer �hnlicher werden. Eine weitere Linie wechselt
		 * zwischen 0 und 1, wenn das beliebteste Individuum wechselt. So soll die
		 * Dominanz eines Superindividuums erkannt werden.
		 * 
		 * @param tn       Tabelle
		 * @param database Datenbank
		 */
		public static void visu3(String tn, String database) {

			Connection c = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection(path + database + ".db");
				c.setAutoCommit(false);
				stmt = c.createStatement();

				plot_popularity(tn, rs, stmt);

				stmt.close();
				c.commit();
				c.close();
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
		}

		/**
		 * Die Methode berechnet den Median aus den gegebenen Werten.
		 * 
		 * @param values Ein Array mit Werten, aus denen der Median berechnet werden
		 *               soll
		 * @return Median
		 */
		public static double calc_median(Double[] values) {
			double median = 0;
			// call by name
			Arrays.sort(values);
			int n = values.length;
			if (!(n % 2 == 0)) {
				median = values[((n + 1) / 2) - 1];
			} else {
				median = 0.5 * (values[n / 2] + values[(n / 2) - 1]);
			}
			return median;
		}

		/**
		 * Berechnet aus mehreren Versionen die durchschnittliche Fitness und stellt sie
		 * dar.
		 * 
		 * @param tn          Tabellenname (Vorraussetzung f�r die Anwendung dieser
		 *                    Methode ist, dass es max_versionen Tabellen gibt, die alle
		 *                    mit dem angegebenen String tn beginnen und jeweils die
		 *                    Versionsnummer von 0 bis (max_versionen-1) am Ende
		 *                    ausweisen. tn wird ohne Versionsnummer angegeben, da diese
		 *                    innerhalb einer for-Schleife angehongen wird).
		 * @param rs          ResultSet
		 * @param stmt        Statement
		 * @param maxgen      Maximal durchgef�hrte Generationen
		 * @param max_version maximale <b>Anzahl</b> an Versionen
		 */
		private static void plot_average_fitness(String tn, ResultSet rs, Statement stmt, int max_version, int maxgen) {
			dataset = new XYSeriesCollection();
			series = new XYSeries[3];

			series[0] = new XYSeries("Fitness-Median");
			dataset.addSeries(series[0]);
			series[1] = new XYSeries("beste Fitness");
			dataset.addSeries(series[1]);
			series[2] = new XYSeries("schlechteste Fitness");
			dataset.addSeries(series[2]);

			dot = new XYDotRenderer();
			// dot.setDotHeight(4);
			// dot.setDotWidth(4);

			xax = new NumberAxis("Generation");
			yax = new NumberAxis("Fitness");

			// Erstellen eines Ausgabefensters
			punkteframe = new ApplicationFrame("Fitnessverlauf_" + tn);

			plot = new XYPlot(dataset, xax, yax, dot);
			chart = new JFreeChart(plot);
			chart = ChartFactory.createXYLineChart("Fitnessverlauf_" + tn, "Generationen", "Fitness", dataset,
					PlotOrientation.VERTICAL, true, true, true);
			chartPanel = new ChartPanel(chart);
			punkteframe.setContentPane(chartPanel);
			punkteframe.pack();
			punkteframe.setVisible(true);

			// Die Gr��e der Population bleibt konstant
			int popsize = 0;
			int numb_of_gen = 0;

			// Version kann beliebig gew�hlt werden, da die Generationsanzahl und
			// Populationsgr��e f�r alle Versionen des Exp. konstant sind
			try {
				rs = stmt.executeQuery("SELECT COUNT(ID) AS popsize FROM " + tn + "0" + " WHERE generation = 0;");

				if (rs.next()) {
					popsize = rs.getInt("popsize");
				}

				/*
				 * rs = stmt.executeQuery("SELECT MAX(generation) AS gens FROM "+tn+"0;");
				 * 
				 * if(rs.next()) { numb_of_gen = rs.getInt("gens"); }
				 */
				numb_of_gen = maxgen;

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// Eine Liste der Fitnesswerte aus denen sp�ter der Median errechnet wird
			Double[] fitness_values = new Double[popsize];

			// Die Fitnesswerte pro Generation sammeln

			for (int g = 0; g < numb_of_gen; g++) {

				for (int i = 0; i < fitness_values.length; i++) {
					fitness_values[i] = 0.0;
				}

				for (int vs = 0; vs < max_version; vs++) {
					try {
						// Z�hle die Anzahl der ID-Eintr�ge, bei denen das entsprechende Individuum dem
						// jeweiligen Ursprungsindividuum zugeordnet wird und in der Generation g
						// existierte
						rs = stmt.executeQuery("SELECT fitness  FROM " + tn + vs + " WHERE generation = " + g + " ;");

						int i = 0;
						while (rs.next()) {
							fitness_values[i] = fitness_values[i] + rs.getDouble("fitness");
							i++;
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				// Das Fitness-Mittel bilden
				for (int i = 0; i < fitness_values.length; i++) {
					fitness_values[i] = fitness_values[i] / max_version;
				}

				// Den Datensatz erstellen
				series[0].add(g, (calc_median(fitness_values)));

				// fitness_values sollte durch calc_median "global" sortiert sein

				// bester Wert
				series[1].add(g, fitness_values[0]);
				// schlechtester Wert
				series[2].add(g, fitness_values[fitness_values.length - 1]);
			}

			Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1300, 700);
			File f = new File(chartpath + "ev_" + tn + ".svg");
			try {
				exportChartAsSVG(chart, r, f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * Die Methode erzeugt ein Diagramm, indem der Fitnessverlauf �ber alle
		 * Generationen aufgezeigt wird. Es wird sowohl der Median als auch der beste
		 * und schlechteste Fitnesswert eingetragen. Je niedriger der Wert, desto besser
		 * die Fitness.
		 * 
		 * @param tn   Name der Tabelle
		 * @param rs   Resultset
		 * @param stmt Statement
		 */
		private static void plot_fitness(String tn, ResultSet rs, Statement stmt) {
			dataset = new XYSeriesCollection();
			series = new XYSeries[3];
			series[0] = new XYSeries("Fitness-Median");
			dataset.addSeries(series[0]);
			series[1] = new XYSeries("beste Fitness");
			dataset.addSeries(series[1]);
			series[2] = new XYSeries("schlechteste Fitness");
			dataset.addSeries(series[2]);
			dot = new XYDotRenderer();
			// dot.setDotHeight(4);
			// dot.setDotWidth(4);
			xax = new NumberAxis("Generation");
			yax = new NumberAxis("Fitness");

			// Erstellen eines Ausgabefensters
			// punkteframe = new ApplicationFrame("Fitnessverlauf_"+tn);

			plot = new XYPlot(dataset, xax, yax, dot);
			chart = new JFreeChart(plot);
			chart = ChartFactory.createXYLineChart("Fitnessverlauf", "Generationen", "Fitness", dataset,
					PlotOrientation.VERTICAL, true, true, true);
			chartPanel = new ChartPanel(chart);
			// punkteframe.setContentPane(chartPanel);
			// punkteframe.pack();
			// punkteframe.setVisible(true);

			// Die Gr��e der Population bleibt konstant
			int popsize = 0;
			int numb_of_gen = 0;

			Double[] fitness_values = null;

			try {
				rs = stmt.executeQuery("SELECT COUNT(ID) AS popsize FROM " + tn + " WHERE generation = 0;");

				if (rs.next()) {
					popsize = rs.getInt("popsize");
				}
				rs = stmt.executeQuery("SELECT MAX(generation) AS gens FROM " + tn + ";");

				if (rs.next()) {
					numb_of_gen = rs.getInt("gens");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// Die Fitnesswerte pro Generation sammeln

			for (int g = 0; g < numb_of_gen; g++) {
				System.out.println(g);
				try {
					// Z�hle die Anzahl der ID-Eintr�ge, bei denen das entsprechende Individuum dem
					// jeweiligen Ursprungsindividuum zugeordnet wird und in der Generation g
					// existierte
					rs = stmt.executeQuery("SELECT fitness  FROM " + tn + " WHERE generation = " + g + " ;");

					// Eine Liste der Fitnesswerte aus denen sp�ter der Median errechnet wird
					fitness_values = new Double[popsize];

					int i = 0;
					while (rs.next()) {
						fitness_values[i] = rs.getDouble("fitness");
						i++;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// Den Datensatz erstellen
				series[0].add(g, (calc_median(fitness_values)));

				// fitness_values sollte durch calc_median "global" sortiert sein
				// bester Wert
				series[1].add(g, fitness_values[0]);
				// schlechtester Wert
				series[2].add(g, fitness_values[fitness_values.length - 1]);

			}
			Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1300, 700);
			File f = new File(chartpath + tn + ".svg");
			try {
				exportChartAsSVG(chart, r, f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Die Methode erstellt ein Diagramm, mit dem man die Anzahl der Zuordnungen des
		 * Individuums mit den meisten Zuordnungen verfolgen kann. Dadurch soll die
		 * "Beliebtheit" visualisiert werden. Wenn die Population zu konvergieren droht,
		 * wird sich die Anzahl der Zuordnungen des beliebtesten Individuums erh�hen.
		 * Sollte die Anzahl konstant bleiben, wird an einer weiteren Linie, die den
		 * Median der �hnlichkeitsanteile miteinbezieht ersichtlich, ob die zugeordneten
		 * Individuen dem Individuum immer �hnlicher werden. Eine weitere Linie wechselt
		 * zwischen 0 und 1, wenn das beliebteste Individuum wechselt. So soll die
		 * Dominanz eines Superindividuums erkannt werden.
		 * 
		 * @param tn   Name der Tabelle
		 * @param rs   ResultSet
		 * @param stmt Statement
		 */
		private static void plot_popularity(String tn, ResultSet rs, Statement stmt) {

			// Speichert den Ursprungsindividuumsindex f�r den Vergleich der vorherigen
			// Generation
			int change_check = 0;
			int changes = 0;
			List<int[]> asscn = new ArrayList<int[]>();
			int popsize = 0;
			try {
				rs = stmt.executeQuery("SELECT COUNT(ID) AS popsize FROM " + tn + " WHERE generation = 0 ;");
				if (rs.next()) {
					popsize = rs.getInt("popsize");
				}
				rs = stmt.executeQuery("SELECT MAX(generation) AS gens FROM " + tn + ";");
				if (rs.next()) {
					max_gen = rs.getInt("gens");
				}
				// Sammlung der Zuordnungen pro Generation, um sp�ter das beliebteste
				// Individuum einer Generation zu ermitteln
				for (int i = 0; i < popsize; i++) {
					// Alle Urspringsindividuen-Diagramme erstellen
					asscn.add(collect_association(i, tn, rs, stmt));
				}
				dataset = new XYSeriesCollection();
				series = new XYSeries[3];
				series[0] = new XYSeries("Anzahl der meisten Zuordnungen");
				dataset.addSeries(series[0]);
				series[1] = new XYSeries("Meiste Zuordnungen mit Median");
				dataset.addSeries(series[1]);
				series[2] = new XYSeries("Superindividuum");
				dataset.addSeries(series[2]);

				dot = new XYDotRenderer();
				// dot.setDotHeight(4);
				// dot.setDotWidth(4);

				xax = new NumberAxis("Generation");
				yax = new NumberAxis("Beliebtheit");

				// Erstellen eines Ausgabefensters
				punkteframe = new ApplicationFrame("Beliebtheit der Ursprungsindividuen_" + tn);

				plot = new XYPlot(dataset, xax, yax, dot);
				chart = new JFreeChart(plot);
				chart = ChartFactory.createXYLineChart("Beliebheitsverlauf", "Generationen", "Beliebheit und Wechsel",
						dataset, PlotOrientation.VERTICAL, true, true, true);

				chartPanel = new ChartPanel(chart);
				punkteframe.setContentPane(chartPanel);
				punkteframe.pack();
				punkteframe.setVisible(true);

				for (int g = 0; g <= max_gen; g++) {
				
					// Zuordnungen einer Generation sammeln
					List<Integer> max_asscn = new ArrayList<Integer>();
					for (int i = 0; i < asscn.size(); i++) {
						max_asscn.add(asscn.get(i)[g]);
					}
					/*
					 * for(int[] current : asscn) { max_asscn.add(current[g]); }
					 */
					// H�chstwert an Zuordnungen dieser Generation
					int maximum_asscn = Collections.max(max_asscn);
					// beliebtestes Individuum
					int most_pop_iv = max_asscn.indexOf(maximum_asscn);

					// Wechsel-Check des beliebtesten Individuums
					if (g > 0 && change_check != most_pop_iv) {
						if (changes == 0) {
							changes = 1;
						} else {
							changes = 0;
						}
					}
					change_check = most_pop_iv;
					// Eine Liste der �hnlichkeitsanteile aus denen sp�ter der Median errechnet wird
					Double[] sim_values = new Double[maximum_asscn];
					
					// Abfrage der �hnlichkeitsanteile
					rs = stmt.executeQuery("SELECT similarity FROM " + tn + " WHERE simil_association = " + most_pop_iv
							+ " AND generation = " + g + " ;");
					
					int i = 0;
					while (rs.next()) {
						sim_values[i] = rs.getDouble("similarity");
						i++;
					}
					double median = calc_median(sim_values);
					
					// Superindividuum
					series[2].add(g, changes);
					// Der Parameter ist der Name des Punkts
					// Anzahl Zuordnungen des beliebtesten Individuums
					series[0].add(g, maximum_asscn);
					// mit Median
					series[1].add(g, maximum_asscn * median);

				}

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
			Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1300, 700);
			File f = new File(chartpath + "popularity_" + tn + ".svg");
			
			/*
			try {
				exportChartAsSVG(chart, r, f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
^			*/
		}

		/**
		 * Die Methode erzeugt ein Diagramm, indem die Anzahl der Zuordnungen pro
		 * Generation zu einem bestimmten Individuum der Ursprungspopulation und der
		 * Median der �hnlichkeitswerte, sowie der geringste und der h�chste
		 * �hnlichkeitswert abgebildet werden.
		 * 
		 * @param iv_index Index des jeweiligen Individuums im Ursprungspopulationsarray
		 *                 initial_pop der Klasse Main_1 und Main_2
		 * @param tn       Name der Tabelle
		 * @param rs       Resultset
		 * @param stmt     Statement
		 * @see Main_1, Main_2
		 */
		private static int[] collect_association(int iv_index, String tn, ResultSet rs, Statement stmt) {

			// Anzahl der Genertionen der Individuen bestimmen, die dem Individuum iv
			// zugeordnet werden
			int numb_of_gen = 0;
			try {
				rs = stmt.executeQuery("SELECT MAX(generation) AS gens FROM " + tn + " ;");
				if (rs.next()) {
					numb_of_gen = rs.getInt("gens");
					max_gen = numb_of_gen;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			// Die Anteile der Individuen der Abfrage sammeln
			int[] asscn_per_gen = new int[numb_of_gen + 1];
			// Bestimme f�r jede Generation die �bereinstimmungsanteile gegen�ber dem
			// Ursprungsindividuum
			// numb_of_gen ist der gr��te gefundene Wert. Da es aber auch Generation 0 gibt
			// ist die totale Anzahl an Generationen
			// eigentlich numb_of_gen+1
			for (int g = 0; g <= numb_of_gen; g++) {
				try {
					// Z�hle die Anzahl der ID-Eintr�ge, bei denen das entsprechende Individuum dem
					// jeweiligen Ursprungsindividuum zugeordnet wird und in der Generation g
					// existierte
					rs = stmt.executeQuery("SELECT COUNT(id) AS numb_of_asscn FROM " + tn + " WHERE simil_association = "
							+ iv_index + " AND generation = " + g + " ;");

					int numb_of_asscn = rs.getInt("numb_of_asscn");
					asscn_per_gen[g] = numb_of_asscn;
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return asscn_per_gen;
		}

		/**
		 * Code von:
		 * <a href ="http://dolf.trieschnigg.nl/jfreechart/">dolf.trieschnigg</a>,
		 * Erkl�rungen: <a href
		 * ="https://www.tutorialspoint.com/java/java_files_io.htm">tutorialspoint</a>
		 * 
		 * Erzeugt eine SVG Datei des Diagramms
		 * 
		 * @param chart   das Diagramm
		 * @param bounds  Dimensionen
		 * @param svgFile SVG Datei (hier Pfad zum Speichern angeben)
		 * @throws IOException wenn das Schreiben der Datei fehlschl�gt
		 */
		public static void exportChartAsSVG(JFreeChart chart, Rectangle2D.Double bounds, File svgFile) throws IOException {
			/*
			// Get a DOMImplementation and create an XML document
			DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
			Document document = domImpl.createDocument(null, "svg", null);

			// Create an instance of the SVG Generator
			SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

			// draw the chart in the SVG generator
			chart.draw(svgGenerator, bounds, null);

			// Write svg file
			OutputStream outputStream = new FileOutputStream(svgFile);
			Writer out = new OutputStreamWriter(outputStream, "UTF-8");
			svgGenerator.stream(out, true );
			outputStream.flush();
			outputStream.close();
			*/
		}

		
}


