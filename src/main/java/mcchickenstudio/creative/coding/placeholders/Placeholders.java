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

package mcchickenstudio.creative.coding.placeholders;

import mcchickenstudio.creative.coding.blocks.actions.Action;
import mcchickenstudio.creative.coding.blocks.events.CreativeEvent;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import mcchickenstudio.creative.plots.Plot;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class Placeholders {

    private static Placeholders instance;
    private static final String PATTERN_PLACEHOLDER = "%[A-Za-z0-9]+%";

    public synchronized static Placeholders getInstance() {
        if (instance == null) {
            instance = new Placeholders();
        }
        return instance;
    }

    public String parseAction(String text, Action action) {
        String result = parseEvent(text,action);
        result = result.replace("%space%"," ");
        result = result.replace("%empty%","");
        result = result.replace("%new-line%","\n");
        result = parseWorld(result,action);
        if (result.contains("%random")) {
            result = parseRandom(text,action);
        }
        if (result.contains("%player")) {
            result = parsePlayer(text,action);
        }
        if (result.contains("%entity")) {
            result = parseEntity(text,action);
        }
        return result;
    }

    private String parseRandom(String text, Action action) {
        Player randomPlayer = null;
        List<Player> playerList = action.getExecutor().getPlot().getPlayers();
        if (action.getEntity() instanceof Player player) {
            playerList.remove(player);
            if (playerList.isEmpty()) {
                randomPlayer = player;
            }
        }
        if (!playerList.isEmpty()) {
            Random r = new Random();
            int i = r.nextInt(playerList.size());
            randomPlayer = playerList.get(i);
        }
        if (randomPlayer != null) {
            text = text.replace("%random%",randomPlayer.getName()).replace("%random_uuid%",randomPlayer.getUniqueId().toString());
        }
        return text;
    }

    private String parseEvent(String text, Action action) {
        CreativeEvent creativeEvent = action.getEvent();
        if (creativeEvent instanceof PlayerDamagesMobEvent event) {
            text = text
                    .replace("%damager%",event.getDamager().getName())
                    .replace("%damager_uuid%",event.getDamager().getUniqueId().toString())
                    .replace("%victim%",event.getVictim().getName())
                    .replace("%victim_uuid%",event.getVictim().getUniqueId().toString());

        }
        return text;
    }

    private String parseWorld(String text, Action action) {
        Plot plot = action.getExecutor().getPlot();
        text = text
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%players_amount%", String.valueOf(plot.getPlayers().size()))
                .replace("%entities_amount%", String.valueOf(plot.world.getEntityCount() + (plot.devPlot != null && plot.devPlot.world != null ? plot.devPlot.world.getEntityCount() : 0)));
        return text;
    }

    private String parsePlayer(String text, Action action) {
        if (action.getHandler().getEvent().getSelection().getFirst() instanceof Player player) {
            text = text
                    .replace("%player%",player.getName())
                    .replace("%player_uuid%",player.getUniqueId().toString())
                    .replace("%display_name%",player.getDisplayName());
        }
        return text;
    }

    private String parseEntity(String text, Action action) {
        if (action.getHandler().getEvent().getSelection().getFirst() instanceof Entity entity) {
            text = text
                    .replace("%entity%", entity.getName())
                    .replace("%entity_uuid%", entity.getUniqueId().toString());
        }
        return text;
    }

}
