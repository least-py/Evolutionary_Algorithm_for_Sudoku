package sudevo_2;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Diese Klasse dient der Gewinnung von Ähnlichkeitsdaten, um Aussagen über die Diversität zu treffen.
 * @author Lea
 *
 */
public class Diversity_2 {

	/**
	 * Berechnung der Anteile gleicher Allele bei gleichen Loci
	 * @param genome_1 Genom eines Individuums
	 * @param genome_2 Genom eines anderen Individuums
	 * @return Quotient aus Treffern und Anzahl der Felder
	 */
	public static double calc_similarity(int[] genome_1, int[] genome_2) {
		int match = 0;
		for(int g = 0; g < (Main_2.getN()*Main_2.getN()); g++) {
				if(genome_1[g] == genome_2[g]) {
					match++;
			}
		}
		return (double)match/(Main_2.getN() * Main_2.getN());
	}
	
	/**
	 * Ordnet einem Individuum ein Ausgangsindividuum mit der höchsten Übereinstimmung zu.
	 * @param i Individuum
	 * @return den Index des zu dem Individuum ähnlichen Ausgangsindividuums innerhalb des Ausgangspopulationsarrays
	 */
	public static void associate_similarity(Individual_2 i) {
		//Enthält die Ähnlichkeit zu jedem Individuum der Ausgangspopulation
		ArrayList<Double> similarity_list = new ArrayList<Double>();
		
		double similarity = 0.0;
		ArrayList<Double> list = new ArrayList<Double>();
		for(Individual_2 ii : Main_2.getInitial_pop()) {
			//Ähnlichkeitswert errechnen
			similarity = calc_similarity(i.getGenome(), ii.getGenome());
			//Wert der Liste hinzufügen
			list.add(similarity);
			//Wert der similarity Liste hinzufügen
			similarity_list.add(similarity);
		}
		//Den gröten Ähnlichkeitsanteil ermitteln
		//Liste wird sortiert -> Zuordnung geschieht über Index, deshalb die zweite
		//Liste similarity_list
		double max = Collections.max(list);
		i.setSimilarity(max);
		i.setAssociation(similarity_list.indexOf(max));
		
	}
}
