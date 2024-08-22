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

package mcchickenstudio.creative.coding.blocks.actions;

import mcchickenstudio.creative.coding.blocks.actions.controlactions.events.CancelEventAction;
import mcchickenstudio.creative.coding.blocks.actions.controlactions.lines.*;
import mcchickenstudio.creative.coding.blocks.actions.other.LaunchFunctionAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.appearance.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.communication.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.inventory.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.movement.KickPlayerAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.movement.SaddleEntityAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.movement.TeleportPlayerAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.params.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.state.*;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.item.*;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.list.*;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.location.*;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.map.CreateMapAction;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.map.GetFromMapByKeyAction;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.map.PutIntoMapAction;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.number.*;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.other.DeleteVariableAction;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.other.SetVariableRandomValueAction;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.other.SetVariableValueAction;
import mcchickenstudio.creative.coding.blocks.actions.variableactions.text.*;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.blocks.SetBlockTypeAction;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.entity.CreateExplosionAction;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.entity.SpawnEntityAction;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.entity.SpawnParticleAction;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.entity.StrikeLightningAction;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.world.SetTimeAction;
import mcchickenstudio.creative.coding.blocks.actions.worldactions.world.SetWeatherAction;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsBlockEqualsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsLookingAtBlockCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsNearLocationCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsStandingOnBlockCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.inventory.*;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.params.MessageEqualsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.params.PlayerNameEqualsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.state.HasSavedPurchaseCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.state.IsFlyingCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.state.IsLikedWorldCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.state.IsSneakingCondition;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.number.NumberGreaterCondition;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.number.NumberInRangeCondition;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.number.NumberLessCondition;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.other.VariableEqualsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.other.VariableExistsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.variableconditions.other.VariableIsNullCondition;
import mcchickenstudio.creative.coding.menus.MenusCategory;
import mcchickenstudio.creative.coding.menus.layouts.ArgumentSlot;
import mcchickenstudio.creative.coding.menus.layouts.ParameterSlot;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.utils.hooks.HookUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.ItemUtils.*;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.messageExists;

public enum ActionType {

    /**
     * <h1>Player Actions.</h1>
     * Actions with players.
     * <p>Categories: Communication, Inventory, Movement, Params, State, Appearance</p>
     */

    // Communication
    PLAYER_SEND_MESSAGE(                ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, SendMessageAction.class, Material.WRITABLE_BOOK, new ArgumentSlot("messages", ValueType.TEXT,(byte)18), new ParameterSlot("type",Arrays.asList("new-line","join-spaces","join"),Material.PAPER, Material.MAP, Material.FILLED_MAP)),
    PLAYER_SEND_DIALOG(                 ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, SendDialogAction.class, Material.GLOBE_BANNER_PATTERN,         new ArgumentSlot("messages", ValueType.TEXT, (byte) 18),new ArgumentSlot("cooldown", ValueType.NUMBER)),
    PLAYER_SHOW_ACTIONBAR(              ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowActionbarAction.class, Material.BOOK,       new ArgumentSlot("actionbar", ValueType.TEXT, (byte) 18)),
    PLAYER_SHOW_TITLE(                  ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowTitleAction.class, Material.OAK_SIGN, new ArgumentSlot("title", ValueType.TEXT),new ArgumentSlot("subtitle", ValueType.TEXT),new ArgumentSlot("fade-in", ValueType.NUMBER),new ArgumentSlot("stay", ValueType.NUMBER),new ArgumentSlot("fade-out", ValueType.NUMBER)),
    PLAYER_SHOW_ADVANCEMENT(            ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowAdvancementAction.class, Material.EMERALD, new ArgumentSlot("icon", ValueType.ITEM),new ParameterSlot("style",Arrays.asList("goal","task","challenge"),Material.EMERALD,Material.DIAMOND,Material.BEACON),new ArgumentSlot("title", ValueType.TEXT),new ArgumentSlot("message", ValueType.TEXT)),
    PLAYER_CLEAR_CHAT(                  ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ClearChatAction.class, Material.BUCKET         ),
   // PLAYER_SEND_COMPONENT(              ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, null, new ArgumentSlot("")),
    PLAYER_PLAY_SOUND(                  ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, PlaySoundAction.class, Material.MUSIC_DISC_OTHERSIDE, new ArgumentSlot("sound", ValueType.TEXT),new ArgumentSlot("volume", ValueType.NUMBER),new ArgumentSlot("pitch", ValueType.NUMBER), new ArgumentSlot("location", ValueType.LOCATION),new ParameterSlot("category",Arrays.asList("ambient","blocks","hostile","master","music","neutral","players","records","voice","weather"), Material.CYAN_STAINED_GLASS,Material.GRASS_BLOCK,Material.ZOMBIE_HEAD,Material.GOLDEN_PICKAXE,Material.NOTE_BLOCK,Material.PIGLIN_HEAD,Material.PLAYER_HEAD,Material.MUSIC_DISC_CAT,Material.NAUTILUS_SHELL,Material.WATER_BUCKET)),
    PLAYER_STOP_SOUNDS(                 ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, StopSoundsAction.class, Material.MUSIC_DISC_11,         new ArgumentSlot("sounds", ValueType.TEXT,(byte) 18)),
    PLAYER_SHOW_WIN_SCREEN(             ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowWinScreenAction.class, Material.DRAGON_EGG),
    PLAYER_SHOW_DEMO_SCREEN(            ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowDemoScreenAction.class, Material.FARMLAND),
    PLAYER_REQUEST_PURCHASE(            ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, RequestPurchaseAction.class, Material.GOLD_INGOT, "Vault",  new ArgumentSlot("id", ValueType.TEXT), new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("save"), new ArgumentSlot("price", ValueType.NUMBER)),


    // Inventory
    PLAYER_GIVE_ITEMS(                  ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GiveItemsAction.class, Material.CHEST_MINECART,  new ArgumentSlot("items", ValueType.ITEM,(byte) 27)),
    PLAYER_SAVE_INVENTORY(              ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SaveInventoryAction.class, Material.HOPPER),
    PLAYER_RESTORE_INVENTORY(           ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, RestoreInventoryAction.class, Material.DROPPER),
    PLAYER_SET_ITEMS(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemsAction.class, Material.DARK_OAK_CHEST_BOAT,          new ArgumentSlot("items", ValueType.ITEM,(byte) 27,true)),
    PLAYER_SET_ARMOR(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetArmorAction.class, Material.NETHERITE_CHESTPLATE,          new ArgumentSlot("helmet", ValueType.ITEM),new ArgumentSlot("chestplate", ValueType.ITEM),new ArgumentSlot("leggings", ValueType.ITEM),new ArgumentSlot("boots", ValueType.ITEM),new ParameterSlot("replace-with-air")),
    PLAYER_GIVE_RANDOM_ITEM(            ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GiveRandomItemAction.class, Material.LIME_SHULKER_BOX, new ArgumentSlot("items", ValueType.ITEM, (byte) 27)),
    PLAYER_CLEAR_INVENTORY(             ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, ClearInventoryAction.class, Material.BUCKET),
    PLAYER_REMOVE_ITEMS(             ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, RemoveItemsAction.class, Material.COBWEB, new ArgumentSlot("items", ValueType.ITEM,(byte) 27)),
    PLAYER_SET_SLOT(                    ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetSlotAction.class, Material.SLIME_BALL,        new ArgumentSlot("slot", ValueType.NUMBER)),
    PLAYER_SET_COMPASS_TARGET(          ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetCompassTarget.class, Material.COMPASS,        new ArgumentSlot("location", ValueType.LOCATION)),
    PLAYER_SET_HOTBAR(           ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetHotBarAction.class, Material.BIRCH_CHEST_BOAT,         new ArgumentSlot("items", ValueType.ITEM,(byte) 9,true),new ParameterSlot("replace-with-air")),
    PLAYER_SET_ITEM_COOLDOWN(           ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemCooldownAction.class, Material.CLOCK,         new ArgumentSlot("item", ValueType.ITEM),new ArgumentSlot("cooldown", ValueType.NUMBER)),
    //PLAYER_SET_CURSOR_ITEM(             ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, null, Material.TRIPWIRE_HOOK,         new ArgumentSlot("item",VariableType.ITEM)),
    //PLAYER_SWING_HAND(                  ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, null,Material.SHIELD),
    //PLAYER_DAMAGE_ITEM(                 ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, null, Material.NETHERITE_SCRAP,  new ArgumentSlot("item",VariableType.ITEM),new ArgumentSlot("damage",VariableType.NUMBER)),
    PLAYER_OPEN_SIGN(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenSignAction.class, Material.OAK_SIGN, new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("side",Arrays.asList("front","back"),Material.OAK_SIGN,Material.WARPED_SIGN)),
    PLAYER_OPEN_CONTAINER(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenContainerAction.class, Material.BARREL, new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("save")),
    PLAYER_OPEN_BOOK(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenBookAction.class, Material.BOOK, new ArgumentSlot("book", ValueType.ITEM)),
    PLAYER_GET_ITEM_BY_SLOT(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GetItemAction.class, Material.MAGMA_CREAM, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("slot", ValueType.NUMBER)),

    // Movement
    PLAYER_TELEPORT(                    ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, TeleportPlayerAction.class, Material.ENDER_PEARL,  new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("consider",Arrays.asList("all","only-coordinates","only-rotation"),Material.ENDER_EYE,Material.PAPER,Material.PLAYER_HEAD)),
    //PLAYER_RANDOM_TELEPORT(             ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.CHORUS_FRUIT,         new ArgumentSlot("locations",VariableType.LOCATION,(byte)18)),
    //PLAYER_TELEPORT_QUEUE(              ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.ENDER_EYE,          new ArgumentSlot("locations",VariableType.LOCATION,(byte)18),new ArgumentSlot("cooldown",VariableType.NUMBER)),
    PLAYER_KICK(                        ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      KickPlayerAction.class, Material.BARRIER         ),
    PLAYER_SADDLE_ENTITY(               ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      SaddleEntityAction.class, Material.SADDLE,  new ArgumentSlot("entity", ValueType.TEXT)),
    //PLAYER_LAUNCH_VERTICAL(             ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.FIREWORK_ROCKET,         new ArgumentSlot("power",VariableType.NUMBER)),
    //PLAYER_LAUNCH_HORIZONTAL(           ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.CHAINMAIL_BOOTS,         new ArgumentSlot("power",VariableType.NUMBER)),
    //PLAYER_LAUNCH_TO_LOCATION(          ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.MAP,         new ArgumentSlot("location",VariableType.LOCATION)),
    //PLAYER_SET_ROTATION(                ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, null, Material.PLAYER_HEAD, new ArgumentSlot("yaw",VariableType.NUMBER),new ArgumentSlot("pitch",VariableType.NUMBER)),
    //PLAYER_SET_SPECTATOR_TARGET(        ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, null, Material.SKELETON_SKULL, new ArgumentSlot("entity",VariableType.TEXT)),

    // Params
    PLAYER_SET_HEALTH(                  ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetHealthAction.class, Material.APPLE,             new ArgumentSlot("health", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_HUNGER(                  ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetHungerAction.class, Material.COOKED_CHICKEN,  new ArgumentSlot("hunger", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_WALK_SPEED(              ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetWalkSpeedAction.class, Material.CHAINMAIL_BOOTS, new ArgumentSlot("speed", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_FLY_SPEED(               ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFlySpeedAction.class, Material.FEATHER, new ArgumentSlot("speed", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_MAX_HEALTH(              ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetMaxHealthAction.class, Material.GOLDEN_APPLE, new ArgumentSlot("health", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_FIRE_TICKS(              ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFireTicksAction.class, Material.CAMPFIRE,  new ArgumentSlot("ticks", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_FREEZE_TICKS(            ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFreezeTicksAction.class, Material.ICE, new ArgumentSlot("ticks", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_NO_DAMAGE_TICKS(         ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetNoDamageTicksAction.class, Material.TOTEM_OF_UNDYING, new ArgumentSlot("ticks", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_EXP(                     ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,  SetExpAction.class, Material.SLIME_SPAWN_EGG,  new ArgumentSlot("exp", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_EXP_LEVEL(               ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetExpLevelAction.class, Material.SUGAR_CANE, new ArgumentSlot("level", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_TOTAL_EXPERIENCE(        ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetTotalExpAction.class, Material.EXPERIENCE_BOTTLE, new ArgumentSlot("exp", ValueType.NUMBER),new ParameterSlot("add")),
    //PLAYER_GIVE_POTION_EFFECTS(         ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,  null, Material.POTION,         new ArgumentSlot("potions",VariableType.POTION,(byte) 18)),
    PLAYER_CLEAR_POTION_EFFECTS(        ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, ClearPotionEffectsAction.class, Material.MILK_BUCKET,         new ArgumentSlot("potions", ValueType.POTION)),
    //PLAYER_REMOVE_POTION_EFFECT(        ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, null, Material.GLASS_BOTTLE,         new ArgumentSlot("potions",VariableType.POTION, (byte) 18)),
    PLAYER_SET_FLYING_FALL_DAMAGE(      ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,  SetFlyingFallDamageAction.class, Material.RABBIT_HIDE,  new ParameterSlot("boolean")),
    PLAYER_SET_FALL_DISTANCE(           ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetFallDistanceAction.class, Material.RABBIT_FOOT, new ArgumentSlot("distance", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_LAST_DAMAGE(             ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetLastDamageAction.class, Material.REDSTONE, new ArgumentSlot("damage", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_CAN_PICKUP_ITEM(         ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetCanPickupItem.class, Material.GLOWSTONE_DUST, new ParameterSlot("boolean")),
    PLAYER_SET_ARROWS_IN_BODY(          ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetArrowsInBodyAction.class, Material.ARROW, new ArgumentSlot("count", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_SHIELD_BLOCKING_DELAY(   ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetShieldBlockingDelay.class, Material.SHIELD, new ArgumentSlot("delay", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_BEE_STINGER_COOLDOWN(    ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetBeeStingerCooldownAction.class, Material.BEE_NEST, new ArgumentSlot("cooldown", ValueType.NUMBER),new ParameterSlot("add")),
    PLAYER_SET_MAXIMUM_NO_DAMAGE_TICKS( ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetMaxNoDamageTicksAction.class, Material.ENCHANTED_GOLDEN_APPLE, new ArgumentSlot("ticks", ValueType.NUMBER),new ParameterSlot("add")),

    // State
    PLAYER_SET_GAMEMODE(                ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGameModeAction.class, Material.CRAFTING_TABLE,  new ParameterSlot("game-mode", Arrays.asList("adventure","survival","creative","spectator"), Material.PUFFERFISH_BUCKET,Material.CRAFTING_TABLE,Material.TNT,Material.FEATHER)),
    PLAYER_SET_FLYING(                  ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetFlyingAction.class, Material.FEATHER, new ParameterSlot("flying", Material.CHAINMAIL_BOOTS, Material.FEATHER), new ParameterSlot("allow-flight", Material.GRASS_BLOCK, Material.GLASS)),
    PLAYER_SET_GLOWING(                 ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGlowingAction.class, Material.DRAGON_BREATH,  new ParameterSlot("glowing", Arrays.asList(false,true), Material.WHITE_STAINED_GLASS, Material.GLASS)),
    PLAYER_SET_GLIDING(                 ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGlidingAction.class, Material.ELYTRA, new ParameterSlot("boolean", Material.CHAINMAIL_BOOTS, Material.ELYTRA)),
    PLAYER_SET_SPRINTING(               ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetSprintingAction.class, Material.GOLDEN_BOOTS, new ParameterSlot("boolean", Material.LEATHER_BOOTS, Material.NETHERITE_BOOTS)),

    // Appearance
    PLAYER_SET_TIME(                    ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, PlayerSetTimeAction.class, Material.CLOCK, new ArgumentSlot("time", ValueType.NUMBER)),
    PLAYER_SET_WEATHER(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, PlayerSetWeatherAction.class, Material.WATER_BUCKET, new ParameterSlot("weather", Arrays.asList("clean","storm"), Material.SUNFLOWER, Material.WATER_BUCKET)),
    //PLAYER_SPAWN_PARTICLE(              ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, null, Material.NETHER_STAR, new ArgumentSlot("particle",VariableType.PARTICLE,(byte) 18),new ArgumentSlot("location",VariableType.LOCATION),new ArgumentSlot("count",VariableType.NUMBER)),
    PLAYER_SET_RESOURCE_PACK(           ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetResourcePackAction.class, Material.SHROOMLIGHT, new ArgumentSlot("url", ValueType.TEXT)),
    PLAYER_SET_WORLD_BORDER(            ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetWorldBorderAction.class, Material.END_CRYSTAL, new ArgumentSlot("center", ValueType.LOCATION), new ArgumentSlot("radius", ValueType.NUMBER), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("damage", ValueType.NUMBER), new ArgumentSlot("warning-distance", ValueType.NUMBER), new ArgumentSlot("warning-time", ValueType.NUMBER), new ArgumentSlot("safe-distance", ValueType.NUMBER)),
    PLAYER_SEND_SIGN_CHANGE(            ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SendSignChangeAction.class, Material.OAK_SIGN, new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("number", ValueType.NUMBER)),
    PLAYER_SHOW_ENTITY(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowEntityAction.class, Material.PIGLIN_HEAD, new ArgumentSlot("entity", ValueType.TEXT)),
    PLAYER_HIDE_ENTITY(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HideEntityAction.class, Material.SKELETON_SKULL, new ArgumentSlot("entity", ValueType.TEXT)),
    PLAYER_SHOW_PLAYER(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowPlayerAction.class, Material.PLAYER_HEAD, new ArgumentSlot("player", ValueType.TEXT)),
    PLAYER_HIDE_PLAYER(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HidePlayerAction.class, Material.WITHER_SKELETON_SKULL, new ArgumentSlot("player", ValueType.TEXT)),
    PLAYER_SHOW_ELDER_GUARDIAN(         ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowElderGuardianAction.class, Material.ELDER_GUARDIAN_SPAWN_EGG, new ParameterSlot("silent",Material.STRUCTURE_VOID,Material.NAUTILUS_SHELL)),

    /**
     * <h1>Player Conditions.</h1>
     */

    IF_PLAYER_STANDS_ON_BLOCK(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsStandingOnBlockCondition.class, Material.GRASS_BLOCK, new ArgumentSlot("blocks", ValueType.ITEM,(byte) 9)),
    IF_PLAYER_LOOKS_AT_BLOCK(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsLookingAtBlockCondition.class, Material.CHEST, new ArgumentSlot("blocks", ValueType.ITEM,(byte) 9)),
    IF_PLAYER_BLOCK_EQUALS(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsBlockEqualsCondition.class, Material.OAK_LOG, new ArgumentSlot("blocks", ValueType.ITEM,(byte) 9)),
    IF_PLAYER_ITEM_EQUALS(                   ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, IsItemEqualsCondition.class, Material.GLOW_ITEM_FRAME, new ArgumentSlot("items", ValueType.ITEM,(byte) 18), new ParameterSlot("ignore-amount", Material.BEETROOT_SEEDS, Material.OAK_BUTTON), new ParameterSlot("ignore-name", Material.NAME_TAG, Material.STRING), new ParameterSlot("ignore-lore", Material.WRITABLE_BOOK, Material.COBWEB), new ParameterSlot("ignore-enchantments", Material.ENCHANTED_BOOK, Material.BOOK), new ParameterSlot("ignore-flags",Material.BLUE_BANNER,Material.WHITE_BANNER), new ParameterSlot("ignore-material",Material.CRAFTING_TABLE,Material.WHITE_STAINED_GLASS)),
    IF_PLAYER_HAS_ITEM_COOLDOWN(                   ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemCooldownCondition.class, Material.CLOCK, new ArgumentSlot("items", ValueType.ITEM,(byte) 18)),
    IF_PLAYER_IS_NEAR_LOCATION(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsNearLocationCondition.class, Material.COMPASS, new ArgumentSlot("locations", ValueType.LOCATION,(byte) 18),new ArgumentSlot("distance", ValueType.NUMBER)),

    IF_PLAYER_NAME_EQUALS(                 ActionCategory.PLAYER_CONDITION, MenusCategory.PARAMS, PlayerNameEqualsCondition.class, Material.NAME_TAG, new ArgumentSlot("names", ValueType.TEXT,(byte) 18), new ParameterSlot("require-caps")),
    IF_PLAYER_MESSAGE_EQUALS(              ActionCategory.PLAYER_CONDITION, MenusCategory.PARAMS, MessageEqualsCondition.class, Material.BOOK, new ArgumentSlot("messages", ValueType.TEXT,(byte) 18), new ParameterSlot("require-caps")),
    IF_PLAYER_LIKED_WORLD(                 ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsLikedWorldCondition.class, Material.GOLDEN_APPLE),
    IF_PLAYER_HAS_SAVED_PURCHASE(                 ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, HasSavedPurchaseCondition.class, Material.GOLD_BLOCK, new ArgumentSlot("names", ValueType.TEXT,(byte) 18)),
    IF_PLAYER_HAS_ITEM(                 ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemCondition.class, Material.CHEST_MINECART, new ArgumentSlot("items", ValueType.ITEM,(byte) 18)),
    IF_PLAYER_INVENTORY_NAME_EQUALS(              ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, IsInventoryNameEqualsCondition.class, Material.BOOK, new ArgumentSlot("names", ValueType.TEXT,(byte) 18), new ParameterSlot("require-color",Material.WHITE_WOOL,Material.BLUE_WOOL), new ParameterSlot("require-caps")),
    IF_PLAYER_HAS_ITEM_IN_HAND(                 ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemInHandCondition.class, Material.WOODEN_SWORD, new ArgumentSlot("items", ValueType.ITEM,(byte) 18), new ParameterSlot("hand",Arrays.asList("main-hand","off-hand","main-or-off-hands","main-and-off-hands"),Material.DIAMOND_SWORD,Material.SHIELD,Material.BOW, Material.CHEST), new ParameterSlot("ignore-amount", Material.BEETROOT_SEEDS, Material.OAK_BUTTON), new ParameterSlot("ignore-name", Material.NAME_TAG, Material.STRING), new ParameterSlot("ignore-lore", Material.WRITABLE_BOOK, Material.COBWEB), new ParameterSlot("ignore-enchantments", Material.ENCHANTED_BOOK, Material.BOOK), new ParameterSlot("ignore-flags",Material.BLUE_BANNER,Material.WHITE_BANNER), new ParameterSlot("ignore-material",Material.CRAFTING_TABLE,Material.WHITE_STAINED_GLASS)),
    IF_PLAYER_IS_SNEAKING(                 ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsSneakingCondition.class, Material.RABBIT),
    IF_PLAYER_IS_FLYING(                   ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsFlyingCondition.class, Material.FEATHER),

    /**
     * <h1>Control Actions.</h1>
     */

    //ELSE(ActionCategory.ELSE_CONDITION,null,null,null),
    CONTROL_THROW_ERROR(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, ThrowErrorAction.class, Material.TNT_MINECART, new ArgumentSlot("message", ValueType.TEXT)),
    CONTROL_STOP_CODE_LINE(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, StopCodeLineAction.class, Material.STRUCTURE_VOID),
    CONTROL_WAIT(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, WaitAction.class, Material.CLOCK, new ArgumentSlot("time", ValueType.NUMBER)),
    CONTROL_LAUNCH_CYCLES(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, LaunchCyclesAction.class, Material.OXIDIZED_COPPER, new ArgumentSlot("names", ValueType.TEXT, (byte) 27)),
    CONTROL_STOP_CYCLES(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, StopCyclesAction.class, Material.WEATHERED_CUT_COPPER_STAIRS, new ArgumentSlot("names", ValueType.TEXT, (byte) 27)),


    CONTROL_CANCEL_EVENT(                 ActionCategory.CONTROL_ACTION, MenusCategory.EVENTS, CancelEventAction.class, Material.BARRIER),

    /**
     * <h1>World Actions.</h1>
     */

    WORLD_SET_TIME(                 ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SetTimeAction.class, Material.CLOCK, new ArgumentSlot("time", ValueType.NUMBER)),
    WORLD_SET_WEATHER(                 ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SetWeatherAction.class, Material.WATER_BUCKET, new ParameterSlot("weather", Arrays.asList("clean","storm","thunder"), Material.SUNFLOWER, Material.WATER_BUCKET, Material.TRIDENT), new ArgumentSlot("duration", ValueType.NUMBER)),

    WORLD_SPAWN_ENTITY(                 ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnEntityAction.class, Material.SPAWNER, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 9), new ArgumentSlot("type", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("show-name", true, Material.NAME_TAG, Material.STRING), new ParameterSlot("gravity", true, Material.SAND, Material.COBWEB), new ParameterSlot("ai", true, Material.PLAYER_HEAD, Material.SKELETON_SKULL), new ParameterSlot("glowing", Material.GLASS, Material.WHITE_STAINED_GLASS), new ParameterSlot("invisible", Material.POTION, Material.GLASS_BOTTLE), new ParameterSlot("invulnerable", Material.REDSTONE, Material.TOTEM_OF_UNDYING), new ParameterSlot("visible-for-all", true, Material.GRASS_BLOCK, Material.GRAY_STAINED_GLASS)),
    WORLD_CREATE_EXPLOSION(                 ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, CreateExplosionAction.class, Material.TNT, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("power", ValueType.NUMBER), new ParameterSlot("fire", Material.GUNPOWDER, Material.CAMPFIRE), new ParameterSlot("damage", Material.GRASS_BLOCK, Material.MAGMA_BLOCK)),
    WORLD_STRIKE_LIGHTNING(                 ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, StrikeLightningAction.class, Material.TRIDENT, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("damage", true, Material.REDSTONE, Material.GLOWSTONE_DUST)),
    WORLD_SPAWN_PARTICLE(                 ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnParticleAction.class, Material.NETHER_STAR, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("particle", ValueType.PARTICLE), new ArgumentSlot("count", ValueType.NUMBER), new ArgumentSlot("offset-x", ValueType.NUMBER), new ArgumentSlot("offset-y", ValueType.NUMBER), new ArgumentSlot("offset-z", ValueType.NUMBER)),

    WORLD_SET_BLOCK_TYPE(                 ActionCategory.WORLD_ACTION, MenusCategory.MOVEMENT, SetBlockTypeAction.class, Material.STONE, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("type", ValueType.ITEM)),

    /**
     * <h1>Variable Actions.</h1>
     */

    VAR_SET_VALUE( ActionCategory.VARIABLE_ACTION, MenusCategory.OTHER, SetVariableValueAction.class, Material.IRON_INGOT, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("value", ValueType.ANY)),
    VAR_SET_RANDOM_VALUE( ActionCategory.VARIABLE_ACTION, MenusCategory.OTHER, SetVariableRandomValueAction.class, Material.PUMPKIN_SEEDS, new ArgumentSlot("values", ValueType.ANY, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_DELETE_VARIABLE( ActionCategory.VARIABLE_ACTION, MenusCategory.OTHER, DeleteVariableAction.class, Material.BARRIER, new ArgumentSlot("variables", ValueType.VARIABLE, (byte) 18)),

    VAR_SUM_NUMBERS( ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SumNumbersAction.class, Material.BRICK, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_SUBTRACT_NUMBERS( ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SubtractNumbersAction.class, Material.NETHER_BRICK, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_MULTIPLY_NUMBER( ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, MultiplyNumberAction.class, Material.COPPER_INGOT,new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_DIVIDE_NUMBER( ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, DivideNumberAction.class, Material.NETHERITE_INGOT, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_SET_RANDOM_NUMBER( ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, RandomNumberAction.class, Material.ENDER_EYE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("min", ValueType.NUMBER), new ArgumentSlot("max", ValueType.NUMBER)),
    VAR_MODULE_NUMBER( ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, ModuleNumberAction.class, Material.TURTLE_HELMET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_ROUND_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, RoundNumberAction.class, Material.SNOWBALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER), new ParameterSlot("type", Arrays.asList("ceil","floor"), Material.SNOWBALL, Material.SUNFLOWER)),

    VAR_PARSE_NUMBER( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ParseNumberAction.class, Material.SLIME_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_CHAR_AT( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, CharAtTextAction.class, Material.IRON_NUGGET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_CONCAT_TEXT( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ConcatTextAction.class, Material.BARREL,  new ArgumentSlot("text", ValueType.TEXT, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE), new ParameterSlot("type",Arrays.asList("new-line","join-spaces","join"),Material.PAPER, Material.MAP, Material.FILLED_MAP)),
    VAR_SUBSTRING_TEXT( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, SubstringTextAction.class, Material.SHEARS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("from", ValueType.NUMBER), new ArgumentSlot("to", ValueType.NUMBER)),
    VAR_TRANSLATE_COLORS( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, TranslateColorsAction.class, Material.PURPLE_WOOL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("character", ValueType.TEXT)),
    VAR_STRIP_COLORS( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, StripColorAction.class, Material.WHITE_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_REVERSE_TEXT( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ReverseTextAction.class, Material.ENCHANTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_UPPER_CASE_TEXT( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, UpperCaseTextAction.class, Material.OBSERVER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_LOWER_CASE_TEXT( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, LowerCaseTextAction.class, Material.DROPPER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_SPLIT_TEXT( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, SplitTextAction.class, Material.CHISELED_BOOKSHELF, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("splitter", ValueType.TEXT)),
    VAR_TEXT_LENGTH( ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, null, Material.SLIME_BALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),

    VAR_MODIFY_LOCATION( ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, ModifyLocationAction.class, Material.WHITE_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("yaw", ValueType.NUMBER), new ArgumentSlot("pitch", ValueType.NUMBER), new ArgumentSlot("x", ValueType.NUMBER), new ArgumentSlot("y", ValueType.NUMBER), new ArgumentSlot("z", ValueType.NUMBER), new ParameterSlot("add")),
    VAR_GET_DISTANCE( ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetDistanceAction.class, Material.SPYGLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION)),
    VAR_GET_LOCATION_X( ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationXAction.class, Material.RED_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_LOCATION_Y( ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationYAction.class, Material.GREEN_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_LOCATION_Z( ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationZAction.class, Material.BLUE_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_LOCATION_YAW( ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationYawAction.class, Material.YELLOW_STAINED_GLASS_PANE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_LOCATION_PITCH( ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationPitchAction.class, Material.ORANGE_STAINED_GLASS_PANE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),

    VAR_CREATE_LIST( ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, CreateListAction.class, Material.BOOKSHELF, new ArgumentSlot("elements",ValueType.ANY,(byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_GET_LIST_SIZE( ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetListSizeAction.class, Material.SLIME_BALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_ADD_TO_LIST( ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, AddToListAction.class, Material.KNOWLEDGE_BOOK, new ArgumentSlot("elements",ValueType.ANY,(byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_SET_IN_LIST( ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, SetInListAction.class, Material.CAULDRON, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("index", ValueType.NUMBER), new ArgumentSlot("value", ValueType.ANY)),
    VAR_GET_BY_ID_FROM_LIST( ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetByIdFromListAction.class, Material.LAVA_BUCKET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_REMOVE_BY_ID_FROM_LIST( ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, RemoveByIdFromListAction.class, Material.LAVA_BUCKET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_GET_RANDOM_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetRandomFromListAction.class, Material.ENDER_EYE, new ArgumentSlot("variable",ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),

    VAR_CREATE_MAP( ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, CreateMapAction.class, Material.CHEST_MINECART, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("keys", ValueType.VARIABLE), new ArgumentSlot("values", ValueType.VARIABLE)),
    VAR_PUT_INTO_MAP( ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, PutIntoMapAction.class, Material.CHEST, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("key", ValueType.ANY), new ArgumentSlot("value", ValueType.ANY)),
    VAR_GET_FROM_MAP_BY_KEY( ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, GetFromMapByKeyAction.class, Material.MINECART, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("map", ValueType.VARIABLE), new ArgumentSlot("key", ValueType.ANY)),
    VAR_GET_KEYS_SET( ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, GetFromMapByKeyAction.class, Material.NAME_TAG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("map", ValueType.VARIABLE)),
    VAR_GET_VALUES_SET( ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, GetFromMapByKeyAction.class, Material.APPLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("map", ValueType.VARIABLE)),

    VAR_CLONE_ITEM( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, CloneItemAction.class, Material.ITEM_FRAME, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_SET_ITEM_DISPLAY_NAME( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemDisplayNameAction.class, Material.NAME_TAG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT)),
    VAR_SET_ITEM_AMOUNT( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemAmountAction.class, Material.PUMPKIN_SEEDS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("amount", ValueType.NUMBER)),
    VAR_SET_ITEM_LORE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemLoreAction.class, Material.WRITABLE_BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_ADD_ITEM_LORE_LINE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, AddItemLoreLineAction.class, Material.FEATHER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("line", ValueType.TEXT)),
    VAR_SET_ITEM_LORE_LINE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemLoreLineAction.class, Material.CHICKEN_SPAWN_EGG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("index", ValueType.NUMBER), new ArgumentSlot("line", ValueType.TEXT)),
    VAR_SET_ITEM_PAGES( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemPagesAction.class, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("list", ValueType.VARIABLE)),
    //VAR_SET_ITEM_PAGE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, null, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("index", ValueType.NUMBER), new ArgumentSlot("page", ValueType.TEXT)),
    VAR_ADD_ITEM_PAGE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, AddItemPageAction.class, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("page", ValueType.TEXT)),
    VAR_SET_ITEM_DAMAGE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemDamageAction.class, Material.STICK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("damage", ValueType.NUMBER), new ParameterSlot("add")),
    VAR_SET_ITEM_MAX_DAMAGE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemMaxDamageAction.class, Material.WOODEN_PICKAXE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("damage", ValueType.NUMBER), new ParameterSlot("add")),

    VAR_GET_ITEM_DAMAGE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemDamageAction.class, Material.REDSTONE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_MAX_DAMAGE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemMaxDamageAction.class, Material.REDSTONE_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_AMOUNT( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemAmountAction.class, Material.MELON_SEEDS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_DISPLAY_NAME( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemDisplayNameAction.class, Material.NAME_TAG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_LORE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemLoreAction.class, Material.BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_LORE_LINE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemLoreLineAction.class, Material.WRITABLE_BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("index", ValueType.NUMBER)),
    //VAR_GET_ITEM_PAGES( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, null, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),


    /**
     * <h1>Variable Conditions.</h1>
     */

    IF_VAR_EQUALS(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VariableEqualsCondition.class, Material.NETHERITE_INGOT, new ArgumentSlot("first", ValueType.ANY), new ArgumentSlot("second", ValueType.ANY)),
    IF_VAR_IS_NULL(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VariableIsNullCondition.class, Material.STRUCTURE_VOID, new ArgumentSlot("values", ValueType.ANY, (byte) 18), new ParameterSlot("all")),
    IF_VAR_EXISTS(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VariableExistsCondition.class, Material.MAGMA_CREAM, new ArgumentSlot("variables", ValueType.VARIABLE, (byte) 18), new ParameterSlot("all")),

    IF_VAR_TEXT_CONTAINS(ActionCategory.VARIABLE_CONDITION, MenusCategory.TEXT_OPERATIONS, null, Material.BOOK, new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("contains", ValueType.TEXT)),
    IF_VAR_TEXT_STARTS_WITH(ActionCategory.VARIABLE_CONDITION, MenusCategory.TEXT_OPERATIONS, null, Material.BOOK, new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("contains", ValueType.TEXT)),
    IF_VAR_TEXT_ENDS_WITH(ActionCategory.VARIABLE_CONDITION, MenusCategory.TEXT_OPERATIONS, null, Material.BOOK, new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("contains", ValueType.TEXT)),

    IF_VAR_NUMBER_GREATER(ActionCategory.VARIABLE_CONDITION, MenusCategory.NUMBER_OPERATIONS, NumberGreaterCondition.class, Material.NETHERITE_INGOT, new ArgumentSlot("first", ValueType.NUMBER), new ParameterSlot("equals", Material.BRICK, Material.BRICKS), new ArgumentSlot("second", ValueType.NUMBER)),
    IF_VAR_NUMBER_LESS(ActionCategory.VARIABLE_CONDITION, MenusCategory.NUMBER_OPERATIONS, NumberLessCondition.class, Material.NETHERITE_INGOT, new ArgumentSlot("first", ValueType.NUMBER), new ParameterSlot("equals", Material.NETHER_BRICK, Material.NETHER_BRICKS), new ArgumentSlot("second", ValueType.NUMBER)),
    IF_VAR_NUMBER_IN_RANGE(ActionCategory.VARIABLE_CONDITION, MenusCategory.NUMBER_OPERATIONS, NumberInRangeCondition.class, Material.NETHERITE_INGOT, new ArgumentSlot("min", ValueType.NUMBER), new ParameterSlot("min-equals", Material.NETHER_BRICK, Material.NETHER_BRICKS), new ArgumentSlot("number", ValueType.NUMBER), new ParameterSlot("max-equals", Material.BRICK, Material.BRICKS), new ArgumentSlot("max", ValueType.NUMBER)),

    IF_VAR_LIST_IS_EMPTY(ActionCategory.VARIABLE_CONDITION, MenusCategory.LIST_OPERATIONS, null, Material.NETHERITE_INGOT, new ArgumentSlot("list", ValueType.VARIABLE)),
    IF_VAR_LIST_CONTAINS(ActionCategory.VARIABLE_CONDITION, MenusCategory.LIST_OPERATIONS, null, Material.NETHERITE_INGOT, new ArgumentSlot("list", ValueType.VARIABLE), new ArgumentSlot("value", ValueType.ANY)),

    /**
     * <h1>Other Actions.</h1>
     */

    //HANDLER_CATCH_ERROR(ActionCategory.HANDLER_ACTION, MenusCategory.OTHER, CatchErrorAction.class, Material.RED_DYE, new ArgumentSlot("variable", ValueType.VARIABLE)),
    //HANDLER_MEASURE_TIME(ActionCategory.HANDLER_ACTION, MenusCategory.OTHER, MeasureTimeAction.class, Material.CLOCK, new ArgumentSlot("variable", ValueType.VARIABLE)),

    //REPEAT_ALWAYS(ActionCategory.REPEAT_ACTION, MenusCategory.OTHER, RepeatAlwaysAction.class, Material.BEACON),
    //REPEAT_FOR_LOOP(ActionCategory.REPEAT_ACTION, MenusCategory.OTHER, RepeatForLoopAction.class, Material.SLIME_BALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ParameterSlot("type", Arrays.asList("less","less-equals","greater","greater-equals"), Material.BRICK, Material.BRICKS, Material.NETHER_BRICK, Material.NETHER_BRICKS), new ArgumentSlot("range", ValueType.NUMBER), new ArgumentSlot("add", ValueType.NUMBER)),

    LAUNCH_FUNCTION(ActionCategory.LAUNCH_FUNCTION_ACTION, MenusCategory.OTHER, LaunchFunctionAction.class, Material.LAPIS_ORE);


    final Class<? extends Action> actionClass;
    private final ActionCategory category;
    private final MenusCategory menusCategory;
    private final Material material;
    private final boolean selectionMustBeInWorld;
    private final String requiredPlugin;
    ArgumentSlot[] layout;

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material) {
        this.actionClass = actionClass;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.selectionMustBeInWorld = true;
        this.requiredPlugin = null;
    }

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material, String requiredPlugin, ArgumentSlot... argumentSlots) {
        this.actionClass = actionClass;
        this.layout = argumentSlots;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.selectionMustBeInWorld = true;
        this.requiredPlugin = requiredPlugin;
    }

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material, ArgumentSlot... argumentSlots) {
        this.actionClass = actionClass;
        this.layout = argumentSlots;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.selectionMustBeInWorld = true;
        this.requiredPlugin = null;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }

    public boolean isSelectionMustBeInWorld() {
        return selectionMustBeInWorld;
    }

    public boolean isChestRequired() {
        return layout != null;
    }

    public final String getLocaleName() {
        String path = "items.developer." + (isCondition() ? "conditions" : "actions") + "." + this.name().toLowerCase().replace("_","-") + ".name";
        if (messageExists(path)) {
            return getLocaleMessage(path);
        } else {
            return this.name().toLowerCase().replace("_","-");
        }
    }

    public ArgumentSlot[] getArgumentsSlots() {
        return layout;
    }

    public byte getArgumentSlotID(ArgumentSlot slot) {
        for (byte i = 0; i < this.getArgumentsSlots().length; i++) {
            if (this.getArgumentsSlots()[i] == slot) return (byte) (i+1);
        }
        return -1;
    }


    public boolean isCondition() {
        return category.isCondition();
    }

    public static ActionType getType(Block block) {
        if (block.getType() == Material.LAPIS_ORE) {
            return LAUNCH_FUNCTION;
        }
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        String signLine = getSignLine(signBlock.getLocation(), (byte) 3);
        if (signLine != null) {
            for (ActionType actionType : values()) {
                if (actionType.name().equals(signLine.toUpperCase())) {
                    return actionType;
                }
            }
        }
        return null;
    }

    public boolean isDisabled() {
        return getActionClass() == null || (requiredPlugin != null && !HookUtils.isPluginEnabled(requiredPlugin));
    }

    public String getRequiredPlugin() {
        return requiredPlugin;
    }

    public static Set<MenusCategory> getMenusCategories(ActionCategory category) {
        Set<MenusCategory> set = new HashSet<>();
        for (ActionType type : values()) {
            if (type.category == category) {
                set.add(type.menusCategory);
            }
        }
        return set;
    }

    public static List<ActionType> getActionsByCategories(ActionCategory actionCategory, MenusCategory menusCategory) {
        List<ActionType> list = new ArrayList<>();
        for (ActionType type : values()) {
            if (type.category == actionCategory && type.menusCategory == menusCategory) {
                list.add(type);
            }
        }
        return list;
    }

    public final ItemStack getIcon() {
        ItemStack icon = createItem(this.material, 1, "items.developer." + (this.isCondition() ? "conditions" : "actions") + "." + this.name().toLowerCase().replace("_","-"));
        if (isDisabled()) {
            icon.setType(Material.RED_STAINED_GLASS);
            addLoreAtEnd(icon,getLocaleMessage("disabled"));
        }
        return icon;
    }

    public ActionCategory getCategory() {
        return category;
    }
}
