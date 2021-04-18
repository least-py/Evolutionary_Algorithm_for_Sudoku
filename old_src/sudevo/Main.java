package sudevo;

import javax.swing.JFileChooser;

import sudevo_gcp_strategy.Main_gcp;
import sudevo_genetic.Main_1;

public class Main {

	private static String path;
	private static String chart_path;

	public static void main(String[] args) {

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Pick a folder for your charts");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		while (chart_path == null) {
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
				path = "jdbc:sqlite:" + chooser.getCurrentDirectory() + "\\";
				chart_path = "" + chooser.getSelectedFile() + "\\";
				System.out.println(chart_path);

			} else {
				System.out.println("Select a path for saving your charts");
			}
		}

		// Falls visu3 auch getestet wird, sollte die Populationsgröße verringert
		// werden.
		// Ansonsten entstehen über 50 Diagramme!!

		// Darauf achten, dass die Methoden der beiden Main Klassen mit Tabellennamen
		// gestartet werden, die noch nicht existieren.
		// Methoden aus Visualisation mit Tabellennamen starten, die bereits existieren
		// -> Main vor Visualisation

		// Darauf achten, dass man bei 0 (Tabellenversionen) beginnt, wenn man visu2
		// testet.

		// Datenbanken werden automatisch erstellt, wenn sie vorher nicht existierten.

		// Algorithmus_1 testen

		Main_1.ev_stragety("test_1_0", "test", 0.8, 0.8, 5, 2000, 0.2, 0.9);
		Main_1.ev_stragety("test_1_1", "test", 0.8, 0.8, 5, 2000, 0.2, 0.9);
		Main_1.ev_stragety("test_1_2", "test", 0.8, 0.8, 5, 2000, 0.2, 0.9);
		Visualisation.visu("test_1_1", "test");
		Visualisation.visu2("test_1_", "test", 3, 2000);
		Visualisation.visu3("test_1_1", "test");

		// Algorithmus_2 testen
		Main_gcp.gcp_test("test_2_0", "test", 25, 2000, 0.5, 1.0, 5, 1.0);
		Main_gcp.gcp_test("test_2_1", "test", 25, 2000, 0.5, 1.0, 5, 1.0);
		Main_gcp.gcp_test("test_2_2", "test", 25, 2000, 0.5, 1.0, 5, 1.0);
		Visualisation.visu("test_2_0", "test");
		Visualisation.visu2("test_2_", "test", 3, 2000);
		Visualisation.visu3("test_2_1", "test");
	}

	public static String getChartPath() {
		return chart_path;
	}

	public static String getPath() {
		return path;
	}

}
