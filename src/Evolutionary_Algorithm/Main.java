package Evolutionary_Algorithm;

import javax.swing.JFileChooser;

import Evolutionary_Algorithm.*;

public class Main {

	public static int N = 9;
	private static String path;
	private static String chart_path;
	
	public static void main(String[] args) {
		file_chooser();
		
		int popsize = 100;
		int max_generations = 5000;
		double selection_amount = 0.5;
		double crossover_amount = 0.5;
		int q = 5;
		double mutation_prob = 0.3;
		double max_simil = 0.8;
		
		EvolutionaryCircle sudevo = new EvolutionaryCircle("die base", "firsttry", path, popsize, max_generations, selection_amount,
									crossover_amount, q, mutation_prob, max_simil);

	}

	
	public static void file_chooser() {
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
	}
}


