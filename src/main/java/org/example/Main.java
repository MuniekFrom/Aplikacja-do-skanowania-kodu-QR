package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.qr.QRGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Main extends Application {

    private Timeline countdownTimeline;
    private int secondsLeft = 300;

    private Label timerLabel;
    private Label tokenLabel;
    private Label statusLabel;
    private ImageView qrView;
    private TextField employeeInput;

    @Override
    public void start(Stage stage) {

        Label titleLabel = new Label("System kontroli dostępu");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Wygeneruj jednorazowy token QR dla pracownika");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        employeeInput = new TextField();
        employeeInput.setPromptText("Wpisz ID lub login pracownika");
        employeeInput.setMaxWidth(300);

        Button generateButton = new Button("Wygeneruj token QR");
        generateButton.setMaxWidth(300);
        generateButton.setStyle("""
                -fx-background-color: #2563eb;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 10 20;
                """);

        Button refreshButton = new Button("Odśwież kod");
        refreshButton.setMaxWidth(300);
        refreshButton.setDisable(true);
        refreshButton.setStyle("""
                -fx-background-color: #16a34a;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 10 20;
                """);

        qrView = new ImageView();
        qrView.setFitWidth(260);
        qrView.setFitHeight(260);
        qrView.setPreserveRatio(true);

        timerLabel = new Label("Kod wygasa za: 05:00");
        timerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        timerLabel.setVisible(false);

        tokenLabel = new Label("Token nie został jeszcze wygenerowany");
        tokenLabel.setWrapText(true);
        tokenLabel.setMaxWidth(330);
        tokenLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #16a34a;");

        generateButton.setOnAction(e -> {
            String employeeId = employeeInput.getText().trim();

            if (employeeId.isEmpty()) {
                statusLabel.setText("Wpisz ID lub login pracownika.");
                statusLabel.setStyle("-fx-text-fill: #dc2626;");
                return;
            }

            generateToken(employeeId);
            refreshButton.setDisable(false);
        });

        refreshButton.setOnAction(e -> {
            String employeeId = employeeInput.getText().trim();

            if (!employeeId.isEmpty()) {
                generateToken(employeeId);
            }
        });

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setMaxWidth(380);
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0, 0, 6);
                """);

        card.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                employeeInput,
                generateButton,
                refreshButton,
                qrView,
                timerLabel,
                tokenLabel,
                statusLabel
        );

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #dbeafe, #eff6ff);");

        Scene scene = new Scene(root, 500, 700);

        stage.setTitle("QR Access Token");
        stage.setScene(scene);
        stage.show();
    }

    private void generateToken(String employeeId) {

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        String expiresAtText = expiresAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String qrContent =
                "employeeId=" + employeeId +
                        ";token=" + token +
                        ";expiresAt=" + expiresAtText;

        Image qrImage = QRGenerator.generateQR(qrContent, 300, 300);
        qrView.setImage(qrImage);

        tokenLabel.setText(
                "Pracownik: " + employeeId +
                        "\nToken: " + token.substring(0, 12) + "..." +
                        "\nWażny do: " + expiresAtText
        );

        statusLabel.setText("Token QR został wygenerowany.");
        statusLabel.setStyle("-fx-text-fill: #16a34a;");

        startCountdown();
    }

    private void startCountdown() {

        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        secondsLeft = 300;
        timerLabel.setVisible(true);

        countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    secondsLeft--;

                    int minutes = secondsLeft / 60;
                    int seconds = secondsLeft % 60;

                    timerLabel.setText(
                            String.format("Kod wygasa za: %02d:%02d", minutes, seconds)
                    );

                    if (secondsLeft <= 0) {
                        countdownTimeline.stop();
                        timerLabel.setText("Kod wygasł");
                        statusLabel.setText("Wygeneruj nowy kod QR.");
                        statusLabel.setStyle("-fx-text-fill: #dc2626;");
                        qrView.setImage(null);
                    }
                })
        );

        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    public static void main(String[] args) {
        launch();
    }
}