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

package ua.mcchickenstudio.opencreative.utils;

import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.settings.groups.Group;

import java.util.HashMap;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>CooldownUtils</h1>
 * This class contains utils for checking and modifying player's cooldown.
 */
public final class CooldownUtils {

    static final HashMap<Player, Long> genericCommandCooldown = new HashMap<>();
    static final HashMap<Player, Long> advertisementCommandCooldown = new HashMap<>();
    static final HashMap<Player, Long> creativeChatCooldown = new HashMap<>();
    static final HashMap<Player, Long> worldChatCooldown = new HashMap<>();
    static final HashMap<Player, Long> modulesManipulationsCooldown = new HashMap<>();
    static final HashMap<Player, Long> blocksDuplicationCooldown = new HashMap<>();

    public enum CooldownType {
        GENERIC_COMMAND, ADVERTISEMENT_COMMAND, CREATIVE_CHAT, WORLD_CHAT,
        MODULE_MANIPULATION, BLOCKS_DUPLICATION
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

        if (player.hasPermission("opencreative.cooldown.bypass")) return 0;
        long cooldownEndTime = getCooldownFromMap(player,type);
        if (cooldownEndTime == 0L) return 0;

        long currentTime = System.currentTimeMillis();

        if (cooldownEndTime < currentTime) {
            return 0;
        } else {
            return Math.round(cooldownEndTime - currentTime)/1000;
        }
    }

    /**
     * Checks if player has cooldown. If not, sets it and returns true.
     * If cooldown is active, returns false.
     *
     * @param player Player to check and set cooldown
     * @param group Group object for retrieving cooldown durations
     * @param type Cooldown type to check/set
     * @return true if cooldown was not active and now set; false if still on cooldown
     */
    public static boolean checkAndSetCooldown(Player player, Group group, CooldownType type) {
        int currentCooldown = getCooldown(player, type);
        if (currentCooldown > 0) return false;

        setCooldown(player, getGroupCooldown(type, group), type);
        return true;
    }

    public static boolean checkAndSetCooldownWithMessage(Player player, Group group, CooldownType type) {
        if (!checkAndSetCooldown(player, group, type)) {
            player.sendMessage(getLocaleMessage("cooldown")
                    .replace("%cooldown%", String.valueOf(getCooldown(player, type))));
            return false;
        }
        return true;
    }

    public static boolean checkAndSetCooldownWithMessage(Player player, CooldownType type) {
        Group group = OpenCreative.getSettings().getGroups().getGroup(player);
        return checkAndSetCooldownWithMessage(player, group, type);
    }

    public static void clearPlayerCooldowns(Player player) {
        genericCommandCooldown.remove(player);
        advertisementCommandCooldown.remove(player);
        creativeChatCooldown.remove(player);
        worldChatCooldown.remove(player);
        modulesManipulationsCooldown.remove(player);
        blocksDuplicationCooldown.remove(player);
    }

    private static HashMap<Player, Long> getCooldownMap(CooldownType type) {
        return switch (type) {
            case GENERIC_COMMAND -> genericCommandCooldown;
            case ADVERTISEMENT_COMMAND -> advertisementCommandCooldown;
            case CREATIVE_CHAT -> creativeChatCooldown;
            case WORLD_CHAT -> worldChatCooldown;
            case MODULE_MANIPULATION -> modulesManipulationsCooldown;
            case BLOCKS_DUPLICATION -> blocksDuplicationCooldown;
        };
    }

    private static int getGroupCooldown(CooldownType type, Group group) {
        return switch (type) {
            case GENERIC_COMMAND -> group.getGenericCommandCooldown();
            case ADVERTISEMENT_COMMAND -> group.getAdvertisementCooldown();
            case CREATIVE_CHAT -> group.getCreativeChatCooldown();
            case WORLD_CHAT -> group.getChatCooldown();
            case MODULE_MANIPULATION -> group.getModuleManipulationCooldown();
            case BLOCKS_DUPLICATION -> group.getBlocksDuplicationCooldown();
        };
    }
}
