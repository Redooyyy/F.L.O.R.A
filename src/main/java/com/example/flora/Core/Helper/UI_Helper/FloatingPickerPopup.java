package com.example.flora.Core.Helper.UI_Helper;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Function;


public class FloatingPickerPopup {

    protected static final int DEFAULT_DEBOUNCE_MS = 220;
    protected static final int DEFAULT_MAX_ROWS = 6;
    private static final double POPUP_WIDTH = 260.0;
    private static final double GAP = 5.0;

    private static final String POPUP_STYLE =
            "-fx-background-color:#13111F;" +
                    "-fx-background-radius:14;" +
                    "-fx-border-color:#2A2740;" +
                    "-fx-border-radius:14;" +
                    "-fx-border-width:1;" +
                    "-fx-effect:dropshadow(gaussian,#000000CC,20,0,0,6);";

    private static final String ROW_IDLE =
            "-fx-background-color:transparent;" +
                    "-fx-background-radius:9;" +
                    "-fx-text-fill:#C4B5F5;" +
                    "-fx-font-size:12.5px;" +
                    "-fx-cursor:hand;";

    private static final String ROW_HOVER =
            "-fx-background-color:#201D34;" +
                    "-fx-background-radius:9;" +
                    "-fx-text-fill:#EDE9F6;" +
                    "-fx-font-size:12.5px;" +
                    "-fx-cursor:hand;";

    private final TextField field;
    private final AnchorPane root;
    private final Function<String, List<String>> queryFn;
    private final BiConsumer<String, TextField> onSelect;
    private final boolean popupAbove;
    private final boolean showOnFocus; // true = show on focus, false = only on typing
    private final int debounceMs;
    private final int maxRows;

    private final VBox popup = new VBox(2);
    private Timer debounce = null;
    private boolean attached = false;

    public FloatingPickerPopup(Builder b) {
        this.field = b.field;
        this.root = b.root;
        this.queryFn = b.queryFn;
        this.onSelect = b.onSelect;
        this.popupAbove = b.popupAbove;
        this.showOnFocus = b.showOnFocus;
        this.debounceMs = b.debounceMs;
        this.maxRows = b.maxRows;
        buildPopup();
    }


    public void attach() {
        if (attached) return;
        attached = true;

        if (!root.getChildren().contains(popup)) {
            root.getChildren().add(popup);
        }


        field.focusedProperty().addListener((obs, wasFocused, nowFocused) -> {
            if (nowFocused) {
                if (showOnFocus) {

                    triggerSearch(field.getText());
                }
            } else {

                PauseTransition pt = new PauseTransition(Duration.millis(180));
                pt.setOnFinished(e -> hidePopup());
                pt.play();
            }
        });


        field.textProperty().addListener((obs, oldVal, newVal) -> {
            cancelDebounce();

            if (newVal == null || newVal.isBlank()) {
                if (showOnFocus && field.isFocused()) {
                    // Field is focused but empty → still show full list
                    triggerSearch("");
                } else {
                    hidePopup();
                }
                return;
            }

            // Debounced search on every keystroke
            debounce = new Timer(true);
            debounce.schedule(new TimerTask() {
                @Override
                public void run() {
                    List<String> hits = queryFn.apply(newVal.trim());
                    Platform.runLater(() -> showSuggestions(hits));
                }
            }, debounceMs);
        });
    }

    public void hide() {
        hidePopup();
    }


    private void buildPopup() {
        popup.setPadding(new Insets(8, 6, 8, 6));
        popup.setStyle(POPUP_STYLE);
        popup.setPrefWidth(POPUP_WIDTH);
        popup.setVisible(false);
        popup.setManaged(false);
        popup.setOpacity(0);
        popup.setMouseTransparent(true);
    }


    private void triggerSearch(String query) {
        List<String> hits = queryFn.apply(query == null ? "" : query.trim());
        Platform.runLater(() -> showSuggestions(hits));
    }

    private void showSuggestions(List<String> hits) {
        popup.getChildren().clear();

        if (hits == null || hits.isEmpty()) {
            Label none = new Label("No results found");
            none.setStyle("-fx-text-fill:#3A3756; -fx-font-size:11px; -fx-font-style:italic;");
            none.setPadding(new Insets(6, 10, 6, 10));
            popup.getChildren().add(none);
        } else {
            List<String> shown = hits.size() > maxRows ? hits.subList(0, maxRows) : hits;
            for (String item : shown) {
                popup.getChildren().add(buildRow(item));
            }
        }

        positionPopup();

        if (!popup.isVisible()) {
            popup.setVisible(true);
            popup.setManaged(true);
            animateIn();
        }
    }

    private Label buildRow(String value) {
        Label row = new Label("@" + value);
        row.setStyle(ROW_IDLE);
        row.setPadding(new Insets(7, 12, 7, 12));
        row.setPrefWidth(POPUP_WIDTH - 12);
        row.setAlignment(Pos.CENTER_LEFT);

        row.setOnMouseEntered(e -> row.setStyle(ROW_HOVER + "-fx-padding:7 12 7 12;"));
        row.setOnMouseExited(e -> row.setStyle(ROW_IDLE + "-fx-padding:7 12 7 12;"));
        row.setOnMouseClicked(e -> {
            onSelect.accept(value, field);
            hidePopup();
        });
        return row;
    }


    private void positionPopup() {
        double fieldX = field.localToScene(0, 0).getX() - root.localToScene(0, 0).getX();
        double fieldY = field.localToScene(0, 0).getY() - root.localToScene(0, 0).getY();

        double y;
        if (popupAbove) {
            double estimatedHeight = 16 + (Math.min(maxRows, 6) * 38.0);
            y = fieldY - estimatedHeight - GAP;
        } else {
            y = fieldY + field.getHeight() + GAP;
        }

        AnchorPane.setLeftAnchor(popup, fieldX);
        AnchorPane.setTopAnchor(popup, y);
        AnchorPane.setRightAnchor(popup, null);
        AnchorPane.setBottomAnchor(popup, null);
    }

    private void animateIn() {
        popup.setMouseTransparent(false);
        double fromY = popupAbove ? 6 : -6;
        popup.setTranslateY(fromY);

        FadeTransition ft = new FadeTransition(Duration.millis(180), popup);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        TranslateTransition tt = new TranslateTransition(Duration.millis(180), popup);
        tt.setFromY(fromY);
        tt.setToY(0);
        tt.play();
    }

    private void hidePopup() {
        if (!popup.isVisible()) return;

        FadeTransition ft = new FadeTransition(Duration.millis(140), popup);
        ft.setFromValue(popup.getOpacity());
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            popup.setVisible(false);
            popup.setManaged(false);
            popup.setMouseTransparent(true);
            popup.getChildren().clear();
        });
        ft.play();
    }

    private void cancelDebounce() {
        if (debounce != null) {
            debounce.cancel();
            debounce = null;
        }
    }
}