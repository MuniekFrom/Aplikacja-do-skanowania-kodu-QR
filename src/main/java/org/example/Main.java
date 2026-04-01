package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.qr.QRGenerator;
import org.example.qr.QRRefreshService;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        QRRefreshService refreshService = new QRRefreshService();

        TextField input = new TextField();
        input.setPromptText("Enter text for QR");

        Button generateButton = new Button("Generate QR");

        Label countdownLabel = new Label("Refreshing in: 30 s");
        countdownLabel.setVisible(false);
        countdownLabel.setManaged(false);

        ImageView qrView = new ImageView();

        generateButton.setOnAction(e -> {
            String text = input.getText();
            if (!text.isEmpty()) {
                Image qrImage = QRGenerator.generateQR(text, 300, 300);
                qrView.setImage(qrImage);

                countdownLabel.setVisible(true);
                countdownLabel.setManaged(true);

                refreshService.start(qrView, input, countdownLabel);
            }
        });

        VBox root = new VBox(10, input, generateButton, qrView, countdownLabel);
        root.setStyle("-fx-padding: 20");

        Scene scene = new Scene(root, 400, 500);

        stage.setTitle("QR Generator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}