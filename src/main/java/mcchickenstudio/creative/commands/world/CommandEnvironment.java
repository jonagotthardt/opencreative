/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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

/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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

package mcchickenstudio.creative.commands.world;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.variables.WorldVariable;
import mcchickenstudio.creative.coding.variables.VariableLink;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandEnvironment implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (cooldown > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                return true;
            }
            setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (!plot.isDeveloper(player)) {
                player.sendMessage(getLocaleMessage("not-developer"));
                return true;
            }
            if (args.length == 0) {
                player.sendMessage(getLocaleMessage("environment.help"));
            } else {
                switch (args[0].toLowerCase()) {
                    case "vars", "variables":
                        if (args.length == 1) {
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("size")) {
                            player.sendMessage(getLocaleMessage("environment.variables.size").replace("%count%", String.valueOf(plot.getWorldVariables().getTotalVariablesAmount())));
                        } else if (args[1].equalsIgnoreCase("clear")) {
                            plot.getWorldVariables().clearVariables();
                            player.sendMessage(getLocaleMessage("environment.variables.cleared"));
                        } else if (args[1].equalsIgnoreCase("list")) {
                            int page = 0;
                            List<WorldVariable> allVariables = new ArrayList<>(plot.getWorldVariables().getSet());
                            if (allVariables.isEmpty()) {
                                player.sendMessage(getLocaleMessage("environment.variables.list.empty"));
                                return true;
                            }
                            if (args.length > 2) {
                                try {
                                    page = Integer.parseInt(args[2]) - 1;
                                    if (page < 0 || page * 20 > allVariables.size() || (page + 1) * 20 > allVariables.size()) {
                                        page = 0;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                            int current = Math.min(((page + 1) * 20), allVariables.size());
                            List<WorldVariable> variables = new ArrayList<>(allVariables.subList(page * 20, current));
                            player.playSound(player.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 100, 0.7f);
                            player.sendMessage(getLocaleMessage("environment.variables.list.header").replace("%current%", String.valueOf(current)).replace("%amount%", String.valueOf(variables.size())));
                            for (WorldVariable variable : variables) {
                                String name = variable.getName();
                                VariableLink.VariableType type = variable.getVarType();
                                String value = (variable.getValue() != null ? variable.getValue().toString() : "null");
                                if (name.length() > 40) {
                                    name = name.substring(0, 40) + "...";
                                }
                                if (value.length() > 40) {
                                    value = value.substring(0, 40) + "...";
                                }
                                player.sendMessage(getLocaleMessage("environment.variables.list.variable", false).replace("%name%", name).replace("%type%", type.getLocalized()).replace("%value%", value));
                            }
                            TextComponent navigation = Component.text(getLocaleMessage("environment.variables.list.navigation"));
                            if (page * 20 > 20) {
                                navigation = navigation.append(Component.text(getLocaleMessage("environment.variables.list.previous-page")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/environment variables list " + (page - 1))));
                            }
                            if (allVariables.size() > current + 1) {
                                navigation = navigation.append(Component.text(getLocaleMessage("environment.variables.list.next-page")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/environment variables list " + (page + 1))));
                            }
                            if (!Component.text(getLocaleMessage("environment.variables.list.navigation")).equals(navigation)) {
                                player.sendMessage(navigation);
                            }
                            player.sendMessage(" ");
                        }
                        break;
                    case "containers": {
                        if (!Main.debug) return true;
                        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
                        if (devPlot == null) {
                            player.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        for (byte y = 1; y < devPlot.getFloors() * 4; y = (byte) (y + 4)) {
                            for (byte z = 4; z < 96; z = (byte) (z + 4)) {
                                for (byte x = 6; x < 96; x = (byte) (x + 2)) {
                                    Block chestBlock = new Location(devPlot.world, x, y + 1, z).getBlock();
                                    if (chestBlock.getType() == Material.CHEST || chestBlock.getType() == Material.BARREL) {
                                        ItemStack[] data = ((InventoryHolder) chestBlock.getState()).getInventory().getContents();
                                        chestBlock.setType(chestBlock.getType() == Material.CHEST ? Material.BARREL : Material.CHEST);
                                        ((Container) chestBlock.getState()).getInventory().setContents(data);
                                        BlockData blockData = chestBlock.getBlockData();
                                        ((Directional) blockData).setFacing(BlockFace.SOUTH);
                                        chestBlock.setBlockData(blockData);
                                        chestBlock.getState().update();
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case "addfloor":
                        if (!Main.debug) return true;
                        DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
                        if (devPlot == null) {
                            player.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        byte y = 0;
                        for (byte z = 0; z < 100; z++) {
                            for (byte x = 0; x < 100; x++) {
                                Block copyBlock = new Location(devPlot.world,x,y,z).getBlock();
                                Block newBlock = new Location(devPlot.world,x,y+8,z).getBlock();
                                newBlock.setType(copyBlock.getType());
                            }
                        }
                        break;
                    case "debug": {
                        if (args.length == 1) {
                            player.sendMessage(getLocaleMessage("environment.debug.help"));
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("enable")) {
                            for (Player plotPlayer : plot.getPlayers()){
                                plotPlayer.sendMessage(getLocaleMessage("environment.debug.enabled",player));
                            }
                            player.playSound(player.getLocation(),Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM,100,1);
                            plot.setDebug(true);
                        } else if (args[1].equalsIgnoreCase("disable")) {
                            for (Player plotPlayer : plot.getPlayers()){
                                plotPlayer.sendMessage(getLocaleMessage("environment.debug.disabled",player));
                            }
                            player.playSound(player.getLocation(),Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM,100,1);
                            plot.setDebug(false);
                        }
                    }

                }
            }
        }
        return true;
    }
}
