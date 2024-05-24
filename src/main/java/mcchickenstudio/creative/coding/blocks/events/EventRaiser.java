package mcchickenstudio.creative.coding.blocks.events;

import mcchickenstudio.creative.coding.blocks.events.player.fighting.*;
import mcchickenstudio.creative.coding.blocks.events.player.interaction.*;
import mcchickenstudio.creative.coding.blocks.events.player.inventory.*;
import mcchickenstudio.creative.coding.blocks.events.player.movement.*;
import mcchickenstudio.creative.coding.blocks.events.player.world.*;
import mcchickenstudio.creative.plots.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class EventRaiser {

    // Player Events
    // World

    public static boolean raiseJoinEvent(Player player) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        JoinEvent creativeEvent = new JoinEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseQuitEvent(Player player) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        QuitEvent creativeEvent = new QuitEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayEvent(Player player) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlayEvent creativeEvent = new PlayEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseLikeEvent(Player player) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        LikeEvent creativeEvent = new LikeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseAdvertisedEvent(Player player) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        AdvertisedEvent creativeEvent = new AdvertisedEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseChatEvent(Player player, PlayerChatEvent bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        ChatEvent creativeEvent = new ChatEvent(player, bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Movement

    public static boolean raiseJumpEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        JumpEvent creativeEvent = new JumpEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseMoveEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlayerMoveEvent creativeEvent = new PlayerMoveEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartFlyingEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StartFlyingEvent creativeEvent = new StartFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopFlyingEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StopFlyingEvent creativeEvent = new StopFlyingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartSneakingEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StartSneakingEvent creativeEvent = new StartSneakingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopSneakingEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StopSneakingEvent creativeEvent = new StopSneakingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartRunningEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StartRunningEvent creativeEvent = new StartRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopRunningEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StopRunningEvent creativeEvent = new StopRunningEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseTeleportEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        TeleportEvent creativeEvent = new TeleportEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Inventory

    public static boolean raiseBookWriteEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        BookWriteEvent creativeEvent = new BookWriteEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseCloseInventoryEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        CloseInventoryEvent creativeEvent = new CloseInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemChangeEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        ItemChangeEvent creativeEvent = new ItemChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemClickEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        ItemClickEvent creativeEvent = new ItemClickEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemDropEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        ItemDropEvent creativeEvent = new ItemDropEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseSlotChangeEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        SlotChangeEvent creativeEvent = new SlotChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemMoveEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        ItemMoveEvent creativeEvent = new ItemMoveEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseItemPickupEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        ItemPickupEvent creativeEvent = new ItemPickupEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseOpenInventoryEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        OpenInventoryEvent creativeEvent = new OpenInventoryEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Interaction

    public static boolean raiseBlockInteractionEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        BlockInteractionEvent creativeEvent = new BlockInteractionEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseDamageBlockEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        DamageBlockEvent creativeEvent = new DamageBlockEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseDestroyEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        DestroyBlockEvent creativeEvent = new DestroyBlockEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseFishEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        FishEvent creativeEvent = new FishEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseLeftClickEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        LeftClickEvent creativeEvent = new LeftClickEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseRightClickEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        RightClickEvent creativeEvent = new RightClickEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStartSpectatingEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StartSpectatingEvent creativeEvent = new StartSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseStopSpectatingEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        StopSpectatingEvent creativeEvent = new StopSpectatingEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseWorldInteractEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        WorldInteractEvent creativeEvent = new WorldInteractEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlaceBlockEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlaceBlockEvent creativeEvent = new PlaceBlockEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseMobInteractionEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        MobInteractionEvent creativeEvent = new MobInteractionEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    // Fighting

    public static boolean raiseHungerChangeEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        HungerChangeEvent creativeEvent = new HungerChangeEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raiseMobDamagesPlayerEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        MobDamagesPlayerEvent creativeEvent = new MobDamagesPlayerEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerDamagedEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlayerDamagedEvent creativeEvent = new PlayerDamagedEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerDamagedMobEvent(Player player, EntityDamageByEntityEvent bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlayerDamagesMobEvent creativeEvent = new PlayerDamagesMobEvent(player,bukkitEvent);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerDeathEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlayerDeathEvent creativeEvent = new PlayerDeathEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerRespawnEvent(Player player, Event bukkitEvent) {
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlayerRespawnEvent creativeEvent = new PlayerRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

    public static boolean raisePlayerTotemRespawnEvent(Entity entity, Event bukkitEvent) {
        if (!(entity instanceof Player)) return false;
        Player player = (Player) entity;
        if (PlotManager.getInstance().getPlotByPlayer(player) == null) return false;
        if (PlotManager.getInstance().getDevPlot(player) != null) return false;
        PlayerTotemRespawnEvent creativeEvent = new PlayerTotemRespawnEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(creativeEvent);
        return true;
    }

}
