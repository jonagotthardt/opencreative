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

import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.QuitEvent;
import ua.mcchickenstudio.opencreative.commands.CreativeChat;
import ua.mcchickenstudio.opencreative.indev.OfflineWander;
import ua.mcchickenstudio.opencreative.planets.Planet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ua.mcchickenstudio.opencreative.planets.PlanetPlayer;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;


import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.*;

public final class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.spigot().respawn();

        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorldName(player.getWorld().getName().replace("dev",""));
        if (planet != null) {
            new QuitEvent(player).callEvent();
            PlanetPlayer planetPlayer = planet.getWorldPlayers().getPlanetPlayer(player);
            if (planetPlayer != null) planetPlayer.save();
            planet.getWorldPlayers().unregisterPlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    planet.getInformation().updateIcon();
                }
            }.runTaskAsynchronously(OpenCreative.getPlugin());
            if (planet.getOnline() <= 1) {
                planet.getTerritory().unload();
            }
        }
        player.setGameMode(GameMode.ADVENTURE);
        teleportToLobby(player);

        ChatListener.confirmation.remove(player);
        CreativeChat.creativeChatOff.remove(player);
        removeFromPermissionsMap(player);
        CooldownUtils.clearPlayerCooldowns(player);

    }


}
