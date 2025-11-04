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

package ua.mcchickenstudio.opencreative.managers.hints;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.listeners.player.ChangedWorld;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;

public final class Hints implements HintManager {

    @Override
    public void checkForHints(@NotNull Player player) {
        if (!isEntityInDevPlanet(player)) {
            if (player.getInventory().getItemInMainHand().getType() != Material.PAPER) {
                return;
            }
            if (ChangedWorld.isPlayerWithLocation(player)) {
                // If player is in build world and they're setting location
                player.sendActionBar(MessageUtils.getPlayerLocaleComponent("environment.hints.location.build", player));
            }
            return;
        }
        Block block = player.getTargetBlockExact(5);
        if (block != null && block.getBlockData() instanceof WallSign) {
            Block farBlock = block.getRelative(BlockFace.NORTH);
            ExecutorCategory executor = ExecutorCategory.getByMaterial(farBlock.getType());
            if (executor != null) {
                String hint = switch (executor) {
                    case ExecutorCategory.CYCLE -> "cycle";
                    case ExecutorCategory.METHOD -> "method";
                    case ExecutorCategory.FUNCTION -> "function";
                    default -> "event";
                };
                player.sendActionBar(MessageUtils.getPlayerLocaleComponent("environment.hints."+hint, player));
                return;
            }
            ActionCategory action = ActionCategory.getByMaterial(farBlock.getType());
            if (action != null) {
                String hint = switch (action) {
                    case ActionCategory.LAUNCH_FUNCTION_ACTION -> "launch-function";
                    case ActionCategory.LAUNCH_METHOD_ACTION -> "launch-method";
                    default -> {
                        if (action.isCondition()) yield "condition";
                        yield "action";
                    }
                };
                player.sendActionBar(MessageUtils.getPlayerLocaleComponent("environment.hints."+hint, player));
            }
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty()) return;
        ValueType type = ValueType.getByMaterial(item.getType());
        if (type == ValueType.TEXT && item.getType() != Material.BOOK) {
            if (item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION) {
                player.sendActionBar(MessageUtils.getPlayerLocaleComponent("environment.hints.potion", player));
            }
            return;
        }
        String hint = switch (type) {
            case TEXT, NUMBER, EVENT_VALUE, VECTOR, POTION, PARTICLE, VARIABLE, BOOLEAN ->
                    type.name().toLowerCase().replace("_", "-");
            case LOCATION -> "location.dev";
            default -> "";
        };
        if (!hint.isEmpty()) player.sendActionBar(MessageUtils.getPlayerLocaleComponent("environment.hints."+hint, player));
    }

    @Override
    public void init() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isEntityInLobby(player)) {
                        continue;
                    }
                    checkForHints(player);
                }
            }
        };
        runnable.runTaskTimer(OpenCreative.getPlugin(), 40L, 40L);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Default Hints";
    }
}
