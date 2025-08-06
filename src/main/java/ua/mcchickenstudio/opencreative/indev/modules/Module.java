/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2025, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.indev.modules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.CodingBlockPlacer;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.listeners.player.InteractListener;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.placeDevBlock;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getModuleConfig;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getCodingValueKey;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parseModuleLines;

/**
 * <h1>Module</h1>
 * This class represents a module, a code that
 * can be pasted in developer world.
 * <p>Modules are stored in ./modules/moduleID.yml files.
 */
@ApiStatus.Experimental
public class Module {

    private final int id;
    private final ModuleInfo info;
    private UUID owner;

    public Module(int id) {
        this.id = id;
        this.info = new ModuleInfo(this);

        String uuid = getModuleConfig(this).getString("owner","");
        try {
            owner = UUID.fromString(uuid);
        } catch (Exception ignored) {
            owner = new UUID(0,0);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                info.updateIcon();
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
    }

    public @NotNull UUID getOwner() {
        return owner;
    }

    public @NotNull String getOwnerName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
        return offlinePlayer.hasPlayedBefore() ? offlinePlayer.getName() : "Unknown owner";
    }

    public int getId() {
        return id;
    }

    public @NotNull ModuleInfo getInformation() {
        return info;
    }

    /**
     * Builds code from module.
     * @param devPlanet dev planet where module will be pasted.
     * @param player player who loads module.
     * @return true - built module successfully, false - failed.
     */
    public boolean place(@NotNull DevPlanet devPlanet, @NotNull Player player) {

        if (!devPlanet.isLoaded()) return false;
        if (!devPlanet.getWorld().getPlayers().contains(player)) return false;

        FileConfiguration config = getModuleConfig(this);
        ConfigurationSection section = config.getConfigurationSection("code.blocks");
        if (section == null) {
            return true;
        }

        List<Location> freeColumns = new ArrayList<>();
        int requiredColumns = section.getKeys(false).size();
        for (DevPlatform platform : devPlanet.getPlatforms()) {
            freeColumns.addAll(platform.getFreeColumns());
            if (freeColumns.size() >= requiredColumns) {
                break;
            }
        }

        CodingBlockPlacer placer = new CodingBlockPlacer(devPlanet.getSignMaterial(), devPlanet.getContainerMaterial(),
                devPlanet.getDevPlatformer().getCodingBlocksLimit(devPlanet));
        CodingBlockPlacer.CodePlacementResult result = placer.placeCodingLine(devPlanet, section);
        if (result == CodingBlockPlacer.CodePlacementResult.NOT_ENOUGH_CODING_LINES) {
            player.sendMessage(getLocaleMessage("modules.few-space")
                    .replace("%required%", String.valueOf(requiredColumns)));
            return false;
        } else if (result == CodingBlockPlacer.CodePlacementResult.ERROR) {
            player.sendMessage(parseModuleLines(this,getLocaleMessage("modules.fail",player)));
            return false;
        } else {
            for (Player planetPlayer : devPlanet.getPlanet().getPlayers()) {
                if (devPlanet.getPlanet().getWorldPlayers().canDevelop(planetPlayer)) {
                    planetPlayer.sendMessage(parseModuleLines(this,getLocaleMessage("modules.installed",player)));
                }
            }
            return true;
        }
    }

}
