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

package mcchickenstudio.creative.coding.menus.layouts;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.variables.ValueType;
import mcchickenstudio.creative.menu.AbstractMenu;
import mcchickenstudio.creative.menu.buttons.ParameterButton;
import mcchickenstudio.creative.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.ItemUtils.*;

/**
 * <h1>Layout</h1>
 * This class represents an inventory menu, that opens
 * if player clicks on coding block chest to fill arguments.
 * @see LayoutMaker
 */
public abstract class Layout extends AbstractMenu {

    protected final ActionType actionType;
    protected final List<Byte> argsSlots = new ArrayList<>();
    protected final List<ParameterButton> parameterButtons = new ArrayList<>();
    protected final ArgumentSlot[] requiredSlots;
    private final Block chestBlock;

    public Layout(byte rows, ActionType actionType, Block chestBlock) {
        super(rows, ChatColor.stripColor(actionType.getLocaleName()));
        this.actionType = actionType;
        this.chestBlock = chestBlock;
        this.requiredSlots = actionType.getArgumentsSlots();
    }

    protected void fillDecorationItems() {
        for (byte slot = 0; slot < (byte) (getRows()*9); slot++) {
            setItem(slot,DECORATION_PANE_ITEM);
        }
    }

    @Override
    public void fillItems(Player player) {
        fillDecorationItems();
        fillVarsItems();
    }

    protected abstract void fillVarsItems();

    protected ItemStack getFromContent(byte slot) {
        if (!(chestBlock.getState() instanceof Chest chest)) return ItemStack.empty();
        if (slot < 0 || slot >= chest.getBlockInventory().getContents().length) return ItemStack.empty();
        return chest.getBlockInventory().getContents()[slot];
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) return;
        ItemStack currentItem = event.getCursor();
        if (argsSlots.contains((byte) event.getRawSlot())) {
            ItemStack argItem = event.getClickedInventory().getItem(event.getRawSlot());
            for (ParameterButton parameter : parameterButtons) {
                if (itemEquals(argItem,parameter.getItem())) {
                    event.setCancelled(true);
                    if (getValueType(currentItem) == ValueType.VARIABLE) {
                        event.getClickedInventory().setItem(event.getRawSlot(),currentItem);
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_VAULT_ACTIVATE,100f,0.7f);
                    } else {
                        parameter.next();
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE,100f,1.7f);
                        event.getClickedInventory().setItem(event.getRawSlot(),parameter.getItem());
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(),Sound.BLOCK_BARREL_OPEN,100,0.6f);
    }

    @Override
    public final void onClose(InventoryCloseEvent event) {
        saveArgumentsItems(event.getInventory());
        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(),Sound.BLOCK_BARREL_CLOSE,100,0.6f);
    }

    private void saveArgumentsItems(Inventory inventory) {
        int chestSlot = 0;
        for (byte argSlot : argsSlots) {
            if (!(chestBlock.getState() instanceof Chest)) continue;
            ItemStack argItem = inventory.getItem(argSlot);
            for (ParameterButton rb : parameterButtons) {
                if (argItem == null) continue;
                ItemStack itemStack = argItem.clone();
                for (ItemFlag flag : itemStack.getItemFlags()) {
                    itemStack.removeItemFlags(flag);
                }
                if (itemStack.equals(rb.getItem(true))) {
                    if (argItem.hasItemMeta()) {
                        ItemMeta itemMeta;
                        if (rb.getCurrentValue() instanceof Byte) {
                            argItem.setType(Material.SLIME_BALL);
                            itemMeta = argItem.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&c")+ rb.getCurrentValue()+".0");
                        } else if (rb.getCurrentValue() instanceof Boolean) {
                            boolean value = (boolean) rb.getCurrentValue();
                            argItem.setType(Material.CLOCK);
                            itemMeta = argItem.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&" + (value ? "a" : "c") + value));
                        } else {
                            argItem.setType(Material.BOOK);
                            itemMeta = argItem.getItemMeta();
                            itemMeta.setDisplayName(rb.getCurrentValue().toString());
                        }
                        itemMeta.lore(null);
                        argItem.setItemMeta(itemMeta);
                        setPersistentData(argItem,getCodingValueKey(), ValueType.getByMaterial(argItem.getType()).name());
                        setPersistentData(argItem,getCodingDoNotDropMeKey(), "1");
                    }
                }
            }
            ((Chest) chestBlock.getState()).getBlockInventory().setItem(chestSlot++,argItem);
            chestBlock.getState().update(true);
        }
    }

    public ArgumentSlot[] getRequiredSlots() {
        return requiredSlots;
    }

    protected void setArgSlotVertical(byte argNumber, byte slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem((byte) (slot-9), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
        setArgSlot(argNumber,slot);
        setItem((byte) (slot+9), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
    }

    protected void setArgSlotHorizontal(byte argNumber, byte slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem((byte) (slot-1), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
        setArgSlot(argumentSlot,slot);
        setItem((byte) (slot+1), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
    }

    protected void setGlass(byte argNumber, byte slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem(slot, argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
    }

    protected void setArgSlot(byte argNumber, byte slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setArgSlot(argumentSlot,slot);
    }

    private byte currentSlot = 0;
    private void setArgSlot(ArgumentSlot argumentSlot, byte slot) {
        ItemStack contentItem = getFromContent(currentSlot++);
        if (argumentSlot.isParameter()) {
            Object value = "";
            if (contentItem != null && contentItem.hasItemMeta()) {
                String display = ChatColor.stripColor(contentItem.getItemMeta().getDisplayName());
                if (contentItem.getType() == Material.SLIME_BALL) {
                    value = Byte.parseByte(display.replace(".0",""));
                } else if (contentItem.getType() == Material.CLOCK) {
                    value = Boolean.parseBoolean(display);
                } else {
                    value = display;
                }
            }
            ParameterButton rb = createParamButton((ParameterSlot) argumentSlot,value);
            if (contentItem != null && getValueType(contentItem) == ValueType.VARIABLE) {
                setItem(slot,contentItem);
            } else {
                setItem(slot,rb.getItem());
            }
            parameterButtons.add(rb);
        } else {
            setItem(slot,contentItem);
        }
        argsSlots.add(slot);
    }


    protected ParameterButton createParamButton(ParameterSlot argumentSlot, Object value) {
        String path = "items.developer." + (actionType.isCondition() ? "conditions" : "actions") + "." + actionType.name().toLowerCase().replace("_","-") + ".arguments." + argumentSlot.getPath();
        return new ParameterButton(value, argumentSlot.getValues(),argumentSlot.getPath(),"items.developer",path,argumentSlot.getIcons());
    }

    protected byte getRow(byte slot) {
        if (slot < 9) return (byte) 1;
        else if (slot < 18) return (byte) 2;

        else if (slot < 27) return (byte) 3;
        else if (slot < 36) return (byte) 4;
        else if (slot < 45) return (byte) 5;
        else if (slot < 54) return (byte) 6;
        else return (byte) 0;
    }

    protected List<Byte> getRowSlots(byte row) {
        byte lastSlot = (byte) (row * 9 - 1);
        byte firstSlot = (byte) (lastSlot-8);
        List<Byte> slots = new ArrayList<>();
        for (byte slot = firstSlot; slot < lastSlot; slot++) {
            slots.add(slot);
        }
        return slots;
    }

    protected List<Byte> getFreeSlots(byte row) {
        List<Byte> slots = new ArrayList<>();
        for (byte slot : getRowSlots(row)) {
            if (getItem(slot) == DECORATION_PANE_ITEM) slots.add(slot);
        }
        return slots;
    }

    protected List<Byte> getCentredSlots(byte count, byte row) {
        List<Byte> slots = new ArrayList<>();
        switch (count) {
            case 1:
                slots.add((byte) (row*9-5));
                break;
            case 2:
                slots.add((byte) (row*9-7));
                slots.add((byte) (row*9-3));
                break;
            case 3:
                slots.add((byte) (row*9-8));
                slots.add((byte) (row*9-5));
                slots.add((byte) (row*9-2));
                break;
            case 4:
                slots.add((byte) (row*9-8));
                slots.add((byte) (row*9-6));
                slots.add((byte) (row*9-4));
                slots.add((byte) (row*9-2));
                break;
            case 5:
                slots.add((byte) (row*9-9));
                slots.add((byte) (row*9-7));
                slots.add((byte) (row*9-5));
                slots.add((byte) (row*9-3));
                slots.add((byte) (row*9-1));
                break;
            case 6:
                slots.add((byte) (row*9-8));
                slots.add((byte) (row*9-7));
                slots.add((byte) (row*9-6));
                slots.add((byte) (row*9-4));
                slots.add((byte) (row*9-3));
                slots.add((byte) (row*9-2));
                break;
            case 7:
                slots.add((byte) (row*9-8));
                slots.add((byte) (row*9-7));
                slots.add((byte) (row*9-6));
                slots.add((byte) (row*9-5));
                slots.add((byte) (row*9-4));
                slots.add((byte) (row*9-3));
                slots.add((byte) (row*9-2));
                break;
            default:
                slots.add((byte) (row*9-9));
                slots.add((byte) (row*9-8));
                slots.add((byte) (row*9-7));
                slots.add((byte) (row*9-6));
                slots.add((byte) (row*9-5));
                slots.add((byte) (row*9-4));
                slots.add((byte) (row*9-3));
                slots.add((byte) (row*9-2));
                slots.add((byte) (row*9-1));
                break;
        }
        return slots;
    }

    protected byte getArgSlotsSize() {
        byte count = 0;
        for (ArgumentSlot slot : actionType.getArgumentsSlots()) {
            count += slot.getListSize();
        }
        return count;
    }
}
