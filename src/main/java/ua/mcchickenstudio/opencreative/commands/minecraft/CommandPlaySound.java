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

package ua.mcchickenstudio.opencreative.commands.minecraft;

import org.bukkit.Registry;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class CommandPlaySound implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getServer().dispatchCommand(sender,"minecraft:playsound " + String.join(" ",args));
        } else {
            int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (cooldown > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                return true;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (!player.hasPermission("opencreative.play-sound.bypass")) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
                if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player))) {
                    player.sendMessage(getLocaleMessage("not-owner"));
                    return true;
                }
            }
            if (args.length == 0) {
                sender.sendMessage(getLocaleMessage("commands.play-sound.help"));
                return true;
            }
            Player target = player;
            String soundString;
            Sound sound = null;
            float volume = 100f;
            float pitch = 1f;
            // playsound entity.villager.yes
            if (args.length <= 3) {
                soundString = args[args.length == 1 ? 0 : 1];
                if (args.length > 1) {
                    try {
                        volume = Float.parseFloat(args[1]);
                    } catch (NumberFormatException ignored) {}
                }
                if (args.length > 2) {
                     try {
                         pitch = Float.parseFloat(args[2]);
                     } catch (NumberFormatException ignored) {}
                }
            } else if (args.length == 4) {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(getLocaleMessage("no-player-found"));
                    return true;
                } else if (!sender.hasPermission("opencreative.play-sound.bypass")) {
                    Planet targetPlanet = OpenCreative.getPlanetsManager().getPlanetByPlayer(target);
                    if (!player.hasPermission("opencreative.play-sound.bypass")) {
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.equals(targetPlanet)) {
                            player.sendMessage(getLocaleMessage("no-player-found"));
                            return true;
                        }
                    }
                }
                soundString = args[1];
                try {
                    volume = Float.parseFloat(args[2]);
                } catch (NumberFormatException ignored) {}
                try {
                    pitch = Float.parseFloat(args[3]);
                } catch (NumberFormatException ignored) {}
            } else {
                sender.sendMessage(getLocaleMessage("commands.play-sound.help"));
                return true;
            }
            try {
                sound = Sound.valueOf(soundString.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
            volume = Math.clamp(volume,1,100);
            pitch = Math.clamp(pitch,0.1f,2.0f);
            if (sound == null) {
                try {
                    target.playSound(target.getLocation(),soundString,volume,pitch);
                } catch (Exception ignored) {}
            } else {
                target.playSound(target.getLocation(),sound,volume,pitch);
            }
            sender.sendMessage(getLocaleMessage("commands.play-sound.played").replace("%sound%",soundString).replace("%volume%",String.valueOf(volume)).replace("%pitch%",String.valueOf(pitch)).replace("%player%",target.getName()));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                tabCompleter.addAll(player.getWorld().getPlayers().stream().map(Player::getName).toList());
            } else if (args.length == 2) {
                tabCompleter.addAll(Registry.SOUNDS.stream().filter(s -> s.getKey().asMinimalString().startsWith(args[1])).map(sound -> sound.getKey().asMinimalString()).toList());
            } else if (args.length == 3) {
                tabCompleter.add("100");
                tabCompleter.add("50");
                tabCompleter.add("20");
                tabCompleter.add("10");
            } else if (args.length == 4) {
                tabCompleter.add("1");
                tabCompleter.add("2");
                tabCompleter.add("0.1");
                tabCompleter.add("0.5");
            }
        }
        return tabCompleter;
    }
}
