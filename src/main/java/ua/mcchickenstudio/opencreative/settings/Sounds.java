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

package ua.mcchickenstudio.opencreative.settings;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import ua.mcchickenstudio.opencreative.OpenCreative;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningMessage;

public enum Sounds {

    LOBBY,
    OPENCREATIVE("block.beacon.activate",2),
    RELOADING("block.beacon.ambient",2),
    RELOADED("block.beacon.deactivate",2),

    MENU_NEXT_PAGE("item.book.page_turn"),
    MENU_PREVIOUS_PAGE("item.book.page_turn"),
    MENU_NEXT_CHOICE("ui.button.click"),
    MENU_OPEN,
    MENU_OPEN_GENERATION,
    MENU_OPEN_ENVIRONMENT("block.amethyst_block.chime",0.1f),
    MENU_OPEN_WORLD_SETTINGS("block.amethyst_block.resonate",0.1f),
    MENU_OPEN_RECOMMENDATIONS,
    MENU_OPEN_WORLDS_BROWSER("block.vault.activate"),
    MENU_OPEN_ENTITIES_BROWSER,
    MENU_OPEN_EVENTS_BROWSER,
    MENU_OPEN_ACTIONS_BROWSER,
    MENU_OPEN_CONDITIONS_BROWSER,
    MENU_OPEN_VALUES_BROWSER("ui.loom.select_pattern"),
    MENU_OPEN_CONFIRMATION,

    WORLD_GENERATION,
    WORLD_CONNECTION("block.trial_spawner.about_to_spawn_item"),
    WORLD_CONNECTED("block.beacon.activate",2),
    WORLD_LIKED("item.bottle.fill_dragonbreath",1.3f),
    WORLD_DISLIKED("item.bottle.fill_dragonbreath",0.7f),
    WORLD_ADVERTISED,
    WELCOME_TO_NEW_WORLD("ui.toast.challenge_complete",0.1f),

    WORLD_MODE_BUILD("block.beacon.power_select",1.7f),
    WORLD_MODE_DEV("entity.illusioner.prepare_mirror",0.5f),
    WORLD_MODE_PLAY,

    WORLD_NOW_BUILDER("entity.cat.ambient"),
    WORLD_NOW_DEVELOPER("entity.cat.ambient"),
    WORLD_NOW_DEVELOPER_GUEST("entity.cat.ambient"),
    WORLD_KICKED("entity.cat.hurt"),
    WORLD_BANNED("entity.cat.hurt"),

    PLAYER_CANCEL("entity.villager.no"),
    PLAYER_FAIL("block.amethyst_block.break",0.1f),
    PLAYER_ERROR("block.amethyst_block.break",0.1f),
    PLAYER_TELEPORT("entity.illusioner.mirror_move",0.1f),

    WORLD_CODE_ERROR("block.beacon.deactivate",1.7f),
    WORLD_CODE_COMPILE_ERROR("block.beacon.deactivate",1.7f),
    WORLD_CODE_CRITICAL_ERROR("block.respawn_anchor.deplete",0.5f),
    WORLD_NOTIFICATION,

    WORLD_SETTINGS_FAIL,
    WORLD_SETTINGS_SUCCESS,
    WORLD_SETTINGS_TIME_CHANGE("block.respawn_anchor.charge",1.2f),
    WORLD_SETTINGS_AUTOSAVE_ON("block.beacon.activate",0.7f),
    WORLD_SETTINGS_AUTOSAVE_OFF("block.respawn_anchor.deplete",0.3f),
    WORLD_SETTINGS_SHARING_PUBLIC("block.iron_door.open"),
    WORLD_SETTINGS_SHARING_PRIVATE("entity.evoker.fangs",0.6f),
    WORLD_SETTINGS_SPAWN_TELEPORT("entity.illusioner.mirror_move",0.8f),
    WORLD_SETTINGS_SPAWN_SET("entity.illusioner.cast_spell",0.8f),
    WORLD_SETTINGS_OWNER_SET,
    WORLD_PURCHASE("entity.player.levelup", 1.2f),

    DEV_CONNECTED("block.beacon.activate",2),
    DEV_NOT_ALLOWED("block.amethyst_block.break",1.2f),
    DEV_OPEN_CHEST("block.ender_chest.open",0.6f),
    DEV_OPEN_BARREL("block.barrel.open",0.6f),
    DEV_CLOSED_CHEST("block.ender_chest.close"),
    DEV_CLOSED_BARREL("block.barrel.close",0.6f),
    DEV_SET_EVENT("entity.experience_orb.pickup",1.7f),
    DEV_SET_ACTION("entity.experience_orb.pickup", 1.5f),
    DEV_SET_CONDITION("entity.experience_orb.pickup", 1.3f),
    DEV_SET_TARGET("block.end_portal_frame.fill",0.2f),
    DEV_SET_METHOD("block.enchantment_table.use",0.7f),
    DEV_SET_FUNCTION("block.enchantment_table.use",1.2f),
    DEV_VAR_LIST("ui.loom.select_pattern",0.7f),
    DEV_PLATFORM_COLOR("entity.illusioner.prepare_mirror"),
    DEV_PLATFORM_CLAIM("ui.toast.challenge_complete",1.7f),

    DEV_VARIABLE_PARAMETER("block.vault.activate",0.7f),
    DEV_NEXT_PARAMETER("block.amethyst_block.resonate",1.7f),
    DEV_CHANGE_CATEGORY("item.book.page_turn",0.5f),
    DEV_NAMED_METHOD,
    DEV_NAMED_FUNCTION,
    DEV_NAMED_CYCLE,
    DEV_TAKE_VALUE("entity.allay.item_thrown",2f),
    DEV_VALUE_SET,
    DEV_TEXT_SET,
    DEV_PARTICLE_SET("entity.firework_rocket.large_blast_far",1.2f),
    DEV_POTION_SET("block.brewing_stand.brew",1.2f),
    DEV_POTION_APPLY,
    DEV_POTION_REMOVE,
    DEV_LOCATION_SET,
    DEV_LOCATION_TELEPORT("entity.illusioner.mirror_move",0.7f),
    DEV_LOCATION_TELEPORT_BACK,
    DEV_EVENT_VALUE_SET("item.bottle.fill_dragonbreath",1.7f),
    DEV_VECTOR_SET,
    DEV_VECTOR_APPLY,
    DEV_BOOLEAN_TRUE,
    DEV_BOOLEAN_FALSE,
    DEV_MOVE_BLOCKS_RIGHT,
    DEV_MOVE_BLOCKS_LEFT,
    DEV_DEBUG_ON("entity.allay.ambient_without_item"),
    DEV_DEBUG_OFF("entity.allay.ambient_with_item"),

    MAINTENANCE_NOTIFY("block.bell.use",0.1f),
    MAINTENANCE_COUNT("block.end_portal_frame.fill",2),
    MAINTENANCE_START("block.beacon.power_select",0.5f),
    MAINTENANCE_END("block.beacon.power_select",0.7f),
    ;

    private final String name;
    private final float pitch;

    Sounds(String name, float pitch) {
        this.name = name;
        this.pitch = pitch;
    }

    Sounds() {
        this.name = "entity.player.levelup";
        this.pitch = 1;
    }

    Sounds(String name) {
        this.name = name;
        this.pitch = 1;
    }

    public void playSound(Audience audience) {
        String nameSpace = "minecraft";
        String soundName = this.name;
        float pitch = this.pitch;
        SettingsSound customSound = OpenCreative.getSettings().getSounds().get(this);
        if (customSound != null) {
            soundName = customSound.sound();
            pitch = customSound.pitch();
        }
        if (soundName.contains(":")) {
            nameSpace = soundName.split(":")[0];
            soundName = soundName.split(":")[1];
        }
        try {
            audience.playSound(Sound.sound(Key.key(nameSpace, soundName),
                    Sound.Source.RECORD, 100f, pitch));
        } catch (Exception error) {
            sendWarningMessage("Can't play sound: " + nameSpace + " " + soundName,error);
        }
    }

}
