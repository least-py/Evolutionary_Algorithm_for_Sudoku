package Evolutionary_Algorithm;


public class EvolutionaryCircle {
	
	private static int id;
	private int N = Sudevo.Main.N;
	private int generation;
	private Individual[] initial_pop;
	
	public EvolutionaryCircle(String database, String tablename, String path, int populationsize, 
							  int max_generations, double selection_amount, double crossover_amount, int q, double mutation_prob) {
		
		this.set_Id(0);
		
		Tracking tracker = new Tracking(database, path);
		tracker.create_table(tablename);
		
		
		
	}
	
	private void circle(Tracking tracker, int populationsize,  int max_generations, double selection_amount,
						double crossover_amount, int q, double mutation_prob) {
		
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
}
