package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>WorldTemplate</h1>
 * This class represents a world template, that can be
 * used for creating a new worlds. Template folders are
 * located in /plugins/OpenCreative/templates/ directory.
 * <p>
 * Template folder must be Minecraft world folder.
 */
public class WorldTemplate extends WorldGenerator {

    private final String folderName;

    /**
     * Constructor of world template.
     *
     * @param id          short id of world generator that will be used in world generation menu.
     *                    <p>
     *                    It must be lower-snake-cased, for example: "flat", "nostalgia_world".
     *                    If some of registered generators has same ID as new, it will be not added.
     * @param displayIcon icon of world generator that will be displayed in world generation menu.
     * @param folderName name of folder from /plugins/OpenCreative/templates/.
     */
    public WorldTemplate(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull String folderName) {
        super(id, displayIcon);
        this.folderName = folderName;
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Copied " + folderName +  " world from templates directory";
    }

    /**
     * Returns name of world folder, located in /plugins/OpenCreative/templates directory.
     * @return name of world folder for copying and pasting.
     */
    public @NotNull String getFolderName() {
        return folderName;
    }

    @Override
    public void modifyWorldCreator(@NotNull WorldCreator creator) {}

    @Override
    public void afterCreation(@NotNull World world) {}
}
