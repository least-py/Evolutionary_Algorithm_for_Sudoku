package Evolutionary_Algorithm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Tracking {
	
	private String database;
	private Connection c;
	private Statement stmt;
	private String path;

	
	public Tracking(String db_name, String path) {
		this.database = db_name;
		this.path = path;
		establish_connection();
		
	}
	
	private void establish_connection() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(path + database + ".db");
			c.setAutoCommit(false);
			System.out.println("Datenbank geöffnet");
			stmt = c.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void close_connection() {
		try {
			stmt.close();
			c.commit();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void create_table(String table_name) {
		try {

			String sqltab = "CREATE TABLE " + table_name + " (fitness            REAL    NOT NULL, "
					+ " generation          INT    NOT NULL, " + " simil_association   INT , "
					+ " similarity          REAL, " + " ID             INT)";
			stmt.executeUpdate(sqltab);

			System.out.println("Tabelle " + table_name + " erstellt");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}
	
	
	public void database_entry(String table_name, Individual iv, int generation) {
		
		try {
			Statement stmt = c.createStatement();
			String sql = "INSERT INTO " + table_name + "(FITNESS, GENERATION, SIMIL_ASSOCIATION, SIMILARITY, ID)"
					+ "VALUES (" + (double) iv.getFitness() + ", " + generation + ", " + iv.getAssociation() + ", "
					+ iv.getSimilarity() + ", " + iv.getIdentification() + ");";

			stmt.executeUpdate(sql);

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	
	public void print_table(String tablename) {
		try {
			System.out.println("Einträge ausgeben");

			Statement stmt = c.createStatement();

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
}
