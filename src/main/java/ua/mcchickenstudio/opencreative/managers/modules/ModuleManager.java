package ua.mcchickenstudio.opencreative.managers.modules;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.modules.Module;
import ua.mcchickenstudio.opencreative.managers.Manager;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;

import java.util.Set;
import java.util.UUID;

/**
 * <h1>ModuleManager</h1>
 * This interface represents a module manager,
 * that registers, creates and deletes modules.
 */
public interface ModuleManager extends Manager {

    /**
     * Register a module to base, so it will be visible
     * in modules browser menu.
     * @param module module to register
     */
    void registerModule(@NotNull Module module);

    /**
     * Creates a new module and registers it in base.
     * @param owner owner of module.
     * @param devPlanet dev planet to parse executor locations.
     * @param locations set of location, that contains executors.
     */
    void createModule(@NotNull Player owner, @NotNull DevPlanet devPlanet, @NotNull Set<Location> locations);

    /**
     * Deletes module from files and base.
     * @param module module to delete.
     */
    void deleteModule(@NotNull Module module);

    /**
     * Returns modules, that were made by player with
     * specified unique ID.
     * @param uuid unique id of player.
     * @return set of player created modules.
     */
    @NotNull Set<Module> getPlayerModules(@NotNull UUID uuid);

    /**
     * Returns set of all registered modules in base.
     * @return set of modules.
     */
    @NotNull Set<Module> getModules();

    /**
     * Returns module by its ID.
     * @param id id to get module.
     * @return module - if found, or null - not exists.
     */
    @Nullable Module getModuleById(@NotNull String id);

}
