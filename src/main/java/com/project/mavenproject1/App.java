package com.project.mavenproject1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * JavaFX App
 */
public class App extends Application {

    WebServer webServer;
    private TextArea logTextArea;
    private TextField portTextField, webDirectoryTextField, logDirectoryTextField;
    private Button startButton, stopButton, dirButton, logButton;
    private Label statusLabel;
    String dirPath, logPath, portString, pbr, dbr, lbr;
    int port;
    boolean start = true;

    DirectoryChooser directoryChooser = new DirectoryChooser();
    DirectoryChooser logChooser = new DirectoryChooser();

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException, IOException {
        
        BufferedReader portBR = new BufferedReader(new FileReader("port.txt"));
        pbr = portBR.readLine();
        
        BufferedReader dirBR = new BufferedReader(new FileReader("dir.txt"));
        dbr = dirBR.readLine();
        
        BufferedReader logBR = new BufferedReader(new FileReader("log.txt"));
        lbr = logBR.readLine();

        Font font = new Font("Arial", 16);

        primaryStage.setTitle("Simple Web Server");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label portLabel = new Label("Port:");
        portLabel.setFont(font);
        GridPane.setConstraints(portLabel, 0, 0);
        portTextField = new TextField(pbr);
        portTextField.setPromptText("Port");
        portTextField.setFont(font);
        GridPane.setConstraints(portTextField, 1, 0);

        Label webDirectoryLabel = new Label("Web Directory:");
        GridPane.setConstraints(webDirectoryLabel, 0, 1);
        webDirectoryLabel.setFont(font);
        dirButton = new Button("Choose Directory");
        dirButton.setFont(font);
        GridPane.setConstraints(dirButton, 1, 1);

        webDirectoryTextField = new TextField(dbr);
        webDirectoryTextField.setEditable(false);
        webDirectoryTextField.setFont(font);
        GridPane.setConstraints(webDirectoryTextField, 1, 2);

        Label logDirectoryLabel = new Label("Log Directory:");
        GridPane.setConstraints(logDirectoryLabel, 0, 3);
        logDirectoryLabel.setFont(font);
        logButton = new Button("Log Directory");
        logButton.setFont(font);
        GridPane.setConstraints(logButton, 1, 3);

        logDirectoryTextField = new TextField(lbr);
        logDirectoryTextField.setEditable(false);
        logDirectoryTextField.setFont(font);
        GridPane.setConstraints(logDirectoryTextField, 1, 4);

        startButton = new Button("Start Server");
        startButton.setFont(font);
        GridPane.setConstraints(startButton, 0, 5);
        stopButton = new Button("Stop Server");
        stopButton.setFont(font);
        stopButton.setDisable(true);
        GridPane.setConstraints(stopButton, 1, 5);

        logTextArea = new TextArea();
        logTextArea.setFont(font);
        logTextArea.setEditable(false);
        logTextArea.setPrefRowCount(10);
        GridPane.setConstraints(logTextArea, 0, 6, 2, 1);

        grid.getChildren().addAll(portLabel, portTextField, webDirectoryLabel, dirButton, webDirectoryTextField,
                logDirectoryLabel, logButton, logDirectoryTextField, startButton, stopButton, logTextArea);

        primaryStage.setScene(new Scene(grid, 400, 400));
        primaryStage.show();

        dirButton.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            this.dirPath = selectedDirectory.getAbsolutePath() + "/";
            System.out.println(dirPath);
            webDirectoryTextField.appendText(dirPath);
            
            try {
                FileWriter myWriter = new FileWriter("dir.txt",false);
                myWriter.write(dirPath);
                myWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        logButton.setOnAction(e -> {
            File selectedDirectory = logChooser.showDialog(primaryStage);
            this.logPath = selectedDirectory.getAbsolutePath() + "/";
            System.out.println(logPath);
            logDirectoryTextField.appendText(logPath);
            
            try {
                FileWriter myWriter = new FileWriter("log.txt",false);
                myWriter.write(logPath);
                myWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        startButton.setOnAction((ActionEvent e) -> {
            App.this.port = Integer.parseInt(portTextField.getText());
            dirPath = dbr;
            logPath = lbr;
            webServer = new WebServer(port, dirPath, logPath);
            webServer.start();
            logTextArea.appendText("Server started on port " + port + " \n");
            
            portString = String.valueOf(port);

            try {
                FileWriter myWriter = new FileWriter("port.txt",false);
                myWriter.write(portString);
                myWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            startButton.setDisable(true);
            stopButton.setDisable(false);
        });
//
        stopButton.setOnAction(e -> {
                webServer.stop();
                logTextArea.appendText("Server stopped\n");
                stopButton.setDisable(true);
                startButton.setDisable(false);
                
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                readLogAndUpdateTextArea();
            }
        }, 0, 2000);

    }

    private void readLogAndUpdateTextArea() {
        try {
            String logFileName = "2024-05-21.log";
            Path logFilePath = Paths.get("/Users/dwiwijayanto/Documents/tryweb/", logFileName);
            List<String> lines = Files.readAllLines(logFilePath);
            StringBuilder logContent = new StringBuilder();
            for (String line : lines) {
                logContent.append(line).append("\n");
            }
            // Memperbarui logTextArea di thread JavaFX Application
            Platform.runLater(() -> {
                logTextArea.setText(logContent.toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
