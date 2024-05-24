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

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import static mcchickenstudio.creative.utils.MessageUtils.*;

public class CommandDev implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(((Player) sender).getPlayer());
            if (plot == null) {
                ((Player) sender).getPlayer().sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (getCooldown(((Player) sender).getPlayer(), CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                ((Player) sender).getPlayer().sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(((Player) sender).getPlayer(), CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(((Player) sender).getPlayer(), plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);

            List<String> developers = new ArrayList<>();
            List<String> trustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_TRUSTED);
            List<String> notTrustedDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_NOT_TRUSTED);
            List<String> guestsDevelopers = FileUtils.getPlayersFromPlotConfig(plot, Plot.PlayersType.DEVELOPERS_GUESTS);

            developers.addAll(notTrustedDevelopers);
            developers.addAll(trustedDevelopers);
            developers.addAll(guestsDevelopers);

            if (args.length == 0 || args.length == 3) {
                // Проверка на владельца мира
                if (plot.owner.equalsIgnoreCase(sender.getName()) || developers.contains(sender.getName())) {
                    Player plotOwner = Bukkit.getPlayer(plot.owner);
                    if (notTrustedDevelopers.contains(sender.getName())) {
                        if (plotOwner == null) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                        Plot ownerPlot = PlotManager.getInstance().getPlotByPlayer(plotOwner);
                        if (!(ownerPlot == plot)) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                    }
                    clearPlayer(((Player) sender).getPlayer());
                    sender.sendMessage(getLocaleMessage("world.dev-mode.help", ((Player) sender).getPlayer()));

                    if (args.length == 3) {
                        try {
                            double x = Double.parseDouble(args[0]);
                            double y = Double.parseDouble(args[1]);
                            double z = Double.parseDouble(args[2]);
                            plot.teleportToDevPlot(((Player) sender).getPlayer(),x,y,z);
                        } catch (Exception ignored) {
                            plot.teleportToDevPlot(((Player) sender).getPlayer());
                        }
                    } else {
                        plot.teleportToDevPlot(((Player) sender).getPlayer());
                    }

                    if (guestsDevelopers.contains(sender.getName())) {
                        ((Player) sender).setGameMode(GameMode.ADVENTURE);
                    } else {
                        ((Player) sender).setGameMode(GameMode.CREATIVE);
                    }
                    giveItems(((Player) sender).getPlayer());
                    ((Player) sender).sendTitle(getLocaleMessage("world.dev-mode.title"), getLocaleMessage("world.dev-mode.subtitle"));
                    ((Player) sender).playSound(((Player) sender).getLocation(), Sound.valueOf("BLOCK_BEACON_POWER_SELECT"), 100, 1.3f);

                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", ((Player) sender).getPlayer()));
                }
            } else {
                if (!plot.owner.equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                if (plot.owner.equalsIgnoreCase(args[0])) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                if (notTrustedDevelopers.contains(args[0])) {
                    plot.setBuilderTrusted(args[0], true);
                    sender.sendMessage(getLocaleMessage("world.players.developers.trusted").replace("%player%", args[0]));
                } else if (trustedDevelopers.contains(args[0])) {
                    plot.removeBuilder(args[0]);
                    sender.sendMessage(getLocaleMessage("world.players.developers.removed").replace("%player%", args[0]));
                } else {
                    Player addedPlayer = Bukkit.getPlayer(args[0]);
                    if (addedPlayer != null && addedPlayer != ((Player) sender).getPlayer()) {
                        Plot plot1 = PlotManager.getInstance().getPlotByPlayer(addedPlayer);
                        if (plot == plot1) {
                            sender.sendMessage(getLocaleMessage("world.players.developers.added").replace("%player%", addedPlayer.getName()));
                            plot.setDeveloperTrusted(addedPlayer.getName(), false);
                        } else {
                            sender.sendMessage(getLocaleMessage("no-player-found"));
                        }
                    } else {
                        sender.sendMessage(getLocaleMessage("no-player-found"));
                    }
                }
            }
        }
        return true;
    }

    private void giveItems(Player player) {
        ItemStack eventPlayerItem = createItem(Material.DIAMOND_BLOCK,1,"items.developer.event-player");
        player.getInventory().setItem(0, eventPlayerItem);

        ItemStack actionPlayerItem = createItem(Material.COBBLESTONE,1,"items.developer.action-player");
        player.getInventory().setItem(1, actionPlayerItem);

        ItemStack conditionPlayerItem = createItem(Material.OAK_PLANKS,1,"items.developer.condition-player");
        player.getInventory().setItem(2, conditionPlayerItem);

        ItemStack flySpeedChangerItem = createItem(Material.FEATHER,1,"items.developer.fly-speed-changer");
        player.getInventory().setItem(5, flySpeedChangerItem);

        /*ItemStack actionVarItem = new ItemStack(Material.IRON_BLOCK,1);
        ItemMeta actionVarItemMeta = actionVarItem.getItemMeta();
        actionVarItemMeta.setDisplayName(getLocaleItemName("items.developer.action-var.name"));
        actionVarItemMeta.setLore(getLocaleItemDescription("items.developer.action-var.lore"));
        actionVarItem.setItemMeta(actionVarItemMeta);
        player.getInventory().setItem(2,actionVarItem);*/

        player.getInventory().setItem(7, createItem(Material.IRON_INGOT,1,"items.developer.variables"));

       /* ItemStack varTextItem = new ItemStack(Material.BOOK, 64);
        ItemMeta varTextItemMeta = varTextItem.getItemMeta();
        varTextItemMeta.setDisplayName(getLocaleItemName("items.developer.text.name"));
        varTextItemMeta.setLore(getLocaleItemDescription("items.developer.text.lore"));
        varTextItem.setItemMeta(varTextItemMeta);
        player.getInventory().setItem(6, varTextItem);

        ItemStack varNumberItem = new ItemStack(Material.SLIME_BALL,64);
        ItemMeta varNumberItemMeta = varNumberItem.getItemMeta();
        varNumberItemMeta.setDisplayName(getLocaleItemName("items.developer.number.name"));
        varNumberItemMeta.setLore(getLocaleItemDescription("items.developer.number.lore"));
        varNumberItem.setItemMeta(varNumberItemMeta);
        player.getInventory().setItem(7,varNumberItem);*/

        ItemStack worldSettingsItem = new ItemStack(Material.COMPASS, 1);
        ItemMeta worldSettingsItemMeta = worldSettingsItem.getItemMeta();
        worldSettingsItemMeta.setDisplayName(getLocaleItemName("items.developer.world-settings.name"));
        worldSettingsItemMeta.setLore(getLocaleItemDescription("items.developer.world-settings.lore"));
        worldSettingsItem.setItemMeta(worldSettingsItemMeta);
        player.getInventory().setItem(8, worldSettingsItem);
    }
}
