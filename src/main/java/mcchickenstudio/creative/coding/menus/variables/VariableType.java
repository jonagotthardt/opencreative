package mcchickenstudio.creative.coding.menus.variables;

import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static mcchickenstudio.creative.utils.ItemUtils.createItem;
import static mcchickenstudio.creative.utils.MessageUtils.messageExists;

public enum VariableType {

    TEXT(Material.BOOK, Material.BROWN_STAINED_GLASS_PANE),
    NUMBER(Material.SLIME_BALL, Material.LIME_STAINED_GLASS_PANE),
    LOCATION(Material.PAPER, Material.WHITE_STAINED_GLASS_PANE),
    BOOLEAN(Material.CLOCK, Material.YELLOW_STAINED_GLASS_PANE),
    ITEM(Material.ITEM_FRAME, Material.ORANGE_STAINED_GLASS_PANE),
    PARAMETER(Material.ANVIL, Material.BLACK_STAINED_GLASS_PANE);

    private final Material itemMaterial;
    private final Material menuGlass;

    VariableType(Material item, Material menuGlass) {
        this.itemMaterial = item;
        this.menuGlass = menuGlass;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public Material getMenuGlass() {
        return menuGlass;
    }

    public ItemStack getGlassItem() {
        return createItem(getMenuGlass(),1,"items.developer.placeholders."+this.name().toLowerCase());
    }

    public ItemStack getGlassItem(ActionType action, Byte number) {
        String messagePath = "items.developer.actions."+action.name().toLowerCase().replace("_","-")+".placeholders." + number;
        if (messageExists(messagePath+".name") && messageExists(messagePath+".lore")) {
            return createItem(getMenuGlass(),1,messagePath);
        }
        return createItem(getMenuGlass(),1,"items.developer.placeholders."+this.name().toLowerCase());
    }
}
