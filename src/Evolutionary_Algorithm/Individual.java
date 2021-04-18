package Evolutionary_Algorithm;


public class Individual {

	private static int N = Sudevo.Main.N;
	private int id;
	private double similarity;
	private int association;
	private int[] genome;
	private int fitness;
	
	
	public Individual() {
		this.setIdentification(EvolutionaryCircle.get_Id());
		EvolutionaryCircle.incr_Id();
		int N_squared = N * N;
		this.genome = new int[N_squared];
		for (int i = 0; i < N_squared; i++) {
			getGenome()[i] = (int) (Math.random() * N) + 1;

		}
	}
	
	public Individual(int[] genome) {
		this.setIdentification(EvolutionaryCircle.get_Id());
		EvolutionaryCircle.incr_Id();
		this.genome = genome;
	}
	
	public int getGene(int index) {
		return genome[index];
	}
	
	public void setFitness(int f) {
		fitness = f;
	}

	public int getFitness() {
		return fitness;
	}

	public int[] getGenome() {
		return genome;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public int getIdentification() {
		return id;
	}

	public void setIdentification(int id) {
		this.id = id;
	}

	public int getAssociation() {
		return association;
	}

	public void setAssociation(int association) {
		this.association = association;
	}
}
