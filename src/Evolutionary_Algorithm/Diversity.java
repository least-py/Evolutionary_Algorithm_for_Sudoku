package Evolutionary_Algorithm;

import java.util.ArrayList;
import java.util.Collections;

public class Diversity {

	public static int N = Main.N;
	
	public static double calc_similarity(int[] genome_1, int[] genome_2) {
		int match = 0;
		int quadr_N = N*N;
		for (int gene = 0; gene < (quadr_N); gene++) {
			if (genome_1[gene] == genome_2[gene]) {
				match++;
			}
		}
		return (double) (match / (quadr_N));
	}
	
	public static void associate_similarity(Individual given_indivudal) {
		// Contains the similarity to each individual of the source population
		ArrayList<Double> similarity_list = new ArrayList<Double>();

		double similarity = 0.0;
		ArrayList<Double> list = new ArrayList<Double>();

		for (Individual iterated_individual : EvolutionaryCircle.getInitial_pop()) {
			similarity = calc_similarity(given_indivudal.getGenome(), iterated_individual.getGenome());
			list.add(similarity);
			similarity_list.add(similarity);
		}
		
		double max = Collections.max(list);
		given_indivudal.setSimilarity(max);
		given_indivudal.setAssociation(similarity_list.indexOf(max));

	}
}
