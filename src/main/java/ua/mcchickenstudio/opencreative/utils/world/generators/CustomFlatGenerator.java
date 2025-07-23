package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;

/**
 * <h1>CustomFlatGenerator</h1>
 * This class represents a custom flat world generator,
 * that has generation pattern to set blocks.
 */
public final class CustomFlatGenerator extends AbstractFlatGenerator {

    /**
     * @param id id of generator.
     * @param displayIcon icon to display.
     * @param generation generation pattern, that consists of amounts and material layers.
     * <pre>
     * {@code
     * "bedrock" // Generates only 1 layer with bedrock
     * "bedrock,2*sand" // Generates bedrock on Y = 0, and sand on Y = 1, 2.
     * "bedrock,2*air,stone" // Generates bedrock on Y = 0, and stone on Y = 3.
     * } </pre>
     */
    public CustomFlatGenerator(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull String generation) {
        super(id, displayIcon);
        Map<Integer, Material> generationBlocks = getBlocksFromText(generation);
        for (int y : generationBlocks.keySet()) {
            blocks.put(y, generationBlocks.get(y));
        }
    }

    private @NotNull Map<Integer, Material> getBlocksFromText(@NotNull String text) {
        Map<Integer, Material> blocksMap = new LinkedHashMap<>();
        try {
            // minecraft:bedrock,2*minecraft:dirt,1*minecraft:grass_block
            text = text.toUpperCase().replace("-","_").replace("MINECRAFT:", "");
            // bedrock,2*dirt,1*grass_block
            String[] blocks = text.split(",");
            int y = 0;
            for (String blockData : blocks) {
                String[] amountAndMaterial = blockData.split("\\*");
                String amountString = "";
                String materialString = "";
                if (amountAndMaterial.length == 1) {
                    amountString = "1";
                    materialString = amountAndMaterial[0];
                } else if (amountAndMaterial.length == 2) {
                    amountString = amountAndMaterial[0];
                    materialString = amountAndMaterial[1];
                }
                int amount = 1;
                Material material = Material.AIR;
                try {
                    amount = Integer.parseInt(amountString);
                    if (amount < 0 || amount > 320) amount = 1;
                } catch (NumberFormatException ignored) {}
                material = Objects.requireNonNullElse(Material.getMaterial(materialString), material);
                if (!material.isBlock() || material == Material.AIR) {
                    y += amount;
                    continue;
                }
                for (int i = 0; i < amount; i++) {
                    blocksMap.put(y, material);
                    y++;
                }
            }
        } catch (Exception error) {
            sendWarningErrorMessage("Can't parse custom flat generation pattern for " + getID() + ": " + text);
        }
        return blocksMap;
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
