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

package mcchickenstudio.creative.events.player;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.coding.blocks.events.EventRaiser;
import mcchickenstudio.creative.menu.world.browsers.WorldsBrowserMenu;
import mcchickenstudio.creative.menu.world.settings.WorldSettingsPlayersMenu;
import mcchickenstudio.creative.plots.DevPlot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


import static mcchickenstudio.creative.utils.ColorUtils.parseRGB;
import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.parsePAPI;

public class PlayerChat implements Listener {

    public static final Map<Player, String> confirmation = new HashMap<>();

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().startsWith("!")) {
            if (event.isCancelled()) return;
            player.performCommand("cc " + event.getMessage().replaceFirst("!",""));
            event.setCancelled(true);
            return;
        } else {
            EventRaiser.raiseChatEvent(event.getPlayer(), event);
        }
        checkDevItems(player,event.getMessage());
        checkConfirmation(player,event.getMessage());
        if (event.isCancelled()) return;
        event.setCancelled(true);
        if (getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT) > 0) {
            player.sendMessage(getLocaleMessage("world.chat-cooldown").replace("%cooldown%",String.valueOf(getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT))));
        } else {
            setCooldown(player,Main.getPlugin().getConfig().getInt("cooldowns.world-chat"), CooldownUtils.CooldownType.WORLD_CHAT);
            String message = ChatColor.translateAlternateColorCodes('&',parsePAPI(player,Main.getPlugin().getConfig().getString("messages.world-chat")).replace("%player%",player.getName()).replace("%message%",event.getMessage()));
            Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
            if (plot != null) {
                DevPlot devPlot = PlotManager.getInstance().getDevPlot(player);
                if (devPlot != null) {
                    for (Player p : plot.getPlayers()) {
                        if (plot.getWorldPlayers().canDevelop(p)) {
                            p.sendMessage(message);
                        }
                    }
                } else {
                    for (Player p : plot.getPlayers()) {
                        p.sendMessage(message);
                    }
                }
            } else {
                for (Player p : player.getWorld().getPlayers()) {
                    p.sendMessage(message);
                }
            }

            Main.getPlugin().getLogger().info("[WORLD-CHAT: "+player.getWorld().getName()+"] "+player.getName()+": "+event.getMessage());
        }

    }

    private void checkDevItems(Player player, String message) {
        if (player.getWorld().getName().contains("dev")) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.BOOK) {
                ItemMeta meta = itemInHand.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',message.replace("%space%", " ").replace("%empty%","").replace("&&","§")));
                itemInHand.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.4f);
                setPersistentData(itemInHand,getCodingValueKey(),"TEXT");
                player.setItemInHand(itemInHand);
                player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),ChatColor.translateAlternateColorCodes('&',meta.getDisplayName()));
            } else if (itemInHand.getType() == Material.SLIME_BALL) {
                String numberString = ChatColor.stripColor(message);
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
                StringBuilder newValue = new StringBuilder(ChatColor.stripColor(message));
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
            } else if (itemInHand.getType() == Material.BLACK_DYE) {
                int[] rgbColor = parseRGB(message);
                int red = rgbColor[0];
                int green = rgbColor[1];
                int blue = rgbColor[2];
                ItemMeta meta = itemInHand.getItemMeta();
                if (meta != null) {
                    meta.displayName(Component.text(red + " " + green + " " + blue).color(TextColor.color(red,green,blue)));
                    itemInHand.setItemMeta(meta);
                }
                setPersistentData(itemInHand,getCodingValueKey(),"COLOR");
                player.sendTitle(getLocaleMessage("world.dev-mode.set-variable"),meta.getDisplayName());
                player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,100,1.4f);
                player.getInventory().setItemInMainHand(itemInHand);
            }
        }
    }

    private void checkConfirmation(Player player, String message) {
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (confirmation.isEmpty()) return;
        if (confirmation.containsKey(player)) {
            player.clearTitle();
            switch(confirmation.get(player)) {
                case "title":
                    String newName = "§f" + ChatColor.translateAlternateColorCodes('&',message);
                    if (player.getName().equals(plot.getOwner())) {
                        if (!(ChatColor.stripColor(newName).length() > 30) && (ChatColor.stripColor(newName).length() > 4)) {
                            plot.getInformation().setDisplayName(newName);
                            player.sendMessage(getLocaleMessage("settings.world-name.changed").replace("%name%",newName));
                            Plot finalPlot = plot;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    finalPlot.getInformation().updateIcon();
                                }
                            }.runTaskAsynchronously(Main.getPlugin());
                        } else {
                            player.sendMessage(getLocaleMessage("settings.world-name.error"));
                        }
                    }
                    break;
                case "id":
                    plot = PlotManager.getInstance().getPlotByPlayer(player);
                    String newID = message;
                    if (player.getName().equals(plot.getOwner())) {
                        String pattern = "^[a-zA-Zа-яА-Я0-9]+$";
                        if (newID.length() > 2 && newID.length() < 16 && newID.matches(pattern) && !Character.isDigit(newID.charAt(0))) {
                            boolean existsID = false;
                            for (Plot searchablePlot : PlotManager.getInstance().getPlots()) {
                                if (searchablePlot.getInformation().getCustomID().equalsIgnoreCase(newID)) {
                                    existsID = true;
                                    break;
                                }
                            }
                            if (!existsID) {
                                plot.getInformation().setCustomID(newID);
                                player.sendMessage(getLocaleMessage("settings.world-id.changed").replace("%id%",newID));
                                Plot finalPlot1 = plot;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        finalPlot1.getInformation().updateIcon();
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
                    String newDescription = ChatColor.translateAlternateColorCodes('&',message);
                    if (player.getName().equals(plot.getOwner())) {
                        if (!(ChatColor.stripColor(newDescription).length() > 256) && (ChatColor.stripColor(newDescription).length() > 4))  {
                            newDescription = String.join("\\n",splitDescription(newDescription,39));
                            plot.getInformation().setDescription(newDescription);
                            player.sendMessage(getLocaleMessage("settings.world-description.changed").replace("%description%",newDescription));
                            Plot finalPlot2 = plot;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    finalPlot2.getInformation().updateIcon();
                                }
                            }.runTaskAsynchronously(Main.getPlugin());
                        } else {
                            player.sendMessage(getLocaleMessage("settings.world-description.error"));
                        }
                    }
                    break;
                case "searchPlotByPlotName":
                    List<Plot> foundPlotsByName = PlotManager.getInstance().getPlotsByPlotName(message);
                    if (!foundPlotsByName.isEmpty()) {
                        new WorldsBrowserMenu(player,foundPlotsByName).open(player);
                    } else {
                        player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                    }
                    break;
                case "searchPlotByID":
                    List<Plot> foundPlotsByID = PlotManager.getInstance().getPlotsByID(message);
                    if (!foundPlotsByID.isEmpty()) {
                        new WorldsBrowserMenu(player,foundPlotsByID).open(player);
                    } else {
                        player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                    }
                    break;
                case "transfer-ownership":
                    plot = PlotManager.getInstance().getPlotByPlayer(player);
                    if (plot != null && WorldSettingsPlayersMenu.playersSelected.get(player) != null && plot.getOwner().equalsIgnoreCase(player.getName())) {
                        if (message.equals(plot.worldID)) {
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
                        if (message.equals(plot.worldID)) {
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
                            plot.getWorldPlayers().removeBuilder(player.getName());
                            plot.getWorldPlayers().removeDeveloper(player.getName());
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
