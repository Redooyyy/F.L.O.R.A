package com.example.flora.Core.Helper.UI_Helper;

import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.example.flora.Core.Helper.UI_Helper.FloatingPickerPopup.DEFAULT_DEBOUNCE_MS;
import static com.example.flora.Core.Helper.UI_Helper.FloatingPickerPopup.DEFAULT_MAX_ROWS;

public class Builder {

    final TextField field;
    final AnchorPane root;

    Function<String, List<String>> queryFn = q -> List.of();
    BiConsumer<String, TextField> onSelect = (v, f) -> f.setText(v);
    boolean popupAbove = false;
    boolean showOnFocus = false; // default: only on typing
    int debounceMs = DEFAULT_DEBOUNCE_MS;
    int maxRows = DEFAULT_MAX_ROWS;

    public Builder(TextField field, AnchorPane root) {
        this.field = field;
        this.root = root;
    }

    public Builder suggestions(Function<String, List<String>> queryFn) {
        this.queryFn = queryFn;
        return this;
    }

    public Builder staticList(List<String> list) {
        this.queryFn = query -> list.stream()
                .filter(item -> query == null || query.isBlank()
                        || item.toLowerCase().contains(query.toLowerCase()))
                .toList();
        return this;
    }

    public Builder onSelect(BiConsumer<String, TextField> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public Builder popupAbove(boolean above) {
        this.popupAbove = above;
        return this;
    }


    public Builder showOnFocus(boolean show) {
        this.showOnFocus = show;
        return this;
    }

    public Builder debounceMs(int ms) {
        this.debounceMs = ms;
        return this;
    }

    public Builder maxRows(int rows) {
        this.maxRows = rows;
        return this;
    }

    public FloatingPickerPopup build() {
        return new FloatingPickerPopup(this);
    }
}