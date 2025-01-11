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

package ua.mcchickenstudio.opencreative.menu.world;

import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.menu.AbstractMenu;
import ua.mcchickenstudio.opencreative.menu.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.planets.PlanetManager;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;

public class WorldGenerationMenu extends AbstractMenu {

    private final Player player;
    private final ParameterButton generatorButton;
    private final ParameterButton environmentButton;
    private final ParameterButton generateStructures;
    private final ItemStack createButton = createItem(Material.PUFFERFISH_BUCKET,1,"menus.world-creation.items.create");

    public WorldGenerationMenu(Player player) {
        super(3, MessageUtils.getLocaleMessage("menus.world-creation.title",false));
        this.player = player;
        this.generatorButton = new ParameterButton("flat", List.of("flat","empty","water","survival","large_biomes"), "type", "menus.world-creation", "menus.world-creation.items.type", List.of(Material.MOSS_BLOCK, Material.GLASS, Material.WATER_BUCKET, Material.OAK_SAPLING, Material.MYCELIUM));
        this.environmentButton = new ParameterButton("normal", List.of("normal","nether","the_end"), "environment", "menus.world-creation", "menus.world-creation.items.environment", List.of(Material.GRASS_BLOCK, Material.NETHERRACK, Material.END_STONE));
        this.generateStructures = new ParameterButton(false, List.of(false,true), "generate-structures", "menus.world-creation", "menus.world-creation.items.generate-structures", List.of(Material.DECORATED_POT, Material.BOOKSHELF));

    }

    @Override
    public void fillItems(Player player) {
        setItem(10,generatorButton.getItem());
        setItem(11,environmentButton.getItem());
        setItem(12,generateStructures.getItem());
        setItem(16,createButton);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) {
            return;
        }
        event.setCancelled(true);
        switch (event.getRawSlot()) {
            case 10 -> {
                generatorButton.next();
                setItem(event.getRawSlot(),generatorButton.getItem());
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE,100,1f);
            }
            case 11 -> {
                environmentButton.next();
                setItem(event.getRawSlot(),environmentButton.getItem());
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP,100,0.1f);
            }
            case 12 -> {
                generateStructures.next();
                setItem(event.getRawSlot(),generateStructures.getItem());
                player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,100,2);
            }
            case 16 -> {
                if (PlanetManager.getInstance().getPlayerPlanets(player).size() < OpenCreative.getSettings().getGroups().getGroup(player).getWorldsLimit()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,100,0.1f);
                    player.closeInventory();
                    PlanetManager.getInstance().createPlanet(player, WorldUtils.generateWorldID(), WorldUtils.WorldGenerator.valueOf(generatorButton.getCurrentValue().toString().toUpperCase()), World.Environment.valueOf(environmentButton.getCurrentValue().toString().toUpperCase()),new Random().nextInt(),Boolean.parseBoolean(generateStructures.getCurrentValue().toString()));
                }
                player.closeInventory();
            }
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT,100,1);
    }
}
