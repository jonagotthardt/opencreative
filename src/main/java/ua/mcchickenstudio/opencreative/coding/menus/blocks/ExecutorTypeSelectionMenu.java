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

package ua.mcchickenstudio.opencreative.coding.menus.blocks;

import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorCategory;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.time.Duration;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getCodingValueKey;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.getPersistentData;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.toComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.translateBlockSign;

public final class ExecutorTypeSelectionMenu extends BlocksWithMenusCategoryMenu<ExecutorType> {

    private final ExecutorCategory executor;

    public ExecutorTypeSelectionMenu(@NotNull Player player,
                                     @NotNull Location location,
                                     @NotNull ExecutorCategory executor) {
        super(player, location, "events",
                executor.name().toLowerCase(),
                executor.getStainedPane(), executor.getDefaultCategory());
        this.executor = executor;
    }

    @Override
    protected ItemStack getElementIcon(ExecutorType type) {
        return type.getIcon();
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        event.setCancelled(true);
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(getPlayer());
        Block codingBlock = signLocation.getBlock().getRelative(BlockFace.NORTH);
        if (signLocation.getWorld().getName().contains("dev") && devPlanet != null) {
            String typeString = getPersistentData(item, getCodingValueKey());
            ExecutorType executorType = null;
            try {
                executorType = ExecutorType.valueOf(typeString);
            } catch (Exception ignored) {
            }
            ExecutorCategory executorCategory = executorType == null ? null : ExecutorCategory.getByMaterial(codingBlock.getType());
            if (executorCategory != null) {
                devPlanet.setCodeChanged(true);
                setSignLine(signLocation, 2, executorCategory.name().toLowerCase());
            }
            if (setSignLine(signLocation, 3, typeString.toLowerCase())) {
                devPlanet.setCodeChanged(true);
                translateBlockSign(signLocation.getBlock());
                getPlayer().closeInventory();
                getPlayer().showTitle(Title.title(
                        toComponent(getLocaleMessage("world.dev-mode.set-events")), item.getItemMeta().displayName(),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(1), Duration.ofMillis(750))
                ));
                Sounds.DEV_SET_EVENT.play(event.getWhoClicked());
                event.getWhoClicked().swingMainHand();
            }
        }
    }

    @Override
    public List<ExecutorType> getElements() {
        return ExecutorType.getExecutorsByCategories(executor, currentCategory);
    }
}
