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

package ua.mcchickenstudio.opencreative.listeners.entity;

import com.destroystokyo.paper.event.entity.*;
import com.destroystokyo.paper.event.entity.WitchReadyPotionEvent;
import io.papermc.paper.event.entity.EntityDamageItemEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import io.papermc.paper.event.entity.PufferFishStateChangeEvent;
import io.papermc.paper.event.entity.WardenAngerChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.fightning.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.EntityInteractedBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.FireworkExplodedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.PiglinBarteredEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.TurtleLaysEggEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement.EndermanEscapedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement.EntityEnteredBlockEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement.EntityJumpedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement.HorseJumpedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.state.*;
import ua.mcchickenstudio.opencreative.planets.Planet;

public class EntityStateListener implements Listener {

    @EventHandler
    public void onEntityEvent(PigZombieAngerEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new PigZombieAngeredEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(BatToggleSleepEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityBatToggledSleepModeEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(SlimeSplitEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new SlimeSplittedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(WitchReadyPotionEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new ua.mcchickenstudio.opencreative.coding.blocks.events.entity.state.WitchReadyPotionEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(SheepRegrowWoolEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new SheepRegrownWoolEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(PufferFishStateChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new PufferfishStateChangedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(CreeperIgniteEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new CreeperIgnitedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(CreeperPowerEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new CreeperPoweredEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityEnterLoveModeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityEnteredLoveModeEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(TurtleGoHomeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new TurtleGoesHomeEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityResurrectEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityResurrectedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityPotionEffectEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityPotionEffectedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(WardenAngerChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new WardenAngerChangedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityAirChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityAirChangedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityEnterBlockEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityEnteredBlockEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityJumpEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityJumpedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(HorseJumpEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new HorseJumpedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EndermanEscapeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EndermanEscapedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityDropItemEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityDroppedItemEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityPickupItemEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityPickedUpItemEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(ItemMergeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new ItemMergedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(ItemDespawnEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new ItemDespawnedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityDamageItemEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityDamagedItemEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(PiglinBarterEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new PiglinBarteredEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityInteractEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityInteractedBlockEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(TurtleLayEggEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new TurtleLaysEggEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(FireworkExplodeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new FireworkExplodedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityDeathEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityDiedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityShootBowEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityShotBowEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(WitchThrowPotionEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new WitchThrownPotionEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(WitchConsumePotionEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new WitchConsumedPotionEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityLoadCrossbowEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityLoadedCrossbowEvent(event).callEvent();
    }
}
