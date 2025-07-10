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

import ua.mcchickenstudio.opencreative.OpenCreative;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * <h1>Advancement</h1>
 * This class represents a custom advancement, used in
 * {@link ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication.ShowAdvancementAction Show Advancement Action}.
 */
public class Advancement {

    private final Material material;
    private final AdvancementStyle advancementStyle;
    private final String title;
    private final String message;
    private final NamespacedKey nameSpacedKey = new NamespacedKey(OpenCreative.getPlugin(), UUID.randomUUID().toString());

    private Advancement(ItemStack itemStack, AdvancementStyle style, String title, String message) {
        this.material = itemStack.getType();
        this.advancementStyle = style;
        this.title = title;
        this.message = message;
    }

    public static Advancement make(ItemStack itemStack, AdvancementStyle style, String title, String message) {
        return new Advancement(itemStack,style,title,message);
    }

    public void show(Player player) {
        load();
        player.getAdvancementProgress(Bukkit.getAdvancement(nameSpacedKey)).awardCriteria("trigger");
        Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> player.getAdvancementProgress(Bukkit.getAdvancement(nameSpacedKey)).revokeCriteria("trigger"), 10);
    }

    @SuppressWarnings("deprecation")
    private void load() {
        Bukkit.getUnsafe().loadAdvancement(nameSpacedKey, "{\n" +
                "    \"criteria\": {\n" +
                "        \"trigger\": {\n" +
                "            \"trigger\": \"minecraft:impossible\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"display\": {\n" +
                "        \"icon\": {\n" +
                "            \"id\": \"minecraft:" + material.toString().toLowerCase() + "\"\n" +
                "        },\n" +
                "        \"title\": {\n" +
                "            \"text\": \"" + title + "\n" + message + "\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "            \"text\": \"\"\n" +
                "        },\n" +
                "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                "        \"frame\": \"" + advancementStyle.toString().toLowerCase() + "\",\n" +
                "        \"announce_to_chat\": false,\n" +
                "        \"show_toast\": true,\n" +
                "        \"hidden\": true\n" +
                "    },\n" +
                "    \"requirements\": [\n" +
                "        [\n" +
                "            \"trigger\"\n" +
                "        ]\n" +
                "    ]\n" +
                "}");
    }

    public enum AdvancementStyle {

        GOAL,
        TASK,
        CHALLENGE

    }

}
