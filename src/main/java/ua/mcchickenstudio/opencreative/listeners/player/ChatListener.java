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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.ChatEvent;
import ua.mcchickenstudio.opencreative.events.player.WorldChatEvent;
import ua.mcchickenstudio.opencreative.coding.modules.Module;
import ua.mcchickenstudio.opencreative.coding.modules.ModuleSettingsMenu;
import ua.mcchickenstudio.opencreative.menus.world.browsers.WorldsBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.world.settings.PlayerControlMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.Planet;
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
import ua.mcchickenstudio.opencreative.settings.items.ItemsGroup;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import ua.mcchickenstudio.opencreative.utils.PlayerConfirmation;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ColorUtils.parseRGB;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

public final class ChatListener implements Listener {

    public static final Map<Player, PlayerConfirmation> confirmation = new HashMap<>();

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        try {
            Player player = event.getPlayer();
            if (message.startsWith("!")) {
                if (event.isCancelled()) return;
                String creativeChatCommand;
                if (message.equals("!")) {
                    creativeChatCommand = "cc";
                } else {
                    creativeChatCommand = "cc " + message.replaceFirst("!", "");
                }
                Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> player.performCommand(creativeChatCommand));
                event.setCancelled(true);
                return;
            }
            checkDevItems(player, message, event);
            checkConfirmation(player,message);
            if (event.isCancelled()) return;
            event.setCancelled(true);
            if (getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT) > 0) {
                player.sendMessage(getLocaleMessage("world.chat-cooldown").replace("%cooldown%",String.valueOf(getCooldown(player, CooldownUtils.CooldownType.WORLD_CHAT))));
                return;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getChatCooldown(), CooldownUtils.CooldownType.WORLD_CHAT);

            String format = OpenCreative.getPlugin().getConfig().getString("messages.world-chat", "&7 %player%&8: &f%message%");
            Component formatted = toComponent(parsePAPI(player, format)
                    .replace("%player%", player.getName())
                    .replace("%message%", MiniMessage.miniMessage().escapeTags(message)));
            if (formatted.clickEvent() == null) formatted = formatted.clickEvent(ClickEvent.suggestCommand(message));

            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            WorldChatEvent creativeEvent = new WorldChatEvent(player, message, formatted ,player.getWorld(), planet);
            Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
                creativeEvent.callEvent();
                if (creativeEvent.isCancelled()) return;
                Component finalMessage = creativeEvent.getFormattedMessage();
                if (planet != null) {
                    DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
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
                        sendLocalChatForSpying(player, message, planet);
                        OpenCreative.getPlugin().getLogger().info("[WORLD-CHAT: " + planet.getId() + "dev] " + player.getName() + ": " + message);
                    } else {
                        // If player in build world
                        ChatEvent chatEvent = new ChatEvent(event.getPlayer(), message);
                        chatEvent.callEvent();
                        if (chatEvent.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }
                        if (planet.getPlayers().size() == 1 && !chatEvent.isHandledByCode() && OpenCreative.getSettings().isNotifyNoPlayersAround()) {
                            player.sendMessage(getPlayerLocaleComponent("chat-no-near-players", player)
                                    .clickEvent(ClickEvent.suggestCommand("!" + message)));
                        }
                        for (Player p : planet.getPlayers()) {
                            p.sendMessage(finalMessage);
                        }
                        sendLocalChatForSpying(player, message, planet);
                        OpenCreative.getPlugin().getLogger().info("[WORLD-CHAT: " + planet.getId() + "] " + player.getName() + ": " + message);
                    }
                } else {
                    if (player.getWorld().getPlayers().size() == 1 && OpenCreative.getSettings().isNotifyNoPlayersAround()) {
                        player.sendMessage(getPlayerLocaleComponent("chat-no-near-players", player)
                                .clickEvent(ClickEvent.suggestCommand("!" + message)));
                    }
                    for (Player p : player.getWorld().getPlayers()) {
                        p.sendMessage(finalMessage);
                    }
                    sendLocalChatForSpying(player, message, planet);
                    OpenCreative.getPlugin().getLogger().info("[WORLD-CHAT: " + player.getWorld().getName() + "] " + player.getName() + ": " + message);
                }
            },1L);
        } catch (Exception error) {
            event.setCancelled(true);
            sendPlayerErrorMessage(event.getPlayer(),"Can't handle chat message: " + message,error);
        }
    }

    private void checkDevItems(Player player, String message, AsyncChatEvent event) {
        if (isEntityInDevPlanet(player)) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.BOOK) {
                ItemMeta meta = itemInHand.getItemMeta();
                Component newName = LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(message.replace("%space%", " "));
                meta.displayName(newName);
                itemInHand.setItemMeta(meta);
                Sounds.DEV_TEXT_SET.play(player);
                setPersistentData(itemInHand,getCodingValueKey(),"TEXT");
                player.getInventory().setItemInMainHand(itemInHand);
                player.showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-variable")), meta.displayName(),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                ));
                player.swingMainHand();
                if (OpenCreative.getSettings().isCancelChatOnValueSet()) {
                    event.setCancelled(true);
                }
            } else if (itemInHand.getType() == Material.SLIME_BALL) {
                String numberString = ChatColor.stripColor(message);
                if (numberString.equalsIgnoreCase("p") || numberString.equalsIgnoreCase("pi")) {
                    numberString = "3.1415926";
                }
                Double number = parseTicks(numberString);
                if (number == null) {
                    player.showTitle(Title.title(
                        Component.empty(), toComponent(getLocaleMessage("world.dev-mode.set-variable-number-error")),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(750))
                    ));
                    return;
                }
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
                player.swingMainHand();
                if (OpenCreative.getSettings().isCancelChatOnValueSet()) {
                    event.setCancelled(true);
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
                player.swingMainHand();
                if (OpenCreative.getSettings().isCancelChatOnValueSet()) {
                    event.setCancelled(true);
                }
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
                player.swingMainHand();
                if (OpenCreative.getSettings().isCancelChatOnValueSet()) {
                    event.setCancelled(true);
                }
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
                        duration = ((Double) parseTicks(potionDataList[0], 0)).intValue();
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
                if (OpenCreative.getSettings().isCancelChatOnValueSet()) {
                    event.setCancelled(true);
                }
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
                player.swingMainHand();
                if (OpenCreative.getSettings().isCancelChatOnValueSet()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private void checkConfirmation(Player player, String input) {
        if (confirmation.isEmpty()) return;
        if (!confirmation.containsKey(player)) return;
        PlayerConfirmation confirm = confirmation.get(player);
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
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
                planet.getInformation().updateIconAsync();
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
                for (Planet searchablePlanet : OpenCreative.getPlanetsManager().getPlanets()) {
                    if (searchablePlanet.getInformation().getCustomID().equalsIgnoreCase(input)) {
                        player.sendMessage(getLocaleMessage("settings.world-id.taken"));
                        return;
                    }
                }
                planet.getInformation().setCustomID(input);
                player.sendMessage(getLocaleMessage("settings.world-id.changed").replace("%id%", input));
                planet.getInformation().updateIconAsync();
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
                planet.getInformation().updateIconAsync();
            }
            case FIND_PLANETS_BY_NAME -> {
                Set<Planet> foundPlanetsByName = OpenCreative.getPlanetsManager().getPlanetsContainingName(input);
                if (!foundPlanetsByName.isEmpty()) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> new WorldsBrowserMenu(player, foundPlanetsByName).open(player));
                } else {
                    player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                }
            }
            case FIND_PLANETS_BY_ID -> {
                Set<Planet> foundPlanetsByID = OpenCreative.getPlanetsManager().getPlanetsContainingID(input);
                if (!foundPlanetsByID.isEmpty()) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> new WorldsBrowserMenu(player, foundPlanetsByID).open(player));
                } else {
                    player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                }
            }
            case FIND_PLANETS_BY_OWNER -> {
                Set<Planet> foundPlanets = OpenCreative.getPlanetsManager().getPlanetsByOwner(input);
                if (!foundPlanets.isEmpty()) {
                    Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () ->new WorldsBrowserMenu(player, foundPlanets).open(player));
                } else {
                    player.sendMessage(getLocaleMessage("menus.all-worlds.items.search.not-found"));
                }
            }
            case TRANSFER_OWNERSHIP -> {
                String newOwner = PlayerControlMenu.getConfirmationNewOwner(player);
                if (planet != null && newOwner != null && planet.isOwner(player)) {
                    if (input.equals(String.valueOf(planet.getId()))) {
                        Player newOwnerPlayer = Bukkit.getPlayerExact(newOwner);
                        if (newOwnerPlayer == null) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", newOwner));
                            return;
                        }
                        if (!planet.getPlayers().contains(newOwnerPlayer)) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.offline").replace("%player%", newOwner));
                            return;
                        }
                        if (OpenCreative.getPlanetsManager().getPlanetsByOwner(newOwnerPlayer).size() >= OpenCreative.getSettings().getGroups().getGroup(newOwnerPlayer).getWorldsLimit()) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%", newOwner));
                            return;
                        }
                        planet.setChangingOwner(true);
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.awaiting").replace("%player%", newOwner));
                        newOwnerPlayer.sendMessage(getLocaleMessage("world.players.transfer-ownership.confirm-new")
                                .replace("%player%", player.getName()).replace("%id%", String.valueOf(planet.getId())));
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
                        if (OpenCreative.getPlanetsManager().getPlanetsByOwner(player).size() >= OpenCreative.getSettings().getGroups().getGroup(player).getWorldsLimit()) {
                            player.sendMessage(getLocaleMessage("world.players.transfer-ownership.limit").replace("%player%", player.getName()));
                            return;
                        }
                        oldOwner.sendMessage(getLocaleMessage("world.players.transfer-ownership.transferred-old").replace("%player%", player.getName()));
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.transferred-new"));
                        planet.setChangingOwner(false);
                        Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                            planet.setOwner(player.getName());
                            planet.getWorldPlayers().removeBuilder(player.getName());
                            planet.getWorldPlayers().removeDeveloper(player.getName());

                            if (planet.getMode() == Planet.Mode.BUILD) {
                                ItemsGroup.BUILD_OWNER.setItems(player);
                                ItemsGroup.BUILD_OWNER.removeItems(oldOwner);
                            } else {
                                ItemsGroup.PLAY_OWNER.setItems(player);
                                ItemsGroup.PLAY_OWNER.removeItems(oldOwner);
                            }

                            oldOwner.setGameMode(GameMode.ADVENTURE);
                        });
                    } else {
                        if (oldOwner != null)
                            oldOwner.sendMessage(getLocaleMessage("world.players.transfer-ownership.cancelled"));
                        player.sendMessage(getLocaleMessage("world.players.transfer-ownership.wrong-id"));
                        planet.setChangingOwner(false);
                    }
                }
            }
            case MODULE_NAME_CHANGE -> {
                Module module = ModuleSettingsMenu.getCurrentEditingModule(player);
                ModuleSettingsMenu.removeFromCurrentEditing(player);
                if (module == null || !module.isOwner(player)) return;
                String newName = "§f" + ChatColor.translateAlternateColorCodes('&',input);
                String uncoloredName = ChatColor.stripColor(newName);
                if (uncoloredName.length() > OpenCreative.getSettings().getModuleNameMaxLength() || uncoloredName.length() < OpenCreative.getSettings().getModuleNameMinLength()) {
                    player.sendMessage(getLocaleMessage("settings.module-name.error")
                            .replace("%min%",String.valueOf(OpenCreative.getSettings().getModuleNameMinLength()))
                            .replace("%max%",String.valueOf(OpenCreative.getSettings().getModuleNameMaxLength())));
                    return;
                }
                module.getInformation().setDisplayName(newName);
                player.sendMessage(getLocaleMessage("settings.module-name.changed").replace("%name%",newName));
            }
            case MODULE_DESCRIPTION_CHANGE -> {
                Module module = ModuleSettingsMenu.getCurrentEditingModule(player);
                ModuleSettingsMenu.removeFromCurrentEditing(player);
                if (module == null || !module.isOwner(player)) return;
                String newDescription = "§f" + ChatColor.translateAlternateColorCodes('&',input);
                String uncoloredDescription = ChatColor.stripColor(newDescription);
                if (uncoloredDescription.length() > OpenCreative.getSettings().getModuleDescriptionMaxLength() ||
                        uncoloredDescription.length() < OpenCreative.getSettings().getModuleDescriptionMinLength()) {
                    player.sendMessage(getLocaleMessage("settings.module-description.error")
                            .replace("%min%",String.valueOf(OpenCreative.getSettings().getModuleDescriptionMinLength()))
                            .replace("%max%",String.valueOf(OpenCreative.getSettings().getModuleDescriptionMaxLength())));
                    return;
                }
                newDescription = String.join("\\n", splitDescription(newDescription, 39));
                module.getInformation().setDescription(newDescription);
                player.sendMessage(getLocaleMessage("settings.module-description.changed").replace("%description%", newDescription));
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

    private static void sendLocalChatForSpying(@NotNull Player player, @NotNull String message, @Nullable Planet planet) {
        Set<Player> playersWithEnabledSpying = getPlayersWithEnabledSpying();
        if (playersWithEnabledSpying.isEmpty()) return;
        String worldName = player.getWorld().getName();
        if (planet != null) {
            if (isEntityInDevPlanet(player)) {
                worldName = planet.getId() + "dev";
            } else {
                worldName = String.valueOf(planet.getId());
            }
        }
        if (isEntityInLobby(player)) {
            worldName = "Lobby";
        }
        String format = OpenCreative.getPlugin().getConfig().getString("messages.world-chat-spy", "&8 (%world%) &7%player%&8: &f%message%");
        Component formatted = toComponent(parsePAPI(player, format)
                .replace("%world%", worldName)
                .replace("%player%", player.getName())
                .replace("%message%", MiniMessage.miniMessage().escapeTags(message)));
        if (formatted.clickEvent() == null) formatted = formatted.clickEvent(ClickEvent.suggestCommand(message));
        for (Player spy : playersWithEnabledSpying) {
            if (spy.getWorld().equals(player.getWorld())) continue;
            Planet spyPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(spy);
            if (spyPlanet != null && spyPlanet.equals(planet)) continue;
            spy.sendMessage(formatted);
        }
    }
}
