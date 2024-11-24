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

package mcchickenstudio.creative.commands.world.modes;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.PlayerUtils.clearPlayer;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.*;

public class CommandDev implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player player) {
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (args.length == 0 || args.length == 3) {
                if (plot.getWorldPlayers().canDevelop(player) || plot.getWorldPlayers().isDeveloperGuest(player)) {
                    if (!plot.getWorldPlayers().isTrustedDeveloper(player)) {
                        Player plotOwner = Bukkit.getPlayer(plot.getOwner());
                        if (plotOwner == null) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                        Plot ownerPlot = PlotManager.getInstance().getPlotByPlayer(plotOwner);
                        if (!plot.equals(ownerPlot)) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                    }
                    EventRaiser.raiseQuitEvent(player);
                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack[] playerInventoryItems = (PlotManager.getInstance().getDevPlot(player) == null ?  playerInventory.getContents() : new ItemStack[]{});
                    clearPlayer(player);
                    sender.sendMessage(getLocaleMessage("world.dev-mode.help", player));
                    if (args.length == 3) {
                        try {
                            double x = Double.parseDouble(args[0]);
                            double y = Double.parseDouble(args[1]);
                            double z = Double.parseDouble(args[2]);
                            plot.connectToDevPlot(player,x,y,z);
                        } catch (Exception error) {
                            plot.connectToDevPlot(player);
                        }
                    } else {
                        plot.connectToDevPlot(player);
                    }
                    if (plot.getWorldPlayers().isDeveloperGuest(player)) {
                        player.setGameMode(GameMode.ADVENTURE);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                    } else {
                        player.setGameMode(GameMode.CREATIVE);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                    }
                    ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                    if (plot.isOwner(player)) {
                        player.getInventory().setItem(8, worldSettingsItem);
                    }
                    giveItems(player);
                    for (ItemStack item : playerInventoryItems) {
                        if (item != null && !itemEquals(item,worldSettingsItem)) {
                            player.getInventory().addItem(item);
                        }
                    }
                    player.showTitle(Title.title(
                            toComponent(getLocaleMessage("world.dev-mode.title")), toComponent(getLocaleMessage("world.dev-mode.subtitle")),
                            Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(2), Duration.ofMillis(750))
                    ));
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 100, 1.3f);
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 100, 0.5f);
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", player));
                }
            } else {
                if (!plot.isOwner(sender.getName())) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                String nickname = args[0];
                Player onlinePlayer = Bukkit.getPlayer(nickname);
                if (!plot.getWorldPlayers().getAllDevelopers().contains(nickname)) {
                    if (onlinePlayer != null) {
                        nickname = onlinePlayer.getName();
                    }
                }
                if (plot.isOwner(nickname)) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                /*
                 * Checks if player's name contains in not trusted
                 * or trusted developers.
                 */
                if (plot.getWorldPlayers().getDevelopersNotTrusted().contains(nickname)) {
                    plot.getWorldPlayers().addDeveloper(nickname,true);
                    sender.sendMessage(getLocaleMessage("world.players.developers.trusted").replace("%player%", nickname));
                    return true;
                }
                if (plot.getWorldPlayers().getDevelopersTrusted().contains(nickname)) {
                    plot.getWorldPlayers().removeDeveloper(nickname);
                    sender.sendMessage(getLocaleMessage("world.players.developers.removed").replace("%player%", nickname));
                    return true;
                }
                /*
                 * Adds online player as not trusted developers, if he's not
                 * listed in developers.
                 */
                if (onlinePlayer != null) {
                    Plot playerPlot = PlotManager.getInstance().getPlotByPlayer(onlinePlayer);
                    if (plot.equals(playerPlot)) {
                        sender.sendMessage(getLocaleMessage("world.players.developers.added").replace("%player%", onlinePlayer.getName()));
                        plot.getWorldPlayers().addDeveloper(onlinePlayer.getName(),false);
                    } else {
                        sender.sendMessage(getLocaleMessage("no-player-found"));
                    }
                } else {
                    sender.sendMessage(getLocaleMessage("no-player-found"));
                }
            }
        }
        return true;
    }

    private void giveItems(Player player) {

        player.getInventory().setHeldItemSlot(0);

        ItemStack eventPlayerItem = createItem(Material.DIAMOND_BLOCK,1,"items.developer.event-player");
        player.getInventory().setItem(0, eventPlayerItem);

        ItemStack actionPlayerItem = createItem(Material.COBBLESTONE,1,"items.developer.action-player");
        player.getInventory().setItem(1, actionPlayerItem);

        ItemStack conditionPlayerItem = createItem(Material.OAK_PLANKS,1,"items.developer.condition-player");
        player.getInventory().setItem(2, conditionPlayerItem);

        ItemStack actionVar = createItem(Material.IRON_BLOCK,1,"items.developer.action-var");
        player.getInventory().setItem(13, actionVar);

        ItemStack conditionVarItem = createItem(Material.OBSIDIAN,1,"items.developer.condition-var");
        player.getInventory().setItem(22, conditionVarItem);

        ItemStack functionItem = createItem(Material.LAPIS_BLOCK,1,"items.developer.function");
        player.getInventory().setItem(9, functionItem);

        ItemStack launchFunction = createItem(Material.LAPIS_ORE,1,"items.developer.launch-function");
        player.getInventory().setItem(10, launchFunction);


        ItemStack actionControl = createItem(Material.COAL_BLOCK,1,"items.developer.action-control");
        player.getInventory().setItem(11, actionControl);

        ItemStack conditionEntity = createItem(Material.BRICKS,1,"items.developer.condition-entity");
        player.getInventory().setItem(12, conditionEntity);

        ItemStack actionWorld = createItem(Material.NETHER_BRICKS,1,"items.developer.action-world");
        player.getInventory().setItem(20, actionWorld);

        ItemStack actionEntity = createItem(Material.MOSSY_COBBLESTONE,1,"items.developer.action-entity");
        player.getInventory().setItem(21, actionEntity);

        ItemStack eventEntityItem = createItem(Material.GOLD_BLOCK,1,"items.developer.event-entity");
        player.getInventory().setItem(27, eventEntityItem);

        ItemStack worldConditionItem = createItem(Material.RED_NETHER_BRICKS,1,"items.developer.condition-world");
        player.getInventory().setItem(28, worldConditionItem);

        ItemStack eventWorldItem = createItem(Material.REDSTONE_BLOCK,1,"items.developer.event-world");
        player.getInventory().setItem(29, eventWorldItem);

        ItemStack cycleItem = createItem(Material.OXIDIZED_COPPER,1,"items.developer.cycle");
        player.getInventory().setItem(18, cycleItem);

        ItemStack selectionItem = createItem(Material.PURPUR_BLOCK,1,"items.developer.action-selection");
        player.getInventory().setItem(19, selectionItem);

        ItemStack linesControllerItem = createItem(Material.COMPARATOR,1,"items.developer.lines-controller");
        player.getInventory().setItem(26, linesControllerItem);

        ItemStack arrowNotItem = createItem(Material.ARROW,1,"items.developer.arrow-not");
        player.getInventory().setItem(35, arrowNotItem);

        int slot = 8;
        if (player.getInventory().getItem(8) != null) {
            slot = 7;
        }

        ItemStack bookHelperItem = createItem(Material.WRITTEN_BOOK,1,"items.developer.coding-book");
        BookMeta bookMeta = (BookMeta) bookHelperItem.getItemMeta();
        bookMeta.setTitle("Coding");
        bookMeta.setAuthor("OpenCreative+");
        bookMeta.setPages(getBookPages("items.developer.coding-book.pages"));
        bookHelperItem.setItemMeta(bookMeta);
        player.getInventory().setItem(slot == 8 ? slot-1 : 17, bookHelperItem);

        ItemStack flySpeedChangerItem = createItem(Material.FEATHER,1,"items.developer.fly-speed-changer");
        player.getInventory().setItem(slot == 8 ? 17 : 16, flySpeedChangerItem);

        player.getInventory().setItem(slot, createItem(Material.IRON_INGOT,1,"items.developer.variables"));

    }
}
