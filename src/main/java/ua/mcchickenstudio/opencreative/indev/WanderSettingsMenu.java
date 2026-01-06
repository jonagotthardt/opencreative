/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
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
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.modules.ModulesBrowserMenu;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.world.browsers.WorldsBrowserMenu;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

public class WanderSettingsMenu extends AbstractMenu {

    private final String nickname;
    private final OfflineWander wander;

    private final ItemStack ABOUT_WANDER;
    private final ItemStack CHANGE_DESCRIPTION = createItem(Material.BOOK, 1, "menus.profile-settings.items.change-description", "description");
    private final ItemStack CHANGE_SOCIAL_DISCORD = createItem(Material.BLUE_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-discord", "discord");
    private final ItemStack CHANGE_SOCIAL_TWITTER = createItem(Material.CYAN_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-twitter", "twitter");
    private final ItemStack CHANGE_SOCIAL_YOUTUBE = createItem(Material.RED_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-youtube", "youtube");
    private final ItemStack CHANGE_SOCIAL_TELEGRAM = createItem(Material.LIGHT_BLUE_STAINED_GLASS, 1, "menus.profile-settings.items.change-social-telegram", "telegram");

    public WanderSettingsMenu(@NotNull String nickname) {
        super(4, getLocaleMessage("menus.profile-settings.title", false));
        this.nickname = nickname;
        this.wander = OpenCreative.getOfflineWander(Bukkit.getOfflinePlayer(nickname).getUniqueId());
        ABOUT_WANDER = getHead();
    }

    private ItemStack getHead() {
        ItemStack item = createItem(Material.PLAYER_HEAD, 1, "menus.profile-settings.items.player");
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(nickname);
            skullMeta.setPlayerProfile(profile);
            item.setItemMeta(skullMeta);
        }
        List<String> lore = new ArrayList<>();
        OfflinePlayer offlinePlayer = wander.getOfflinePlayer();
        for (String loreLine : MessageUtils.getLocaleItemDescription("menus.player-profile.items.player.lore")) {
            if (loreLine.contains("%description%")) {
                String description = wander.getDescription() == null ? getLocaleMessage("profiles.default-description") : wander.getDescription();
                String[] newLines = description.split("\\\\n");
                for (String newLine : newLines) {
                    lore.add(loreLine.replace("%description%", ChatColor.translateAlternateColorCodes('&', newLine)));
                }
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', parsePAPI(offlinePlayer, loreLine)));
            }
        }
        meta.setLore(lore);
        replacePlaceholderInLore(item, "%player%", nickname);
        replacePlaceholderInLore(item, "%gender%", wander.getGender() == null
                ? OfflineWander.Gender.UNKNOWN.getLocaleName() : wander.getGender().getLocaleName());
        return item;
    }

    @Override
    public void fillItems(Player player) {
        setItem(10, CHANGE_DESCRIPTION);
        setItem(12, CHANGE_SOCIAL_DISCORD);
        setItem(13, CHANGE_SOCIAL_TELEGRAM);
        setItem(14, CHANGE_SOCIAL_TWITTER);
        setItem(15, CHANGE_SOCIAL_YOUTUBE);
        setItem(31, ABOUT_WANDER);
        setItem(createItem(Material.CYAN_STAINED_GLASS_PANE, 1), 29, 33);
        setItem(DECORATION_PANE_ITEM, 27, 28, 34, 35);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        switch (getItemType(item)) {
            case "description" -> {

                new WorldsBrowserMenu(player, OpenCreative.getPlanetsManager().getPlanetsByOwner(nickname))
                        .open(player);
            }
            case "modules" -> {
                new ModulesBrowserMenu(player, new ArrayList<>(OpenCreative.getModuleManager().getPlayerModules(wander.getUniqueId())))
                        .open(player);
            }
            case "favorites" -> {
                Set<Planet> favorites = new LinkedHashSet<>();
                for (int id : wander.getFavoriteWorlds()) {
                    Planet planet = OpenCreative.getPlanetsManager().getPlanetById(String.valueOf(id));
                    if (planet != null) favorites.add(planet);
                }
                new WorldsBrowserMenu(player, favorites).open(player);
            }
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }
}
