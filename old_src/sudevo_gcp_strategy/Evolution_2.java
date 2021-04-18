package sudevo_gcp_strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 
 * In dieser Klasse befindet sich das "Handwerkzeug der Evolution".
 * 
 * @author Lea
 *
 */

public class Evolution_2 {

	private static int[][] adjacency_matrix = adjacency_matrix();

	/**
	 * Die Methode selektiert Individuen, indem sie q Individuen zufällig
	 * gleichverteilt auswählt und das beste (mit der besten Fitness) anschließend
	 * ausgibt. Je höher q gewählt wird, desto höher ist der Selektionsdruck.
	 * 
	 * @param pop die Population
	 * @param q   Anzahl an Feinden
	 * @return bestes Individuum
	 */
	public static Individual_2 tournament_selection(Individual_2[] pop, int q) {
		Individual_2[] ivs = new Individual_2[q];
		for (int i = 0; i < q; i++) {
			ivs[i] = pop[(int) (Math.random() * pop.length)];
		}
		return getFittest(ivs, 1)[0];
	}

	/**
	 * Bestimmt zufällig ein Individuum der Population. Alle Individuen haben die
	 * selbe Chance.
	 * 
	 * @param pop die Population
	 * @return ein zufällig gewähltes Individuum.
	 */
	public static Individual_2 rndm_selection(Individual_2[] pop) {
		return pop[(int) (Math.random() * pop.length)];
	}

	/**
	 * Dieser Rekombinationsoperator vermischt die Genome der beiden gegebenen
	 * Individuen. Bei jedem Gen entscheidet die angegebene Wahrscheinlichkeit, ob
	 * das Gen des besseren Individuums ausgewählt wird.
	 * 
	 * @param iv_1
	 * @param iv_2
	 * @param cprob
	 * @return neu zusammengesetztes Individuum
	 */
	public static Individual_2 parameterized_uniform_crossover(Individual_2 iv_1, Individual_2 iv_2, double cprob) {
		int N_squared = Main_gcp.getN() * Main_gcp.getN();
		int[] child_genome = new int[N_squared];
		// Zuordnung des angepasstesten Induviduums
		Individual_2 parent_1 = null;
		Individual_2 parent_2 = null;
		if (iv_1.getFitness() < iv_2.getFitness()) {
			// parent_1 hat die bessere Fitness
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
		return new Individual_2(child_genome);

	}

	public static Individual_2 one_point_crossover(Individual_2 iv_1, Individual_2 iv_2) {
		int N_squared = Main_gcp.getN() * Main_gcp.getN();
		int[] child_genome = new int[N_squared];

		// Zuordnung des angepasstesten Induviduums
		Individual_2 parent_1 = null;
		Individual_2 parent_2 = null;
		if (iv_1.getFitness() < iv_2.getFitness()) {
			// parent_1 hat die bessere Fitness
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
		return new Individual_2(child_genome);

	}

	private static int[][] adjacency_array() {
		int N_squared = Main_gcp.getN() * Main_gcp.getN();
		// index_grid ist eine quadratische Matrix mit den Knotenindizes
		int[][] index_grid = new int[Main_gcp.getN()][Main_gcp.getN()];
		int index = 0;
		for (int z = 0; z < Main_gcp.getN(); z++) {
			for (int s = 0; s < Main_gcp.getN(); s++) {
				index_grid[z][s] = index;
				index++;
			}
		}

		int[][] box_squares = new int[Main_gcp.getN()][Main_gcp.getN()];
		index = 0;
		// Box beginnt links oben, nächste Box ist die Box darunter
		int sqroot = (int) Math.sqrt(Main_gcp.getN());
		int[] box = new int[Main_gcp.getN()];
		// hb steht für horizontale box, damit die vertikale Erstellabfolge horizontal
		// verschoben werden kann
		for (int hb = 0; hb < sqroot; hb++) {
			// vb steht für vertikale box, damit die vertikale Erstellabfolge gewährleistet
			// wird
			for (int vb = 0; vb < sqroot; vb++) {

				// Box erstellen: Zeilen werden bis zum "Boxrand" dem boxarray nacheinander
				// hinzugefügt
				for (int z = 0; z < sqroot; z++) {
					for (int s = 0; s < sqroot; s++) {
						// (z*sqroot)+s sorgt dafür, dass die Elemente den richtigen Indices von 0 bis
						// n-1 zugeordnet werden
						box[(z * sqroot) + s] = index_grid[z + (vb * sqroot)][s + (hb * sqroot)];
					}
				}
				equal_arrays(box_squares[index], box);
				index++;
			}
		}
		// Durch das Hinzufügen der jeweilig ganzen Zeile, Spalte und Box werden manche
		// Indizes doppelt oder dreifach aufgenommen
		// Die spätere Nutzung der Indexlist beeinflusst dies jedoch nicht (außer
		// Schleifen)
		// Ist es aufwendiger die Häufungen zu entfernen, als mehrfach abzufragen?
		int[][] adjacency_array = new int[N_squared][3 * Main_gcp.getN()];
		// i repräsentiert einen Knoten
		for (int i = 0; i < N_squared; i++) {
			// leichteres Hinzufügen von Elementen
			ArrayList<Integer> list = new ArrayList<Integer>();
			// Box hinzufügen
			for (int b = 0; b < Main_gcp.getN(); b++) {
				for (int n = 0; n < Main_gcp.getN(); n++) {
					if (i == box_squares[b][n]) {
						for (int e = 0; e < Main_gcp.getN(); e++) {
							list.add(box_squares[b][e]);
						}
					}
				}
			}
			// Spalte hinzufügen
			for (int e = 0; e < Main_gcp.getN(); e++) {
				list.add((i % Main_gcp.getN()) + (e * Main_gcp.getN()));
			}
			// Zeile hinzufpgen
			int index_row = (int) (i / Main_gcp.getN());
			for (int e = 0; e < Main_gcp.getN(); e++) {
				list.add(index_grid[index_row][e]);
			}
			// Das Array an der Stelle i repräsentiert die Knoten, die mit dem Knoten i
			// verbunden sind (!Auf Schleife achten!)
			for (int n = 0; n < 3 * Main_gcp.getN(); n++) {
				adjacency_array[i][n] = list.get(n);
			}
		}
		return adjacency_array;
	}
	// nicht unnötig? Ohne die Matrix werden die Schleifen und das Abfragen von
	// bereits abgefragten Verbindungen verhindert

	/**
	 * Erzeugt die Adjazenzmatrix für ein Sudoku bestimmten Grades. Der Grad wird
	 * durch den Parameter in Main_2 bestimmt.
	 * 
	 * @return Adjazenzmatrix (2D-Array)
	 */
	public static int[][] adjacency_matrix() {
		int N_squared = Main_gcp.getN() * Main_gcp.getN();
		int[][] adjm = new int[N_squared][N_squared];
		int[][] adjacency_array = adjacency_array();
		for (int i = 0; i < N_squared; i++) {
			for (int e : adjacency_array[i]) {
				// Da diese adjacency_matrix schleifenfrei ist (Kein Knoten ist durch eine Kante
				// mit sich selbst verbunden), muss die Hauptdiagonale aus Nullen bestehen
				if (e != i) {
					adjm[i][e] = 1;
				}
			}
		}
		// print_matrix(adjm);
		return adjm;
	}

	/**
	 * Gibt eine Matrix in der Konsole aus.
	 * 
	 * @param matrix Matrix
	 */
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

	/**
	 * Mit dieser Methode lässt sich eine bestimmte Anzahl an Individuen mit der
	 * besten Fitness auswählen.
	 * 
	 * @param ivs    Population
	 * @param amount Anzahl an Individuen, die ermittelt werden sollen
	 * @return Liste mit Referenzen der besten Individuen
	 */
	public static Individual_2[] getFittest(Individual_2[] ivs, int amount) {
		// aufsteigendes Sortieren der Individuen nach dem Fitnesswert
		Individual_2[] popcopy = ivs.clone();
		sort_pop(popcopy);
		Individual_2[] selection = new Individual_2[amount];
		for (int i = 0; i < amount; i++) {
			selection[i] = popcopy[i];
		}
		return selection;
	}

	/**
	 * Sortiert ein Array mit Individuen aufsteigend.
	 * 
	 * @param pop Array mit Individuen
	 * @return sortiertes Array
	 */
	public static Individual_2[] sort_pop(Individual_2[] pop) {
		Arrays.sort(pop, new Comparator<Individual_2>() {
			@Override

			public int compare(Individual_2 iv_1, Individual_2 iv_2) {
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

	/**
	 * Ermittelt und setzt die Fitness eines Individuums mit Hilfe der
	 * Adjazenzmatrix. N muss in Main_2 gesetzt sein.
	 * 
	 * @param iv Individuum
	 */
	public static void calc_fitness(Individual_2 iv) {
		int N_squared = Main_gcp.getN() * Main_gcp.getN();
		int fitness = 0;
		// mit v (für vertice) auf den jeweiligen Knoten zugreifen
		for (int v = 0; v < N_squared; v++) {
			// mit e(für edge) auf den mit v verbundenen Knoten zugreifen
			for (int e = 0; e < N_squared; e++) {
				// Ist eine Kante vorhanden? && Ist die Farbe gleich?
				if (getAdjacency_matrix()[v][e] == 1 && iv.getGenome()[v] == iv.getGenome()[e]) {
					// Strafpunkte hochzählen
					fitness++;
				}
			}
		}
		iv.setFitness(fitness);
	}

	/**
	 * Prüft ob ein Element in einem Array vorhanden ist.
	 * 
	 * @param z   das gesuchte Element
	 * @param arr Array
	 * @return wahr oder falsch
	 */
	public static boolean inArray(int z, int[] arr) {
		for (int i : arr) {
			if (i == z) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gleicht zwei Arrays an. a nimmt die Werte von b an.
	 * 
	 * @param a ein Array
	 * @param b ein anderes Array
	 */
	public static void equal_arrays(int[] a, int[] b) {
		// call by reference
		if (b.length <= a.length) {
			for (int i = 0; i < a.length; i++) {
				a[i] = b[i];
			}
		}
	}

	/**
	 * Mutiert ein Individuum, indem die Knoten mit einer Farbe eingefärbt werden,
	 * die vorher noch nicht ermittelt wurde.
	 * 
	 * @param iv Individuum
	 */
	public static void valid_colors_mutation(Individual_2 iv) {

		int N_squared = Main_gcp.getN() * Main_gcp.getN();
		// mit v auf den jeweiligen Knoten zugreifen
		for (int v = 0; v < N_squared; v++) {
			ArrayList<Integer> adjacency_colors = new ArrayList<Integer>();

			// mit e auf den mit v verbundenen Knoten zugreifen
			for (int e = 0; e < N_squared; e++) {
				// Ist eine Kante vorhanden u?
				if (adjacency_matrix[v][e] == 1) {
					// Hinzufügen der Farbe des verbunden Knotens
					adjacency_colors.add(iv.getGenome()[e]);

					// Sind die Knoten gleich gefärbt?
					if (iv.getGenome()[v] == iv.getGenome()[e]) {
						ArrayList<Integer> valid_colors = new ArrayList<Integer>();
						for (int f = 1; f <= (int) Math.sqrt(N_squared); f++) {
							// Ermittlung der Farben, die kein verbundener Knoten aufweist
							if (!(adjacency_colors.contains(f))) {
								valid_colors.add(f);
							}
						}
						if (valid_colors.size() > 0) {
							// Knoten mit einer gültigen zufälligen Farbe neu färben
							iv.getGenome()[v] = valid_colors.get((int) (Math.random() * valid_colors.size()));

						}
					}
				}
			}

			// }

		}
	}
	/*
	 * /** Mutiert ein Individuum, indem die Knoten mit einer Farbe eingefärbt
	 * werden, die vorher noch nicht ermittelt wurde.
	 * 
	 * @param iv Individuum
	 * 
	 * public static void valid_colors_mutation(Individual_2 iv) {
	 * ArrayList<Integer> adjacency_colors = new ArrayList<Integer>(); int N_squared
	 * = Main_2.getN()*Main_2.getN(); //mit v auf den jeweiligen Knoten zugreifen
	 * for(int v = 0; v < N_squared; v++) {
	 * 
	 * //mit e auf den mit v verbundenen Knoten zugreifen for(int e = 0; e <
	 * N_squared; e++) { //Ist eine Kante vorhanden ? if(adjacency_matrix[v][e] == 1
	 * ) { //Hinzufügen der Farbe des verbunden Knotens
	 * adjacency_colors.add(iv.getGenome()[e]);
	 * 
	 * //Sind die Knoten gleich gefärbt? if ( iv.getGenome()[v] ==
	 * iv.getGenome()[e]){ ArrayList<Integer> valid_colors = new
	 * ArrayList<Integer>(); for(int f = 1; f <= (int)Math.sqrt(N_squared); f++) {
	 * //Ermittlung der Farben, die kein verbundener Knoten aufweist
	 * if(!(adjacency_colors.contains(f))) { valid_colors.add(f); } }
	 * if(valid_colors.size()>0) { //Knoten mit einer gültigen zufälligen Farbe neu
	 * färben iv.getGenome()[v] =
	 * valid_colors.get((int)(Math.random()*valid_colors.size()));
	 * 
	 * } } } }
	 * 
	 * 
	 * //}
	 * 
	 * 
	 * } }
	 */

	/**
	 * Mutiert ein Indiviuum, indem der Knoten mit einer zufälligen Farbe neu
	 * eingefärbt wird.
	 * 
	 * @param iv Individuum
	 */
	public static void all_colors_mutation(Individual_2 iv) {
		int N_squared = Main_gcp.getN() * Main_gcp.getN();
		// mit v auf den jeweiligen Knoten zugreifen
		for (int v = 0; v < N_squared; v++) {

			// mit e auf den mit v verbundenen Knoten zugreifen
			for (int e = 0; e < N_squared; e++) {
				// Ist eine Kante vorhanden?
				if (getAdjacency_matrix()[v][e] == 1 && iv.getGenome()[v] == iv.getGenome()[e]) {
					// neu färben mit einer zufälligen Farbe
					iv.getGenome()[v] = (int) (Math.random() * ((int) Math.sqrt(N_squared))) + 1;
				}
			}
		}
	}

	public static int[][] getAdjacency_matrix() {
		return adjacency_matrix;
	}

	public static void setAdjacency_matrix(int[][] adjacency_matrix) {
		Evolution_2.adjacency_matrix = adjacency_matrix;
	}

}
