package sudevo_1;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 
 * In dieser Klasse befindet sich das "Handwerkzeug der Evolution".
 * 
 * @author Lea
 *
 */

public class Evolution_1 {

	/**
	 * Berechnet die Strafpunkte eines Individuums, die sich aus der Anzahl der
	 * Zahlen, die die Regeln verletzten ergibt. Betrachtet werden Spalten und
	 * Boxen, da die Zeilen korrekt initialisiert sind.
	 * 
	 * @param iv das Individuum, dessen Fitness berechnet werden soll
	 * @return Fitness des Individuums
	 */
	public static int calc_fitness(Individual_1 iv) {
		int column_fitness = 0;
		int box_fitness = 0;

		int[] column = new int[Main_1.getN()];
		// Spaltenbewertung
		for (int c = 0; c < Main_1.getN(); c++) {
			// das c-te Elemente jeder Zeile ergibt die c-te Spalte
			for (int r = 0; r < Main_1.getN(); r++) {
				column[r] = iv.getGenome()[r][c];
			}
			Arrays.sort(column);
			for (int r = 0; r < Main_1.getN() - 1; r++) {
				// Sind die Elemente gleich, wird ein Strafpunkt vergeben
				if (column[r] == column[r + 1]) {
					column_fitness++;
				}
			}
		}

		// Boxenbewertung
		// Box beginnt links oben, nächste Box ist die Box darunter
		int squrt = (int) Math.sqrt(Main_1.getN());
		int[] box = new int[Main_1.getN()];
		// horizontale box, damit die vertikale Erstellabfolge horizontal verschoben
		// werden kann
		for (int horizontal_box = 0; horizontal_box < squrt; horizontal_box++) {
			// vertikale box, damit die vertikale Erstellabfolge gewährleistet wird
			for (int vertical_box = 0; vertical_box < squrt; vertical_box++) {

				// Box erstellen: Zeilen werden bis zum "Boxrand" dem boxarray nacheinander
				// hinzugefügt
				for (int z = 0; z < squrt; z++) {
					for (int s = 0; s < squrt; s++) {
						// (z*squrt)+s sorgt dafür, dass die Elemente den richtigen Indices von 0 bis
						// n-1 zugeordnet werden
						box[(z * squrt) + s] = iv.getGenome()[z + (vertical_box * squrt)][s + (horizontal_box * squrt)];
					}
				}
				// sortieren und bewerten
				Arrays.sort(box);
				for (int z = 0; z < Main_1.getN() - 1; z++) {
					// Sind die Elemente gleich, wird ein Strafpunkt vergeben
					if (box[z] == box[z + 1]) {
						box_fitness++;
					}
				}
			}
		}

		// Fitness aktualisieren
		iv.setFitness(column_fitness + box_fitness);
		return (column_fitness + box_fitness);
	}

	/**
	 * Sortiert eine gegebene Population aufsteigend nach dem Fitnesswert.
	 * 
	 * @param pop eine Population
	 * @return die sortierte Population
	 */
	public static Individual_1[] sort_pop(Individual_1[] pop) {
		Arrays.sort(pop, new Comparator<Individual_1>() {
			@Override

			public int compare(Individual_1 iv_1, Individual_1 iv_2) {
				if (calc_fitness(iv_1) < calc_fitness(iv_2)) {
					return -1;
				} else if (calc_fitness(iv_1) > calc_fitness(iv_2)) {
					return 1;
				}
				return 0;
			}
		});
		return pop;
	}

	/**
	 * Liefert eine bestimmte Anzahl an Individuen mit den besten Fitnesswerten aus
	 * einer Sammlung an Individuen. Die übergebenen Individuen bleiben unsortiert.
	 * 
	 * @param ivs    Individuen
	 * @param amount Anzahl an Individuen, die bestimmt werden soll
	 * @return ein Array, das die besten Individuen enthält
	 */
	public static Individual_1[] getFittest(Individual_1[] ivs, int amount) {
		// aufsteigendes Sortieren der Individuen nach dem Fitnesswert
		// Klonen, da die übergebene Population sonst sortiert bleibt
		// -> call by reference
		Individual_1[] popcopy = ivs.clone();
		sort_pop(popcopy);
		Individual_1[] selection = new Individual_1[amount];
		for (int i = 0; i < amount; i++) {
			selection[i] = popcopy[i];
		}
		return selection;
	}

	/**
	 * Selektiert Individuen durch zufällig gleichverteilte Auswahl an Individuen,
	 * von denen eine bestimmte Anzahl an Individuen mit den besten Fitnesswerten
	 * ermittelt wird, sodass prinzipiell auch schlechtere Individuen selektiert
	 * werden können. Die Methode simuliert <b>ein</b> Turnier!
	 * 
	 * @param pop              die Population
	 * @param enemies          Anzahl an Gegnern in der engeren Auswahl
	 * @param amount_of_champs Anzahl an Individuen, die in der engeren Auswahl
	 *                         gewinnen
	 * @return ein Array, das die besten Individuen der engeren Auswahl darstellen
	 */
	public static Individual_1[] tournament_selection(Individual_1[] pop, int enemies, int amount_of_champs) {
		Individual_1[] selection = new Individual_1[enemies];
		for (int i = 0; i < enemies; i++) {
			selection[i] = pop[(int) (Math.random() * pop.length)];
		}
		return getFittest(selection, amount_of_champs);
	}

	/**
	 * Bestimmt zufällig ein Individuum der Population. Alle Individuen haben die
	 * selbe Chance. Es handelt sich um eine uniforme Selektion.
	 * 
	 * @param pop die Population
	 * @return ein zufällig gewähltes Individuum.
	 */
	public static Individual_1 rndm_selection(Individual_1[] pop) {
		return pop[(int) (Math.random() * pop.length)];
	}

	/**
	 * Rekombiniert die jeweiligen Zeilen zweier Individuen, um ein neues Individuum
	 * zu schaffen. Durch cprob kann man die Wahrscheinlichkeit der Übernahme einer
	 * Zeile des besseren Individuums steuern.
	 * 
	 * @param iv_1  ein Individuum
	 * @param iv_2  ein anderes Individuum
	 * @param cprob die Wahrscheinlichkeit, mit der eine Zeile des besseren
	 *              Individuums übernommen wird
	 * @return ein neu zusammengesetztes Individuum
	 */
	public static Individual_1 row_crossover(Individual_1 iv_1, Individual_1 iv_2, double cprob) {
		int[][] child_genome = new int[Main_1.getN()][Main_1.getN()];
		// Zuordnung des angepasstesten Induviduums
		Individual_1 parent_1 = null;
		Individual_1 parent_2 = null;
		if (iv_1.getFitness() < iv_2.getFitness()) {
			parent_1 = iv_2;
			parent_2 = iv_1;
		} else {
			parent_1 = iv_1;
			parent_2 = iv_2;
		}
		for (int i = 0; i < Main_1.getN(); i++) {
			if (Math.random() < cprob) {
				child_genome[i] = parent_1.getGenome()[i];
			} else {
				child_genome[i] = parent_2.getGenome()[i];
			}
		}
		return new Individual_1(child_genome);
	}

	/**
	 * Die Methode tauscht zwei Zahlen einer Zeile miteinander und erhält somit die
	 * Gültigkeit.
	 * 
	 * @param iv          das Individuum
	 * @param mprob       die Wahrscheinlichkeit, dass eine Mutation stattfindet
	 * @param total_swaps Anzahl an höchst möglichen Mutationen pro Individuum
	 * 
	 */
	public static void swap_mutation(Individual_1 iv, double mprob, int total_swaps) {

		// Die Methode muss das Individuum nicht zurückgeben, da dieses auch noch
		// nach dem Aufruf mutiert bleibt -> call by reference
		for (int trial = 0; trial < total_swaps; trial++) {
			if (Math.random() < mprob) {
				int rndm_index_1 = (int) (Math.random() * (Main_1.getN()));
				int rndm_index_2 = (int) (Math.random() * (Main_1.getN()));
				int row = (int) (Math.random() * (Main_1.getN()));
				int gene_1 = iv.getGene(row, rndm_index_1);
				int gene_2 = iv.getGene(row, rndm_index_2);
				iv.setGene(row, rndm_index_1, gene_2);
				iv.setGene(row, rndm_index_2, gene_1);
			}
		}

	}

}
