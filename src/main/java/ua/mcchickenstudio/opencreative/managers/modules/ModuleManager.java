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

public interface ModuleManager extends Manager {

    void registerModule(@NotNull Module module);

    void createModule(@NotNull Player owner, @NotNull DevPlanet devPlanet, @NotNull Set<Location> locations);

    void deleteModule(@NotNull Module module);

    @NotNull Set<Module> getPlayerModules(@NotNull UUID uuid);

    @NotNull Set<Module> getModules();

    @Nullable Module getModuleById(@NotNull String id);

}
