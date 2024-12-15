package ua.mcchickenstudio.opencreative.utils.core;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.BlockActionCoverage;

@UtilityClass
public class Ticker {
    public static void runTicker() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(OpenCreative.getPlugin(), () -> {
            AsyncScheduler.run(() -> {
                BlockActionCoverage.tick();
            });
        }, 1L, 1L);
    }
}
