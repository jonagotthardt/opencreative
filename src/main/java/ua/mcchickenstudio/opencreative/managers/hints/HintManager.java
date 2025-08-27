package ua.mcchickenstudio.opencreative.managers.hints;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;

/**
 * <h1>HintManager</h1>
 * This interface represents a hint manager,
 * that sends suggestions for players, when they
 * are holding coding item or looking on coding block.
 */
public interface HintManager extends Manager {

    /**
     * Checks player for suggestions and sends hint
     * in action bar if it's necessary.
     * @param player player to check.
     */
    void checkForHints(@NotNull Player player);

}
