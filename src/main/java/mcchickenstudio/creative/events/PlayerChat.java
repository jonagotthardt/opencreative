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

package mcchickenstudio.creative.events;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.menu.WorldSettingsPlayersMenu;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import mcchickenstudio.creative.menu.AllWorldsMenu;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.FileUtils.*;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.parsePAPI;

public class PlayerChat implements Listener {

    public static Map<Player, String> confirmation = new HashMap<>();
    final Plugin plugin = Main.getPlugin();

    @EventHandler
    public void onChat(PlayerChatEvent event) {

        Player player = event.getPlayer();

        Plot plot1 = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot1 != null) EventRaiser.raiseChatEvent(event.getPlayer(), event);
        if (event.getMessage().startsWith("!")) {
            event.setCancelled(true);
            player.performCommand("cc " + event.getMessage().replaceFirst("!",""));
            return;
        }

        if (getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT) > 0) {
            player.sendMessage(getLocaleMessage("world.chat-cooldown").replace("%cooldown%",String.valueOf(getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT))));
        } else {
            setCooldown(player,plugin.getConfig().getInt("cooldowns.world-chat"), CooldownUtils.CooldownType.WORLD_CHAT);
            if (event.isCancelled()) return;
            event.setCancelled(true);
            for (Player p : player.getWorld().getPlayers()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',parsePAPI(player,plugin.getConfig().getString("messages.world-chat")).replace("%player%",player.getName()).replace("%message%",event.getMessage())));
            }
            Main.getPlugin().getLogger().info("[WORLD-CHAT: "+player.getWorld().getName()+"] "+player.getName()+": "+event.getMessage());
        }

        event.setCancelled(true);
        if (player.getWorld().getName().contains("dev")) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.BOOK) {
                ItemMeta meta = itemInHand.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',event.getMessage().replace("%space%", " ").replace("%new-line%", "\n").replace("%empty%","").replace("&&","§")));
                itemInHand.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.4f);
                setPersistentData(itemInHand,getCodingValueKey(),"TEXT");
                player.setItemInHand(itemInHand);
                player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),ChatColor.translateAlternateColorCodes('&',meta.getDisplayName()));
            } else if (itemInHand.getType() == Material.SLIME_BALL) {
                String numberString = ChatColor.stripColor(event.getMessage());
                if (numberString.equalsIgnoreCase("p") || numberString.equalsIgnoreCase("pi")) {
                    numberString = "3.1415926";
                }
                try {
                    double number = Double.parseDouble(numberString);
                    ItemMeta meta = itemInHand.getItemMeta();
                    meta.setDisplayName("§c" + number);
                    itemInHand.setItemMeta(meta);
                    setPersistentData(itemInHand,getCodingValueKey(),"NUMBER");
                    player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.7f);
                    player.setItemInHand(itemInHand);
                    player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),meta.getDisplayName());
                } catch (NumberFormatException exception) {
                    player.sendTitle("",getLocaleMessage("world.dev-mode.set-variable-number-error"));
                }
            } else if (itemInHand.getType() == Material.MAGMA_CREAM) {
                StringBuilder newValue = new StringBuilder(ChatColor.stripColor(event.getMessage()));
                ItemMeta meta = itemInHand.getItemMeta();
                char insert = 'c';
                if (itemInHand.hasItemMeta()) {
                    String itemName = meta.getDisplayName();
                    if (itemName.length() >= 2) {
                        insert = itemName.charAt(1);
                    }
                }
                newValue.insert(0,ChatColor.translateAlternateColorCodes('&',"&" + insert));
                meta.setDisplayName(newValue.toString());
                itemInHand.setItemMeta(meta);
                setPersistentData(itemInHand,getCodingValueKey(),"VARIABLE");
                setPersistentData(itemInHand,getCodingVariableTypeKey(),insert == 'a' ? "SAVED" : insert == 'e' ? "GLOBAL" : "LOCAL");
                player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.4f);
                player.getInventory().setItemInMainHand(itemInHand);
                player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),ChatColor.translateAlternateColorCodes('&',meta.getDisplayName()));
            }
        }

        if (confirmation.isEmpty()) return;
        if (confirmation.containsKey(player)) {
            Plot plot;
            player.clearTitle();
            switch(confirmation.get(player)) {
                case "title":
                    plot = PlotManager.getInstance().getPlotByPlayer(player);
                    String newName = "§f" + ChatColor.translateAlternateColorCodes('&',event.getMessage());
                    if (player.getName().equals(plot.getOwner())) {
                        if (!(ChatColor.stripColor(newName).length() > 30) && (ChatColor.stripColor(newName).length() > 4)) {
                            plot.setPlotName(newName);
                            player.sendMessage(getLocaleMessage("settings.world-name.changed").replace("%name%",newName));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plot.updatePlotIcon();
                                }
                            }.runTaskAsynchronously(Main.getPlugin());
                        } else {
                            player.sendMessage(getLocaleMessage("settings.world-name.error"));
                        }
                    }
                    break;
                case "id":
                    plot = PlotManager.getInstance().getPlotByPlayer(player);
                    String newID = event.getMessage();
                    if (player.getName().equals(plot.getOwner())) {
                        String pattern = "^[a-zA-Zа-яА-Я0-9]+$";
                        if (newID.length() > 2 && newID.length() < 16 && newID.matches(pattern) && !Character.isDigit(newID.charAt(0))) {
                            boolean existsID = false;
                            for (Plot searchablePlot : PlotManager.getInstance().getPlots()) {
                                if (searchablePlot.getPlotCustomID().equalsIgnoreCase(newID)) {
                                    existsID = true;
                                    break;
                                }
                            }
                            if (!existsID) {
                                plot.setPlotCustomID(newID);
                                player.sendMessage(getLocaleMessage("settings.world-id.changed").replace("%id%",newID));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        plot.updatePlotIcon();
                                    }
                                }.runTaskAsynchronously(Main.getPlugin());
                            } else {
                                player.sendMessage(getLocaleMessage("settings.world-id.taken"));
                            }
                        } else {
                            player.sendMessage(getLocaleMessage("settings.world-id.error"));
                        }
                    }
                    break;
                case "description":
                    plot = PlotManager.getInstance().getPlotByPlayer(player);
                    String newDescription = ChatColor.translateAlternateColorCodes('&',event.getMessage());
                    if (player.getName().equals(plot.getOwner())) {
                        if (!(ChatColor.stripColor(newDescription).length() > 256) && (ChatColor.stripColor(newDescription).length() > 4))  {
                            newDescription = String.join("\\n",splitDescription(newDescription,39));
                            plot.setPlotDescription(newDescription);
                            player.sendMessage(getLocaleMessage("settings.world-description.changed").replace("%description%",newDescription));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plot.updatePlotIcon();
                                }
                            }.runTaskAsynchronously(Main.getPlugin());
                        } else {
                            player.sendMessage(getLocaleMessage("settings.world-description.error"));
                        }
                    }
                    break;
                case "searchPlotByPlotName":
                    List<Plot> foundPlotsByName = PlotManager.getInstance().getPlotsByPlotName(event.getMessage());
                    if (!foundPlotsByName.isEmpty()) {
                        AllWorldsMenu.openInventory(player,1,foundPlotsByName);
                    } else {
                        player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                    }
                    break;
                case "searchPlotByID":
                    List<Plot> foundPlotsByID = PlotManager.getInstance().getPlotsByID(event.getMessage());
                    if (!foundPlotsByID.isEmpty()) {
                        AllWorldsMenu.openInventory(player,1,foundPlotsByID);
                    } else {
                        player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                    }
                    break;
                case "transfer-ownership":
                    plot = PlotManager.getInstance().getPlotByPlayer(player);
                    if (plot != null && WorldSettingsPlayersMenu.playersSelected.get(player) != null && plot.getOwner().equalsIgnoreCase(player.getName())) {
                        if (event.getMessage().equals(plot.worldID)) {
                            String newOwner = WorldSettingsPlayersMenu.playersSelected.get(player);
                            Player newOwnerPlayer = Bukkit.getPlayer(newOwner);
                            if (newOwnerPlayer == null) {
                                player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%",newOwner));
                                return;
                            }
                            if (!plot.getPlayers().contains(newOwnerPlayer)) {
                                player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%",newOwner));
                                return;
                            }
                            if (PlotManager.getInstance().getPlayerPlots(newOwnerPlayer).size() >= PlayerUtils.getPlayerPlotsLimit(newOwnerPlayer)) {
                                player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%",newOwner));
                                return;
                            }
                            plot.currentlyTransferringOwnership = true;
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.awaiting").replace("%player%",newOwner));
                            newOwnerPlayer.sendMessage(getLocaleMessage("world.players.transfer-ownership.confirm-new").replace("%player%",player.getName()).replace("%id%",plot.worldID));
                            confirmation.put(newOwnerPlayer,"get-ownership");
                        } else {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.wrong-id"));
                        }
                    }
                    break;
                case "get-ownership":
                    plot = PlotManager.getInstance().getPlotByPlayer(player);
                    if (plot != null) {
                        Player oldOwner = Bukkit.getPlayer(plot.getOwner());
                        if (event.getMessage().equals(plot.worldID)) {
                            if (oldOwner == null) {
                                player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%",player.getName()));
                                return;
                            }
                            if (!plot.getPlayers().contains(oldOwner)) {
                                player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%",player.getName()));
                                return;
                            }
                            if (PlotManager.getInstance().getPlayerPlots(player).size() >= PlayerUtils.getPlayerPlotsLimit(player)) {
                                player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%",player.getName()));
                                return;
                            }
                            oldOwner.sendMessage(getLocaleMessage("world.players.transfer-ownership.transferred-old").replace("%player%",player.getName()));
                            oldOwner.setGameMode(GameMode.ADVENTURE);
                            plot.removeBuilder(player.getName());
                            plot.removeDeveloper(player.getName());
                            plot.setOwner(player.getName());
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.transferred-new"));
                            player.playSound(player.getLocation(),Sound.UI_TOAST_CHALLENGE_COMPLETE,100,1.5f);
                            plot.currentlyTransferringOwnership = false;
                        } else {
                            if (oldOwner != null) oldOwner.sendMessage(getLocaleMessage("world.players.transfer-ownership.cancelled"));
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.wrong-id"));
                            plot.currentlyTransferringOwnership = false;
                        }
                    }
            }
            confirmation.remove(player);
        }
    }

    private static List<String> splitDescription(String input, int maxLength) {

        List<String> setDescriptionWords = new ArrayList<>();

        if (input.contains("\\n")) {
            String[] newDescriptionWords = input.split("\\n");
            setDescriptionWords.addAll(Arrays.asList(newDescriptionWords));
        } else {
            String[] newDescriptionWords = input.split("\\s+");

            int currentSize = 0;
            StringBuilder newLine = new StringBuilder();

            if (newDescriptionWords.length > 1) {
                for (String word : newDescriptionWords) {

                    if (currentSize + word.length() > maxLength) {

                        if (word.length() > maxLength) {

                            String newStr = newLine.toString().replaceAll("(.{" + maxLength + "}[^\\n])", "$1\\\\n");
                            String[] newStrings = newStr.split("\\\\n");

                            setDescriptionWords.addAll(Arrays.asList(newStrings));

                        } else {
                            setDescriptionWords.add(newLine.toString().trim());
                        }

                        newLine = new StringBuilder();
                        currentSize = 0;

                    }
                    currentSize += word.length();
                    newLine.append(word).append(" ");
                }
            } else {

                input = input.replaceAll("(.{"+maxLength+"})", "$1\\\\n");

                String[] newStrings = input.split("\\\\n");
                setDescriptionWords.addAll(Arrays.asList(newStrings));
            }

            setDescriptionWords.add(newLine.toString().trim());
        }
        return setDescriptionWords;
    }
}
