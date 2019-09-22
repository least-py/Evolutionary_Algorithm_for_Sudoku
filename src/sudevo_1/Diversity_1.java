package sudevo_1;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Diese Klasse dient der Gewinnung von �hnlichkeitsdaten, um Aussagen �ber die Diversit�t zu treffen.
 * @author Lea
 *
 */
public class Diversity_1 {
	
	/**
	 * Berechnung der Anteile gleicher Zahlen an gleichen Stellen
	 * @param genome_1 Genom eines Individuums
	 * @param genome_2 Genom eines anderen Individuums
	 * @return Quotient aus Treffern und Anzahl der Felder
	 */
	public static double calc_similarity(int[][] genome_1, int[][] genome_2) {
		int match = 0;
		for(int r = 0; r < Main_1.getN(); r++) {
			for(int c = 0; c < Main_1.getN(); c++ ) {
				if(genome_1[r][c] == genome_2[r][c]) {
					match++;
				}
			}
		}
		return (double)match/(Main_1.getN() * Main_1.getN());
	}
	
	/**
	 * Ordnet einem Individuum ein Ausgangsindividuum mit der h�chsten �bereinstimmung zu.
	 * @param i Individuum
	 * @return den Index des zu dem Individuum �hnlichen Ausgangsindividuums innerhalb des Ausgangspopulationsarrays
	 */
	public static void associate_similarity(Individual_1 i) {
		//Enth�lt die �hnlichkeit zu jedem Individuum der Ausgangspopulation
		ArrayList<Double> similarity_list = new ArrayList<Double>();
		
		double similarity = 0.0;
		ArrayList<Double> list = new ArrayList<Double>();
		for(Individual_1 ii : Main_1.getInitial_pop()) {
			similarity = calc_similarity(i.getGenome(), ii.getGenome());
			list.add(similarity);
			similarity_list.add(similarity);
		}
		//Den gr��ten �hnlichkeitsanteil ermitteln
		double max = Collections.max(list);
		i.setSimilarity(max);
		i.setAssociation(similarity_list.indexOf(max));
		
	}
	

	
}
