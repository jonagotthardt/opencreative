package mcchickenstudio.creative.coding.blocks.actions;

import mcchickenstudio.creative.coding.blocks.actions.playeractions.communication.PlaySoundAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.communication.SendMessageAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.communication.ShowTitleAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.inventory.ClearInventoryAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.movement.SaddleEntityAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.params.SetGameModeAction;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.params.SetHealthAction;
import mcchickenstudio.creative.coding.menus.layouts.ArgumentSlot;
import mcchickenstudio.creative.coding.menus.variables.VariableType;
import mcchickenstudio.creative.utils.MessageUtils;

public enum ActionType {

    SEND_MESSAGE(SendMessageAction.class, (byte) 1, (byte) 2, new ArgumentSlot[]{new ArgumentSlot(VariableType.TEXT,(byte)45)}),
    PLAY_SOUND(PlaySoundAction.class, (byte) 1, (byte) 3, new ArgumentSlot[]{new ArgumentSlot(VariableType.TEXT),new ArgumentSlot(VariableType.NUMBER),new ArgumentSlot(VariableType.NUMBER)}),
    SHOW_TITLE(ShowTitleAction.class, (byte) 1, (byte) 5, new ArgumentSlot[]{new ArgumentSlot(VariableType.TEXT),new ArgumentSlot(VariableType.TEXT),new ArgumentSlot(VariableType.NUMBER),new ArgumentSlot(VariableType.NUMBER),new ArgumentSlot(VariableType.NUMBER)}),

    SADDLE_ENTITY(SaddleEntityAction.class, (byte) 1, (byte) 1, new ArgumentSlot[]{new ArgumentSlot(VariableType.TEXT)}),
    TELEPORT_PLAYER(SendMessageAction.class, (byte) 3, (byte) 3, new ArgumentSlot[]{new ArgumentSlot(VariableType.LOCATION)}),

    GIVE_ITEMS(SendMessageAction.class, (byte) 3, (byte) 1, new ArgumentSlot[]{new ArgumentSlot(VariableType.ITEM,(byte) 54)}),
    CLEAR_INVENTORY(ClearInventoryAction.class),

    SET_GAMEMODE(SetGameModeAction.class, (byte) 1, (byte) 1, new ArgumentSlot[]{new ArgumentSlot((byte) 1, (byte) 4)}),
    SET_HEALTH(SetHealthAction.class, (byte) 1, (byte) 1, new ArgumentSlot[]{new ArgumentSlot(VariableType.NUMBER),new ArgumentSlot((byte) 1, (byte) 2)}),
    SET_HUNGER(SendMessageAction.class, (byte) 1, (byte) 1, new ArgumentSlot[]{new ArgumentSlot(VariableType.NUMBER),new ArgumentSlot((byte) 1, (byte) 2)});

    final Class<? extends Action> actionClass;
    final byte minArgsAmount;
    final byte maxArgsAmount;

    ArgumentSlot[] layout;

    ActionType(Class<? extends Action> actionClass) {
        this.actionClass = actionClass;
        this.minArgsAmount = 0;
        this.maxArgsAmount = 64;
    }

    ActionType(Class<? extends Action> actionClass, byte minArgs, ArgumentSlot[] argumentSlots) {
        this.actionClass = actionClass;
        this.minArgsAmount = minArgs;
        this.maxArgsAmount = 64;
    }

    ActionType(Class<? extends Action> actionClass, byte minArgs, byte maxArgs, ArgumentSlot[] argumentSlots) {
        this.actionClass = actionClass;
        this.minArgsAmount = minArgs;
        this.maxArgsAmount = maxArgs;
        this.layout = argumentSlots;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }

    public boolean isChestRequired() {
        return this.minArgsAmount > 0;
    }

    public byte getMinArgsAmount() {
        return minArgsAmount;
    }

    public byte getMaxArgsAmount() {
        return maxArgsAmount;
    }

    public final String getLocaleName() {
        return MessageUtils.getLocaleMessage("items.developer.actions." + this.name().toLowerCase().replace("_","-") + ".name", false);
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
}
