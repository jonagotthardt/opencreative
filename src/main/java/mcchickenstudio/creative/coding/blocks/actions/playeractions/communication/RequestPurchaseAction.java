/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com
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

package mcchickenstudio.creative.coding.blocks.actions.playeractions.communication;

import mcchickenstudio.creative.coding.arguments.Arguments;
import mcchickenstudio.creative.coding.blocks.actions.ActionType;
import mcchickenstudio.creative.coding.blocks.actions.Target;
import mcchickenstudio.creative.coding.blocks.actions.playeractions.PlayerAction;
import mcchickenstudio.creative.coding.blocks.executors.Executor;
import mcchickenstudio.creative.menu.ConfirmationMenu;
import mcchickenstudio.creative.plots.Plot;
import mcchickenstudio.creative.plots.PlotManager;
import mcchickenstudio.creative.utils.hooks.HookUtils;
import mcchickenstudio.creative.utils.hooks.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static mcchickenstudio.creative.coding.blocks.events.EventRaiser.raisePlayerPurchaseEvent;
import static mcchickenstudio.creative.utils.MessageUtils.*;

public class RequestPurchaseAction extends PlayerAction {
    public RequestPurchaseAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(Player player) {
        String id = getArguments().getValue("id","example",this);
        String name = getArguments().getValue("name","Example",this);
        boolean save = getArguments().getValue("save",false,this);
        int price = getArguments().getValue("price",100,this);
        Plot plot = PlotManager.getInstance().getPlotByPlayer(player);
        if (plot == null) return;
        new ConfirmationMenu(
                getLocaleMessage("menus.confirmation.request-money", false).replace("%name%", name),
                Material.GOLD_INGOT,
                getLocaleItemName("menus.confirmation.items.request-money.name")
                        .replace("%price%", String.valueOf(price))
                        .replace("%name%", name),
                getLocaleItemDescription("menus.confirmation.items.request-money.lore"),
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Plot currentPlot = PlotManager.getInstance().getPlotByPlayer(player);
                        if (!plot.equals(currentPlot) || !(HookUtils.isPluginEnabled("Vault"))) {
                            cancel();
                            return;
                        }
                        if (VaultUtils.getBalance(player) < price) {
                            player.sendMessage(getLocaleMessage("no-money").replace("%money%",String.valueOf(price)));
                        } else {
                            if (save) {
                                if (plot.getWorldPlayers().getPlotPlayer(player).getPurchases().contains(id.toLowerCase())) {
                                    return;
                                } else {
                                    plot.getWorldPlayers().getPlotPlayer(player).addPurchase(id.toLowerCase());
                                }
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,100,1.2f);
                            if (!plot.isOwner(player)) {
                                VaultUtils.takeMoney(player,price);
                                VaultUtils.giveMoney(Bukkit.getOfflinePlayer(plot.getOwner()),price);
                            }
                            raisePlayerPurchaseEvent(player,id,name,price,save);
                        }
                    }
                }).open(player);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.PLAYER_REQUEST_PURCHASE;
    }
}
