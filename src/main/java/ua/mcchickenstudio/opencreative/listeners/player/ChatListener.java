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

package ua.mcchickenstudio.opencreative.listeners.player;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.EventRaiser;
import ua.mcchickenstudio.opencreative.events.player.WorldChatEvent;
import ua.mcchickenstudio.opencreative.menu.world.browsers.WorldsBrowserMenu;
import ua.mcchickenstudio.opencreative.menu.world.settings.WorldSettingsPlayersMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.utils.PlayerConfirmation;

import java.time.Duration;
import java.util.*;


import static ua.mcchickenstudio.opencreative.utils.ColorUtils.parseRGB;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInDevPlanet;

public final class ChatListener implements Listener {

    public static final Map<Player, PlayerConfirmation> confirmation = new HashMap<>();

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String message = LegacyComponentSerializer.legacyAmpersand().serialize(event.message());
        try {
            Player player = event.getPlayer();
            if (message.startsWith("!")) {
                if (event.isCancelled()) return;
                String creativeChatCommand = "cc " + message.replaceFirst("!","");
                Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> player.performCommand(creativeChatCommand));
                event.setCancelled(true);
                return;
            }
            checkDevItems(player,message);
            checkConfirmation(player,message);
            if (event.isCancelled()) return;
            event.setCancelled(true);
            if (getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT) > 0) {
                player.sendMessage(getLocaleMessage("world.chat-cooldown").replace("%cooldown%",String.valueOf(getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT))));
                return;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getChatCooldown(), CooldownUtils.CooldownType.WORLD_CHAT);
            String formatted = ChatColor.translateAlternateColorCodes('&',parsePAPI(player, OpenCreative.getPlugin().getConfig().getString("messages.world-chat")).replace("%player%",player.getName()).replace("%message%",message));
            Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
            WorldChatEvent creativeEvent = new WorldChatEvent(player, message,formatted,player.getWorld(), planet);
            Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                creativeEvent.callEvent();
                if (creativeEvent.isCancelled()) return;
                String finalMessage = formatted;
                // фикс это
                 // фикс отгрузку дев планет
                if (planet != null) {
                    DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(player);
                    if (devPlanet != null) {
                        // If player in dev world
                        for (Player p : devPlanet.getWorld().getPlayers()) {
                            p.sendMessage(finalMessage);
                        }
                        for (Player p : planet.getTerritory().getWorld().getPlayers()) {
                            if (planet.getWorldPlayers().canDevelop(p)) {
                                p.sendMessage(finalMessage);
                            }
                        }
                        OpenCreative.getPlugin().getLogger().info("[WORLD-CHAT: " + planet.getId() + "dev] " + player.getName() + ": " + message);
                    } else {
                        // If player in build world
                        if (!EventRaiser.raiseChatEvent(event.getPlayer(), message)) {
                            event.setCancelled(true);
                            return;
                        }
                        for (Player p : planet.getPlayers()) {
                            p.sendMessage(finalMessage);
                        }
                        OpenCreative.getPlugin().getLogger().info("[WORLD-CHAT: " + planet.getId() + "] " + player.getName() + ": " + message);
                    }
                } else {
                    for (Player p : player.getWorld().getPlayers()) {
                        p.sendMessage(finalMessage);
                    }
                    OpenCreative.getPlugin().getLogger().info("[WORLD-CHAT: " + player.getWorld().getName() + "] " + player.getName() + ": " + message);
                }
            },1L);
        } catch (Exception error) {
            event.setCancelled(true);
            sendPlayerErrorMessage(event.getPlayer(),"Can't handle chat message: " + message,error);
        }
    }

    private void checkDevItems(Player player, String message) {
        if (isEntityInDevPlanet(player)) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.BOOK) {
                ItemMeta meta = itemInHand.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',message.replace("%space%", " ").replace("%empty%","").replace("&&","§")));
                itemInHand.setItemMeta(meta);
                Sounds.DEV_TEXT_SET.play(player);
                setPersistentData(itemInHand,getCodingValueKey(),"TEXT");
                player.setItemInHand(itemInHand);
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-variable")), meta.displayName(),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
            } else if (itemInHand.getType() == Material.SLIME_BALL) {
                String numberString = ChatColor.stripColor(message);
                if (numberString.equalsIgnoreCase("p") || numberString.equalsIgnoreCase("pi")) {
                    numberString = "3.1415926";
                }
                try {
                    double number = parseTicks(numberString);
                    ItemMeta meta = itemInHand.getItemMeta();
                    meta.setDisplayName("§a" + number);
                    itemInHand.setItemMeta(meta);
                    setPersistentData(itemInHand,getCodingValueKey(),"NUMBER");
                    Sounds.DEV_NUMBER_SET.play(player);
                    player.setItemInHand(itemInHand);
                    player.showTitle(Title.title(
                            toComponent(getLocaleMessage("world.dev-mode.set-variable")), meta.displayName(),
                            Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                    ));
                } catch (NumberFormatException exception) {
                    player.showTitle(Title.title(
                            Component.empty(), toComponent(getLocaleMessage("world.dev-mode.set-variable-number-error")),
                            Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                    ));
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
                Sounds.DEV_VARIABLE_SET.play(player);
                player.getInventory().setItemInMainHand(itemInHand);
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-variable")), meta.displayName(),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
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
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-variable")), meta.displayName(),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
                Sounds.DEV_VALUE_SET.play(player);
                player.getInventory().setItemInMainHand(itemInHand);
            } else if (itemInHand.getType() == Material.POTION || itemInHand.getType() == Material.LINGERING_POTION || itemInHand.getType() == Material.SPLASH_POTION) {
                if (!(itemInHand.getItemMeta() instanceof PotionMeta oldMeta)) {
                    return;
                }
                List<PotionEffect> effects = new ArrayList<>();
                if (oldMeta.hasCustomEffects()) {
                    effects.addAll(oldMeta.getCustomEffects());
                    oldMeta.clearCustomEffects();
                }
                if (oldMeta.getBasePotionType() != null) {
                    effects.addAll(oldMeta.getBasePotionType().getPotionEffects());
                }
                if (effects.isEmpty()) {
                    return;
                }
                message = ChatColor.stripColor(message);
                int amplifier = 1;
                int duration = 1200;
                int effectNumber = 1;
                String[] potionDataList = new String[3];
                if (message.contains(", ")) {
                    potionDataList = message.split(", ");
                } else if (message.contains(" ")) {
                    potionDataList = message.split(" ");
                } else {
                    potionDataList[0] = message;
                }
                if (potionDataList.length >= 1) {
                    try {
                        duration = ((Double) parseTicks(potionDataList[0])).intValue();
                    } catch (NumberFormatException ignored) {}
                }
                if (potionDataList.length >= 2) {
                    try {
                        amplifier = Integer.parseInt(potionDataList[1]);
                    } catch (NumberFormatException ignored) {}
                }
                if (potionDataList.length >= 3) {
                    try {
                        effectNumber = Integer.parseInt(potionDataList[2]);
                    } catch (NumberFormatException ignored) {}
                }
                if (effectNumber < 1) effectNumber = 1;
                PotionEffect effect = effects.get(effectNumber > effects.size() ? 0 : effectNumber-1);
                PotionMeta newMeta = (PotionMeta) new ItemStack(Material.POTION,1).getItemMeta();
                for (PotionEffect oldEffect : effects) {
                    newMeta.addCustomEffect(oldEffect,true);
                }
                newMeta.addCustomEffect(new PotionEffect(effect.getType(),duration,amplifier-1),true);
                player.showTitle(Title.title(
                        Component.empty(), toComponent(getLocaleMessage("world.dev-mode.set-potion").replace("%duration%", convertTime(duration * 50L)).replace("%amplifier%",""+amplifier)),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(2), Duration.ofMillis(500))
                ));
                Sounds.DEV_POTION_SET.play(player);
                itemInHand.setItemMeta(newMeta);
            } else if (itemInHand.getType() == Material.PRISMARINE_SHARD) {
                ItemMeta meta = itemInHand.getItemMeta();
                if (meta == null) return;
                double x = 0;
                double y = 0;
                double z = 0;
                message = ChatColor.stripColor(message);
                String[] coordinates;
                if (message.contains(", ")) {
                    coordinates = message.split(", ");
                } else if (message.contains(" ")) {
                    coordinates = message.split(" ");
                } else {
                    coordinates = new String[]{message};
                }
                try {
                    x = Double.parseDouble(coordinates[0]);
                } catch (NumberFormatException ignored) {}
                if (coordinates.length >= 2) {
                    try {
                        y = Double.parseDouble(coordinates[1]);
                    } catch (NumberFormatException ignored) {}
                }
                if (coordinates.length >= 3) {
                    try {
                        z = Double.parseDouble(coordinates[2]);
                    } catch (NumberFormatException ignored) {}
                }
                meta.setDisplayName("§b" + x + " " + y + " " + z);
                itemInHand.setItemMeta(meta);
                setPersistentData(itemInHand,getCodingValueKey(),"VECTOR");
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-variable")), meta.displayName(),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(2), Duration.ofMillis(500))
                ));
                Sounds.DEV_VECTOR_SET.play(player);
            }
        }
    }

    private void checkConfirmation(Player player, String input) {
        if (confirmation.isEmpty()) return;
        if (!confirmation.containsKey(player)) return;
        PlayerConfirmation confirm = confirmation.get(player);
        Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
        player.clearTitle();
        confirmation.remove(player);
        switch(confirm) {
            case WORLD_NAME_CHANGE -> {
                if (planet == null || !planet.isOwner(player)) return;
                String newName = "§f" + ChatColor.translateAlternateColorCodes('&',input);
                String uncoloredName = ChatColor.stripColor(newName);
                if (uncoloredName.length() > OpenCreative.getSettings().getWorldNameMaxLength() || uncoloredName.length() < OpenCreative.getSettings().getWorldNameMinLength()) {
                    player.sendMessage(getLocaleMessage("settings.world-name.error")
                            .replace("%min%",String.valueOf(OpenCreative.getSettings().getWorldNameMinLength()))
                            .replace("%max%",String.valueOf(OpenCreative.getSettings().getWorldNameMaxLength())));
                    return;
                }
                planet.getInformation().setDisplayName(newName);
                player.sendMessage(getLocaleMessage("settings.world-name.changed").replace("%name%",newName));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        planet.getInformation().updateIcon();
                    }
                }.runTaskAsynchronously(OpenCreative.getPlugin());
            }
            case WORLD_CUSTOM_ID_CHANGE -> {
                if (planet == null || !planet.isOwner(player)) return;
                String pattern = OpenCreative.getSettings().getCustomIdPattern();
                if (input.length() > OpenCreative.getSettings().getCustomIdMaxLength()
                        || input.length() < OpenCreative.getSettings().getCustomIdMinLength()
                        || Character.isDigit(input.charAt(0)) || !input.matches(pattern)) {
                    player.sendMessage(getLocaleMessage("settings.world-id.error")
                            .replace("%min%",String.valueOf(OpenCreative.getSettings().getCustomIdMinLength()))
                            .replace("%max%",String.valueOf(OpenCreative.getSettings().getCustomIdMaxLength())));
                    return;
                }
                for (Planet searchablePlanet : PlanetManager.getInstance().getPlanets()) {
                    if (searchablePlanet.getInformation().getCustomID().equalsIgnoreCase(input)) {
                        player.sendMessage(getLocaleMessage("settings.world-id.taken"));
                        return;
                    }
                }
                planet.getInformation().setCustomID(input);
                player.sendMessage(getLocaleMessage("settings.world-id.changed").replace("%id%", input));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        planet.getInformation().updateIcon();
                    }
                }.runTaskAsynchronously(OpenCreative.getPlugin());
            }
            case WORLD_DESCRIPTION_CHANGE -> {
                if (planet == null || !planet.isOwner(player)) return;
                String newDescription = "§f" + ChatColor.translateAlternateColorCodes('&',input);
                String uncoloredDescription = ChatColor.stripColor(newDescription);
                if (uncoloredDescription.length() > OpenCreative.getSettings().getWorldDescriptionMaxLength() ||
                        uncoloredDescription.length() < OpenCreative.getSettings().getWorldDescriptionMinLength()) {
                    player.sendMessage(getLocaleMessage("settings.world-description.error")
                            .replace("%min%",String.valueOf(OpenCreative.getSettings().getWorldDescriptionMinLength()))
                            .replace("%max%",String.valueOf(OpenCreative.getSettings().getWorldDescriptionMaxLength())));
                    return;
                }
                newDescription = String.join("\\n", splitDescription(newDescription, 39));
                planet.getInformation().setDescription(newDescription);
                player.sendMessage(getLocaleMessage("settings.world-description.changed").replace("%description%", newDescription));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        planet.getInformation().updateIcon();
                    }
                }.runTaskAsynchronously(OpenCreative.getPlugin());
            }
            case FIND_PLANETS_BY_NAME -> {
                Set<Planet> foundPlanetsByName = PlanetManager.getInstance().getPlanetsByPlanetName(input);
                if (!foundPlanetsByName.isEmpty()) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> new WorldsBrowserMenu(player, foundPlanetsByName).open(player));
                } else {
                    player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                }
            }
            case FIND_PLANETS_BY_ID -> {
                Set<Planet> foundPlanetsByID = PlanetManager.getInstance().getPlanetsByID(input);
                if (!foundPlanetsByID.isEmpty()) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> new WorldsBrowserMenu(player, foundPlanetsByID).open(player));
                } else {
                    player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                }
            }
            case FIND_PLANETS_BY_OWNER -> {
                Set<Planet> foundPlanets = PlanetManager.getInstance().getPlanetsByOwner(input);
                if (!foundPlanets.isEmpty()) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () ->new WorldsBrowserMenu(player, foundPlanets).open(player));
                } else {
                    player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                }
            }
            case TRANSFER_OWNERSHIP -> {
                if (planet != null && WorldSettingsPlayersMenu.playersSelected.get(player) != null && planet.isOwner(player)) {
                    if (input.equals(String.valueOf(planet.getId()))) {
                        String newOwner = WorldSettingsPlayersMenu.playersSelected.get(player);
                        Player newOwnerPlayer = Bukkit.getPlayer(newOwner);
                        if (newOwnerPlayer == null) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", newOwner));
                            return;
                        }
                        if (!planet.getPlayers().contains(newOwnerPlayer)) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", newOwner));
                            return;
                        }
                        if (PlanetManager.getInstance().getPlayerPlanets(newOwnerPlayer).size() >= OpenCreative.getSettings().getGroups().getGroup(newOwnerPlayer).getWorldsLimit()) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%", newOwner));
                            return;
                        }
                        planet.setChangingOwner(true);
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.awaiting").replace("%player%", newOwner));
                        newOwnerPlayer.sendMessage(getLocaleMessage("world.players.transfer-ownership.confirm-new").replace("%player%", player.getName()).replace("%id%", String.valueOf(planet.getId())));
                        confirmation.put(newOwnerPlayer, PlayerConfirmation.GET_OWNERSHIP);
                    } else {
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.wrong-id"));
                    }
                }
            }
            case GET_OWNERSHIP -> {
                if (planet != null) {
                    Player oldOwner = Bukkit.getPlayer(planet.getOwner());
                    if (input.equals(String.valueOf(planet.getId()))) {
                        if (oldOwner == null) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", player.getName()));
                            return;
                        }
                        if (!planet.getPlayers().contains(oldOwner)) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", player.getName()));
                            return;
                        }
                        if (PlanetManager.getInstance().getPlayerPlanets(player).size() >= OpenCreative.getSettings().getGroups().getGroup(player).getWorldsLimit()) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%", player.getName()));
                            return;
                        }
                        oldOwner.sendMessage(getLocaleMessage("world.players.transfer-ownership.transferred-old").replace("%player%", player.getName()));
                        oldOwner.setGameMode(GameMode.ADVENTURE);
                        planet.getWorldPlayers().removeBuilder(player.getName());
                        planet.getWorldPlayers().removeDeveloper(player.getName());
                        planet.setOwner(player.getName());
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.transferred-new"));
                        Sounds.WORLD_SETTINGS_OWNER_SET.play(player);
                        planet.setChangingOwner(false);
                    } else {
                        if (oldOwner != null)
                            oldOwner.sendMessage(getLocaleMessage("world.players.transfer-ownership.cancelled"));
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.wrong-id"));
                        planet.setChangingOwner(false);
                    }
                }
            }
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
