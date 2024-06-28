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

package mcchickenstudio.creative.utils.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import mcchickenstudio.creative.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static mcchickenstudio.creative.utils.MessageUtils.getLocaleItemName;
import static mcchickenstudio.creative.utils.MessageUtils.getLocaleMessage;

public class ProtocolLibUtils {

    private static ProtocolManager manager;

    public static void init() {
        manager = ProtocolLibrary.getProtocolManager();
        registerEvents();
    }
    
    private static void registerEvents() {
        Main.getPlugin().getLogger().info("Registering ProtocolLib events...");
        manager.addPacketListener(new PacketAdapter(Main.getPlugin(), PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer container = event.getPacket();
                StructureModifier<ItemStack> itemStackStructureModifier = container.getItemModifier();
                for (int i = 0; i < itemStackStructureModifier.size(); i++) {
                    ItemStack itemStack = itemStackStructureModifier.read(i);
                    if (itemStack != null) {
                        if (!itemStack.hasItemMeta()) continue;
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta.getDisplayName().startsWith("oc.lang.")) {
                            itemMeta.setDisplayName(getLocaleItemName(itemMeta.getDisplayName().replace("oc.lang.","")));
                        }
                        List<String> newLore = new ArrayList<>();
                        if (itemMeta.hasLore()) {
                            List<String> lore = itemMeta.getLore();
                            for (int i1 = 0; i1 < lore.size(); i1++) {
                                String loreLine = lore.get(i1);
                                if (loreLine.startsWith("oc.lang.")) {
                                    newLore.add(getLocaleMessage(loreLine.replace("oc.lang.",""),false));
                                } else {
                                    newLore.add(loreLine);
                                }
                                lore.set(i1, "T");
                            }
                        }
                        itemMeta.setLore(newLore);
                        itemStack.setItemMeta(itemMeta);
                        itemStackStructureModifier.write(i, itemStack);
                    }
                    itemStackStructureModifier.write(i, itemStack);
                }
            }
        });
        Main.getPlugin().getLogger().info("Registered all ProtocolLib events.");
    }

}
