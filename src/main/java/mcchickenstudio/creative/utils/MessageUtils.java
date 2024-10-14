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

package mcchickenstudio.creative.utils;

import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.utils.hooks.HookUtils;
import mcchickenstudio.creative.utils.hooks.PAPIUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import mcchickenstudio.creative.Main;

import java.io.File;
import java.util.*;

public class MessageUtils {


    @NotNull private static final Plugin plugin = Main.getPlugin();

    private static File localizationFile;
    private static FileConfiguration localizationConfig;

    public static String getStringFromComponent(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    /**
     Loads localization file (.yml) from Creative/locales/ folder. If localization file is not found, then it creates a new one.
     **/
    public static void loadLocalizationFile() {

        File localeFile = new File((plugin.getDataFolder() + File.separator + "locales" + File.separator), getLanguage() + ".yml");
        if (localeFile.exists()) {
            localizationFile = localeFile;
        } else {
            String defaultLanguage = getLanguage().equalsIgnoreCase("ru") ? "ru" : "en";
            plugin.getConfig().set("messages.locale",defaultLanguage);
            plugin.saveResource("locales" + File.separator + "en.yml",false);
            plugin.saveResource("locales" + File.separator + "ru.yml",false);
            plugin.reloadConfig();
            localeFile = new File((plugin.getDataFolder() + File.separator + "locales" + File.separator),  defaultLanguage + ".yml");
        }

        localizationConfig = YamlConfiguration.loadConfiguration(localeFile);
    }

    private static String getPrefix() {
        String prefix = plugin.getConfig().getString("messages.prefix");
        if (prefix == null || prefix.equalsIgnoreCase("null")) {
            prefix = ChatColor.translateAlternateColorCodes('&',"&6 Worlds &8| &f");
            plugin.getConfig().set("messages.prefix","&6 Worlds &8| &f");
            plugin.saveConfig();
            return prefix;
        } else {
            return ChatColor.translateAlternateColorCodes('&',prefix);
        }
    }

    private static String getCreativeChatPrefix() {
        String prefix = plugin.getConfig().getString("messages.cc-prefix");
        if (prefix == null || prefix.equalsIgnoreCase("null")) {
            prefix = ChatColor.translateAlternateColorCodes('&',"&6 Creative Chat &8| &7");
            plugin.getConfig().set("messages.cc-prefix","&6 Creative Chat &8| &7");
            plugin.reloadConfig();
            return prefix;
        } else {
            return ChatColor.translateAlternateColorCodes('&',prefix);
        }
    }

    private static String getBranding() {
        String prefix = plugin.getConfig().getString("messages.branding");
        if (prefix == null || prefix.equalsIgnoreCase("null")) {
            prefix = ChatColor.translateAlternateColorCodes('&',"&fOpen&7Creative&a+");
            plugin.getConfig().set("messages.branding","&fOpen&7Creative&a+");
            plugin.reloadConfig();
            return prefix;
        } else {
            return ChatColor.translateAlternateColorCodes('&',prefix);
        }
    }

    private static String getLanguage() {
        Object language = plugin.getConfig().get("messages.locale");
        if (language != null) {
            return String.valueOf(language);
        } else {
            Object defaultLanguage = "en";
            plugin.getConfig().set("messages.locale",defaultLanguage);
            plugin.saveResource("locales" + File.separator + "en.yml",false);
            plugin.saveResource("locales" + File.separator + "ru.yml",false);
            plugin.reloadConfig();
            return "en";
        }
    }

    private static File getLocalizationFile() {
        return localizationFile;
    }

    private static FileConfiguration getLocalization() {
        return localizationConfig;
        //return YamlConfiguration.loadConfiguration(getLocalizationFile());
    }

    /**
     Returns a path, that has specified message. May work wrong, if some messages will be same.
     **/
    public static String getPathFromMessage(String partOfPath, String message) {
        for (String line : getLocalization().getKeys(true)) {
            if (line.startsWith(partOfPath)) {
                if (getLocaleMessage(line).equalsIgnoreCase(message)) {
                    return line;
                }
            }
        }
        if (message.startsWith("§fNot found: ")) {
            return ChatColor.stripColor(message).replace("Not found: ","");
        }
        return null;
    }

    /**
     Returns message from localization file. If message is not found, then returns a detailed error message, that message is not found.
     **/
    public static String getLocaleMessage(String messageID) {
        String originalMessage = getLocalization().getString(messageID);
        if (originalMessage == null || originalMessage.equalsIgnoreCase("null")) {
            ErrorUtils.sendWarningErrorMessage("Not found " + messageID + " in localization file!");
            return "§6 Error §8| §fNot found §6" + messageID + "§f! Administration of server needs to fill that line in §6locales"+File.separator+getLanguage()+".yml";
        } else {
            return ChatColor.translateAlternateColorCodes('&',originalMessage.replace("%prefix%",getPrefix()).replace("%branding%",getBranding()).replace("%cc-prefix%",getCreativeChatPrefix()));
        }
    }

    /**
     Returns message from localization file, that parsed player's placeholders with PlaceholderAPI. If message is not found, then returns a detailed error message, that message is not found.
     **/
    public static String getLocaleMessage(String messageID, Player player) {
        String originalMessage = getLocalization().getString(messageID);
        if (originalMessage == null || originalMessage.equalsIgnoreCase("null")) {
            ErrorUtils.sendWarningErrorMessage("Not found " + messageID + " in localization file!");
            return "§6 Error §8| §fNot found §6" + messageID + "§f! Administration of server needs to fill that line in §6locales"+File.separator+getLanguage()+".yml";
        } else {
            return ChatColor.translateAlternateColorCodes('&',parsePAPI(Bukkit.getOfflinePlayer(player.getName()),originalMessage.replace("%prefix%",getPrefix()).replace("%branding%",getBranding()).replace("%cc-prefix%",getCreativeChatPrefix()).replace("%player%",player.getName())));
        }
    }

    /**
     Returns message from localization file. If message is not found and "returnDetailedError" is true, then returns a detailed error message, that message is not found, or else will return only path to message.
     **/
    public static String getLocaleMessage(String messageID, boolean returnDetailedError) {
        String originalMessage = getLocalization().getString(messageID);
        if (originalMessage == null || originalMessage.equalsIgnoreCase("null")) {
            ErrorUtils.sendWarningErrorMessage("Not found " + messageID + " in localization file!");
            if (returnDetailedError) {
                return "§6 Error §8| §fNot found §6" + messageID + "§f! Administration of server needs to fill that line in §6locales"+File.separator+getLanguage()+".yml";
            } else {
                return messageID;
            }
        } else {
            return ChatColor.translateAlternateColorCodes('&',originalMessage.replace("%prefix%",getPrefix()).replace("%branding%",getBranding()).replace("%cc-prefix%",getCreativeChatPrefix()));
        }
    }

    /**
     Returns item's name from localization file. If message is not found, then returns a detailed error message, that message is not found.
     **/
    public static String getLocaleItemName(String nameID) {
        String originalName = getLocalization().getString(nameID);
        if (originalName == null || originalName.equalsIgnoreCase("null")) {
            ErrorUtils.sendWarningErrorMessage("Not found item name " + nameID + " in localization file!");
            return "§fNot found: " + nameID;
        } else {
            if (originalName.length() > 50) originalName = originalName.substring(0,50);
            return ChatColor.translateAlternateColorCodes('&',originalName.replace("%prefix%",getPrefix()).replace("%cc-prefix%",getCreativeChatPrefix()).replace("%branding%",getBranding()));
        }
    }

    /**
     Returns item's description from localization file. If message is not found, then returns a detailed error message, that message is not found.
     **/
    public static List<String> getLocaleItemDescription(String descriptionID) {
        List<String> originalDescription = getLocalization().getStringList(descriptionID);
        List<String> parsedDescription = new ArrayList<>();
        if (originalDescription.isEmpty()) {
            ErrorUtils.sendWarningErrorMessage("Not found item description " + descriptionID);
            parsedDescription.add("§6Not found item description");
            parsedDescription.add("§6" + descriptionID);
            parsedDescription.add("§fPlease send this to server administration!");
            parsedDescription.add("§f They need to fill this line in ");
            parsedDescription.add("§f localization file: locales" + File.separator + getLanguage() + ".yml");
        } else {
            for (String descriptionLine : originalDescription) {
                parsedDescription.add(ChatColor.translateAlternateColorCodes('&',descriptionLine.replace("%prefix%",getPrefix()).replace("%cc-prefix%",getCreativeChatPrefix()).replace("%branding%",getBranding())));
            }
        }
        return parsedDescription;
    }

    /**
     Returns book's pages from localization file.
     **/
    public static List<String> getBookPages(String localizationID) {
        List<String> foundPages = getLocalization().getStringList(localizationID);
        List<String> pages = new ArrayList<>();
        if (foundPages.isEmpty()) {
            ErrorUtils.sendWarningErrorMessage("Not found book pages " + localizationID);
            pages.add("§4Not found pages: §0" + localizationID + " \nPlease report server administration, they need to fill this line in locales" + File.separator + getLanguage() + ".yml");
        } else {
            for (String page : foundPages) {
                pages.add(ChatColor.translateAlternateColorCodes('&',page.replace("%prefix%",getPrefix()).replace("%cc-prefix%",getCreativeChatPrefix())));
            }
        }
        return pages;
    }

    /**
     Returns elapsed time from old time to current with localized message. For example: if elapsed time is 2 seconds, it will return "2 sec ago".
     **/
    public static String getElapsedTime(long currentTime, long oldTime) {

        String elapsedTime = "";

        long elapsedTimeInSeconds = (currentTime - oldTime) / 1000;
        long elapsedTimeInMinutes = elapsedTimeInSeconds / 60;
        long elapsedTimeInHours = elapsedTimeInMinutes / 60;
        long elapsedTimeInDays = elapsedTimeInHours / 24;

        elapsedTimeInSeconds %= 60;
        elapsedTimeInMinutes %= 60;
        elapsedTimeInHours %= 24;

        if (elapsedTimeInDays > 0) elapsedTime = elapsedTime.concat(elapsedTimeInDays + " " + getLocaleMessage("time.days",false) + " ");
        if (elapsedTimeInHours > 0) elapsedTime = elapsedTime.concat(elapsedTimeInHours + " "+ getLocaleMessage("time.hours",false) +" ");
        if (elapsedTimeInMinutes > 0) elapsedTime = elapsedTime.concat(elapsedTimeInMinutes + " "+ getLocaleMessage("time.minutes",false) +" ");
        if (elapsedTimeInSeconds > 0) elapsedTime = elapsedTime.concat(elapsedTimeInSeconds + " "+ getLocaleMessage("time.seconds",false) +" ");
        if ((currentTime - oldTime) < 1000) elapsedTime = getLocaleMessage("time.less-second",false) + " ";

        return elapsedTime + getLocaleMessage("time.ago",false);

    }

    public static String convertTime(long currentTime) {

        String convertedTime = "";

        long seconds = currentTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        if (days > 0) convertedTime = convertedTime.concat(days + " " + getLocaleMessage("time.days",false) + " ");
        if (hours > 0) convertedTime = convertedTime.concat(hours + " "+ getLocaleMessage("time.hours",false) +" ");
        if (minutes > 0) convertedTime = convertedTime.concat(minutes + " "+ getLocaleMessage("time.minutes",false) +" ");
        if (seconds > 0) convertedTime = convertedTime.concat(seconds + " "+ getLocaleMessage("time.seconds",false));
        if (currentTime < 1000) convertedTime = getLocaleMessage("time.less-second",false);
        if (currentTime < 0) convertedTime = "∞";

        return convertedTime;

    }


    static final Map<Plot,Long> messagesOnce = new HashMap<>();
    /**
     Sends message to plot players once. If cool down is not ended, it will not send message.
     **/
    public static void sendMessageOnce(Plot plot, String message, int onceInSeconds) {

        long currentTime = System.currentTimeMillis();

        if (messagesOnce.get(plot) != null) {
            long timeInMap = messagesOnce.get(plot);
            long elapsedTime = currentTime-timeInMap;
            long elapsedSeconds = elapsedTime/1000;
            if (elapsedSeconds < onceInSeconds) return;
        }

        for (Player player : plot.getPlayers()) {
            player.sendMessage(message);
        }
        messagesOnce.put(plot,currentTime);

    }

    /**
     Sends TextComponent message to plot players once. If cool down is not ended, it will not send message.
     **/
    public static void sendMessageOnce(Plot plot, TextComponent message, int onceInSeconds) {

        long currentTime = System.currentTimeMillis();

        if (messagesOnce.get(plot) != null) {
            long timeInMap = messagesOnce.get(plot);
            long elapsedTime = currentTime-timeInMap;
            long elapsedSeconds = elapsedTime/1000;
            if (elapsedSeconds < onceInSeconds) return;
        }

        for (Player player : plot.getPlayers()) {
            player.sendMessage(message);
        }
        messagesOnce.put(plot,currentTime);

    }

    /**
     Returns string, that parsed plot lines: plot name, description, online, reputation, owner, id, category, uniques, last activity time, creation time.
     **/
    public static String parsePlotLines(Plot plot, String string) {
        String plotReputation = String.valueOf(plot.getPlotReputation());
        if (plot.getPlotReputation() >= 1) plotReputation = "§a+" + plotReputation;
        else if (plot.getPlotReputation() <= -1) plotReputation = "§c" + plotReputation;
        else plotReputation = "§e" + plotReputation;
        return parsePAPI(Bukkit.getOfflinePlayer(plot.getOwner()),string.replace("%plotName%", plot.getInformation().getDisplayName()).replace("%plotOnline%",String.valueOf(plot.getOnline())).replace("%plotOwner%", plot.getOwner()).replace("%plotID%",plot.worldID).replace("%plotCustomID%",plot.getInformation().getCustomID()).replace("%plotCategory%", plot.getInformation().getCategory().getName()).replace("%plotUniques%",String.valueOf(plot.getUniques())).replace("%plotReputation%",plotReputation).replace("%plotLastTime%",getElapsedTime(System.currentTimeMillis(),plot.getLastActivityTime())).replace("%plotCreationTime%",getElapsedTime(System.currentTimeMillis(), plot.getCreationTime())));
    }

    /**
     Returns string, that parsed player's placeholders if PlaceholderAPI is working.
     **/
    public static String parsePAPI(OfflinePlayer player, String string) {
        if (HookUtils.isPlaceholderAPIEnabled) {
            return PAPIUtils.parsePlaceholdersAPI(player,string);
        } else {
            return string;
        }
    }

    public static double parseTicks(String message) {
        double ticks = 0;
        if (message == null) return ticks;
        if (message.isEmpty()) return ticks;
        double modifier = switch (message.toLowerCase().charAt(message.length()-1)) {
            case 's' -> 20;
            case 'm' -> 1200;
            case 'h' -> 72000;
            case 'd' -> 1728000;
            default -> 1;
        };
        if (modifier != 1) {
            message = message.substring(0,message.length()-1);
        }
        try {
            ticks = Double.parseDouble(message);
        } catch (NumberFormatException ignored) {}
        return ticks * modifier;
    }

    public static boolean messageExists(String messageID) {
        String originalMessage = getLocalization().getString(messageID);
        return originalMessage != null && !originalMessage.equalsIgnoreCase("null");
    }
}
