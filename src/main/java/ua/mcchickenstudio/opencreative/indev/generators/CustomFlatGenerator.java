package ua.mcchickenstudio.opencreative.indev.generators;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CustomFlatGenerator extends AbstractFlatGenerator {

    public CustomFlatGenerator(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull String blocks) {
        super(id, displayIcon, new HashMap<>());
    }

    private @NotNull Map<Integer, Material> getBlocksFromText(@NotNull String text) {
        text = text.toLowerCase().replace("-","_").replace("minecraft:", "");
        String[] blocks = text.split(",");
        for (String blockData : blocks) {
            String[] amountAndMaterial = blockData.split("\\*");
            if (amountAndMaterial.length == 0) {

            }
        }
        return new HashMap<>();
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Generates flat world by settings";
    }
}
