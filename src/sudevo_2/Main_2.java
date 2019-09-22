package sudevo_2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sudevo.Main;

/**
 * Hier findet der Evolutionäre Zyklus des Algorithmus_1 statt. Es muss ein gültiger Pfad in der Klasse Main gesetzt sein.
 * Es muss beachtet werden, dass die Methoden nicht erneut mit den selben Tabellennamen oder anderen schon existierenden
 * Tabellennamen ausgeführt werden! 
 * @author Lea
 *
 */

public class Main_2 {
	//BEACHTE!! DER PFAD FÜR DIE DATENBANK MUSS INDIVIDUELL ANGEGEBEN WERDEN
	private static String database = "";
	private static String tablename = "";
	private static int id;
	private static int N = 9;
	private static int popsize;
	private static int generation = 0;
	private static double mut;
	private static double crossover;
	private static int version;
	private static Individual_2[] initial_pop;
	private static Connection c;
	private static Statement stmt;
	private static String path = Main.getPath();
	
	
	/**
	 * Ein Durchlauf des Algorithmus_2. Die maximale Ähnlichkeit der Ausgangspopulation liegt bei 100%
	 * und muss bei Bedarf im Quelltext geändert werden.
	 * @param tn Name der Tabelle
	 * @param db Name der Datenbank
	 * @param popsz Größe der Population
	 * @param max_gen Generationenlimit
	 * @param selection_amount Anteil uniform selektierter Individuen
	 * @param crossover_amount Anteil der aus Rekombination hervorgegangen Individuen
	 * @param q Anzahl der Gegner für die Turnierselektion
	 * @param mutpr Mutationswahrscheinlichkeit
	 */
	public static void gcp_test(String tn, String db, int popsz, int max_gen, double selection_amount, double crossover_amount, int q, double mutpr) {
		setID(0);
		setDatabase(db);
		setTablename(tn);
		setPopsize(popsz);
		setGeneration(0);
		
		
		//Verbindung zur Datenbank herstellen
        try {
        	Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(path+ database +".db");
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
		
		//Tabelle erstellen
		create_table(getDatabase(), tablename);	
		
		//Initialisierung der Ausgangspopulation, die zur Bestimmung der Ähnlichkeit herangezogen wird
		//Maximale Übereinstimmung: 15% (aus Zeitgründen)
		init_initial_pop(1.0);
		
		
		for(int i = 0; i < getPopsize(); i++) {
			
			//bewerten
			Evolution_2.calc_fitness(getInitial_pop()[i]);
			
			//Ähnlichkeitszuordnung
			Diversity_2.associate_similarity(getInitial_pop()[i]);
			
			//Ausgangspopulation der Datenbank hinzufügen
			database_entry(getDatabase(), tablename, getInitial_pop()[i]);
		}

		//Population, mit der aktiv gearbeitet wird, initialisieren
		Individual_2[] pop = new Individual_2[getPopsize()];
		for (int i = 0; i < getPopsize(); i++) {
			pop[i] = new Individual_2(getN());
			Evolution_2.calc_fitness(pop[i]);
		}
		
		Individual_2[] fittest = Evolution_2.getFittest(pop, (int)(getPopsize()* selection_amount));
		
		// T E R M I N I E R U N G S B E D I N G U N G
		
		//Generationenlimit als Terminierungsbedingung, wenn Aussicht auf eine Lösung gering ist
		while(generation < max_gen  && fittest[0].getFitness() > 0) {
			
			fittest = Evolution_2.getFittest(pop, (int)(getPopsize()* selection_amount));
			
			
			generation++;
			System.out.println(generation);
			
			
			Individual_2[] selected_individuals = new Individual_2[getPopsize()];
			
			for(int i = 0; i < getPopsize()* selection_amount; i++) {
				/*
				Individual_2 champ = Evolution_2.getFittest(pop, 1)[0];
				System.out.println("champ:      "+champ.getFitness());
				int[] genome_copy = new int[N*N];
				for(int g = 0; g < (N*N); g++){
						genome_copy[g] = champ.getGenome()[g];
				}
				selected_individuals[i] = new Individual_2(genome_copy);
				*/
				Individual_2 sel = Evolution_2.rndm_selection(pop);
				selected_individuals[i] = sel;
				
			}
			
			//Selektion der restlichen (1 - selection_amount) durch Turnierselektion
			for(int i = (int)(getPopsize()* selection_amount); i < getPopsize(); i++) {
				Individual_2 sel = Evolution_2.tournament_selection(pop, q);
				selected_individuals[i] = sel;
			}
			
			
			// E L T E R N S E L E K T I O N  &  R E K O M B I N A T I O N
			
			for(int i = 0; i < (int)(crossover_amount*getPopsize()); i++) {
				//Die neue Population aus den selektierten Individuen durch Crossover erstellen
				pop[i] = Evolution_2.one_point_crossover(Evolution_2.rndm_selection(selected_individuals),
						Evolution_2.rndm_selection(selected_individuals));
			}
			for(int i = (int)crossover_amount*getPopsize(); i < getPopsize(); i++) {
				//Die neue Population aus den selektierten Individuen erstellen
				pop[i] = selected_individuals[i];
			}
			
			
			for( int i = 0; i < getPopsize(); i++) {
				
				// M U T A T I O N
				if ( Math.random() < mutpr) {
					//Eine mögliche Mutation
					Evolution_2.valid_colors_mutation(pop[i]);;
				}
				
				
			    
				// B E W E R T U N G
				
				Evolution_2.calc_fitness(pop[i]);
				
				//Diversität ermitteln und Ähnlichkeit zuordnen
				Diversity_2.associate_similarity(pop[i]);
				
				//Individuum der Datenbank hinzufügen
				database_entry(getDatabase(), tablename, pop[i]);
			}
		}
		String gene = "";
		
		for(int s = 0; s < N; s++){
			for(int z = 0; z < N; z++){
				gene = gene + " " + fittest[0].getGene(s*N+z);
			}
			System.out.println(gene);
			gene = "";
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
	 * @param max_simil Der maximale Anteil, mit dem ein Individuum mit den übrigen Individuen der Population übereinstimmen darf
	 */
	public static void init_initial_pop(double max_simil) {
		initial_pop = new Individual_2[popsize];
		initial_pop[0] = new Individual_2(N);

		boolean divers = false;
		for(int i = 1; i < popsize; i++) {
			while(!divers) {
				divers = true;
				initial_pop[i] = new Individual_2(N);
				for(int j = i-1; j >=0; j--) {
					
					if( Diversity_2.calc_similarity(initial_pop[i].getGenome(), initial_pop[j].getGenome()) > max_simil) {
						divers = false;
						//Durch vielfache Versuche ein Individuum zu finden, kann sich die ID enorm erhöhen, deshalb wird sie auf nachträglich i gesetzt
						id = i;
						initial_pop[i].setIdentification(i);
						//for-Schleife wird vorzeitig abgebrochen, damit ein neues Individual_2 generiert werden kann
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
	 * <li>fitness:  Enthält die Fitnesswerte. (Datentyp: REAL)</li>
	 * <li>generation: Die Generation, in der das jeweilige Individuum existierte. (Datentyp: INT)</li>
	 * <li>simil_association: Der Index des Urspungsindividuums im initial_pop-Array, das dem jewiligen Individuum am ähnlichsten ist. (Datentyp: INT)</li>
	 * <li>similarity: Der Quotient aus übereinstimmenden Sudoku-Feldern und Gesamtanzahl an Feldern des jeweiligen Individuums und seinem zugeordneten
	 * Ursprungsindividuum. (Datentyp: REAL)</li>
	 * <li>ID: Die Identifikationsnummer des jeweiligen Individuums. Nur innerhalb der selben Version ist die ID einzigartig. (Datentyp: INT)</li>
	 * </ul>
	 * @param database Name der Datenbank
	 * @param table_name Name der Tabelle
	 */
	public static void create_table(String database, String table_name) {
		try {
	        
	        String sqltab = "CREATE TABLE "+ table_name +
	        		" (fitness            REAL    NOT NULL, " + 
	                " generation          INT    NOT NULL, " + 
	                " simil_association   INT , " + 
	                " similarity          REAL, " +
	                " ID             INT)"; 
	        stmt.executeUpdate(sqltab);
	        
	        System.out.println("Tabelle "+table_name+" erstellt");
	       
			}
			catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
	        
	}
	
	
	/**
	 * Mit der Methode wird ein Eintrag eines bestimmten Individuums in die Datenbank ermöglicht. Es wird der Fitnesswert, die Generation,
	 * die Ähnlichkeitszuordnung, der Ähnlichkeitswert und die ID-Nummer hinzugefügt.
	 * @param database Name der Datenbank
	 * @param table_name Name der Tabelle
	 * @param iv das jeweilige Individuum
	 */
	public static void database_entry(String database, String table_name, Individual_2 iv) {
		try {
	        Statement stmt = c.createStatement();
	        String sql = "INSERT INTO " + table_name + "(FITNESS, GENERATION, SIMIL_ASSOCIATION, SIMILARITY, ID)" +
                    "VALUES (" +
	        		(double)iv.getFitness()       + ", " + 
                    generation               + ", " +
	        		iv.getAssociation()   		  + ", " +
                    iv.getSimilarity()            + ", " +
                    iv.getIdentification()+  ");"; 
    
	        stmt.executeUpdate(sql);
	        
	        
			}
			catch ( Exception e ) {
				System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
	}
	
	/**
	 * Gibt die Tabelle in der Konsole aus
	 * @param database Name der Datenbank
	 * @param tablename Name der Tabelle
	 */
	public static void print_table(String database, String tablename) {
		try {
			/*
		 	Class.forName("org.sqlite.JDBC");
	        Connection c = DriverManager.getConnection(path + database +".db");
	        c.setAutoCommit(false);
	        */
	        System.out.println("Einträge ausgeben");
	        
	        Statement stmt = c.createStatement();
	        
        	ResultSet rs = stmt.executeQuery("SELECT * FROM "+tablename+";");
        	
			while(rs.next()) {
				System.out.println("Fitness = "+rs.getInt("fitness"));
				System.out.println("Generation = "+rs.getInt("generation"));
				System.out.println("Similarity association  = "+rs.getInt("simil_association"));
				System.out.println("Similarity = "+rs.getFloat("similarity"));
				System.out.println("ID = "+rs.getInt("id"));
				
			}
			
			
	    }
        catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
        }
	}





	public static int getID() {
		return id;
	}
	public static String getTablename() {
		return tablename;
	}

	public static void setTablename(String tablename) {
		Main_2.tablename = tablename;
	}

	public static int getGeneration() {
		return generation;
	}


	public static double getMut() {
		return mut;
	}

	public static void setMut(double mut) {
		Main_2.mut = mut;
	}

	public static double getCrossover() {
		return crossover;
	}
	
	public static void setCrossover(double crossover) {
		Main_2.crossover = crossover;
	}

	public static int getN() {
		return N;
	}

	public static void setN(int n) {
		N = n;
	}

	public static void setID(int id) {
		Main_2.id = id;
	}

	public static int getPopsize() {
		return popsize;
	}

	public static void setPopsize(int popsize) {
		Main_2.popsize = popsize;
	}

	public static int getVersion() {
		return version;
	}

	public static void setVersion(int version) {
		Main_2.version = version;
	}

	public static void setGeneration(int generation) {
		Main_2.generation = generation;
	}
	
	public static void setDatabase(String string) {
		Main_2.database = string;
	}


	public static String getDatabase() {
		return database;
	}
	
	public static Individual_2[] getInitial_pop() {
		return initial_pop;
	}

	public static void setInitial_pop(Individual_2[] initial_pop) {
		Main_2.initial_pop = initial_pop;
	}
	
}
