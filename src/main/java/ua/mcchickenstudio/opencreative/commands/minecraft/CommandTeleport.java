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

package ua.mcchickenstudio.opencreative.commands.minecraft;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.clearPlayer;

public class CommandTeleport implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            /*
             * If sender is console, then replace with default /minecraft:tp command
             */
            Bukkit.getServer().dispatchCommand(sender, "minecraft:tp " + String.join(" ", args));
            return true;
        }
        int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (cooldown > 0) {
            sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
            return true;
        }
        setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
        if (!player.hasPermission("opencreative.teleport.bypass")) {
            /*
             * Checking is player owner, builder or developer of world.
             * If not, he can't teleport.
             */
            Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (!(planet.isOwner(player) || planet.getWorldPlayers().canDevelop(player) || planet.getWorldPlayers().canBuild(player))) {
                player.sendMessage(getLocaleMessage("not-owner"));
                return true;
            }
            /*
             * Players should not teleport in developer world,
             * because it's work depends on game mode.
             */
            if (PlanetManager.getInstance().getDevPlanet(player) != null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
        }
        if (args.length == 1) {
            /*
             * Example: /tp PlayerName
             */
            Player teleportToPlayer = Bukkit.getPlayer(args[0]);
            if (teleportToPlayer == null) {
                player.sendMessage(getLocaleMessage("no-player-found"));
                return true;
            }
            Planet teleportPlanet = PlanetManager.getInstance().getPlanetByPlayer(teleportToPlayer);
            if (!player.hasPermission("opencreative.teleport.bypass")) {
                Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                if (planet == null || !planet.equals(teleportPlanet)) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return true;
                }
                if (PlanetManager.getInstance().getDevPlanet(teleportToPlayer) != null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }

            }
            if (!player.hasPermission("opencreative.teleport.clear-bypass")) {
                if (!player.getWorld().equals(teleportToPlayer.getWorld())) {
                    clearPlayer(player);
                }
                Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                if (planet == null || !planet.equals(teleportPlanet)) {
                    teleportPlanet.connectPlayer(player);
                } else {
                    player.teleport(teleportToPlayer.getLocation());
                }
            } else {
                player.teleport(teleportToPlayer.getLocation());
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100,0.1f);
            if (!player.getWorld().equals(teleportToPlayer.getWorld()) && !player.hasPermission("opencreative.teleport.clear-bypass")) {
                clearPlayer(player);
            }
        } else if (args.length == 2) {
            /*
             * Example: /tp FirstPlayer SecondPlayer
             */
            Player firstPlayer = Bukkit.getPlayer(args[0]);
            if (firstPlayer == null) {
                player.sendMessage(getLocaleMessage("no-player-found"));
                return true;
            }
            Player secondPlayer = Bukkit.getPlayer(args[1]);
            if (secondPlayer == null) {
                player.sendMessage(getLocaleMessage("no-player-found"));
                return true;
            }
            Planet firstPlanet = PlanetManager.getInstance().getPlanetByPlayer(firstPlayer);
            Planet secondPlanet = PlanetManager.getInstance().getPlanetByPlayer(secondPlayer);
            if (!player.hasPermission("opencreative.teleport.others-bypass")) {
                Planet planet = PlanetManager.getInstance().getPlanetByPlayer(player);
                if (planet == null || !planet.equals(firstPlanet) || !planet.equals(secondPlanet) || !firstPlanet.equals(secondPlanet)) {
                    player.sendMessage(getLocaleMessage("no-player-found"));
                    return true;
                }
                if (PlanetManager.getInstance().getDevPlanet(firstPlayer) != null || PlanetManager.getInstance().getDevPlanet(secondPlayer) != null) {
                    player.sendMessage(getLocaleMessage("only-in-world"));
                    return true;
                }
            }
            if (!firstPlayer.getWorld().equals(secondPlayer.getWorld()) && !player.hasPermission("opencreative.teleport.clear-bypass")) {
                clearPlayer(firstPlayer);
            }
            firstPlayer.teleport(secondPlayer.getLocation());
            firstPlayer.playSound(firstPlayer.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100,0.1f);
            if (!firstPlayer.getWorld().equals(secondPlayer.getWorld()) && !firstPlayer.hasPermission("opencreative.teleport.clear-bypass")) {
                clearPlayer(firstPlayer);
            }
        } else if (args.length >= 3) {
            /*
             * Example: /tp 30 4 30
             */
            double x,y,z;
            float yaw,pitch;
            Location location = player.getLocation();
            yaw = location.getYaw();
            pitch = location.getPitch();
            try {
                x = parseCoordinate(args[0], location.getX());
                y = parseCoordinate(args[1], location.getY());
                z = parseCoordinate(args[2], location.getZ());
                if (args.length >= 4) {
                    yaw = parseCoordinate(args[3], location.getYaw());
                }
                if (args.length >= 5) {
                    pitch = parseCoordinate(args[3], location.getPitch());
                }
                Location newLocation = new Location(location.getWorld(),x,y,z,yaw,pitch);
                if (!isOutOfBorders(newLocation)) {
                    player.teleport(newLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,100,0.1f);
                } else {
                    sender.sendMessage(getLocaleMessage("commands.teleport.out-of-borders"));
                }
            } catch (NumberFormatException exception) {
                sender.sendMessage(getLocaleMessage("commands.teleport.help"));
            }
        } else {
            sender.sendMessage(getLocaleMessage("commands.teleport.help"));
            return true;
        }
        return true;
    }

    private double parseCoordinate(String arg, double current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Double.parseDouble(arg.substring(1));
        } else {
            return Double.parseDouble(arg);
        }
    }

    private float parseCoordinate(String arg, float current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Float.parseFloat(arg.substring(1));
        } else {
            return Float.parseFloat(arg);
        }
    }

}
