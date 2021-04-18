package sudevo_gcp_strategy;

/**
 * Ein Individuum stellt eine mögliche Lösung für das Sudoku-Rätsel dar.
 * 
 * @author Lea
 */
public class Individual_2 {

	private int ID;
	private static int N;
	private double similarity;
	private int association;
	private int[] genome;
	private int fitness;

	/**
	 * Konstruktor zum Erzeugen eines Individuums. ! Das Individuum wird ohne
	 * Gültigkeit eines Teilbereichs wie bsw. den Zeilen erzeugt !
	 * 
	 * @param N
	 */
	public Individual_2(int N) {
		this.N = N;
		this.setIdentification(Main_gcp.getID());
		Main_gcp.setID(Main_gcp.getID() + 1);
		int N_squared = N * N;
		this.genome = new int[N_squared];
		for (int i = 0; i < N_squared; i++) {
			getGenome()[i] = (int) (Math.random() * N) + 1;

		}
	}

	/**
	 * Konstruktor zum Erzeugen eines Individuum mit spezifischer Initialisierung
	 * durch ein gegebenes Genom.
	 * 
	 * @param genom ein eindimensionales Array
	 */
	public Individual_2(int[] genome) {
		this.setIdentification(Main_gcp.getID());
		Main_gcp.setID(Main_gcp.getID() + 1);
		this.genome = genome;
		this.N = (int) Math.sqrt(genome.length);
	}

	public int getGene(int index) {
		return genome[index];
	}

	/**
	 * @param f die Fitness eines Individuums, die sich aus den "Strafpunkten", die
	 *          hochgezählt werden, wenn eine der Sudokuregeln durch eine Zahl
	 *          verletzt wird, ergibt.
	 */
	public void setFitness(int f) {
		fitness = f;
	}

	public int getFitness() {
		return fitness;
	}

	public int[] getGenome() {
		return genome;
	}

	public static int getN() {
		return Main_gcp.getN();
	}

	public static void setN(int n) {
		N = n;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public int getIdentification() {
		return ID;
	}

	public void setIdentification(int iD) {
		ID = iD;
	}

	public int getAssociation() {
		return association;
	}

	public void setAssociation(int association) {
		this.association = association;
	}

}
