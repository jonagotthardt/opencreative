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
import mcchickenstudio.creative.coding.blocks.actions.controlactions.lines.StopCodeLineAction;
import mcchickenstudio.creative.coding.blocks.actions.controlactions.lines.ThrowErrorAction;
import mcchickenstudio.creative.coding.blocks.actions.controlactions.lines.WaitAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.appearance.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.communication.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.inventory.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.movement.KickPlayerAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.movement.SaddleEntityAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.movement.TeleportPlayerAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.params.*;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.state.*;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsBlockEqualsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsLookingAtBlockCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsNearLocationCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.blocks.IsStandingOnBlockCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.inventory.HasItemCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.inventory.HasItemInHandCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.params.MessageEqualsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.params.PlayerNameEqualsCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.state.IsFlyingCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.state.IsLikedWorldCondition;
import mcchickenstudio.creative.coding.blocks.conditions.playerconditions.state.IsSneakingCondition;
import mcchickenstudio.creative.coding.menus.MenusCategory;
import mcchickenstudio.creative.coding.menus.layouts.ArgumentSlot;
import mcchickenstudio.creative.coding.blocks.variables.VariableType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static mcchickenstudio.creative.utils.BlockUtils.getSignLine;
import static mcchickenstudio.creative.utils.ItemUtils.addLoreAtEnd;
import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;
import static mcchickenstudio.creative.utils.MessageUtils.messageExists;
public enum ActionType {

    /**
     * <h1>Player Actions.</h1>
     * Actions with players.
     * <p>Categories: Communication, Inventory, Movement, Params, State, Appearance</p>
     */

    // Communication
    PLAYER_SEND_MESSAGE(                ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, SendMessageAction.class, Material.WRITABLE_BOOK, new ArgumentSlot("messages",VariableType.TEXT,(byte)18), new ArgumentSlot("type",(byte) 1, (byte) 3)),
    PLAYER_SEND_DIALOG(                 ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, SendDialogAction.class, Material.GLOBE_BANNER_PATTERN,         new ArgumentSlot("messages",VariableType.TEXT, (byte) 18),new ArgumentSlot("cooldown",VariableType.NUMBER)),
    PLAYER_SHOW_ACTIONBAR(              ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowActionbarAction.class, Material.BOOK,       new ArgumentSlot("actionbar",VariableType.TEXT, (byte) 18)),
    PLAYER_SHOW_TITLE(                  ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowTitleAction.class, Material.OAK_SIGN, new ArgumentSlot("title",VariableType.TEXT),new ArgumentSlot("subtitle",VariableType.TEXT),new ArgumentSlot("fade-in",VariableType.NUMBER),new ArgumentSlot("stay",VariableType.NUMBER),new ArgumentSlot("fade-out",VariableType.NUMBER)),
    PLAYER_SHOW_ADVANCEMENT(            ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowAdvancementAction.class, Material.EMERALD, new ArgumentSlot("icon",VariableType.ITEM),new ArgumentSlot("style",(byte) 1, (byte) 3),new ArgumentSlot("title",VariableType.TEXT),new ArgumentSlot("message",VariableType.TEXT)),
    PLAYER_CLEAR_CHAT(                  ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ClearChatAction.class, Material.BUCKET         ),
   // PLAYER_SEND_COMPONENT(              ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, null, new ArgumentSlot("")),
    PLAYER_PLAY_SOUND(                  ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, PlaySoundAction.class, Material.MUSIC_DISC_OTHERSIDE, new ArgumentSlot("sound",VariableType.TEXT),new ArgumentSlot("volume",VariableType.NUMBER),new ArgumentSlot("pitch",VariableType.NUMBER)),
    PLAYER_STOP_SOUNDS(                 ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, StopSoundsAction.class, Material.MUSIC_DISC_11,         new ArgumentSlot("sounds",VariableType.TEXT,(byte) 18)),
    PLAYER_SHOW_WIN_SCREEN(             ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowWinScreenAction.class, Material.DRAGON_EGG),
    PLAYER_SHOW_DEMO_SCREEN(            ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowDemoScreenAction.class, Material.FARMLAND),

    //fixme: Create classes

    // Inventory
    PLAYER_GIVE_ITEMS(                  ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GiveItemsAction.class, Material.CHEST_MINECART,  new ArgumentSlot("items",VariableType.ITEM,(byte) 27)),
    PLAYER_SET_ITEMS(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemsAction.class, Material.DARK_OAK_CHEST_BOAT,          new ArgumentSlot("items",VariableType.ITEM,(byte) 27,true)),
    PLAYER_SET_ARMOR(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetArmorAction.class, Material.NETHERITE_CHESTPLATE,          new ArgumentSlot("helmet",VariableType.ITEM),new ArgumentSlot("chestplate",VariableType.ITEM),new ArgumentSlot("leggings",VariableType.ITEM),new ArgumentSlot("boots",VariableType.ITEM),new ArgumentSlot("boolean",(byte) 1, (byte) 2)),
    PLAYER_GIVE_RANDOM_ITEM(            ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GiveRandomItemAction.class, Material.LIME_SHULKER_BOX, new ArgumentSlot("items",VariableType.ITEM, (byte) 27)),
    PLAYER_CLEAR_INVENTORY(             ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, ClearInventoryAction.class, Material.BUCKET),
    PLAYER_REMOVE_ITEMS(             ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, RemoveItemsAction.class, Material.COBWEB, new ArgumentSlot("items",VariableType.ITEM,(byte) 27)),

    PLAYER_SET_SLOT(                    ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetSlotAction.class, Material.SLIME_BALL,        new ArgumentSlot("slot",VariableType.NUMBER)),
    PLAYER_SET_COMPASS_TARGET(          ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetCompassTarget.class, Material.COMPASS,        new ArgumentSlot("location",VariableType.LOCATION)),
    PLAYER_SET_HOTBAR(           ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetHotBarAction.class, Material.BIRCH_CHEST_BOAT,         new ArgumentSlot("items",VariableType.ITEM,(byte) 9,true),new ArgumentSlot("boolean",(byte) 1, (byte) 2)),

    PLAYER_SET_ITEM_COOLDOWN(           ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemCooldownAction.class, Material.CLOCK,         new ArgumentSlot("item",VariableType.ITEM),new ArgumentSlot("cooldown",VariableType.NUMBER)),
    //PLAYER_SET_CURSOR_ITEM(             ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, null, Material.TRIPWIRE_HOOK,         new ArgumentSlot("item",VariableType.ITEM)),
    //PLAYER_SWING_HAND(                  ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, null,Material.SHIELD),
    //PLAYER_DAMAGE_ITEM(                 ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, null, Material.NETHERITE_SCRAP,  new ArgumentSlot("item",VariableType.ITEM),new ArgumentSlot("damage",VariableType.NUMBER)),
    PLAYER_OPEN_SIGN(                   ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenSignAction.class, Material.OAK_SIGN, new ArgumentSlot("location",VariableType.LOCATION), new ArgumentSlot("side", (byte) 1, (byte) 2)),


    // Movement
    PLAYER_TELEPORT(                    ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, TeleportPlayerAction.class, Material.ENDER_PEARL,  new ArgumentSlot("location",VariableType.LOCATION)),
    //PLAYER_RANDOM_TELEPORT(             ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.CHORUS_FRUIT,         new ArgumentSlot("locations",VariableType.LOCATION,(byte)18)),
    //PLAYER_TELEPORT_QUEUE(              ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.ENDER_EYE,          new ArgumentSlot("locations",VariableType.LOCATION,(byte)18),new ArgumentSlot("cooldown",VariableType.NUMBER)),
    PLAYER_KICK(                        ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      KickPlayerAction.class, Material.BARRIER         ),
    PLAYER_SADDLE_ENTITY(               ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      SaddleEntityAction.class, Material.SADDLE,  new ArgumentSlot("entity",VariableType.TEXT)),
    //PLAYER_LAUNCH_VERTICAL(             ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.FIREWORK_ROCKET,         new ArgumentSlot("power",VariableType.NUMBER)),
    //PLAYER_LAUNCH_HORIZONTAL(           ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.CHAINMAIL_BOOTS,         new ArgumentSlot("power",VariableType.NUMBER)),
    //PLAYER_LAUNCH_TO_LOCATION(          ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.MAP,         new ArgumentSlot("location",VariableType.LOCATION)),
    //PLAYER_SET_ROTATION(                ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, null, Material.PLAYER_HEAD, new ArgumentSlot("yaw",VariableType.NUMBER),new ArgumentSlot("pitch",VariableType.NUMBER)),
    //PLAYER_SET_SPECTATOR_TARGET(        ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, null, Material.SKELETON_SKULL, new ArgumentSlot("entity",VariableType.TEXT)),

    // Params
    PLAYER_SET_HEALTH(                  ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetHealthAction.class, Material.APPLE,             new ArgumentSlot("health",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_HUNGER(                  ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SendMessageAction.class, Material.COOKED_CHICKEN,  new ArgumentSlot("hunger",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_WALK_SPEED(              ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetWalkSpeedAction.class, Material.CHAINMAIL_BOOTS, new ArgumentSlot("speed",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_FLY_SPEED(               ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFlySpeedAction.class, Material.FEATHER, new ArgumentSlot("speed",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_MAX_HEALTH(              ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetMaxHealthAction.class, Material.GOLDEN_APPLE, new ArgumentSlot("health",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_FIRE_TICKS(              ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFireTicksAction.class, Material.CAMPFIRE,  new ArgumentSlot("ticks",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_FREEZE_TICKS(            ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFreezeTicksAction.class, Material.ICE, new ArgumentSlot("ticks",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_NO_DAMAGE_TICKS(         ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetNoDamageTicksAction.class, Material.TOTEM_OF_UNDYING, new ArgumentSlot("ticks",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_EXP(                     ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,  SetExpAction.class, Material.SLIME_SPAWN_EGG,  new ArgumentSlot("exp",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_EXP_LEVEL(               ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetExpLevelAction.class, Material.SUGAR_CANE, new ArgumentSlot("level",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_TOTAL_EXPERIENCE(        ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetTotalExpAction.class, Material.EXPERIENCE_BOTTLE, new ArgumentSlot("exp",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    //PLAYER_GIVE_POTION_EFFECTS(         ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,  null, Material.POTION,         new ArgumentSlot("potions",VariableType.POTION,(byte) 18)),
    PLAYER_CLEAR_POTION_EFFECTS(        ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, ClearPotionEffectsAction.class, Material.MILK_BUCKET,         new ArgumentSlot("potions",VariableType.POTION)),
    //PLAYER_REMOVE_POTION_EFFECT(        ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, null, Material.GLASS_BOTTLE,         new ArgumentSlot("potions",VariableType.POTION, (byte) 18)),
    PLAYER_SET_FLYING_FALL_DAMAGE(      ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,  SetFlyingFallDamageAction.class, Material.RABBIT_HIDE,  new ArgumentSlot("boolean",(byte) 1, (byte) 2)),
    PLAYER_SET_FALL_DISTANCE(           ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetFallDistanceAction.class, Material.RABBIT_FOOT, new ArgumentSlot("distance",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_LAST_DAMAGE(             ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetLastDamageAction.class, Material.REDSTONE, new ArgumentSlot("damage",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_CAN_PICKUP_ITEM(         ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetCanPickupItem.class, Material.GLOWSTONE_DUST, new ArgumentSlot("boolean",(byte) 1, (byte) 2)),
    PLAYER_SET_ARROWS_IN_BODY(          ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetArrowsInBodyAction.class, Material.ARROW, new ArgumentSlot("count",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_SHIELD_BLOCKING_DELAY(   ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetShieldBlockingDelay.class, Material.SHIELD, new ArgumentSlot("delay",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_BEE_STINGER_COOLDOWN(    ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS,        SetBeeStingerCooldownAction.class, Material.BEE_NEST, new ArgumentSlot("cooldown",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),
    PLAYER_SET_MAXIMUM_NO_DAMAGE_TICKS( ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetMaxNoDamageTicksAction.class, Material.ENCHANTED_GOLDEN_APPLE, new ArgumentSlot("ticks",VariableType.NUMBER),new ArgumentSlot("param",(byte) 1, (byte) 2)),

    // State
    PLAYER_SET_GAMEMODE(                ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGameModeAction.class, Material.CRAFTING_TABLE,  new ArgumentSlot("game-mode", (byte) 1, (byte) 4)),
    PLAYER_SET_FLYING(                  ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetFlyingAction.class, Material.FEATHER, new ArgumentSlot("boolean",(byte) 1,(byte) 2)),
    PLAYER_SET_GLOWING(                 ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGlowingAction.class, Material.DRAGON_BREATH,  new ArgumentSlot("glowing",(byte) 1, (byte) 2)),
    PLAYER_SET_GLIDING(                 ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGlidingAction.class, Material.ELYTRA, new ArgumentSlot("boolean",(byte) 1,(byte) 2)),
    PLAYER_SET_SPRINTING(               ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetSprintingAction.class, Material.GOLDEN_BOOTS, new ArgumentSlot("boolean",(byte) 1,(byte) 2)),

    // Appearance
    PLAYER_SET_TIME(                    ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetTimeAction.class, Material.CLOCK, new ArgumentSlot("time",VariableType.NUMBER),new ArgumentSlot("relative",(byte) 1,(byte) 2)),
    PLAYER_SET_WEATHER(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetWeatherAction.class, Material.WATER_BUCKET, new ArgumentSlot("weather",(byte) 1,(byte) 2)),
    //PLAYER_SPAWN_PARTICLE(              ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, null, Material.NETHER_STAR, new ArgumentSlot("particle",VariableType.PARTICLE,(byte) 18),new ArgumentSlot("location",VariableType.LOCATION),new ArgumentSlot("count",VariableType.NUMBER)),
    PLAYER_SET_RESOURCE_PACK(           ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetResourcePackAction.class, Material.SHROOMLIGHT, new ArgumentSlot("url",VariableType.TEXT)),
    PLAYER_SET_WORLD_BORDER(            ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetWorldBorderAction.class, Material.END_CRYSTAL, new ArgumentSlot("center",VariableType.LOCATION), new ArgumentSlot("radius",VariableType.NUMBER), new ArgumentSlot("time",VariableType.NUMBER), new ArgumentSlot("damage",VariableType.NUMBER), new ArgumentSlot("warning-distance",VariableType.NUMBER), new ArgumentSlot("warning-time",VariableType.NUMBER), new ArgumentSlot("safe-distance",VariableType.NUMBER)),
    PLAYER_SEND_SIGN_CHANGE(            ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SendSignChangeAction.class, Material.OAK_SIGN, new ArgumentSlot("location",VariableType.LOCATION), new ArgumentSlot("text",VariableType.TEXT), new ArgumentSlot("number",VariableType.NUMBER)),
    PLAYER_SHOW_ENTITY(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowEntityAction.class, Material.PIGLIN_HEAD, new ArgumentSlot("entity",VariableType.TEXT)),
    PLAYER_HIDE_ENTITY(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HideEntityAction.class, Material.SKELETON_SKULL, new ArgumentSlot("entity",VariableType.TEXT)),
    PLAYER_SHOW_PLAYER(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowPlayerAction.class, Material.PLAYER_HEAD, new ArgumentSlot("player",VariableType.TEXT)),
    PLAYER_HIDE_PLAYER(                 ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HidePlayerAction.class, Material.WITHER_SKELETON_SKULL, new ArgumentSlot("player",VariableType.TEXT)),
    PLAYER_SHOW_ELDER_GUARDIAN(         ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowElderGuardianAction.class, Material.ELDER_GUARDIAN_SPAWN_EGG, new ArgumentSlot("boolean",(byte) 1,(byte) 2)),

    /**
     * <h1>Player Conditions.</h1>
     */

    IF_PLAYER_STANDS_ON_BLOCK(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsStandingOnBlockCondition.class, Material.GRASS_BLOCK, new ArgumentSlot("blocks",VariableType.ITEM,(byte) 9)),
    IF_PLAYER_LOOKS_AT_BLOCK(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsLookingAtBlockCondition.class, Material.CHEST, new ArgumentSlot("blocks",VariableType.ITEM,(byte) 9)),
    IF_PLAYER_BLOCK_EQUALS(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsBlockEqualsCondition.class, Material.OAK_LOG, new ArgumentSlot("blocks",VariableType.ITEM,(byte) 9)),
    IF_PLAYER_IS_NEAR_LOCATION(                   ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsNearLocationCondition.class, Material.COMPASS, new ArgumentSlot("locations",VariableType.LOCATION,(byte) 18),new ArgumentSlot("distance",VariableType.NUMBER)),



    IF_PLAYER_NAME_EQUALS(                 ActionCategory.PLAYER_CONDITION, MenusCategory.PARAMS, PlayerNameEqualsCondition.class, Material.NAME_TAG, new ArgumentSlot("names",VariableType.TEXT,(byte) 18), new ArgumentSlot("boolean",(byte) 1, (byte) 2)),
    IF_PLAYER_MESSAGE_EQUALS(              ActionCategory.PLAYER_CONDITION, MenusCategory.PARAMS, MessageEqualsCondition.class, Material.BOOK, new ArgumentSlot("messages",VariableType.TEXT,(byte) 18), new ArgumentSlot("boolean",(byte) 1, (byte) 2)),
    IF_PLAYER_LIKED_WORLD(                 ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsLikedWorldCondition.class, Material.GOLDEN_APPLE),
    IF_PLAYER_HAS_ITEM(                 ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemCondition.class, Material.CHEST_MINECART, new ArgumentSlot("items",VariableType.ITEM,(byte) 18)),
    IF_PLAYER_HAS_ITEM_IN_HAND(                 ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemInHandCondition.class, Material.WOODEN_SWORD, new ArgumentSlot("items",VariableType.ITEM,(byte) 18), new ArgumentSlot("hand",(byte) 1, (byte) 4)),


    //ELSE(ActionCategory.ELSE_CONDITION,null,null,null),
    CONTROL_THROW_ERROR(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, ThrowErrorAction.class, Material.TNT_MINECART, new ArgumentSlot("message", VariableType.TEXT)),
    CONTROL_STOP_CODE_LINE(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, StopCodeLineAction.class, Material.STRUCTURE_VOID),
    CONTROL_WAIT(                 ActionCategory.CONTROL_ACTION, MenusCategory.LINES, WaitAction.class, Material.CLOCK, new ArgumentSlot("time",VariableType.NUMBER)),
    CONTROL_CANCEL_EVENT(                 ActionCategory.CONTROL_ACTION, MenusCategory.EVENTS, CancelEventAction.class, Material.BARRIER),


    IF_PLAYER_IS_SNEAKING(                 ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsSneakingCondition.class, Material.RABBIT),
    IF_PLAYER_IS_FLYING(                   ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsFlyingCondition.class, Material.FEATHER);


    final Class<? extends Action> actionClass;
    private final ActionCategory category;
    private final MenusCategory menusCategory;
    private final Material material;
    private final boolean selectionMustBeInWorld;
    ArgumentSlot[] layout;

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material) {
        this.actionClass = actionClass;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.selectionMustBeInWorld = true;
    }

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material, ArgumentSlot... argumentSlots) {
        this.actionClass = actionClass;
        this.layout = argumentSlots;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.selectionMustBeInWorld = true;
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

    public boolean isMultiAction() {
        return false;
    }

    public boolean isCondition() {
        return category.name().toLowerCase().contains("condition");
    }

    public static ActionType getType(Block block) {
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
        return getActionClass() == null;
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

    public static List<ActionType> getActionsByCategories(ActionCategory executorCategory, MenusCategory menusCategory) {
        List<ActionType> list = new ArrayList<>();
        for (ActionType type : values()) {
            if (type.category == executorCategory && type.menusCategory == menusCategory) {
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


}
