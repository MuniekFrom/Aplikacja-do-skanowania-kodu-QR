package org.example.qr;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class QRRefreshService {

    private Timeline refreshTimeline;
    private Timeline countdownTimeline;
    private int secondsLeft = 30;

    public void start(ImageView qrView, TextField input, Label countdownLabel) {
        stop();

        secondsLeft = 30;

        // 🔄 Timer QR co 30s
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    String text = input.getText();
                    if (text != null && !text.isEmpty()) {
                        String dynamicText = text + " | " + System.currentTimeMillis();
                        qrView.setImage(QRGenerator.generateQR(dynamicText, 300, 300));
                    }
                    secondsLeft = 30;
                })
        );

        refreshTimeline.setCycleCount(Timeline.INDEFINITE);


        countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    secondsLeft--;
                    countdownLabel.setText("Refreshing in: " + secondsLeft + " s");

                    if (secondsLeft <= 0) {
                        secondsLeft = 30;
                    }
                })
        );

        countdownTimeline.setCycleCount(Timeline.INDEFINITE);


        String text = input.getText();
        if (text != null && !text.isEmpty()) {
            qrView.setImage(QRGenerator.generateQR(text, 300, 300));
        }

        countdownLabel.setText("Refreshing in: 30 s");

        refreshTimeline.play();
        countdownTimeline.play();
    }

    public void stop() {
        if (refreshTimeline != null) refreshTimeline.stop();
        if (countdownTimeline != null) countdownTimeline.stop();
    }
}