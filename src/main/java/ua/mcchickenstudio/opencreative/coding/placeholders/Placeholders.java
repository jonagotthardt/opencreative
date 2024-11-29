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

package ua.mcchickenstudio.opencreative.coding.placeholders;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.MobDamagesPlayerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerDamagesPlayerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.PlayerKilledPlayerEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.interaction.MobInteractionEvent;
import ua.mcchickenstudio.opencreative.plots.Plot;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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

    public String parseAction(String text, ActionsHandler handler, Action action) {
        String result = parseEvent(text,handler);
        result = result.replace("%space%"," ");
        result = result.replace("%empty%","");
        result = result.replace("%new-line%","\n");
        result = result.replace("%nl%","\n");
        result = result.replace("\\n","\n");
        result = parseWorld(result,handler);
        if (result.contains("%random")) {
            result = parseRandom(text,handler);
        }
        if (result.contains("%target") || result.contains("%select")) {
            result = parseTarget(text,action);
        }
        if (result.contains("%player")) {
            result = parsePlayer(text,handler);
        }
        if (result.contains("%entity")) {
            result = parseEntity(text,handler);
        }
        return result;
    }

    private String parseTarget(String text, Action action) {
        Entity entity = action.getEntity();
        if (entity != null) {
            text = text
                    .replace("%selected%",entity.getName())
                    .replace("%selected_uuid%",entity.getUniqueId().toString())
                    .replace("%target%",entity.getName())
                    .replace("%target_uuid%",entity.getUniqueId().toString());
        }
        if (text.contains("%targets") || text.contains("%selection")) {
            List<String> names = action.getHandler().getSelectedTargets().stream().map(CommandSender::getName).toList();
            text = text.replace("%targets%",String.join(", ",names));
            text = text.replace("%selection%",String.join(", ",names));
        }
        return text;
    }

    private String parseRandom(String text, ActionsHandler handler) {
        Player randomPlayer = null;
        List<Player> playerList = handler.getExecutor().getPlot().getTerritory().getWorld().getPlayers();
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

    private String parseEvent(String text, ActionsHandler handler) {
        WorldEvent worldEvent = handler.getEvent();
        Entity killer = null;
        Entity victim = null;
        if (worldEvent instanceof PlayerDamagesMobEvent event) {
            killer = event.getDamager();
            victim = event.getVictim();
        } else if (worldEvent instanceof PlayerDamagesPlayerEvent event) {
            killer = event.getDamager();
            victim = event.getVictim();
        } if (worldEvent instanceof MobDamagesPlayerEvent event) {
            killer = event.getDamager();
            victim = event.getVictim();
        } if (worldEvent instanceof PlayerKilledPlayerEvent event) {
            killer = event.getKiller();
            victim = event.getVictim();
        }
        if (killer != null) {
            text = text
                    .replace("%killer%",killer.getName())
                    .replace("%killer_uuid%",killer.getUniqueId().toString())
                    .replace("%damager%",killer.getName())
                    .replace("%damager_uuid%",killer.getUniqueId().toString());
        }
        if (victim != null) {
            text = text
                    .replace("%victim%",victim.getName())
                    .replace("%victim_uuid%",victim.getUniqueId().toString());
        }
        return text;
    }

    private String parseWorld(String text, ActionsHandler handler) {
        Plot plot = handler.getExecutor().getPlot();
        text = text
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%players_amount%", String.valueOf(plot.getPlayers().size()))
                .replace("%entities_amount%", String.valueOf(plot.getTerritory().getWorld().getEntityCount() + (plot.getDevPlot() != null && plot.getDevPlot().getWorld() != null ? plot.getDevPlot().getWorld().getEntityCount() : 0)));
        return text;
    }

    private String parsePlayer(String text, ActionsHandler handler) {
        if (handler.getEvent().getSelection().getFirst() instanceof Player player) {
            text = text
                    .replace("%player%",player.getName())
                    .replace("%player_uuid%",player.getUniqueId().toString())
                    .replace("%display_name%",player.getDisplayName());
        }
        return text;
    }

    private String parseEntity(String text, ActionsHandler handler) {
        if (handler.getEvent() instanceof MobInteractionEvent event) {
            text = text
                    .replace("%entity%", event.getEntity().getName())
                    .replace("%entity_uuid%", event.getEntity().getUniqueId().toString());
        } else if (handler.getEvent().getSelection().getFirst() instanceof Entity entity) {
            text = text
                    .replace("%entity%", entity.getName())
                    .replace("%entity_uuid%", entity.getUniqueId().toString());
        }
        return text;
    }

}
