package sudevo_1;

import java.util.ArrayList;

/**
 * Ein Individuum stellt eine mögliche Lösung für das Sudoku-Rätsel dar.
 * @author Lea
 */
public class Individual_1 {
	

	private int ID;
	private static int N;
	private double similarity;
	private int association;
	private int[][] genome;
	private int fitness;
	
	
	
	/**
	 * Konstruktor zum Erzeugen eines Individuum mit spezifischer Initialisierung durch ein gegebenes Genom.
	 * @param genom ein zweidimensionales Array gemäß dem Sudoku-Gitter. Der erste Index entspricht der row.
	 * Der zweite Index entspricht der column bzw. dem Element in der row. 
	 */
	public Individual_1(int[][] genome) {
		this.setIdentification(Main_1.getID());
		Main_1.setID(Main_1.getID() + 1);
		this.genome = genome;
		this.N = genome.length;
	}
	
	/**
	 * Konstruktor zum Erzeugen eines Individuums mit zufälliger Initialisierung des Genoms.
	 * Es werden ausschließlich gültige Zeilen generiert.
	 * @param N ist die höchste Zahl, die in das NxN Sudoku eingesetzt werden darf.
	 */
	public Individual_1(int N) {
		this.setIdentification(Main_1.getID());
		Main_1.setID(Main_1.getID() + 1);
		this.N = N;
		this.genome = new int[getN()][getN()];
		
		for(int row = 0; row < getN(); row++) {
			//Erstellung ausschließlich gültiger Zeilen
			//ArrayList, da Elemente gelöscht werden, um Dopplungen im Genom zu vermeiden
			ArrayList<Integer> list= new ArrayList<Integer>();
			for(int l = 1; l <= N; l++) {
				list.add(l);
			}
			//aus der Liste werden zufällig Zahlen ermittelt, die anschließend in das Genomarray eingebaut werden
			for(int column = 0; column < N; column++) {
				//Erzeugt eine Zufallszahl zwischen 0 und liste.size() (liste.size()-1 größter annehmbarer Wert, da immer abgerundet wird)
				int rnd_index = (int)(Math.random()*list.size());
				this.genome[row][column] = list.get(rnd_index);
				list.remove(rnd_index);
				
			}
		}
	}
	
	/**
	 * Die Methode gibt das Sudoku-Gitter in der Konsole aus.
	 */
	public void phenotype() {
		String s = "";
		for(int in = 0; in < getGenome()[0].length; in++) {
			for(int n = 0; n < getGenome()[0].length; n++) {
				s = s+getGenome()[in][n]+" ";
			}
			System.out.println(s);
			s = "";
		}
		System.out.println("");
	}
	
	
	/**
	 * @param row entspricht der row
	 * @param column entspricht der column bzw. der Stelle des Elements in der gegebenen row
	 * @param gen entspricht der Zahl, die in das Gitter eingefügt wird. Sie muss größer als 0 und kleiner gleich N sein!
	 */
	public void setGene(int row, int column, int gene) {
		genome[row][column] = gene;
	}
	
	/**
	 * @param row entspricht der row
	 * @param column entspricht der column bzw. der Stelle des Elements in der gegebenen row
	 * @return ein Element des Genoms
	 */
	public int getGene(int row, int column) {
		return genome[row][column];
	}
	
	/**
	 * @param f die Fitness eines Individuums, die sich aus den "Strafpunkten", die hochgezählt werden,
	 *  wenn eine der Sudokuregeln durch eine Zahl verletzt wird, ergibt.
	 */
	public void setFitness(int f) {
		fitness = f;
	}
	
	/**
	 * @return die Fitness eines Individuums, die sich aus den "Strafpunkten", die hochgezählt werden,
	 *  wenn eine der Sudokuregeln durch eine Zahl verletzt wird, ergibt.
	 */
	public int getFitness() {
		return fitness;
	}
	
	/**
	 * @return ein zweidimensionales Array, das die Elemente des Sudoku-Gitters enthält.
	 * Der erste Index weist die Spalte zu.
	 * Der zweite Index weist eine Spaltennumer zu, was wiederum einem Element der zuvor zugewiesenen Zeile entspricht.
	 */
	public int[][] getGenome(){
		return genome;
	}

	
	/**
	 * @return die höchste Zahl, die in das NxN Sudoku eingesetzt werden darf.
	 */
	public static int getN() {
		return N;
	}

	/**
	 * 
	 * @param n die höchste Zahl, die in das NxN Sudoku eingesetzt werden darf.
	 */
	public static void setN(int n) {
		N = n;
	}
	
	/**
	 * @return die Ähnlichkeit des Individuums zu einem Individuum der Ausgangspopulation
	 */
	public double getSimilarity() {
		return similarity;
	}

	/**
	 * @param similarity die Ähnlichkeit des Individuums zu einem Individuum der Ausgangspopulation
	 */
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
