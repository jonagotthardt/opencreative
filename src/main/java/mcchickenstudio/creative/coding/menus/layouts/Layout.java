package mcchickenstudio.creative.coding.menus.layouts;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.menu.AbstractMenu;
import mcchickenstudio.creative.menu.buttons.RadioButton;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleItemDescription;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleItemName;

public abstract class Layout extends AbstractMenu {

    protected final ActionType actionType;
    protected final List<Byte> argsSlots = new ArrayList<>();
    protected final List<RadioButton> radioButtons = new ArrayList<>();
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
        if (!(chestBlock.getState() instanceof Chest)) return ItemStack.empty();
        Chest chest = (Chest) chestBlock.getState();
        if (slot < 0 || slot >= chest.getBlockInventory().getContents().length) return ItemStack.empty();
        return chest.getBlockInventory().getContents()[slot];
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) return;
        if (argsSlots.contains((byte) event.getRawSlot())) {
            ItemStack argItem = event.getClickedInventory().getItem(event.getRawSlot());
            for (RadioButton rb : radioButtons) {
                if (itemEquals(argItem,rb.getButtonItem())) {
                    event.setCancelled(true);
                    rb.onChoice();
                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE,100f,1.7f);
                    event.getClickedInventory().setItem(event.getRawSlot(),rb.getButtonItem());
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
            for (RadioButton rb : radioButtons) {
                if (itemEquals(argItem,rb.getButtonItem())) {
                    if (argItem.getItemMeta() != null) {
                        argItem.setType(Material.SLIME_BALL);
                        ItemMeta itemMeta = argItem.getItemMeta();
                        itemMeta.setDisplayName(String.valueOf((float) rb.getCurrentChoice()));
                        itemMeta.setLore(new ArrayList<>());
                        argItem.setItemMeta(itemMeta);
                    }
                }
            }
            ((Chest) chestBlock.getState()).getBlockInventory().setItem(chestSlot++,argItem);
            chestBlock.getState().update(true);
        }
    }

    protected ItemStack getGlass(byte argCount) {
        return actionType.getArgumentsSlots()[argCount].getVarType().getGlassItem(actionType,(byte) (argCount+1));
    }

    public ArgumentSlot[] getRequiredSlots() {
        return requiredSlots;
    }

    protected void setArgSlotWithFrame(byte argNumber, byte slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        setItem((byte) (slot-9), argumentSlot.getVarType().getGlassItem(actionType,argNumber));
        setArgSlot(argNumber,slot);
        setItem((byte) (slot+9), argumentSlot.getVarType().getGlassItem(actionType,argNumber));

    }

    protected void setArgSlot(byte argNumber, byte slot) {
        ArgumentSlot argumentSlot = getRequiredSlots()[argNumber-1];
        if (argumentSlot.getMinParameter() != 0 && getRequiredSlots()[argNumber-1].getMaxParameter() != 0) {
            ItemStack itemArg = getFromContent((byte) (argNumber-1));
            int amount = argumentSlot.getMinParameter();
            if (itemArg.getItemMeta() != null) {
                try {
                    amount = Math.round(Float.parseFloat(itemArg.getItemMeta().getDisplayName()));
                } catch (Exception e) {}
            }
            RadioButton rb = createParamButton(argumentSlot,amount);
            setItem(slot,rb.getButtonItem());
            radioButtons.add(rb);
        } else {
            setItem(slot,getFromContent((byte) (argNumber-1)));
        }
        argsSlots.add(slot);
    }
    protected RadioButton createParamButton(ArgumentSlot argumentSlot, int amount) {
        String path = "items.developer.actions." + actionType.name().toLowerCase().replace("_","-") + ".arguments." + actionType.getArgumentSlotID(argumentSlot);
        return new RadioButton(argumentSlot.getVarType().getItemMaterial(),getLocaleItemName(path+".name"),getLocaleItemDescription(path+".lore"),amount,argumentSlot.getMaxParameter(),
                new ArrayList<>(),path+".choices","items.developer");
    }
}
