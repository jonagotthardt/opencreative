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

public class CooldownUtils {

    static HashMap<Player, Long> genericCommandCooldown = new HashMap<>();
    static HashMap<Player, Long> advertisementCommandCooldown = new HashMap<>();
    static HashMap<Player, Long> creativeChatCooldown = new HashMap<>();
    static HashMap<Player, Long> worldChatCooldown = new HashMap<>();

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
     **/
    public static int getCooldown(Player player, CooldownType type) {

        if (player.hasPermission("creative.cooldownbypass")) return 0;
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
        switch (type) {
            case GENERIC_COMMAND:
                return genericCommandCooldown;
            case ADVERTISEMENT_COMMAND:
                return advertisementCommandCooldown;
            case CREATIVE_CHAT:
                return creativeChatCooldown;
            case WORLD_CHAT:
                return worldChatCooldown;
            default:
                throw new IllegalArgumentException("Невозможно получить задержку с типом: " + type);
        }
    }


}
