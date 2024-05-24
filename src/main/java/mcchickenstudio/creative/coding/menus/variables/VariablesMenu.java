package mcchickenstudio.creative.coding.menus.variables;

import mcchickenstudio.creative.menu.AbstractMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.ItemUtils.itemEquals;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class VariablesMenu extends AbstractMenu  {

    private final ItemStack TEXT_ITEM = createItem(Material.BOOK,1,"menus.developer.variables.items.text");
    private final ItemStack NUMBER_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.number");
    private final ItemStack BOOLEAN_ITEM = createItem(Material.CLOCK,1,"menus.developer.variables.items.boolean");
    private final ItemStack LOCATION_ITEM = createItem(Material.PAPER,1,"menus.developer.variables.items.number");

    private final ItemStack ARRAY_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.array");
    private final ItemStack VARIABLE_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.variable");
    private final ItemStack POTION_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.potion");
    private final ItemStack MAP_ITEM = createItem(Material.SLIME_BALL,1,"menus.developer.variables.items.map");


    public VariablesMenu() {
        super((byte) 1, getLocaleMessage("menus.developer.variables.title"));
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 0,TEXT_ITEM);
        setItem((byte) 1,NUMBER_ITEM);
        setItem((byte) 2,BOOLEAN_ITEM);
        setItem((byte) 3,LOCATION_ITEM);
        setItem((byte) 4,DECORATION_ITEM);
        setItem((byte) 5,DECORATION_ITEM);
        setItem((byte) 6,DECORATION_ITEM);
        setItem((byte) 7,DECORATION_ITEM);
        setItem((byte) 8,DECORATION_ITEM);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null || itemEquals(event.getCurrentItem(), DECORATION_ITEM)) return;
        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),Sound.ENTITY_ALLAY_ITEM_THROWN,100f,2f);
        event.setCursor(null);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 100f, 1f);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {}
}
