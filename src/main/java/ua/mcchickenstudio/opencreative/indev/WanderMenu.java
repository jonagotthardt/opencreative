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

package ua.mcchickenstudio.opencreative.indev;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleItemName;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.substring;
import ua.mcchickenstudio.opencreative.indev.ItemBuilder;

public class WanderMenu extends AbstractMenu {

    private final String nickname;
    private final OfflineWander wander;
    private final ItemStack ABOUT_WANDER;
    private final ItemStack CLOSE_ITEM = createItem(Material.BARRIER, 1);
    private final int SLOT_HEAD = 13;
    private final int SLOT_INFO = 22;
    private final int SLOT_TOGGLE_HINTS = 31;
    private final int SLOT_CLOSE = 49;

    public WanderMenu(@NotNull String nickname) {
        super(6, substring(nickname,30));
        this.nickname = nickname;
        this.wander = OpenCreative.getOfflineWander(Bukkit.getOfflinePlayer(nickname).getUniqueId());
        ABOUT_WANDER = getHead();
    }

    public ItemStack getHead() {
        ItemStack item = createItem(Material.PLAYER_HEAD, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(nickname);
            skullMeta.setPlayerProfile(profile);
            item.setItemMeta(skullMeta);
        }
        return item;
    }

    @Override
    public void fillItems(Player player) {
        for (int i = 0; i < getSize(); i++) setItem(DECORATION_PANE_ITEM, i);

        // Head with name
        ItemStack head = new ItemBuilder(ABOUT_WANDER.clone())
                .translate("items.wander.head")
                .parse("%player%", nickname)
                .getItem();
        setItem(SLOT_HEAD, head);

        // Info item
        ItemStack info = new ItemBuilder(Material.WRITABLE_BOOK)
                .translate("items.wander.info")
                .parse("%online%", wander.isOnline() ? getLocaleItemName("items.wander.yes") : getLocaleItemName("items.wander.no"))
                .parse("%last%", wander.getLastPlayedWorldId())
                .parse("%fav%", wander.getFavoriteWorlds().size())
                .getItem();
        setItem(SLOT_INFO, info);

        // Toggle hints
        boolean hidden = wander.isHideHints();
        ItemStack toggle = new ItemBuilder(hidden ? Material.REDSTONE_TORCH : Material.LEVER)
                .translate(hidden ? "items.wander.hints-hidden" : "items.wander.hints-visible")
                .getItem();
        setItem(toggle, SLOT_TOGGLE_HINTS);

        // Close
        ItemStack close = new ItemBuilder(CLOSE_ITEM.clone())
                .translate("items.wander.close")
                .getItem();
        setItem(SLOT_CLOSE, close);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        if (slot == SLOT_CLOSE) {
            event.getWhoClicked().closeInventory();
            return;
        }
        if (slot == SLOT_TOGGLE_HINTS) {
            wander.hideHints = !wander.isHideHints();
            wander.saveData();
            fillItems((Player) event.getWhoClicked());
            return;
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        // No-op for now
    }
}
