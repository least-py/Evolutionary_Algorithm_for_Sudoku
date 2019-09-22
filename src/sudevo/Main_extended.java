package sudevo;

import sudevo_1.Main_1;
import sudevo_2.Main_2;

public class Main_extended {

	private static String path;
	private static String chart_path;
	
	public static void main(String[] args) {
	
	//Beispiel für path: "jdbc:sqlite:C:\\Users\\Lea\\eclipse-workspace\\Evolutionary_Algorithm\\"
		
	chart_path = "C:\\Users\\Lea\\eclipse-workspace\\Evolutionary_Algorithm\\";
	path = "jdbc:sqlite:C:\\Users\\Lea\\eclipse-workspace\\Evolutionary_Algorithm\\";
	
	
	evoal();
	
	//Falls visu3 auch getestet wird, sollte die Populationsgröße verringert werden.
	//Ansonsten entstehen über 50 Diagramme!!
	
	//Darauf achten, dass die Methoden der beiden Main Klassen mit Tabellennamen gestartet werden, die noch nicht existieren.
	//Methoden aus Visualisation mit Tabellennamen starten, die bereits existieren -> Main vor Visualisation
	
	//Darauf achten, dass man bei 0 (Tabellenversionen) beginnt, wenn man visu2 testet.
	
	//Datenbanken werden automatisch erstellt, wenn sie vorher nicht existierten.
	
	}
	
	public static void evoal() {
		int iteration = 1;
		while(!Main_2.final_version()) {
			System.out.println("ITERATION: "+iteration);
			iteration++;
		}
	}

	public static String getChartPath() {
		return chart_path;
	}
	
	public static String getPath() {
		return path;
	}


}

