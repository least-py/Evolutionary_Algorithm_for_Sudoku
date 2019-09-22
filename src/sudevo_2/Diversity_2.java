package sudevo_2;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Diese Klasse dient der Gewinnung von �hnlichkeitsdaten, um Aussagen �ber die Diversit�t zu treffen.
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
	 * Ordnet einem Individuum ein Ausgangsindividuum mit der h�chsten �bereinstimmung zu.
	 * @param i Individuum
	 * @return den Index des zu dem Individuum �hnlichen Ausgangsindividuums innerhalb des Ausgangspopulationsarrays
	 */
	public static void associate_similarity(Individual_2 i) {
		//Enth�lt die �hnlichkeit zu jedem Individuum der Ausgangspopulation
		ArrayList<Double> similarity_list = new ArrayList<Double>();
		
		double similarity = 0.0;
		ArrayList<Double> list = new ArrayList<Double>();
		for(Individual_2 ii : Main_2.getInitial_pop()) {
			//�hnlichkeitswert errechnen
			similarity = calc_similarity(i.getGenome(), ii.getGenome());
			//Wert der Liste hinzuf�gen
			list.add(similarity);
			//Wert der similarity Liste hinzuf�gen
			similarity_list.add(similarity);
		}
		//Den gr�ten �hnlichkeitsanteil ermitteln
		//Liste wird sortiert -> Zuordnung geschieht �ber Index, deshalb die zweite
		//Liste similarity_list
		double max = Collections.max(list);
		i.setSimilarity(max);
		i.setAssociation(similarity_list.indexOf(max));
		
	}
}
