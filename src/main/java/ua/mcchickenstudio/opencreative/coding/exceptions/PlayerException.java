package ua.mcchickenstudio.opencreative.coding.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * <h1>PlayerException</h1>
 * This class represents a player exception, that has
 * nickname of player, who involved in exception. Used
 * to replace nickname placeholder.
 */
public abstract class PlayerException extends RuntimeException {

    private final @NotNull String playerName;

    public PlayerException(@NotNull String playerName, @NotNull String message) {
        super(message);
        this.playerName = playerName;
    }

    public @NotNull String getPlayerName() {
        return playerName;
    }

}
