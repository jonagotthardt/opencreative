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

package mcchickenstudio.creative.commands;

import mcchickenstudio.creative.menu.CreativeMenu;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.utils.CooldownUtils;
import mcchickenstudio.creative.utils.FileUtils;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.FileUtils.loadLocales;
import static mcchickenstudio.creative.utils.MessageUtils.getElapsedTime;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandCreative implements CommandExecutor {

    final Plugin plugin = Main.getPlugin();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Если аргументы команды есть

        if (args.length > 0) {
            // Если это игрок
            if (sender instanceof Player) {
                if (getCooldown(((Player) sender).getPlayer(), CooldownUtils.CooldownType.GENERIC_COMMAND) > 0) {
                    sender.sendMessage(getLocaleMessage("cooldown"));
                    return true;
                }
                setCooldown(((Player) sender).getPlayer(),plugin.getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
                // Если аргумент равен
                switch (args[0]) {
                    case ("reload"):
                        if (sender.hasPermission("creative.reload")) {
                            sender.sendMessage("§fCreative§b+ §8| §7Перезагрузка плагина и конфига...");
                            ((Player) sender).sendTitle("§f§lCREATIVE§b§l+", "§fПерезагрузка плагина и конфига...", 20, 60, 20);
                            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 100, 1);
                            plugin.reloadConfig();
                            loadLocales();
                            sender.sendMessage("§fCreative§b+ §8| §7Плагин и конфиг перезагружен §aуспешно");
                            ((Player) sender).sendTitle("§f§lCREATIVE§b§l+", "§fУспешно перезагружено!", 20, 60, 20);
                            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 100, 2);
                        }
                        break;
                    case ("resetlocale"):
                        if (sender.hasPermission("creative.resetlocale")) {
                            sender.sendMessage("§fCreative§b+ §8| §fСброс файла локализации...");
                            FileUtils.resetLocales();
                            sender.sendMessage("§fCreative§b+ §8| §fФайл локализации §6успешно§f сброшен.");
                            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 100, 1);
                        }
                        break;
                    case ("info"):
                        if (args.length < 2) return true;
                        Plot plot = PlotManager.getInstance().getPlotByWorldName("world" + args[1]);
                        if (plot == null) return true;
                        long now = System.currentTimeMillis();
                        sender.sendMessage(getLocaleMessage("world.info").replace("%name%",plot.plotName)
                                .replace("%id%", plot.worldID).replace("%creation-time%",getElapsedTime(now,plot.getCreationTime()))
                                .replace("%activity-time%",getElapsedTime(now,plot.getLastActivityTime())).replace("%online%",String.valueOf(plot.getOnline()))
                                .replace("%builders%",plot.getBuilders()).replace("%coders%",plot.getDevelopers()).replace("%owner%",plot.getOwner())
                                .replace("%sharing%",plot.plotSharing.getName()).replace("%mode%",plot.plotMode.getName()).replace("%description%", plot.plotDescription));
                        break;
                }
            // Если это консоль
            } else {
                if (args[0].equals("reload")) {
                    Main.getPlugin().getLogger().info("Creative+ is reloading...");
                    plugin.reloadConfig();
                    loadLocales();
                    Main.getPlugin().getLogger().info("Creative+ is reloaded!");
                }
            }
        // Если аргументов команды нет
        } else {
            try {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.version").replace("%version%", Main.version).replace("%codename%",Main.codename)));
                if (sender instanceof Player) {
                    new CreativeMenu().open((Player) sender);
                }
            } catch (NullPointerException e) {
                sender.sendMessage("§fCreative§b+ %version%§f: %codename%".replace("%version%",Main.version).replace("%codename%",Main.codename));
            }
        }
        return true;
    }
}
