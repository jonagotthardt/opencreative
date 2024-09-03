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

package mcchickenstudio.creative.commands.minecraft;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.CooldownUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mcchickenstudio.creative.utils.CooldownUtils.getCooldown;
import static mcchickenstudio.creative.utils.CooldownUtils.setCooldown;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class CommandStopSound implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getServer().dispatchCommand(sender,"minecraft:stopsound " + String.join(" ",args));
        } else {
            int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (cooldown > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                return true;
            }
            setCooldown(player, Main.getPlugin().getConfig().getInt("cooldowns.generic-command"), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (!player.hasPermission("creative.stop-sound.bypass")) {
                Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                if (plot == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (!(plot.isOwner(player) || plot.getWorldPlayers().canDevelop(player))) {
                    player.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
            }
            if (args.length == 0) {
                player.stopAllSounds();
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-all").replace("%player%",player.getName()));
                return true;
            }
            Player target = player;
            String soundOrCategory;
            if (args.length == 1) {
                soundOrCategory = args[0].toUpperCase();
            } else if (args.length == 2) {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(getLocaleMessage("no-player-found"));
                    return true;
                } else if (!sender.hasPermission("creative.stop-sound.bypass")) {
                    Plot targetPlot = PlotManager.getInstance().getPlotByPlayer(target);
                    if (!player.hasPermission("creative.stop-sound.bypass")) {
                        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (plot == null || !plot.equals(targetPlot)) {
                            player.sendMessage(getLocaleMessage("no-player-found"));
                            return true;
                        }
                    }
                }
                soundOrCategory = args[1].toUpperCase();
            } else {
                sender.sendMessage(getLocaleMessage("commands.stop-sound.help"));
                return true;
            }
            Sound sound = null;
            SoundCategory category =null;
            try {
                sound = Sound.valueOf(soundOrCategory);
            } catch (IllegalArgumentException ignored) {}
            try {
                category = SoundCategory.valueOf(soundOrCategory);
            } catch (IllegalArgumentException ignored) {}
            if (sound != null) {
                target.stopSound(sound);
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-sound").replace("%sound%",soundOrCategory).replace("%player%",target.getName()));
            } else if (category != null) {
                target.stopSound(category);
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-category").replace("%category%",soundOrCategory).replace("%player%",target.getName()));
            } else {
                target.stopAllSounds();
                sender.sendMessage(getLocaleMessage("commands.stop-sound.stopped-all").replace("%player%",target.getName()));
            }
        }
        return true;
    }
}
