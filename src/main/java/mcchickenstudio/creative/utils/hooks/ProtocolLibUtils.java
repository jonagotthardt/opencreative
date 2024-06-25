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

public class ProtocolLibUtils {

   // private static ProtocolManager manager;

    public static void init() {
        // OpenCreative+ 1.6
        /*manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(Main.getPlugin(),PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer container = event.getPacket();
                StructureModifier<ItemStack> itemStackStructureModifier = container.getItemModifier();
                for (int i = 0; i < itemStackStructureModifier.size(); i++) {
                    ItemStack itemStack = itemStackStructureModifier.read(i);
                    if (itemStack != null) {
                        if (!itemStack.hasItemMeta()) continue;
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName("This is a test");
                        if (itemMeta.hasLore()) {
                            List<String> lore = itemMeta.getLore();
                            for (int i1 = 0; i1 < lore.size(); i1++) {
                                lore.set(i1, "This is normal");
                                itemMeta.setLore(lore);
                            }
                        }
                        itemStack.setItemMeta(itemMeta);
                        itemStackStructureModifier.write(i, itemStack);
                    }
                    itemStackStructureModifier.write(i, itemStack);
                }
            }
        });*/
    }

}
