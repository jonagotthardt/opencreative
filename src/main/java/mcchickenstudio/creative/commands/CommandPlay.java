/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.commands;

import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import mcchickenstudio.creative.coding.BlockParser;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.FileUtils;


import java.util.ArrayList;
import java.util.List;


import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;
import static mcchickenstudio.creative.commands.CommandAd.plugin;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.FileUtils.setPlotConfigParameter;
import static mcchickenstudio.creative.utils.MessageUtils.*;


public class CommandPlay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(((Player) sender).getPlayer());
            if (plot == null) {
                ((Player) sender).getPlayer().sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (getCooldown(((Player) sender).getPlayer(), CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                ((Player) sender).getPlayer().sendMessage(getLocaleMessage("cooldown").replace("%cooldown%",String.valueOf(getCooldown(((Player) sender).getPlayer(),CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(((Player) sender).getPlayer(),plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            // Проверка на владельца мира

            List<String> developers = new ArrayList<>();
            List<String> trustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED);
            List<String> notTrustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED);

            developers.addAll(notTrustedDevelopers);
            developers.addAll(trustedDevelopers);

            if (plot.plotMode != Plot.Mode.PLAYING) {
                if (plot.getOwner().equals(sender.getName()) || developers.contains(sender.getName())) {
                    plot.plotMode = Plot.Mode.PLAYING;
                    for (Player p : plot.getPlayers()) {
                        if (PlotManager.getInstance().getDevPlot(p) == null || sender.getName().equals(p.getName())) {
                            clearPlayer(p);
                            plot.world.getSpawnLocation().getChunk().load(true);
                            p.teleport(plot.world.getSpawnLocation());
                            p.sendMessage(getLocaleMessage("world.play-mode.message." + (sender == p ? "owner" : "players")));
                        }
                    }
                    if (plot.script != null && plot.script.exists()) {
                        if (plot.devPlot.isLoaded) {
                            new BlockParser().parseCode(plot.devPlot);
                        } else {
                            plot.script.loadCode();
                        }
                    }
                    if (plot.getOwner().equalsIgnoreCase(sender.getName())) {
                        ItemStack worldSettingsItem = new ItemStack(Material.COMPASS,1);
                        ItemMeta worldSettingsItemMeta = worldSettingsItem.getItemMeta();
                        worldSettingsItemMeta.setDisplayName(getLocaleItemName("items.developer.world-settings.name"));
                        worldSettingsItemMeta.setLore(getLocaleItemDescription("items.developer.world-settings.lore"));
                        worldSettingsItem.setItemMeta(worldSettingsItemMeta);
                        ((Player) sender).getInventory().setItem(8,worldSettingsItem);
                    }
                    for (Player p : plot.getPlayers()) {
                        if (PlotManager.getInstance().getDevPlot(p) == null || sender.getName().equals(p.getName())) {
                            EventRaiser.raiseQuitEvent(p);
                            EventRaiser.raiseJoinEvent(p);
                        }
                    }
                    setPlotConfigParameter(plot,"mode",plot.plotMode);
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", ((Player) sender).getPlayer()));
                }
            } else {
                plot.world.getSpawnLocation().getChunk().load(true);
                ((Player) sender).getPlayer().teleport(plot.world.getSpawnLocation());
                if (plot.isOwner(sender.getName()) || developers.contains(sender.getName())) {
                    clearPlayer(((Player) sender).getPlayer());
                    ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                    if (plot.isOwner(sender.getName())) {
                        ((Player) sender).getPlayer().getInventory().setItem(8,worldSettingsItem);
                    }
                    ((Player) sender).getPlayer().sendMessage(getLocaleMessage("world.play-mode.message.owner"));
                    if (plot.script != null && plot.script.exists()) {
                        if (plot.devPlot.isLoaded) {
                            new BlockParser().parseCode(plot.devPlot);
                        } else {
                            plot.script.loadCode();
                        }
                    }
                } else {
                    ((Player) sender).getPlayer().sendMessage(getLocaleMessage("world.play-mode.message.players"));
                }
                EventRaiser.raiseQuitEvent((Player) sender);
                EventRaiser.raiseJoinEvent((Player) sender);
                EventRaiser.raisePlayEvent((Player) sender);
            }

        }
        return true;
    }
}
