package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.apache.commons.lang.StringUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.ExtensionContent;

import java.util.Random;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>WorldGenerator</h1>
 * This class represents world generator, that
 * can be used for generating new player worlds
 * or on loading old worlds of planets.
 */
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
     * Changes world creator before creating or loading world.
     * Useful to set generator as itself, if it overrides
     * {@link ChunkGenerator#generateSurface(WorldInfo, Random, int, int, ChunkData) generateSurface} method.
     * @param creator creator to change.
     */
    public abstract void modifyWorldCreator(@NotNull WorldCreator creator);

    /**
     * Executes world operations, when it's created or loaded.
     * @param world world to execute code in it.
     */
    public abstract void afterCreation(@NotNull World world);

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
     * Returns localized name for displaying.
     * @return localized name.
     */
    public @NotNull String getLocaleName() {
        return getLocaleMessage("menus.world-creation.items.type.choices." + id,false);
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
