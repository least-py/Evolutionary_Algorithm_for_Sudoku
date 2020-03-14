package sudevo_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sudevo.Main;

/**
 * Hier findet der Evolutionäre Zyklus des Algorithmus_1 statt. Es muss ein
 * gültiger Pfad in der Klasse Main gesetzt sein. Es muss beachtet werden, dass
 * die Methoden nicht erneut mit den selben Tabellennamen oder anderen schon
 * existierenden Tabellennamen ausgeführt werden!
 * 
 * @author Lea
 *
 */
public class Main_1 {

	private static String database = "";
	private static String tablename = "";
	private static int id;
	private static int N = 9;
	private static int popsize;
	private static int generation = 0;
	private static double mut;
	private static double crossover;
	private static Individual_1[] initial_pop;
	private static Connection c;
	private static Statement stmt;
	private static String path = Main.getPath();

	/**
	 * Ermöglicht einen Durchlauf der Evolutionsstrategie. Standardmäßig ist eine
	 * Populationsgröße von 50 Individuen gewählt. Der maximale Ähnlichkeitsanteil
	 * der Ursprungspopulation liegt bei 100% und muss innerhalb des Quellcodes
	 * verändert werden, womit man eine höhere Laufzeit erwarten darf.
	 * 
	 * @param tn               Name der Tabelle
	 * @param db               Name der Datenbank
	 * @param selection_amount Anteil an Kopien des besten Individuums
	 * @param crossover_amount Anteil an durch Rekombination entstandenen Individue
	 * @param total_swaps      Maximal mögliche Mutationen pro Individuum einer
	 *                         Generation
	 * @param max_gen          Maximal mögliche Generationen
	 * @param m                Mutationswahrscheinlichkeit
	 * @param co               Rekombinationswahrscheinlichkeit
	 */

	public static void ev_stragety(String tn, String db, double selection_amount, double crossover_amount,
			int total_swaps, int max_gen, double m, double co) {
		setID(0);
		setDatabase(db);
		setTablename(tn);
		setPopsize(50);
		setMut(m);
		setCrossover(co);
		setGeneration(0);

		// Verbindung zur Datenbank herstellen
		try {
			Class.forName("org.sqlite.JDBC");
			setC(DriverManager.getConnection(path + database + ".db"));
			getC().setAutoCommit(false);
			System.out.println("Datenbank geöffnet");
			setStmt(getC().createStatement());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Tabelle erstellen
		create_table(getDatabase(), tablename);

		// Initialisierung der Ausgangspopulation, die zur Bestimmung der Ähnlichkeit
		// herangezogen wird
		// Maximale Übereinstimmung: 15% (aus Zeitgründen)
		init_initial_pop(1.0);

		for (int i = 0; i < getPopsize(); i++) {

			// bewerten
			Evolution_1.calc_fitness(getInitial_pop()[i]);

			// Ähnlichkeitszuordnung
			Diversity_1.associate_similarity(getInitial_pop()[i]);

			// Ausgangspopulation der Datenbank hinzufügen
			database_entry(getDatabase(), tablename, getInitial_pop()[i]);
		}

		// Population, mit der aktiv gearbeitet wird, initialisieren
		Individual_1[] pop = new Individual_1[getPopsize()];
		for (int i = 0; i < getPopsize(); i++) {
			pop[i] = new Individual_1(getN());
			Evolution_1.calc_fitness(pop[i]);
		}

		Individual_1[] fittest = Evolution_1.getFittest(pop, (int) (getPopsize() * selection_amount));

		// T E R M I N I E R U N G S B E D I N G U N G

		// Generationenlimit und Auffinden einer Lösung als Terminierungsbedingung
		while (generation < max_gen && fittest[0].getFitness() > 0) {

			generation++;
			System.out.println(generation);

			fittest = Evolution_1.getFittest(pop, (int) (getPopsize() * selection_amount));

			// U M W E L T S E L E K T I O N

			Individual_1[] selected_individuals = new Individual_1[getPopsize()];

			// Kopieren des besten Individuums
			// Genom muss kopiert werden, da ansonsten nur die Referenz kopiert wird.
			// Wird nur die Referenz des Individuums kopiert, wird bei der Mutation eines
			// dieser Individuum
			// nicht nur ein Individuum verändert!!
			for (int i = 0; i < getPopsize() * selection_amount; i++) {
				Individual_1 champ = Evolution_1.getFittest(pop, 1)[0];
				int[][] genome_copy = new int[N][N];
				for (int r = 0; r < N; r++) {
					for (int c = 0; c < N; c++) {
						genome_copy[r][c] = champ.getGenome()[r][c];
					}
				}
				selected_individuals[i] = new Individual_1(genome_copy);

			}

			/*
			 * //selection_amount des Genpools werden zufällig ausgewählt for(int i = 0; i <
			 * getPopsize()* selection_amount; i++) { Individual_1 sel =
			 * Evolution_1.rndm_selection(pop); selected_individuals[i] = sel; }
			 */

			// Selektion der restlichen (1 - selection_amount) durch Turnierselektion
			for (int i = (int) (getPopsize() * selection_amount); i < getPopsize(); i++) {
				Individual_1 sel = Evolution_1.tournament_selection(pop, 25, 1)[0];
				selected_individuals[i] = sel;
			}

			// E L T E R N S E L E K T I O N & R E K O M B I N A T I O N

			for (int i = 0; i < (int) crossover_amount * getPopsize(); i++) {
				// Die neue Population aus den selektierten Individuen erstellen

				pop[i] = Evolution_1.row_crossover(Evolution_1.rndm_selection(selected_individuals),
						Evolution_1.rndm_selection(selected_individuals), crossover);

			}
			// Der "hintere" Teil der Population, der im Folgenden kopiert wird, enthält
			// größtenteils
			// die durch Turnierselektion selektierten Individuen
			for (int i = (int) crossover_amount * getPopsize(); i < getPopsize(); i++) {
				// Die neue Population aus den selektierten Individuen erstellen
				pop[i] = selected_individuals[i];
			}

			for (int i = 0; i < getPopsize(); i++) {

				// M U T A T I O N

				// Eine mögliche Mutation
				Evolution_1.swap_mutation(pop[i], mut, total_swaps);

				// B E W E R T U N G

				Evolution_1.calc_fitness(pop[i]);

				// Diversität ermitteln und Ähnlichkeit zuordnen
				Diversity_1.associate_similarity(pop[i]);

				// Individuum der Datenbank hinzufügen
				database_entry(getDatabase(), tablename, pop[i]);

			}

			System.out.println("fittest:  " + fittest[0].getFitness());

		}
		try {
			getStmt().close();
			getC().commit();
			getC().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setGeneration(0);
		fittest[0].phenotype();
	}

	/**
	 * Experiment zum Testen einer der 4 Thesen. Populationsgröße beträgt 25.
	 * 
	 * @param exp              These
	 * @param tn               Tabellenname
	 * @param db               Datenbank
	 * @param selection_amount Anteil der uniform selektierten Individuen
	 * @param crossover_amount Anteil der durch Rekombination entstanden Individuen
	 * @param total_swaps      Maximal mögliche Mutationen pro Individuum einer
	 *                         Generation
	 * @param max_gen          Generationen eines Durchlaufs
	 * @param m                Mutationswahrscheinlichkeit
	 * @param co               Rekombinationswahrscheinlichkeit
	 */
	public static void exp(int exp, String tn, String db, double selection_amount, double crossover_amount,
			int total_swaps, int max_gen, double m, double co) {
		setID(0);
		setDatabase(db);
		setTablename(tn);
		setPopsize(25);
		setMut(m);
		setCrossover(co);
		setGeneration(0);

		// Verbindung zur Datenbank herstellen
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(path + database + ".db");
			c.setAutoCommit(false);
			System.out.println("Datenbank geöffnet");
			stmt = c.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Tabelle erstellen
		create_table(getDatabase(), tablename);

		// Initialisierung der Ausgangspopulation, die zur Bestimmung der Ähnlichkeit
		// herangezogen wird
		// Maximale Übereinstimmung: 15% (aus Zeitgründen)
		init_initial_pop(1.0);

		for (int i = 0; i < getPopsize(); i++) {

			// bewerten
			Evolution_1.calc_fitness(getInitial_pop()[i]);

			// Ähnlichkeitszuordnung
			Diversity_1.associate_similarity(getInitial_pop()[i]);

			// Ausgangspopulation der Datenbank hinzufügen
			database_entry(getDatabase(), tablename, getInitial_pop()[i]);
		}

		// Population, mit der aktiv gearbeitet wird, initialisieren
		Individual_1[] pop = new Individual_1[getPopsize()];
		for (int i = 0; i < getPopsize(); i++) {
			pop[i] = new Individual_1(getN());
			Evolution_1.calc_fitness(pop[i]);
		}

		// popsz 50 konstant
		Individual_1[] fittest = null;
		fittest = Evolution_1.getFittest(pop, (int) (getPopsize() * selection_amount));

		// T E R M I N I E R U N G S B E D I N G U N G

		// Generationenlimit als Terminierungsbedingung, wenn Aussicht auf eine Lösung
		// gering ist
		while (generation < max_gen && fittest[0].getFitness() > 0) {

			generation++;
			System.out.println(generation);

			if (exp == 1) {
				if (generation % 500 == 0) {
					total_swaps = total_swaps + 10;
				}
			} else if (exp == 2) {
				if (generation % 500 == 0 && total_swaps - 1 >= 0) {
					total_swaps = total_swaps - 1;
				}
			} else if (exp == 4) {
				if (generation % 500 == 0 && (co + 0.1 <= 1.0)) {
					co = co + 0.1;
				}
			}

			fittest = Evolution_1.getFittest(pop, (int) (getPopsize() * selection_amount));

			// U M W E L T S E L E K T I O N

			Individual_1[] selected_individuals = new Individual_1[getPopsize()];

			/*
			 * for(int i = 0; i < getPopsize()* selection_amount; i++) { Individual_1 champ
			 * = Evolution_1.getFittest(pop, 1)[0]; int[][] genome_copy = new int[N][N];
			 * for(int r = 0; r < N; r++){ for(int c = 0; c < N; c++){ genome_copy[r][c] =
			 * champ.getGenome()[r][c]; } } selected_individuals[i] = new
			 * Individual_1(genome_copy); }
			 */

			// selection_amount des Genpools werden zufällig ausgewählt
			for (int i = 0; i < getPopsize() * selection_amount; i++) {
				Individual_1 sel = Evolution_1.rndm_selection(pop);
				selected_individuals[i] = sel;
			}

			// Selektion der restlichen (1 - selection_amount) durch Turnierselektion
			for (int i = (int) (getPopsize() * selection_amount); i < getPopsize(); i++) {
				Individual_1 sel = Evolution_1.tournament_selection(pop, 5, 1)[0];
				selected_individuals[i] = sel;
			}

			// E L T E R N S E L E K T I O N & R E K O M B I N A T I O N

			for (int i = 0; i < (int) crossover_amount * getPopsize(); i++) {
				// Die neue Population aus den selektierten Individuen erstellen

				pop[i] = Evolution_1.row_crossover(Evolution_1.rndm_selection(selected_individuals),
						Evolution_1.rndm_selection(selected_individuals), crossover);

			}
			for (int i = (int) crossover_amount * getPopsize(); i < getPopsize(); i++) {
				// Die neue Population aus den selektierten Individuen erstellen
				pop[i] = selected_individuals[i];
			}

			for (int i = 0; i < getPopsize(); i++) {

				// M U T A T I O N

				// Eine mögliche Mutation
				Evolution_1.swap_mutation(pop[i], mut, total_swaps);

				// B E W E R T U N G

				Evolution_1.calc_fitness(pop[i]);

				// Diversität ermitteln und Ähnlichkeit zuordnen
				Diversity_1.associate_similarity(pop[i]);

				// Individuum der Datenbank hinzufügen
				database_entry(getDatabase(), tablename, pop[i]);

			}

		}

		try {
			stmt.close();
			c.commit();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setGeneration(0);
	}

	/**
	 * Initialisiert die Ausgangspopulation
	 * 
	 * @param max_simil Der maximale Anteil, mit dem ein Individuum mit den übrigen
	 *                  Individuen der Population übereinstimmen darf
	 */
	public static void init_initial_pop(double max_simil) {
		setInitial_pop(new Individual_1[getPopsize()]);
		getInitial_pop()[0] = new Individual_1(getN());

		boolean divers = false;
		for (int i = 1; i < getPopsize(); i++) {
			while (!divers) {
				divers = true;
				getInitial_pop()[i] = new Individual_1(getN());
				for (int j = i - 1; j >= 0; j--) {

					if (Diversity_1.calc_similarity(getInitial_pop()[i].getGenome(),
							getInitial_pop()[j].getGenome()) > max_simil) {
						divers = false;
						// Durch vielfache Versuche ein Individuum zu finden, kann sich die ID enorm
						// erhöhen, deshalb wird sie auf nachträglich i gesetzt
						id = i;
						getInitial_pop()[i].setIdentification(i);
						// for-Schleife wird vorzeitig abgebrochen, damit ein neues Individual_1
						// generiert werden kann
						break;
					}
				}
			}
			divers = false;
		}
	}

	/**
	 * Erstellt eine Tabelle in einer Datenbank. Sie enthält die Spalten:
	 * <ul>
	 * <li>fitness: Enthält die Fitnesswerte. (Datentyp: REAL)</li>
	 * <li>generation: Die Generation, in der das jeweilige Individuum existierte.
	 * (Datentyp: INT)</li>
	 * <li>simil_association: Der Index des Urspungsindividuums im
	 * initial_pop-Array, das dem jewiligen Individuum am ähnlichsten ist.
	 * (Datentyp: INT)</li>
	 * <li>similarity: Der Quotient aus übereinstimmenden Sudoku-Feldern und
	 * Gesamtanzahl an Feldern des jeweiligen Individuums und seinem zugeordneten
	 * Ursprungsindividuum. (Datentyp: REAL)</li>
	 * <li>version: Die Nummer des jeweiligen Experiments. (Datentyp: INT)</li>
	 * <li>ID: Die Identifikationsnummer des jeweiligen Individuums. Nur innerhalb
	 * der selben Version ist die ID einzigartig. (Datentyp: INT)</li>
	 * </ul>
	 * 
	 * @param database   Name der Datenbank
	 * @param table_name Name der Tabelle
	 */
	public static void create_table(String database, String table_name) {
		try {

			String sqltab = "CREATE TABLE " + table_name + " (fitness            REAL    NOT NULL, "
					+ " generation          INT    NOT NULL, " + " simil_association   INT , "
					+ " similarity          REAL, " + " ID            	  INT)";
			getStmt().executeUpdate(sqltab);

			System.out.println("Tabelle " + table_name + " erstellt");

			/*
			 * stmt.close(); c.commit(); c.close();
			 */
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	/**
	 * Gibt die Tabelle in der Konsole aus
	 * 
	 * @param database  Datenbank
	 * @param tablename Tabelle
	 */
	public static void print_table(String database, String tablename) {
		try {
			/*
			 * Class.forName("org.sqlite.JDBC"); Connection c =
			 * DriverManager.getConnection(path + database +".db"); c.setAutoCommit(false);
			 */
			System.out.println("Einträge ausgeben");

			Statement stmt = getC().createStatement();

			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + ";");

			while (rs.next()) {
				System.out.println("Fitness = " + rs.getInt("fitness"));
				System.out.println("Generation = " + rs.getInt("generation"));
				System.out.println("Similarity association  = " + rs.getInt("simil_association"));
				System.out.println("Similarity = " + rs.getFloat("similarity"));
				System.out.println("ID = " + rs.getInt("id"));

			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Mit der Methode wird ein Eintrag eines bestimmten Individuums in die
	 * Datenbank ermöglicht. Es wird der Fitnesswert, die Generation, die
	 * Ähnlichkeitszuordnung, der Ähnlichkeitsanteil und die ID-Nummer hinzugefügt.
	 * 
	 * @param database   Name der Datenbank
	 * @param table_name Name der Tabelle
	 * @param iv         das jeweilige Individuum
	 */
	public static void database_entry(String database, String table_name, Individual_1 iv) {
		try {
			/*
			 * Class.forName("org.sqlite.JDBC"); Connection c =
			 * DriverManager.getConnection(path+ database + ".db"); c.setAutoCommit(false);
			 * System.out.println("Opened database successfully");
			 */
			Statement stmt = getC().createStatement();
			String sql = "INSERT INTO " + table_name + "(FITNESS, GENERATION, SIMIL_ASSOCIATION, SIMILARITY, ID)"
					+ "VALUES (" + (double) iv.getFitness() + ", " + getGeneration() + ", " + iv.getAssociation() + ", "
					+ iv.getSimilarity() + ", " + iv.getIdentification() + ");";

			stmt.executeUpdate(sql);

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public static Individual_1[] getInitial_pop() {
		return initial_pop;
	}

	public static void setInitial_pop(Individual_1[] initial_pop) {
		Main_1.initial_pop = initial_pop;
	}

	public static String getTablename() {
		return tablename;
	}

	public static void setTablename(String tablename) {
		Main_1.tablename = tablename;
	}

	public static int getGeneration() {
		return generation;
	}

	public static double getMut() {
		return mut;
	}

	public static void setMut(double mut) {
		Main_1.mut = mut;
	}

	public static double getCrossover() {
		return crossover;
	}

	public static void setCrossover(double crossover) {
		Main_1.crossover = crossover;
	}

	public static int getN() {
		return N;
	}

	public static void setN(int n) {
		N = n;
	}

	public static int getID() {
		return id;
	}

	public static void setID(int id) {
		Main_1.id = id;
	}

	public static int getPopsize() {
		return popsize;
	}

	public static void setPopsize(int popsize) {
		Main_1.popsize = popsize;
	}

	public static void setGeneration(int generation) {
		Main_1.generation = generation;
	}

	public static void setDatabase(String string) {
		Main_1.database = string;
	}

	public static String getDatabase() {
		return database;
	}

	public static Connection getC() {
		return c;
	}

	public static void setC(Connection c) {
		Main_1.c = c;
	}

	public static Statement getStmt() {
		return stmt;
	}

	public static void setStmt(Statement stmt) {
		Main_1.stmt = stmt;
	}
}
