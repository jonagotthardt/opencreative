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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.Layout;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.itemEquals;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public class SelectionActionsMenu extends AbstractMenu {

    private Player player;
    private Location signLocation;

    private final ItemStack varCondition = createItem(Material.OBSIDIAN,1,"items.developer.condition-var");
    private final ItemStack playerCondition = createItem(Material.OAK_PLANKS,1,"items.developer.condition-player");
    private final ItemStack entityCondition = createItem(Material.BRICKS,1,"items.developer.condition-entity");

    private final ItemStack defaultItem = createItem(Target.RANDOM_TARGET.getIcon(),1,"menus.developer.selection.items.default");
    private final ItemStack allPlayers = createItem(Target.ALL_PLAYERS.getIcon(),1,"menus.developer.selection.items.all-players");
    private final ItemStack allEntities = createItem(Target.ALL_ENTITIES.getIcon(),1,"menus.developer.selection.items.all-entities");
    private final ItemStack randomTarget = createItem(Target.RANDOM_TARGET.getIcon(),1,"menus.developer.selection.items.random-target");
    private final ItemStack randomPlayer = createItem(Target.RANDOM_PLAYER.getIcon(),1,"menus.developer.selection.items.random-player");
    private final ItemStack victim = createItem(Target.VICTIM.getIcon(),1,"menus.developer.selection.items.victim");
    private final ItemStack killer = createItem(Target.KILLER.getIcon(),1,"menus.developer.selection.items.killer");


    public SelectionActionsMenu(Player player, Location location) {
        super((byte) 5, getLocaleMessage("blocks.selection_action",false));
        this.player = player;
        this.signLocation = location;
    }

    @Override
    public void fillItems(Player player) {
        setItem((byte) 10,defaultItem);
        setItem((byte) 12,randomTarget);
        setItem((byte) 13,randomPlayer);
        setItem((byte) 16,playerCondition);

        setItem((byte) 19,allPlayers);
        setItem((byte) 25,entityCondition);

        setItem((byte) 28,allEntities);
        setItem((byte) 30,killer);
        setItem((byte) 31,victim);
        setItem((byte) 34,varCondition);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isClickedInMenuSlots(event) || !isPlayerClicked(event)) {
            return;
        }
        event.setCancelled(true);
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;
        if (itemEquals(currentItem, playerCondition)) {
            new PlayerConditionsMenu(player,signLocation).open(player);
        } else if (itemEquals(currentItem, entityCondition)) {
            new EntityConditionsMenu(player,signLocation).open(player);
        } else if (itemEquals(currentItem, varCondition)) {
            new VariableConditionsMenu(player,signLocation).open(player);
        } else if (itemEquals(currentItem, allPlayers)) {
            setLine("all_players");
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,100,0.2f);
            player.closeInventory();
        } else if (itemEquals(currentItem, randomPlayer)) {
            setLine("random_player");
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,100,0.2f);
            player.closeInventory();
        } else if (itemEquals(currentItem, killer)) {
            setLine("killer");
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,100,0.2f);
            player.closeInventory();
        } else if (itemEquals(currentItem, victim)) {
            setLine("victim");
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,100,0.2f);
            player.closeInventory();
        } else if (itemEquals(currentItem, randomTarget)) {
            setLine("random_target");
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,100,0.2f);
            player.closeInventory();
        } else if (itemEquals(currentItem, allEntities)) {
            setLine("all_entities");
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL,100,0.2f);
            player.closeInventory();
        }
    }

    private void setLine(String text) {
        setSignLine(signLocation, (byte) 1,"");
        setSignLine(signLocation, (byte) 2,text);
        setSignLine(signLocation, (byte) 3,"");
        translateBlockSign(signLocation.getBlock());
        Block containerBlock = signLocation.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.NORTH);
        DevPlanet devPlanet = PlanetManager.getInstance().getDevPlanet(signLocation.getWorld());
        if (devPlanet == null) {
            containerBlock.setType(Material.AIR);
            return;
        }
        Layout layout = devPlanet.getOpenedMenu(containerBlock.getLocation());
        if (layout != null) {
            for (Player viewer : layout.getViewers()) {
                viewer.closeInventory();
            }
            devPlanet.unregisterOpenedMenu(containerBlock.getLocation());
        }
        containerBlock.setType(Material.AIR);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }
}
