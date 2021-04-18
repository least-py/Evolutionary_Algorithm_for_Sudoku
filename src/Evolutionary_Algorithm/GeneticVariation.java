package Evolutionary_Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class GeneticVariation {

	private static int N = Main.N;
	private static int N_squared = N*N;
	private static int[][] adjacency_matrix = adjacency_matrix();
	
	public static Individual tournament_selection(Individual[] pop, int q) {
		Individual[] ivs = new Individual[q];
		for (int i = 0; i < q; i++) {
			ivs[i] = pop[(int) (Math.random() * pop.length)];
		}
		return getFittest(ivs, 1)[0];
	}
	
	public static Individual rndm_selection(Individual[] pop) {
		return pop[(int) (Math.random() * pop.length)];
	}

	
	public static Individual parameterized_uniform_crossover(Individual iv_1, Individual iv_2, double cprob) {
		
		int[] child_genome = new int[N_squared];
		Individual parent_1 = null;
		Individual parent_2 = null;
		if (iv_1.getFitness() < iv_2.getFitness()) {
			parent_1 = iv_1;
			parent_2 = iv_2;
		} else {
			parent_1 = iv_2;
			parent_2 = iv_1;
		}
		for (int i = 0; i < N_squared; i++) {
			if (Math.random() < cprob) {
				child_genome[i] = parent_1.getGenome()[i];
			} else {
				child_genome[i] = parent_2.getGenome()[i];
			}
		}
		return new Individual(child_genome);

	}
	
	public static Individual one_point_crossover(Individual iv_1, Individual iv_2) {
		
		int[] child_genome = new int[N_squared];

		Individual parent_1 = null;
		Individual parent_2 = null;
		if (iv_1.getFitness() < iv_2.getFitness()) {
			parent_1 = iv_1;
			parent_2 = iv_2;
		} else {
			parent_1 = iv_2;
			parent_2 = iv_1;
		}
		int point = (int) (Math.random() * N_squared) + 1;
		for (int i = 0; i < point; i++) {
			child_genome[i] = parent_1.getGenome()[i];
		}
		for (int i = point; i < N_squared; i++) {
			child_genome[i] = parent_2.getGenome()[i];
		}
		return new Individual(child_genome);

	}

	private static int[][] adjacency_array() {
		int[][] index_grid = new int[N][N];
		int index = 0;
		for (int z = 0; z < N; z++) {
			for (int s = 0; s < N; s++) {
				index_grid[z][s] = index;
				index++;
			}
		}

		int[][] box_squares = new int[N][N];
		index = 0;
		int sqroot = (int) Math.sqrt(N);
		int[] box = new int[N];
		for (int hb = 0; hb < sqroot; hb++) {
			for (int vb = 0; vb < sqroot; vb++) {

				for (int z = 0; z < sqroot; z++) {
					for (int s = 0; s < sqroot; s++) {
						box[(z * sqroot) + s] = index_grid[z + (vb * sqroot)][s + (hb * sqroot)];
					}
				}
				equal_arrays(box_squares[index], box);
				index++;
			}
		}
		int[][] adjacency_array = new int[N_squared][3 * N];
		for (int i = 0; i < N_squared; i++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int b = 0; b < N; b++) {
				for (int n = 0; n < N; n++) {
					if (i == box_squares[b][n]) {
						for (int e = 0; e < N; e++) {
							list.add(box_squares[b][e]);
						}
					}
				}
			}
			for (int e = 0; e < N; e++) {
				list.add((i % N) + (e * N));
			}
			int index_row = (int) (i / N);
			for (int e = 0; e < N; e++) {
				list.add(index_grid[index_row][e]);
			}
			for (int n = 0; n < 3 * N; n++) {
				adjacency_array[i][n] = list.get(n);
			}
		}
		return adjacency_array;
	}
	
	
	public static int[][] adjacency_matrix() {
		int N_squared = N * N;
		int[][] adjm = new int[N_squared][N_squared];
		int[][] adjacency_array = adjacency_array();
		for (int i = 0; i < N_squared; i++) {
			for (int e : adjacency_array[i]) {
				if (e != i) {
					adjm[i][e] = 1;
				}
			}
		}
		return adjm;
	}

	public static void print_matrix(int[][] matrix) {
		String s = "";
		for (int in = 0; in < matrix.length; in++) {
			for (int n = 0; n < matrix[0].length; n++) {
				s = s + matrix[in][n] + " ";
			}
			System.out.println(s);
			s = "";
		}
		System.out.println("");
	}

	public static Individual[] getFittest(Individual[] ivs, int amount) {
		Individual[] popcopy = ivs.clone();
		sort_pop(popcopy);
		Individual[] selection = new Individual[amount];
		for (int i = 0; i < amount; i++) {
			selection[i] = popcopy[i];
		}
		return selection;
	}

	public static Individual[] sort_pop(Individual[] pop) {
		Arrays.sort(pop, new Comparator<Individual>() {
			
			public int compare(Individual iv_1, Individual iv_2) {
				if (iv_1.getFitness() < iv_2.getFitness()) {
					return -1;
				} else if (iv_1.getFitness() > iv_2.getFitness()) {
					return 1;
				}
				return 0;
			}
		});
		return pop;
	}

	public static void calc_fitness(Individual iv) {
		int N_squared = N * N;
		int fitness = 0;
		for (int v = 0; v < N_squared; v++) {
			for (int e = 0; e < N_squared; e++) {
				if (getAdjacency_matrix()[v][e] == 1 && iv.getGenome()[v] == iv.getGenome()[e]) {
					fitness++;
				}
			}
		}
		iv.setFitness(fitness);
	}

	public static boolean inArray(int z, int[] arr) {
		for (int i : arr) {
			if (i == z) {
				return true;
			}
		}
		return false;
	}

	public static void equal_arrays(int[] a, int[] b) {
		// call by reference
		if (b.length <= a.length) {
			for (int i = 0; i < a.length; i++) {
				a[i] = b[i];
			}
		}
	}

	public static void valid_colors_mutation(Individual iv) {

		int N_squared = N * N;
		for (int v = 0; v < N_squared; v++) {
			ArrayList<Integer> adjacency_colors = new ArrayList<Integer>();

			for (int e = 0; e < N_squared; e++) {
				if (adjacency_matrix[v][e] == 1) {
					adjacency_colors.add(iv.getGenome()[e]);

					if (iv.getGenome()[v] == iv.getGenome()[e]) {
						ArrayList<Integer> valid_colors = new ArrayList<Integer>();
						for (int f = 1; f <= (int) Math.sqrt(N_squared); f++) {
							if (!(adjacency_colors.contains(f))) {
								valid_colors.add(f);
							}
						}
						if (valid_colors.size() > 0) {
							iv.getGenome()[v] = valid_colors.get((int) (Math.random() * valid_colors.size()));

						}
					}
				}
			}
		}
	}
	
	public static void all_colors_mutation(Individual iv) {
		int N_squared = N * N;
		for (int v = 0; v < N_squared; v++) {

			for (int e = 0; e < N_squared; e++) {
				
				if (getAdjacency_matrix()[v][e] == 1 && iv.getGenome()[v] == iv.getGenome()[e]) {
				
					iv.getGenome()[v] = (int) (Math.random() * ((int) Math.sqrt(N_squared))) + 1;
				}
			}
		}
	}

	public static int[][] getAdjacency_matrix() {
		return adjacency_matrix;
	}

	public static void setAdjacency_matrix(int[][] adjacency_matrix) {
		GeneticVariation.adjacency_matrix = adjacency_matrix;
	}
}
