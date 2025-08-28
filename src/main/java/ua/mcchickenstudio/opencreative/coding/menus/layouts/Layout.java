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

package ua.mcchickenstudio.opencreative.coding.menus.layouts;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.sendClosedChestAnimation;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.sendOpenedChestAnimation;

/**
 * <h1>Layout</h1>
 * This class represents an inventory menus, that opens
 * if player clicks on coding block chest to fill arguments.
 * @see LayoutMaker
 */
public abstract class Layout extends AbstractMenu {

    protected final ActionType actionType;
    protected final List<Integer> argsSlots = new ArrayList<>();
    protected final List<ParameterButton> parameterButtons = new ArrayList<>();
    protected final ArgumentSlot[] requiredSlots;
    private final Block containerBlock;
    private final Set<Player> viewers = new HashSet<>();

    public Layout(int rows, ActionType actionType, Block chestBlock) {
        super(rows, ChatColor.stripColor(actionType.getLocaleName()));
        this.actionType = actionType;
        this.containerBlock = chestBlock;
        this.requiredSlots = actionType.getArgumentsSlots();
    }

    protected void fillDecorationItems() {
        for (int slot = 0; slot < (getRows()*9); slot++) {
            setItem(slot,DECORATION_PANE_ITEM);
        }
    }

    @Override
    public void fillItems(Player player) {
        fillDecorationItems();
        fillVarsItems();
    }

    protected abstract void fillVarsItems();

    protected ItemStack getFromContent(int slot) {
        if (!(containerBlock.getState() instanceof InventoryHolder container)) return ItemStack.empty();
        if (slot < 0 || slot >= container.getInventory().getContents().length) return ItemStack.empty();
        return container.getInventory().getContents()[slot];
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        ItemStack currentItem = event.getCursor();
        if (argsSlots.contains(event.getRawSlot())) {
            ItemStack argItem = inventory.getItem(event.getRawSlot());
            for (ParameterButton parameter : parameterButtons) {
                if (itemEquals(argItem,parameter.getItem())) {
                    event.setCancelled(true);
                    if (getValueType(currentItem) == ValueType.VARIABLE) {
                        inventory.setItem(event.getRawSlot(),currentItem);
                        Sounds.DEV_VARIABLE_PARAMETER.play(event.getWhoClicked());
                    } else {
                        parameter.next();
                        Sounds.DEV_NEXT_PARAMETER.play(event.getWhoClicked());
                        inventory.setItem(event.getRawSlot(),parameter.getItem());
                    }
                }
            }
            /*if (!event.isCancelled()) {
                event.setCancelled(true);
                inventory.setItem(event.getRawSlot(),currentItem);
                event.getWhoClicked().setItemOnCursor(inventory.getItem(event.getRawSlot()));
                *//*
            }*/
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        viewers.add((Player) event.getPlayer());
        (containerBlock.getType() == Material.BARREL ? Sounds.DEV_OPEN_BARREL : Sounds.DEV_OPEN_CHEST).play(event.getPlayer());
        for (Player onlinePlayer : event.getPlayer().getWorld().getPlayers()) {
            sendOpenedChestAnimation(onlinePlayer,containerBlock);
        }
    }

    @Override
    public final void onClose(@NotNull InventoryCloseEvent event) {
        saveArgumentsItems(event.getInventory());
        (containerBlock.getType() == Material.BARREL ? Sounds.DEV_CLOSED_BARREL : Sounds.DEV_CLOSED_CHEST).play(event.getPlayer());
        viewers.remove((Player) event.getPlayer());
        if (viewers.isEmpty()) {
            DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet((Player) event.getPlayer());
            if (devPlanet != null) {
                devPlanet.setCodeChanged(true);
                devPlanet.unregisterOpenedMenu(containerBlock.getLocation());
                for (Player onlinePlayer : event.getPlayer().getWorld().getPlayers()) {
                    sendClosedChestAnimation(onlinePlayer,containerBlock);
                }
            }
            destroy();
        }
    }

    private void saveArgumentsItems(Inventory inventory) {
        if (!(containerBlock.getState() instanceof InventoryHolder container)) return;
        int chestSlot = 0;
        for (int argSlot : argsSlots) {
            ItemStack argItem = inventory.getItem(argSlot);
            container.getInventory().setItem(chestSlot,argItem);
            for (ParameterButton rb : parameterButtons) {
                if (argItem == null) continue;
                ItemStack itemStack = argItem.clone();
                for (ItemFlag flag : itemStack.getItemFlags()) {
                    itemStack.removeItemFlags(flag);
                }
                if (itemStack.equals(rb.getItem(true))) {
                    if (itemStack.hasItemMeta()) {
                        ItemMeta itemMeta;
                        if (rb.getCurrentValue() instanceof Integer) {
                            itemStack.setType(Material.SLIME_BALL);
                            itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&a")+ rb.getCurrentValue()+".0");
                        } else if (rb.getCurrentValue() instanceof Boolean) {
                            boolean value = (boolean) rb.getCurrentValue();
                            itemStack.setType(Material.CLOCK);
                            itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&" + (value ? "a" : "c") + value));
                        } else {
                            itemStack.setType(Material.BOOK);
                            itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(rb.getCurrentValue().toString());
                        }
                        itemMeta.lore(null);
                        itemStack.setItemMeta(itemMeta);
                        setPersistentData(itemStack,getCodingValueKey(), ValueType.getByMaterial(itemStack.getType()).name());
                        setPersistentData(itemStack,getCodingDoNotDropMeKey(), "1");
                        container.getInventory().setItem(chestSlot,itemStack);
                    }
                }
            }
            chestSlot++;
        }
        containerBlock.getState().update(true);
    }

    public ArgumentSlot[] getRequiredSlots() {
        return requiredSlots;
    }

    protected void setArgSlotVertical(int argNumber, int slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem((slot-9), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
        setArgSlot(argNumber,slot);
        setItem((slot+9), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
    }

    protected void setArgSlotHorizontal(int argNumber, int slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem((slot-1), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
        setArgSlot(argumentSlot,slot);
        setItem((slot+1), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
    }

    protected void setArgSlotCross(int argNumber, int slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem((slot-9), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
        setItem((slot-1), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
        setArgSlot(argumentSlot,slot);
        setItem((slot+1), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
        setItem((slot+9), argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
    }

    protected void setGlass(int argNumber, int slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem(slot, argumentSlot.getVarType().getGlassItem(actionType,argumentSlot.getPath()));
    }

    protected void setArgSlot(int argNumber, int slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setArgSlot(argumentSlot,slot);
    }

    private int currentSlot = 0;
    private void setArgSlot(ArgumentSlot argumentSlot, int slot) {
        ItemStack contentItem = getFromContent(currentSlot++);
        if (argumentSlot.isParameter()) {
            Object value = "";
            if (contentItem != null && contentItem.hasItemMeta()) {
                String display = ChatColor.stripColor(contentItem.getItemMeta().getDisplayName());
                if (contentItem.getType() == Material.SLIME_BALL) {
                    value = Integer.parseInt(display.replace(".0",""));
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

    public List<Integer> getArgsSlots() {
        return argsSlots;
    }

    protected ParameterButton createParamButton(ParameterSlot argumentSlot, Object value) {
        String path = "items.developer." + (actionType.isCondition() ? "conditions" : "actions") + "." + actionType.name().toLowerCase().replace("_","-") + ".arguments." + argumentSlot.getPath();
        return new ParameterButton(value, argumentSlot.getValues(),argumentSlot.getPath(),"items.developer",path,argumentSlot.getIcons());
    }

    protected int getRow(int slot) {
        if (slot < 9) return 1;
        else if (slot < 18) return 2;

        else if (slot < 27) return 3;
        else if (slot < 36) return 4;
        else if (slot < 45) return 5;
        else if (slot < 54) return 6;
        else return 0;
    }

    protected List<Integer> getRowSlots(int row) {
        int lastSlot = (row * 9 - 1);
        int firstSlot = (lastSlot-8);
        List<Integer> slots = new ArrayList<>();
        for (int slot = firstSlot; slot < lastSlot; slot++) {
            slots.add(slot);
        }
        return slots;
    }

    protected List<Integer> getFreeSlots(int row) {
        List<Integer> slots = new ArrayList<>();
        for (int slot : getRowSlots(row)) {
            if (getItem(slot) == DECORATION_PANE_ITEM) slots.add(slot);
        }
        return slots;
    }

    protected List<Integer> getCentredSlots(int count, int row) {
        List<Integer> slots = new ArrayList<>();
        switch (count) {
            case 1:
                slots.add((row*9-5));
                break;
            case 2:
                slots.add((row*9-7));
                slots.add((row*9-3));
                break;
            case 3:
                slots.add((row*9-8));
                slots.add((row*9-5));
                slots.add((row*9-2));
                break;
            case 4:
                slots.add((row*9-8));
                slots.add((row*9-6));
                slots.add((row*9-4));
                slots.add((row*9-2));
                break;
            case 5:
                slots.add((row*9-9));
                slots.add((row*9-7));
                slots.add((row*9-5));
                slots.add((row*9-3));
                slots.add((row*9-1));
                break;
            case 6:
                slots.add((row*9-8));
                slots.add((row*9-7));
                slots.add((row*9-6));
                slots.add((row*9-4));
                slots.add((row*9-3));
                slots.add((row*9-2));
                break;
            case 7:
                slots.add((row*9-8));
                slots.add((row*9-7));
                slots.add((row*9-6));
                slots.add((row*9-5));
                slots.add((row*9-4));
                slots.add((row*9-3));
                slots.add((row*9-2));
                break;
            case 8:
                slots.add((row*9-9));
                slots.add((row*9-8));
                slots.add((row*9-7));
                slots.add((row*9-6));
                slots.add((row*9-4));
                slots.add((row*9-3));
                slots.add((row*9-2));
                slots.add((row*9-1));
                break;
            default:
                slots.add((row*9-9));
                slots.add((row*9-8));
                slots.add((row*9-7));
                slots.add((row*9-6));
                slots.add((row*9-5));
                slots.add((row*9-4));
                slots.add((row*9-3));
                slots.add((row*9-2));
                slots.add((row*9-1));
                break;
        }
        return slots;
    }

    protected int getArgSlotsSize() {
        int count = 0;
        for (ArgumentSlot slot : actionType.getArgumentsSlots()) {
            count += slot.getListSize();
        }
        return count;
    }

    public Set<Player> getViewers() {
        return viewers;
    }
}
