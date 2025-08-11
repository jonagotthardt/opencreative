package ua.mcchickenstudio.opencreative.indev.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderReplacer {

    private final List<String> placeholders = new ArrayList<>();
    private final List<Component> replacements = new ArrayList<>();

    public PlaceholderReplacer(Object... objects) {
        if (objects.length == 0) return;
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (i % 2 == 0) { // 0, 2, 4 - placeholders
                String text = object == null ? "null" : object.toString();
                placeholders.add(text);
            } else { // 1, 3, 5 - replacements
                Component replacement;
                if (object instanceof String string) {
                    replacement = Component.text(string);
                } else if (object instanceof Component component) {
                    replacement = component;
                } else {
                    String text = object == null ? "null" : object.toString();
                    replacement = Component.text(text);
                }
                replacements.add(replacement);
            }
        }
    }

    public @NotNull TextReplacementConfig get() {
        TextReplacementConfig.Builder config = TextReplacementConfig.builder();
        for (int i = 0; i < placeholders.size(); i++) {
            if (i >= replacements.size()) {
                break;
            }
            config.match("<" + placeholders.get(i) + ">")
                    .replacement(replacements.get(i));
        }
        return config.build();
    }

    public @NotNull Component applyPlaceholders(@NotNull Component input) {
        Component output = input;
        for (int i = 0; i < placeholders.size(); i++) {
            if (i >= replacements.size()) break;
            @RegExp String placeholder = "<" + placeholders.get(i) + ">";
            Component replacement = replacements.get(i);
            output = output.replaceText(TextReplacementConfig.builder()
                    .match(placeholder)
                    .replacement(replacement)
                    .build());
        }
        return output;
    }


}
