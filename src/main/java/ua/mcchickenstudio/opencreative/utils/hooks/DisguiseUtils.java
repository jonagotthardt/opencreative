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

package ua.mcchickenstudio.opencreative.utils.hooks;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class DisguiseUtils {

    public static void clearDisguises(World world) {
        for (Entity entity : world.getEntities()) {
            Disguise disguise = DisguiseAPI.getDisguise(entity);
            if (disguise == null) continue;
            disguise.stopDisguise();
        }
    }

    public static void clearDisguise(Player player) {
        Disguise disguise = DisguiseAPI.getDisguise(player);
        if (disguise == null) return;
        disguise.stopDisguise();
    }

    public static void disguiseAsPlayer(Entity entity, String name, String skin) {
        try {
            PlayerDisguise disguise = PlayerDisguise.class
                    .getDeclaredConstructor(String.class, String.class)
                    .newInstance(name, skin);
            disguise.setEntity(entity);
            disguise.startDisguise();
        } catch (Exception ignored) {}
    }

    public static void disguiseAsMob(Entity entity, EntityType type) {
        MobDisguise mobDisguise = new MobDisguise(DisguiseType.getType(type));
        mobDisguise.setEntity(entity);
        mobDisguise.startDisguise();
    }

}
