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

package ua.mcchickenstudio.opencreative.menus.world;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldGenerator;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldGenerators;
import ua.mcchickenstudio.opencreative.utils.world.generators.WorldTemplate;
import ua.mcchickenstudio.opencreative.menus.AbstractMenu;
import ua.mcchickenstudio.opencreative.menus.buttons.ParameterButton;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.createItem;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public class WorldGenerationMenu extends AbstractMenu {

    private final Player player;
    private final ParameterButton generatorButton;
    private final ParameterButton environmentButton;
    private final ParameterButton generateStructures;
    private final ItemStack createButton = createItem(Material.PUFFERFISH_BUCKET,1,"menus.world-creation.items.create");

    public WorldGenerationMenu(Player player, String generator, String environment, boolean generateStructures) {
        super(3, getLocaleMessage("menus.world-creation.title",false));
        this.player = player;
        this.generatorButton = new ParameterButton(generator,
                WorldGenerators.getInstance().getGeneratorsIDs(),
                "type", "menus.world-creation", "menus.world-creation.items.type",
                WorldGenerators.getInstance().getGeneratorsMaterials());
        this.environmentButton = new ParameterButton(environment, List.of("normal","nether","the_end"), "environment", "menus.world-creation", "menus.world-creation.items.environment", List.of(Material.GRASS_BLOCK, Material.NETHERRACK, Material.END_STONE));
        this.generateStructures = new ParameterButton(generateStructures, List.of(false,true), "generate-structures", "menus.world-creation", "menus.world-creation.items.generate-structures", List.of(Material.DECORATED_POT, Material.BOOKSHELF));
    }

    public WorldGenerationMenu(Player player) {
        this(player,"flat","normal",true);
    }

    @Override
    public void fillItems(Player player) {
        setItem(10,generatorButton.getItem());
        setItem(11,environmentButton.getItem());
        setItem(12,generateStructures.getItem());
        setItem(7,createItem(Material.LIME_STAINED_GLASS_PANE,1));
        setItem(16,createButton);
        setItem(25,createItem(Material.LIME_STAINED_GLASS_PANE,1));
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!isPlayerClicked(event) || !isClickedInMenuSlots(event)) {
            return;
        }
        event.setCancelled(true);
        switch (event.getRawSlot()) {
            case 10 -> {
                generatorButton.next();
                setItem(event.getRawSlot(),generatorButton.getItem());
                Sounds.MENU_GENERATION_CHANGE.play(player);
            }
            case 11 -> {
                environmentButton.next();
                setItem(event.getRawSlot(),environmentButton.getItem());
                Sounds.MENU_ENVIRONMENT_CHANGE.play(player);
            }
            case 12 -> {
                generateStructures.next();
                setItem(event.getRawSlot(),generateStructures.getItem());
                Sounds.MENU_GENERATE_STRUCTURES_CHANGE.play(player);
            }
            case 16 -> {
                player.closeInventory();
                if (!OpenCreative.getStability().isFine()) {
                    player.sendMessage(getLocaleMessage("creative.stability.cannot"));
                    Sounds.PLAYER_FAIL.play(player);
                    return;
                }
                boolean notReachedWorldsLimit = OpenCreative.getPlanetsManager().getPlanetsByOwner(player).size() < OpenCreative.getSettings().getGroups().getGroup(player).getWorldsLimit();
                if (notReachedWorldsLimit) {
                    Sounds.WORLD_GENERATION.play(player);
                    player.closeInventory();
                    WorldGenerator generator = WorldGenerators.getInstance().getById(generatorButton.getCurrentValue().toString());
                    if (generator == null) return;
                    if (generator instanceof WorldTemplate template) {
                        OpenCreative.getPlanetsManager().createPlanet(player, WorldUtils.generateWorldID(),
                                template);
                    } else {
                        World.Environment environment = World.Environment.valueOf(environmentButton.getCurrentValue().toString().toUpperCase());
                        int seed = new Random().nextInt();
                        boolean generateStructure = Boolean.parseBoolean(generateStructures.getCurrentValue().toString());
                        OpenCreative.getPlanetsManager().createPlanet(player, WorldUtils.generateWorldID(),
                                generator, environment, seed, generateStructure);
                    }
                }
            }
        }
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        Sounds.MENU_OPEN_GENERATION.play(player);
    }
}
