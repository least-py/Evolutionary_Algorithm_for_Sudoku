package application;

import java.util.Objects;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import sudevo_gcp_strategy.*;

public class Main extends Application {
	
	private static int sudoku_size_N;
	private static int amount_of_cells = sudoku_size_N * sudoku_size_N;
	private static String[] grid_labels = new String[amount_of_cells];
	
    @Override
    public void start(final Stage stage) {
    	
        GridPane grid = new GridPane();
        sudoku_size_N = 4;
        grid_labels = new String[]{"1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G"};
        
        //init_grid_labels
        fill_sudoku_grid_with_constr(grid, 50);
        
        
        grid.setStyle("-fx-background-color: grey; -fx-padding: 2; -fx-hgap: 2; -fx-vgap: 2;");
        grid.setSnapToPixel(false);

        
        
        StackPane layout = new StackPane();
        layout.setStyle("-fx-background-color: whitesmoke; -fx-padding: 10;");
        layout.getChildren().addAll(grid);
        stage.setScene(new Scene(layout, 1000, 800));
        stage.show();
        
        grid.setGridLinesVisible(true);
        
        
        Button button= new Button("Run");
        layout.getChildren().add(button);
    
    }


    public static void main(String[] args) {
        launch();
    }
    
    private static void init_grid_labels(Individual_2 fittest) {
    	if(Objects.nonNull(fittest)) {
    		int[] genome = fittest.getGenome();
        	for(int cell = 0; cell < amount_of_cells; cell++) {
        		grid_labels[cell] = Integer.toBinaryString(genome[cell]);
        	}
    	}
    }
    
    
    
    private static void fill_sudoku_grid_with_constr(GridPane grid, Integer constraints) {
    	int cell_counter = 0;
        for(int row = 0; row < sudoku_size_N; row++) {
        	
        	for(int column = 0; column < sudoku_size_N; column++) {
        		grid.add(new Label(grid_labels[cell_counter]), column, row);
        		cell_counter++;
        	}
        	grid.getRowConstraints().add(new RowConstraints(constraints)); 
            grid.getColumnConstraints().add(new ColumnConstraints(constraints));
        }
        
        for (Node n : grid.getChildren()) {
            if (n instanceof Control) {
                Control control = (Control) n;
                control.setMaxSize(constraints, constraints);
                control.setStyle("-fx-background-color: cornsilk; -fx-alignment: center;");
            }
        }
    }
    
   
}