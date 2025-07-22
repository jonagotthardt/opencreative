package ua.mcchickenstudio.opencreative.indev.generators;

import org.apache.commons.lang.StringUtils;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.ExtensionContent;

public abstract class WorldGenerator extends ChunkGenerator implements ExtensionContent {

    private final String id;
    private final ItemStack displayIcon;

    /**
     * Constructor of world generator.
     * @param id short id of world generator that will be used in world generation menu.
     *           <p>
     *           It must be lower-snake-cased, for example: "flat", "nostalgia_world".
     *           If some of registered generators has same ID as new, it will be not added.
     * @param displayIcon icon of world generator that will be displayed in world generation menu.
     */
    public WorldGenerator(@NotNull String id, @NotNull ItemStack displayIcon) {
        this.id = id.replace("-","_").toLowerCase();
        this.displayIcon = displayIcon;
    }

    /**
     * Returns an icon that will be used
     * in world generation menu.
     * @return icon of event value to display.
     */
    public ItemStack getDisplayIcon() {
        return displayIcon;
    }

    /**
     * Returns name of world generator for displaying in
     * registry by converting id. Not used in menus.
     * @return display name of world generator.
     */
    public @NotNull String getName() {
        return StringUtils.capitalize(id.replace("_"," "));
    }

    /**
     * Returns id of world generator, that will be used
     * to find it in registry.
     * @return id of world generator.
     */
    public final @NotNull String getID() {
        return id;
    }

}
