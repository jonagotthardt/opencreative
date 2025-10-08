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

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.commands.CommandHandler;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.*;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.clearPlayer;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>DevCommand</h1>
 * This command is responsible for connecting player to
 * developers world, where he can create a code with
 * coding blocks and items.
 * <p>
 * Available: For world developers.
 */
public class DevCommand extends CommandHandler {

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(getLocaleMessage("only-players"));
            return;
        }

        if (!checkAndSetCooldownWithMessage(player, CooldownUtils.CooldownType.GENERIC_COMMAND)) return;

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) {
            player.sendMessage(getLocaleMessage("only-in-world"));
            return;
        }

        if (!OpenCreative.getSettings().isEnabledCoding()) {
            player.sendMessage(getLocaleMessage("world.dev-mode.disabled"));
            return;
        }

        if (args.length == 0 || args.length == 3) {
            if (planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().isDeveloperGuest(player)) {
                if (!planet.getWorldPlayers().isTrustedDeveloper(player)) {
                    Player planetOwner = Bukkit.getPlayer(planet.getOwner());
                    if (planetOwner == null) {
                        sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                        return;
                    }
                    Planet ownerPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(planetOwner);
                    if (!planet.equals(ownerPlanet)) {
                        sender.sendMessage(getLocaleMessage("world.dev-mode.cant-dev-when-offline"));
                        return;
                    }
                }
                new QuitEvent(player).callEvent();
                PlayerInventory playerInventory = player.getInventory();
                ItemStack[] playerInventoryItems = (OpenCreative.getPlanetsManager().getDevPlanet(player) == null ?  playerInventory.getContents() : new ItemStack[]{});
                clearPlayer(player);
                sender.sendMessage(getPlayerLocaleMessage("world.dev-mode.help", player));
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
                }
                ItemStack worldSettingsItem = createItem(Material.COMPASS,1,"items.developer.world-settings");
                if (planet.isOwner(player)) {
                    player.getInventory().setItem(8, worldSettingsItem);
                }
                giveItems(player);
                for (ItemStack item : playerInventoryItems) {
                    if (item != null && !itemEquals(item,worldSettingsItem) && !player.getInventory().containsAtLeast(item,1)) {
                        player.getInventory().addItem(item);
                    }
                }
            } else {
                sender.sendMessage(getPlayerLocaleMessage("not-owner", player));
            }
        } else {
            if (!planet.isOwner(sender.getName())) {
                sender.sendMessage(getLocaleMessage("not-owner"));
                return;
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
                return;
            }
            /*
             * Checks if player's name contains in not trusted
             * or trusted developers.
             */
            if (planet.getWorldPlayers().getDevelopersNotTrusted().contains(nickname)) {
                planet.getWorldPlayers().addDeveloper(nickname,true);
                sender.sendMessage(getLocaleMessage("world.players.developers.trusted").replace("%player%", nickname));
                return;
            }
            if (planet.getWorldPlayers().getDevelopersTrusted().contains(nickname)) {
                planet.getWorldPlayers().removeDeveloper(nickname);
                sender.sendMessage(getLocaleMessage("world.players.developers.removed").replace("%player%", nickname));
                return;
            }
            /*
             * Adds online player as not trusted developers, if he's not
             * listed in developers.
             */
            int limit = planet.getLimits().getDevelopersLimit();
            if (planet.getWorldPlayers().getAllDevelopers().size() > limit) {
                sender.sendMessage(getLocaleMessage("world.players.developers.limit").replace("%limit%",String.valueOf(limit)));
                return;
            }
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

    private void giveItems(Player player) {

        PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(0);

        setItemIfAbsent(inventory, 0,  ExecutorCategory.EVENT_PLAYER.getItem());
        setItemIfAbsent(inventory, 1,  ActionCategory.PLAYER_ACTION.getItem());
        setItemIfAbsent(inventory, 2,  ActionCategory.PLAYER_CONDITION.getItem());
        setItemIfAbsent(inventory, 3,  ActionCategory.ELSE_CONDITION.getItem());

        setItemIfAbsent(inventory, 9, ExecutorCategory.CYCLE.getItem());
        setItemIfAbsent(inventory, 10, ActionCategory.REPEAT_ACTION.getItem());
        setItemIfAbsent(inventory, 11, ActionCategory.CONTROLLER_ACTION.getItem());
        setItemIfAbsent(inventory, 12, ActionCategory.CONTROL_ACTION.getItem());
        setItemIfAbsent(inventory, 13, ActionCategory.VARIABLE_ACTION.getItem());
        setItemIfAbsent(inventory, 14, ActionCategory.VARIABLE_CONDITION.getItem());

        setItemIfAbsent(inventory, 18, ExecutorCategory.EVENT_WORLD.getItem());
        setItemIfAbsent(inventory, 19, ActionCategory.WORLD_CONDITION.getItem());
        setItemIfAbsent(inventory, 20, ActionCategory.WORLD_ACTION.getItem());
        setItemIfAbsent(inventory, 21, ExecutorCategory.METHOD.getItem());
        setItemIfAbsent(inventory, 22, ActionCategory.LAUNCH_METHOD_ACTION.getItem());
        setItemIfAbsent(inventory, 23, ActionCategory.SELECTION_ACTION.getItem());

        setItemIfAbsent(inventory, 27, ExecutorCategory.EVENT_ENTITY.getItem());
        setItemIfAbsent(inventory, 28, ActionCategory.ENTITY_CONDITION.getItem());
        setItemIfAbsent(inventory, 29, ActionCategory.ENTITY_ACTION.getItem());
        setItemIfAbsent(inventory, 30,  ExecutorCategory.FUNCTION.getItem());
        setItemIfAbsent(inventory, 31, ActionCategory.LAUNCH_FUNCTION_ACTION.getItem());

        ItemStack linesControllerItem = createItem(Material.COMPARATOR,1,"items.developer.lines-controller");
        setItemIfAbsent(inventory, 26, linesControllerItem);

        ItemStack arrowNotItem = createItem(Material.ARROW,1,"items.developer.arrow-not");
        setItemIfAbsent(inventory, 35, arrowNotItem);

        int slot = 8;
        if (inventory.getItem(8) != null) {
            slot = 7;
        }

        ItemStack bookHelperItem = createItem(Material.WRITTEN_BOOK,1,"items.developer.coding-book");
        BookMeta bookMeta = (BookMeta) bookHelperItem.getItemMeta();
        bookMeta.setTitle("Coding");
        bookMeta.setAuthor("OpenCreative+");
        bookMeta.setPages(getBookPages("items.developer.coding-book.pages"));
        bookHelperItem.setItemMeta(bookMeta);
        setItemIfAbsent(inventory, slot == 8 ? slot-1 : 17, bookHelperItem);

        ItemStack flySpeedChangerItem = createItem(Material.FEATHER,1,"items.developer.fly-speed-changer");
        setItemIfAbsent(inventory, slot == 8 ? 17 : 16, flySpeedChangerItem);

        setItemIfAbsent(inventory, slot, createItem(Material.IRON_INGOT,1,"items.developer.variables"));

    }

    private void setItemIfAbsent(@NotNull Inventory inventory, int slot, @NotNull ItemStack item) {
        if (!inventory.contains(item,1)) {
            inventory.setItem(slot, item);
        }
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return null;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
        if (planet == null) return null;
        if (planet.isOwner(player)) {
            List<String> list = new ArrayList<>(planet.getWorldPlayers().getAllDevelopers());
            for (Player planetPlayer : planet.getPlayers()) {
                if (planet.isOwner(planetPlayer) || list.contains(planetPlayer.getName())) continue;
                list.add(planetPlayer.getName());
            }
            return list.subList(0,Math.min(10,list.size()));
        }
        return null;
    }
}
