/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package mcchickenstudio.creative.events;

import mcchickenstudio.creative.Main;
import mcchickenstudio.creative.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.PlayerUtils.loadPermissions;

public class PlayerJoin implements Listener {

    private final List<String> blockedPlayers = new ArrayList();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        loadPermissions(event.getPlayer());
        PlayerUtils.teleportToLobby(event.getPlayer());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (event.getPlayer().getWorld() != onlinePlayer.getWorld()) {
                event.getPlayer().hidePlayer(onlinePlayer);
                onlinePlayer.hidePlayer(event.getPlayer());
            }
        }
        if (isBlocked(event.getPlayer().getName())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().kick(Component.text("Lying is not allowed on this server."));
                }
            }.runTaskLater(Main.getPlugin(),40L);
        }
    }

    /**
     * Checks is player blocked from using OpenCreative+ or not. Blocked players are people, who violated GNU GPL v3 license.
     * @param nickname Nickname of player that has to be checked.
     * @return true - if this player is blocked, false - is not blocked.
     */
    private boolean isBlocked(String nickname) {
        return blockedPlayers.contains(nickname.toLowerCase());
    }

    {
        blockedPlayers.add("0xwave");
        blockedPlayers.add("alvess");
        blockedPlayers.add("alvess__");
        blockedPlayers.add("lars_gaming");
    }
}
