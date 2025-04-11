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

package ua.mcchickenstudio.opencreative.commands.world.modes;

import ua.mcchickenstudio.opencreative.OpenCreative;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.clearPlayer;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.giveDevPermissions;

/**
 * <h1>CommandDev</h1>
 * This command is responsible for connecting player to
 * developers world, where he can create a code with
 * coding blocks and items.
 * <p>
 * Available: For world developers.
 */
public class CommandDev implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player player) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                player.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND))));
                return true;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (args.length == 0 || args.length == 3) {
                if (planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().isDeveloperGuest(player)) {
                    if (!planet.getWorldPlayers().isTrustedDeveloper(player)) {
                        Player planetOwner = Bukkit.getPlayer(planet.getOwner());
                        if (planetOwner == null) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                        Planet ownerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(planetOwner);
                        if (!planet.equals(ownerPlanet)) {
                            sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                            return true;
                        }
                    }
                    new QuitEvent(player).callEvent();
                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack[] playerInventoryItems = (OpenCreative.getPlanetsManager().getDevPlanet(player) == null ?  playerInventory.getContents() : new ItemStack[]{});
                    clearPlayer(player);
                    sender.sendMessage(getLocaleMessage("world.dev-mode.help", player));
                    if (args.length == 3) {
                        try {
                            double x = Double.parseDouble(args[0]);
                            double y = Double.parseDouble(args[1]);
                            double z = Double.parseDouble(args[2]);
                            planet.connectToDevPlanet(player,x,y,z);
                        } catch (Exception error) {
                            planet.connectToDevPlanet(player);
                        }
                    } else {
                        planet.connectToDevPlanet(player);
                    }
                    if (planet.getWorldPlayers().isDeveloperGuest(player)) {
                        player.setGameMode(GameMode.ADVENTURE);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                    } else {
                        player.setGameMode(GameMode.CREATIVE);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        giveDevPermissions(player);
                    }
                    ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                    if (planet.isOwner(player)) {
                        player.getInventory().setItem(8, worldSettingsItem);
                    }
                    giveItems(player);
                    for (ItemStack item : playerInventoryItems) {
                        if (item != null && !itemEquals(item,worldSettingsItem)) {
                            player.getInventory().addItem(item);
                        }
                    }
                } else {
                    sender.sendMessage(getLocaleMessage("not-owner", player));
                }
            } else {
                if (!planet.isOwner(sender.getName())) {
                    sender.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
                String nickname = args[0];
                Player onlinePlayer = Bukkit.getPlayer(nickname);
                if (!planet.getWorldPlayers().getAllDevelopers().contains(nickname)) {
                    if (onlinePlayer != null) {
                        nickname = onlinePlayer.getName();
                    }
                }
                if (planet.isOwner(nickname)) {
                    sender.sendMessage(getLocaleMessage("same-player"));
                    return true;
                }
                /*
                 * Checks if player's name contains in not trusted
                 * or trusted developers.
                 */
                if (planet.getWorldPlayers().getDevelopersNotTrusted().contains(nickname)) {
                    planet.getWorldPlayers().addDeveloper(nickname,true);
                    sender.sendMessage(getLocaleMessage("world.players.developers.trusted").replace("%player%", nickname));
                    return true;
                }
                if (planet.getWorldPlayers().getDevelopersTrusted().contains(nickname)) {
                    planet.getWorldPlayers().removeDeveloper(nickname);
                    sender.sendMessage(getLocaleMessage("world.players.developers.removed").replace("%player%", nickname));
                    return true;
                }
                /*
                 * Adds online player as not trusted developers, if he's not
                 * listed in developers.
                 */
                if (onlinePlayer != null) {
                    Planet playerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(onlinePlayer);
                    if (planet.equals(playerPlanet)) {
                        sender.sendMessage(getLocaleMessage("world.players.developers.added").replace("%player%", onlinePlayer.getName()));
                        planet.getWorldPlayers().addDeveloper(onlinePlayer.getName(),false);
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

        player.getInventory().setItem(0, ExecutorCategory.EVENT_PLAYER.getItem());
        player.getInventory().setItem(1, ActionCategory.PLAYER_ACTION.getItem());
        player.getInventory().setItem(2, ActionCategory.PLAYER_CONDITION.getItem());
        player.getInventory().setItem(3, ActionCategory.ELSE_CONDITION.getItem());
        player.getInventory().setItem(13, ActionCategory.VARIABLE_ACTION.getItem());
        player.getInventory().setItem(22, ActionCategory.VARIABLE_CONDITION.getItem());
        player.getInventory().setItem(9, ExecutorCategory.FUNCTION.getItem());
        player.getInventory().setItem(10, ActionCategory.LAUNCH_FUNCTION_ACTION.getItem());
        player.getInventory().setItem(11, ActionCategory.CONTROL_ACTION.getItem());
        player.getInventory().setItem(12, ActionCategory.ENTITY_CONDITION.getItem());
        player.getInventory().setItem(20, ActionCategory.WORLD_ACTION.getItem());
        player.getInventory().setItem(21, ActionCategory.ENTITY_ACTION.getItem());
        player.getInventory().setItem(27, ExecutorCategory.EVENT_ENTITY.getItem());
        player.getInventory().setItem(28, ActionCategory.WORLD_CONDITION.getItem());
        player.getInventory().setItem(29, ExecutorCategory.EVENT_WORLD.getItem());
        player.getInventory().setItem(30, ExecutorCategory.METHOD.getItem());
        player.getInventory().setItem(31, ActionCategory.LAUNCH_METHOD_ACTION.getItem());
        player.getInventory().setItem(18, ExecutorCategory.CYCLE.getItem());
        player.getInventory().setItem(19, ActionCategory.SELECTION_ACTION.getItem());

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
