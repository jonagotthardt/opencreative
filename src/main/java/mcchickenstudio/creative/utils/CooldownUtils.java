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

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * This class contains utils for checking and modifying player's cooldown.
 */
public class CooldownUtils {

    static final HashMap<Player, Long> genericCommandCooldown = new HashMap<>();
    static final HashMap<Player, Long> advertisementCommandCooldown = new HashMap<>();
    static final HashMap<Player, Long> creativeChatCooldown = new HashMap<>();
    static final HashMap<Player, Long> worldChatCooldown = new HashMap<>();

    public enum CooldownType {
        GENERIC_COMMAND, ADVERTISEMENT_COMMAND, CREATIVE_CHAT, WORLD_CHAT
    }

    public static long getCooldownFromMap(Player player, CooldownType type) {

        HashMap<Player, Long> cooldownMap = getCooldownMap(type);

        if (!(cooldownMap.containsKey(player))) return 0L;
        return cooldownMap.get(player);
    }

    /**
     Sets player's cooldown.
     @param player Player to set cooldown.
     @param cooldown Cooldown to set, in seconds.
     @param type Type of cooldown.
     **/
    public static void setCooldown(Player player, int cooldown, CooldownType type) {

        long cooldownInMillis = cooldown * 1000L;
        long currentTime = System.currentTimeMillis();
        long cooldownEndTime = currentTime + cooldownInMillis;

        HashMap<Player, Long> cooldownMap = getCooldownMap(type);
        cooldownMap.put(player, cooldownEndTime);

    }

    /**
     Returns player's cooldown.
     @param player Player for getting cooldown.
     @param type Type of cooldown.
     @return cooldown - Remaining time for passing cooldown, in seconds. Returns 0, if player hasn't cooldown or player has bypass.
     **/
    public static int getCooldown(Player player, CooldownType type) {

        if (player.hasPermission("opencreative.cooldownbypass")) return 0;
        long cooldownEndTime = getCooldownFromMap(player,type);
        if (cooldownEndTime == 0L) return 0;

        long currentTime = System.currentTimeMillis();

        if (cooldownEndTime < currentTime) {
            return 0;
        } else {
            return Math.round(cooldownEndTime - currentTime)/1000;
        }
    }

    private static HashMap<Player, Long> getCooldownMap(CooldownType type) {
        return switch (type) {
            case GENERIC_COMMAND -> genericCommandCooldown;
            case ADVERTISEMENT_COMMAND -> advertisementCommandCooldown;
            case CREATIVE_CHAT -> creativeChatCooldown;
            case WORLD_CHAT -> worldChatCooldown;
        };
    }
}
