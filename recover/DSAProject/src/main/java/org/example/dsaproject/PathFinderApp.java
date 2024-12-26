package org.example.dsaproject;

import com.google.maps.errors.ApiException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PathFinderApp extends Application {

    private List<String> checkpoints = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PathFinder");

        // Welcome Screen
        Label welcomeLabel = new Label("Welcome to PathFinder");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Start Point Input
        Label startPointLabel = new Label("Enter start point:");
        TextField startPointField = new TextField();

        // Checkpoint Input
        Label checkpointLabel = new Label("Enter checkpoint:");
        TextField checkpointField = new TextField();
        Button addCheckpointButton = new Button("Add");
        VBox checkpointBox = new VBox(5, checkpointField, addCheckpointButton);

        // Add Checkpoint Event
        addCheckpointButton.setOnAction(e -> {
            TextField newCheckpointField = new TextField();
            checkpointBox.getChildren().add(checkpointBox.getChildren().size() - 1, newCheckpointField);
            checkpoints.add("");
        });

        // End Point Input
        Label endPointLabel = new Label("Enter end point:");
        TextField endPointField = new TextField();
        Label place = new Label();

        Label warningLabel = new Label("");

        // Find Path Button
        Button findPathButton = new Button("Find Path");
        VBox layout = new VBox(10, welcomeLabel, startPointLabel, startPointField, checkpointLabel, checkpointBox, endPointLabel, endPointField, findPathButton, place, warningLabel);
        layout.setPadding(new Insets(10));

        findPathButton.setOnAction(e -> {
            warningLabel.setText("Loading Data! Please wait...");

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    List<String> places = new ArrayList<>();
                    places.add(startPointField.getText());
                    for (int i = 0; i < checkpointBox.getChildren().size() - 1; i++) {
                        TextField checkpoint = (TextField) checkpointBox.getChildren().get(i);
                        places.add(checkpoint.getText());
                    }
                    places.add(endPointField.getText());
                    String[] placesArray = places.toArray(new String[0]);
                    try {
                        Main main = new Main();
                        String[][] ans = main.findShortestPath(placesArray);
                        Platform.runLater(() -> showResultScreen(primaryStage, ans));
                    } catch (IOException | InterruptedException | ApiException | PlaceNotFound ex) {
                        Platform.runLater(() -> warningLabel.setText(ex.getLocalizedMessage()));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });

        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setScene(new Scene(layout));
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.show();
    }

    private void showResultScreen(Stage stage, String[][] pathData) {
        Label resultLabel = new Label("Here is your path:");
        resultLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        resultLabel.setAlignment(Pos.CENTER);

        // TableView for displaying the path data
        TableView<PathData> table = new TableView<>();

        // Define columns
        TableColumn<PathData, String> stepColumn = new TableColumn<>("Step");
        stepColumn.setCellValueFactory(new PropertyValueFactory<>("step"));
        TableColumn<PathData, String> fromColumn = new TableColumn<>("From");
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        TableColumn<PathData, String> toColumn = new TableColumn<>("To");
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));
        TableColumn<PathData, String> distanceColumn = new TableColumn<>("Distance");
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        TableColumn<PathData, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Add columns to the table
        table.getColumns().addAll(stepColumn, fromColumn, toColumn, distanceColumn, timeColumn);
        stepColumn.setPrefWidth(50);
        fromColumn.setPrefWidth(150);
        toColumn.setPrefWidth(150);
        distanceColumn.setPrefWidth(100);
        timeColumn.setPrefWidth(100);

        // Populate the table with data
        for (int i = 0; i < pathData.length; i++) {
            String step = String.valueOf(i + 1);
            String from = pathData[i][0];
            String to = pathData[i][1];
            String distance = pathData[i][2];
            String time = pathData[i][3];

            PathData pathData1 = new PathData(step, from, to, distance, time);
            table.getItems().add(pathData1);
        }

        Label totalDistanceLabel = new Label("Total Distance: " + TSPSolver.totalDistance);
        totalDistanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox layout = new VBox(10, resultLabel, table, totalDistanceLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);

        // Adjust the table height based on the number of rows
        double rowHeight = 25; // Adjust this value as needed
        double tableHeight = rowHeight * table.getItems().size() + 28; // 28 is for header and padding

        table.setPrefHeight(tableHeight);

        stage.setScene(new Scene(layout, 600, tableHeight + 100)); // Adjust height based on table
    }

    public static class PathData {
        private final String step;
        private final String from;
        private final String to;
        private final String distance;
        private final String time;

        public PathData(String step, String from, String to, String distance, String time) {
            this.step = step;
            this.from = from;
            this.to = to;
            this.distance = distance;
            this.time = time;
        }

        public String getStep() {
            return step;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getDistance() {
            return distance;
        }

        public String getTime() {
            return time;
        }
    }
}
