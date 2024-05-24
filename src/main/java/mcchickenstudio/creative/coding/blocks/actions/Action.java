package mcchickenstudio.creative.coding.blocks.actions;

import mcchickenstudio.creative.coding.blocks.events.EventVariables;
import mcchickenstudio.creative.coding.blocks.events.player.fighting.PlayerDamagesMobEvent;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.coding.blocks.executors.player.fighting.PlayerDamagesMobExecutor;
import mcchickenstudio.creative.coding.blocks.executors.player.world.ChatExecutor;
import mcchickenstudio.creative.coding.exceptions.TooFewArgsException;
import mcchickenstudio.creative.plots.Plot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Action</h1>
 * This class represents Action that will be executed in executor.
 * @since 1.5
 * @version 1.5
 * @author McChicken Studio
 */
public abstract class Action {

    private final Executor EXECUTOR;
    private final int X;
    private List<Entity> entities;

    protected final List<String> ARGUMENTS = new ArrayList<>();

    /**
     * Creates an Action with linked executor and specified arguments.
     * @param executor Executor where this action will be added.
     * @param x X from Action's block location in developers plot.
     * @param arguments List of arguments for action.
     */
    public Action(Executor executor, int x, List<String> arguments) {
        this.EXECUTOR = executor;
        this.X = x;
        setArguments(arguments);
    }

    public abstract void execute(List<Entity> selection);
    public abstract ActionType getActionType();
    public abstract ActionCategory getActionCategory();

    /**
     * Sets arguments for actions.
     * @param args List of arguments.
     */
    public final void setArguments(List<String> args) {
        this.ARGUMENTS.clear();
        args.forEach(this::addArgument);
    }

    private void addArgument(String arg) {
        ARGUMENTS.add(arg);
    }

    protected final List<String> getArguments() {
        return ARGUMENTS;
    }

    public final Executor getExecutor() {
        return EXECUTOR;
    }

    public final int getX() {
        return X;
    }

    //FIXME: Replace it
    protected String parseEntityPlaceholders(String text, Entity entity) {
        Plot plot = getExecutor().getPlot();
        String newText = text;
        newText = text.replace("%player%",entity.getName())
                .replace("%entity%",entity.getName())
                .replace("%plot_online%",String.valueOf(plot.getOnline()))
                .replace("%plot_name%",plot.getPlotName())
                .replace("%plot_description%",plot.getPlotDescription());
        if (EXECUTOR instanceof PlayerDamagesMobExecutor) {
            PlayerDamagesMobEvent event = (PlayerDamagesMobEvent) EXECUTOR.getEvent();
            newText = newText.replace("%damager%",event.getDamager().getName())
                    .replace("%damage%",String.valueOf(event.getDamage()));
        } else if (EXECUTOR instanceof ChatExecutor) {
            newText = newText.replace("%message%",(String) getExecutor().getVarValue(EventVariables.Variable.MESSAGE));
        }
        return newText;
    }

    protected String parseColors(String text) {
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    protected String parseText(String text, Entity entity) {
        return parseColors(parseEntityPlaceholders(text,entity)).replace("\\n","\n");
    }


}
