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

package ua.mcchickenstudio.opencreative.commands.experiments;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.coding.values.EventValue;
import ua.mcchickenstudio.opencreative.coding.values.EventValues;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import ua.mcchickenstudio.opencreative.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlayerErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class TestificationExperiment extends Experiment {

    @Override
    public @NotNull String getId() {
        return "testification";
    }

    @Override
    public @NotNull String getName() {
        return "Release Testification";
    }

    @Override
    public @NotNull String getDescription() {
        return "Checks before releasing";
    }

    @Override
    public void handleCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(getLocaleMessage("too-few-args"));
            return;
        }
        if (args[0].equalsIgnoreCase("translation")) {
            List<String> untranslatedBlocks = new ArrayList<>();
            for (ExecutorType executor : ExecutorType.values()) {
                String path = "items.developer.events." + executor.name().toLowerCase().replace("_", "-") + ".name";
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
                path = "blocks." + executor.name().toLowerCase();
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
            }
            for (ActionType action : ActionType.values()) {
                String path = "items.developer." + (action.isCondition() ? "conditions" : "actions") + "." + action.name().toLowerCase().replace("_", "-") + ".name";
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
                path = "blocks." + action.name().toLowerCase();
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
            }
            for (EventValue value : EventValues.getInstance().getEventValues()) {
                String path = "items.developer.event-values.items." + value.getID().toLowerCase().replace("_", "-") + ".name";
                if (!MessageUtils.messageExists(path)) {
                    untranslatedBlocks.add(path);
                }
            }
            for (String string : untranslatedBlocks) {
                sender.sendMessage(Component.text(string).clickEvent(ClickEvent.suggestCommand(string)));
            }
            if (untranslatedBlocks.isEmpty()) {
                sender.sendMessage("Everything is translated :)");
                return;
            }
            sender.sendMessage("--- Untranslated: " + untranslatedBlocks.size());
        } else if (args[0].equalsIgnoreCase("item")) {
            if (!(sender instanceof Player player)) {
                return;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (args.length == 1) return;
            switch (args[1].toLowerCase()) {
                case "1" -> {
                    sender.sendMessage("map");
                    Map<String, Object> serialized = item.serialize();
                    ItemStack newItem = ItemStack.deserialize(serialized);
                    player.getInventory().addItem(newItem);
                    sender.sendMessage(serialized.toString());
                }
                case "2" -> {
                    sender.sendMessage("byte");
                    byte[] serialized = item.serializeAsBytes();
                    ItemStack newItem = ItemStack.deserializeBytes(serialized);
                    player.getInventory().addItem(newItem);
                    sender.sendMessage(Arrays.toString(serialized));
                }
                case "3" -> {
                    sender.sendMessage("byte string");
                    try {
                        String object = ItemUtils.saveItemAsByteArray(item);
                        sender.sendMessage(object);
                        player.getInventory().addItem(ItemUtils.loadItemFromByteArray(object));
                    } catch (Exception error) {
                        sendPlayerErrorMessage(player, "Failed to test items", error);
                    }

                }
            }

        }
    }

    @Override
    public @Nullable List<String> tabCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of("translation", "item");
        }
        if (args.length == 1) {
            return List.of("1", "2", "3", "4");
        }
        return null;
    }

}
