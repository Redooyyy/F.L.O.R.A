package com.example.flora.Features.Project.UI;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel.DATE_FMT;


public final class DetailUIHelper {

    private DetailUIHelper() {
    }


    public static Label buildDeadlineChip(Task t) {
        Label chip = new Label();
        refreshDeadlineChip(chip, t);
        return chip;
    }

    public static void refreshDeadlineChip(Label chip, Task t) {
        LocalDate deadline = DateAndTime.parseDate(t.getDueDate());
        if (deadline == null) {
            chip.setText("No deadline");
            chip.setStyle(chipStyle("#4A4060", "rgba(74,64,96,0.10)", "rgba(74,64,96,0.25)"));
        } else {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            String col, bg, br;
            if (days < 0) {
                col = "#F87171";
                bg = "rgba(248,113,113,0.12)";
                br = "rgba(248,113,113,0.35)";
            } else if (days <= 3) {
                col = "#FBB024";
                bg = "rgba(251,176,36,0.12)";
                br = "rgba(251,176,36,0.35)";
            } else {
                col = "#34D399";
                bg = "rgba(52,211,153,0.10)";
                br = "rgba(52,211,153,0.30)";
            }
            chip.setText((days < 0 ? "⚠ " : "📅 ") + deadline.format(DATE_FMT));
            chip.setStyle(chipStyle(col, bg, br));
        }
        chip.setPadding(new Insets(3, 8, 3, 8));
    }

    public static String chipStyle(String col, String bg, String br) {
        return "-fx-background-color:" + bg + ";"
                + "-fx-background-radius:20;"
                + "-fx-border-color:" + br + ";"
                + "-fx-border-radius:20;"
                + "-fx-border-width:1;"
                + "-fx-text-fill:" + col + ";"
                + "-fx-font-size:11px;"
                + "-fx-font-weight:bold;";
    }


    public static String statusStyleClass(TaskStatus s) {
        if (s == null) return "status-todo";
        return switch (s) {
            case DONE -> "status-done";
            case IN_PROGRESS -> "status-progress";
            case IN_REVIEW -> "status-review";
            default -> "status-todo";
        };
    }

    public static String statusLabel(TaskStatus s) {
        return switch (s) {
            case TODO -> "📌 To Do";
            case IN_PROGRESS -> "⚙ In Progress";
            case IN_REVIEW -> "👁 In Review";
            case DONE -> "✅ Done";
        };
    }

    public static String bugStatusStyleClass(BugStatus s) {
        if (s == null) return "status-todo";
        return switch (s) {
            case CLOSED -> "status-done";
            case IN_PROGRESS -> "status-progress";
            default -> "status-todo";
        };
    }

    public static String severityBadgeClass(BugSeverity s) {
        return switch (s) {
            case CRITICAL -> "severity-critical";
            case HIGH -> "severity-high";
            case MEDIUM -> "severity-medium";
            case LOW -> "severity-low";
        };
    }

    public static String severityLeftBorder(BugSeverity s) {
        String color = switch (s) {
            case CRITICAL -> "rgba(248,113,113,0.70)";
            case HIGH -> "rgba(251,146,60,0.70)";
            case MEDIUM -> "rgba(251,183,36,0.60)";
            case LOW -> "rgba(52,211,153,0.50)";
        };
        return "-fx-border-color: " + color + " transparent transparent transparent;"
                + "-fx-border-width: 0 0 0 3;"
                + "-fx-background-radius: 12;"
                + "-fx-border-radius: 12;";
    }


    public static StringConverter<LocalDate> dateConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate d) {
                return d == null ? "" : d.format(DATE_FMT);
            }

            @Override
            public LocalDate fromString(String s) {
                try {
                    return (s == null || s.isBlank()) ? null : LocalDate.parse(s, DATE_FMT);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    public static Button quickAdjust(String label, DatePicker dp, long days) {
        Button b = new Button(label);
        b.getStyleClass().add("update-btn");
        b.setPadding(new Insets(3, 6, 3, 6));
        b.setStyle("-fx-font-size:10px;");
        b.setOnAction(ev -> {
            LocalDate cur = dp.getValue() != null ? dp.getValue() : LocalDate.now();
            dp.setValue(cur.plusDays(days));
        });
        return b;
    }


    public static Label emptyLabel(String msg) {
        Label l = new Label(msg);
        l.setTextFill(Color.web("#4A4060"));
        l.setStyle("-fx-font-size:13px; -fx-font-style:italic;");
        l.setPadding(new Insets(20, 14, 10, 14));
        return l;
    }

    public static void staggerIn(VBox card, int delayMs) {
        card.setOpacity(0);
        card.setTranslateY(14);
        FadeTransition ft = new FadeTransition(Duration.millis(260), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(delayMs));
        ft.play();
        javafx.animation.TranslateTransition tt =
                new javafx.animation.TranslateTransition(Duration.millis(260), card);
        tt.setFromY(14);
        tt.setToY(0);
        tt.setDelay(Duration.millis(delayMs));
        tt.play();
    }
}