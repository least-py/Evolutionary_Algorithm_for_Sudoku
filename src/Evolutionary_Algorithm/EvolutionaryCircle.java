package Evolutionary_Algorithm;


public class EvolutionaryCircle {
	
	private static int id;
	private int N = Sudevo.Main.N;
	private int generation;
	private static Individual[] initial_pop;
	private String tablename;
	
	public EvolutionaryCircle(String database, String tablename, String path, int populationsize, 
							  int max_generations, double selection_amount, double crossover_amount, int q, double mutation_prob, double max_simil) {
		
		id = 0;
		this.generation = 0;
		this.tablename = tablename;
		
		Tracking tracker = new Tracking(database, path);
		tracker.create_table(tablename);
		
		init_initial_pop(max_simil, populationsize);
		
		for (Individual iv : getInitial_pop()) {

			GeneticVariation.calc_fitness(iv);

			Diversity.associate_similarity(iv);

			tracker.database_entry(tablename, iv, generation);
	
		}
	}
	
	private void circle(Tracking tracker, int popsize,  int max_generations, double selection_amount,
						double crossover_amount, int q, double mutation_prob) {
		
		Individual[] pop = new Individual[popsize];
		for (int i = 0; i < popsize; i++) {
			pop[i] = new Individual();
			GeneticVariation.calc_fitness(pop[i]);
		}

		Individual[] fittest = GeneticVariation.getFittest(pop, (int) (popsize * selection_amount));

		while (generation < max_generations && fittest[0].getFitness() > 0) {

			fittest = GeneticVariation.getFittest(pop, (int) (popsize * selection_amount));

			generation++;
			System.out.println(generation);

			Individual[] selected_individuals = new Individual[popsize];

			for (int i = 0; i < popsize * selection_amount; i++) {
				Individual sel = GeneticVariation.rndm_selection(pop);
				selected_individuals[i] = sel;

			}

			for (int i = (int) (popsize * selection_amount); i < popsize; i++) {
				Individual sel = GeneticVariation.tournament_selection(pop, q);
				selected_individuals[i] = sel;
			}

			for (int i = 0; i < (int) (crossover_amount * popsize); i++) {
				pop[i] = GeneticVariation.one_point_crossover(GeneticVariation.rndm_selection(selected_individuals),
						GeneticVariation.rndm_selection(selected_individuals));
			}
			for (int i = (int) crossover_amount * popsize; i < popsize; i++) {
				pop[i] = selected_individuals[i];
			}

			for (int i = 0; i < popsize; i++) {

				if (Math.random() < mutation_prob) {
					GeneticVariation.valid_colors_mutation(pop[i]);
					
				}

				GeneticVariation.calc_fitness(pop[i]);

				Diversity.associate_similarity(pop[i]);

				tracker.database_entry(tablename, pop[i], generation);
			}
		}
		String gene = "";

		for (int s = 0; s < N; s++) {
			for (int z = 0; z < N; z++) {
				gene = gene + " " + fittest[0].getGene(s * N + z);
			}
			System.out.println(gene);
			gene = "";
		}
		
		tracker.close_connection();
		generation = 0;
	}

	
	private void init_initial_pop(double max_simil, int popsize) {
		initial_pop = new Individual[popsize];
		getInitial_pop()[0] = new Individual();

		boolean diverse = false;
		for (int i = 1; i < popsize; i++) {
			while (!diverse) {
				diverse = true;
				getInitial_pop()[i] = new Individual();
				
				for (int j = i - 1; j >= 0; j--) {

					if (Diversity.calc_similarity(getInitial_pop()[i].getGenome(),
							getInitial_pop()[j].getGenome()) > max_simil) {
						diverse = false;
						id = i;
						getInitial_pop()[i].setIdentification(i);
						break;
					}
				}
			}
			diverse = false;
		}
	}
	
	public static int get_Id() {
		return id;
	}
	
	public static void incr_Id() {
		id = id + 1;
	}

	public static void set_Id(int new_id) {
		id = new_id;
	}

	public static Individual[] getInitial_pop() {
		return initial_pop;
	}

}
