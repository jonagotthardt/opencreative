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

package ua.mcchickenstudio.opencreative.commands.world;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.GamePlayEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.coding.variables.WorldVariable;
import ua.mcchickenstudio.opencreative.coding.variables.VariableLink;
import ua.mcchickenstudio.opencreative.menus.world.settings.WorldEnvironmentMenu;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.CooldownUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.getCooldown;
import static ua.mcchickenstudio.opencreative.utils.CooldownUtils.setCooldown;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.*;

/**
 * <h1>CommandEnvironment</h1>
 * This command is responsible for setting up world's
 * developers world and code environment.
 * <p>
 * Available: For world developers.
 */
public class CommandEnvironment implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            int cooldown = getCooldown(player, CooldownUtils.CooldownType.GENERIC_COMMAND);
            if (cooldown > 0) {
                sender.sendMessage(getLocaleMessage("cooldown").replace("%cooldown%", String.valueOf(cooldown)));
                return true;
            }
            setCooldown(player, OpenCreative.getSettings().getGroups().getGroup(player).getGenericCommandCooldown(), CooldownUtils.CooldownType.GENERIC_COMMAND);
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
            if (planet == null) {
                player.sendMessage(getLocaleMessage("only-in-world"));
                return true;
            }
            if (!planet.getWorldPlayers().canDevelop(player)) {
                player.sendMessage(getLocaleMessage("not-developer"));
                return true;
            }
            if (args.length == 0) {
                new WorldEnvironmentMenu(player, planet.getDevPlanet()).open(player);
            } else {
                switch (args[0].toLowerCase()) {
                    case "vars", "variables", "var":
                        if (args.length == 1) {
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("size")) {
                            player.sendMessage(getLocaleMessage("environment.variables.size").replace("%count%", String.valueOf(planet.getVariables().getTotalVariablesAmount())));
                        } else if (args[1].equalsIgnoreCase("set")) {
                            if (args.length <= 4) {
                                player.sendMessage(getLocaleMessage("environment.variables.set.help"));
                                return true;
                            }
                            // /env var set VAR_NAME VAR_TYPE VALUE_TYPE VALUE
                            String varName = args[2];
                            VariableLink.VariableType type = VariableLink.VariableType.getEnum(args[3]);
                            if (type == null || type == VariableLink.VariableType.LOCAL) return true;
                            ValueType valueType = ValueType.TEXT;
                            Object value = null;
                            switch (args[4].toLowerCase()) {
                                case "number", "n", "num", "numb" -> {
                                    if (args.length == 5) return true;
                                    String numberString = args[5];
                                    if (numberString.equalsIgnoreCase("p") || numberString.equalsIgnoreCase("pi")) {
                                        numberString = "3.1415926";
                                    }
                                    valueType = ValueType.NUMBER;
                                    try {
                                        value = parseTicks(numberString);
                                    } catch (NumberFormatException ignored) {}
                                }
                                case "boolean", "bool", "b" -> {
                                    if (args.length == 5) return true;
                                    value = Boolean.parseBoolean(args[5]);
                                    valueType = ValueType.BOOLEAN;
                                }
                                case "text", "t" -> value = String.join(" ",Arrays.copyOfRange(args,5,args.length));
                                case "item", "i" -> {
                                    value = player.getInventory().getItemInMainHand();
                                    valueType = ValueType.ITEM;
                                }
                                case "location", "loc" -> {
                                    try {
                                        if (args.length < 8) return true;
                                        double x = parseCoordinate(args[5],player.getX());
                                        double y = parseCoordinate(args[6],player.getY());
                                        double z = parseCoordinate(args[7],player.getZ());
                                        float yaw = player.getYaw();
                                        float pitch = player.getPitch();
                                        if (args.length >= 9) {
                                            yaw = parseCoordinate(args[8],player.getYaw());
                                        }
                                        if (args.length >= 10) {
                                            pitch = parseCoordinate(args[9],player.getPitch());
                                        }
                                        value = new Location(planet.getTerritory().getWorld(),x,y,z,yaw,pitch);
                                        valueType = ValueType.LOCATION;
                                    } catch (NumberFormatException ignored) {}
                                }
                                case "vector", "vec" -> {
                                    try {
                                        if (args.length < 6) return true;
                                        double x = Double.parseDouble(args[5]);
                                        double y = Double.parseDouble(args[6]);
                                        double z = Double.parseDouble(args[7]);
                                        value = new Vector(x,y,z);
                                        valueType = ValueType.VECTOR;
                                    } catch (NumberFormatException ignored) {}
                                }
                            }
                            if (value != null) {
                                if (planet.getVariables().setVariableValue(new VariableLink(varName,type),valueType,value)) {
                                    player.sendMessage(getLocaleMessage("environment.variables.set.message")
                                            .replace("%variable%",varName)
                                            .replace("%value%",value.toString().length() > 100 ? value.toString().substring(0,100) + "..." : value.toString()));
                                } else {
                                    player.sendMessage(getLocaleMessage("environment.variables.set.limit")
                                            .replace("%limit%",String.valueOf(planet.getLimits().getVariablesAmountLimit())));
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("get")) {
                            VariableLink.VariableType type = VariableLink.VariableType.GLOBAL;
                            if (args.length == 2) return true;
                            String varName = args[2];
                            if (args.length >= 4) {
                                type = VariableLink.VariableType.getEnum(args[3]);
                                if (type == null || type == VariableLink.VariableType.LOCAL) type = VariableLink.VariableType.GLOBAL;
                            }
                            WorldVariable var = planet.getVariables().getVariable(varName,type,null);
                            if (var == null) {
                                player.sendMessage(getLocaleMessage("environment.variables.get.empty"));
                            } else {
                                String message = getLocaleMessage("environment.variables.get.message")
                                        .replace("%variable%",varName)
                                        .replace("%type%",var.getType().getLocaleName())
                                        .replace("%valuetype%",var.getVarType().getLocalized());
                                message = message.replace("%value%",message.length()+var.getValue().toString().length() > 700 ? var.getValue().toString().substring(0,Math.min(var.getValue().toString().length(),700)) + "..." : var.getValue().toString());
                                player.sendMessage(message);
                            }
                        }
                        else if (args[1].equalsIgnoreCase("clear")) {
                            planet.getVariables().clearVariables();
                            player.sendMessage(getLocaleMessage("environment.variables.cleared"));
                        } else if (args[1].equalsIgnoreCase("list")) {
                            int page = 0;
                            List<WorldVariable> allVariables = new ArrayList<>(planet.getVariables().getSet());
                            if (allVariables.isEmpty()) {
                                player.sendMessage(getLocaleMessage("environment.variables.list.empty"));
                                return true;
                            }
                            if (args.length > 2) {
                                try {
                                    page = Integer.parseInt(args[2]) - 1;
                                    if (page < 0 || page * 20 > allVariables.size()) {
                                        page = 0;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                            int current = Math.min(((page + 1) * 20), allVariables.size());
                            List<WorldVariable> variables = new ArrayList<>(allVariables.subList(page * 20, current));
                            Sounds.DEV_VAR_LIST.play(player);
                            player.sendMessage(getLocaleMessage("environment.variables.list.header").replace("%current%", String.valueOf(current)).replace("%amount%", String.valueOf(allVariables.size())));
                            for (WorldVariable variable : variables) {
                                String name = variable.getName();
                                VariableLink.VariableType type = variable.getVarType();
                                String value = (variable.getValue() != null ? variable.getValue().toString() : "null");
                                if (name.length() > 40) {
                                    name = name.substring(0, 40) + "...";
                                }
                                if (value.length() > 40) {
                                    value = value.substring(0, 40) + "...";
                                }
                                player.sendMessage(getLocaleMessage("environment.variables.list.variable", false).replace("%name%", name).replace("%type%", type.getLocalized()).replace("%value%", value));
                            }
                            Component navigation = toComponent(getLocaleMessage("environment.variables.list.navigation"));
                            if (page * 20 > 20) {
                                navigation = navigation.append(toComponent(getLocaleMessage("environment.variables.list.previous-page")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/environment variables list " + (page - 1))));
                            }
                            if (allVariables.size() > current) {
                                navigation = navigation.append(toComponent(getLocaleMessage("environment.variables.list.next-page")).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/environment variables list " + (page + 1))));
                            }
                            if (!toComponent(getLocaleMessage("environment.variables.list.navigation")).equals(navigation)) {
                                player.sendMessage(navigation);
                            }
                            player.sendMessage(" ");
                        }
                        break;
                    case "containers", "barrel", "barrels": {
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            player.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        devPlanet.setContainerMaterial(devPlanet.getContainerMaterial() == Material.CHEST ? Material.BARREL : Material.CHEST);
                        devPlanet.updateContainers();
                        break;
                    }
                    case "container": {
                        if (args.length < 2) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        Material material = Material.CHEST;
                        try {
                            material = Material.valueOf((args[1].equalsIgnoreCase("chest") || (args[1].equalsIgnoreCase("barrel") || args[1].equalsIgnoreCase("shulker_box")) ? args[1].toUpperCase() : args[1].toUpperCase()+"_SHULKER_BOX"));
                        } catch (Exception ignored) {}
                        if (devPlanet.setContainerMaterial(material)) {
                            devPlanet.updateContainers();
                        }
                        break;

                    }
                    case "drops", "drop", "drop-items": {
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            player.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        boolean value = !devPlanet.isDropItems();
                        if (args.length >= 2) {
                            value = switch (args[1].toLowerCase()) {
                                case "on", "enable" -> true;
                                default -> false;
                            };
                        }
                        player.sendMessage(getLocaleMessage("environment.drops." + (value ? "enabled" : "disabled")));
                        devPlanet.setDropItems(value);
                        Sounds.DEV_SETTINGS_DROP_ITEMS.play(player);
                        break;
                    }
                    case "night-vision": {
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            player.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        boolean value = !devPlanet.isNightVision();
                        if (args.length >= 2) {
                            value = switch (args[1].toLowerCase()) {
                                case "on", "enable" -> true;
                                default -> false;
                            };
                        }
                        devPlanet.setNightVision(value);
                        if (value) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,Integer.MAX_VALUE,0,false,false,false));
                        } else {
                            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        }
                        player.sendMessage(getLocaleMessage("environment.night-vision." + (value ? "enabled" : "disabled")));
                        Sounds.DEV_SETTINGS_NIGHT_VISION.play(player);
                        break;
                    }
                    case "save-location": {
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            player.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        boolean value = !devPlanet.isSaveLocation();
                        if (args.length >= 2) {
                            value = switch (args[1].toLowerCase()) {
                                case "on", "enable" -> true;
                                default -> false;
                            };
                        }
                        player.sendMessage(getLocaleMessage("environment.save-location." + (value ? "enabled" : "disabled")));
                        devPlanet.setSaveLocation(value);
                        Sounds.DEV_SETTINGS_SAVE_LOCATION.play(player);
                        break;
                    }
                    case "createplatform": {
                        if (!sender.hasPermission("opencreative.debug")) {
                            sender.sendMessage(getLocaleMessage("no-perms"));
                            return true;
                        }
                        if (args.length < 3) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        int x = 1;
                        int z = 1;
                        try {
                            x = Integer.parseInt(args[1]);
                        } catch (Exception ignored) {}
                        try {
                            z = Integer.parseInt(args[2]);
                        } catch (Exception ignored) {}
                        if (devPlanet.createPlatform(x,z)) {
                            sender.sendMessage("Created platform " + x + " " + z);
                        }
                        break;
                    }
                    case "platform", "p": {
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        if (devPlanet.getPlatforms().size() >= devPlanet.getPlanet().getLimits().getCodingPlatformsLimit()) {
                            sender.sendMessage(getLocaleMessage("environment.platform.limit").replace("%amount%",String.valueOf(devPlanet.getPlanet().getLimits().getCodingPlatformsLimit())));
                            return true;
                        }
                        int[][] platformCoordinates = {
                                {2, 1}, {1, 2}, {2, 2}, {3, 1}, {1, 3}, {2, 3}, {3, 2}, {3, 3}
                        };
                        DevPlatform platform = null;
                        for (int[] coords : platformCoordinates) {
                            platform = new DevPlatform(devPlanet.getWorld(), coords[0], coords[1]);
                            if (!platform.exists()) {
                                break;
                            }
                        }
                        devPlanet.claimPlatform(platform, player);
                        break;
                    }
                    case "sign": {
                        if (args.length < 2) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        Material material = Material.OAK_WALL_SIGN;
                        try {
                            material = Material.valueOf(args[1].toUpperCase()+"_WALL_SIGN");
                        } catch (Exception ignored) {}
                        if (devPlanet.setSignMaterial(material)) {
                            Sounds.DEV_PLATFORM_SIGN.play(player);
                            devPlanet.updateSigns();
                        }
                        break;
                    }
                    case "floor": {
                        if (args.length < 2) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        Material material = Material.WHITE_STAINED_GLASS;
                        try {
                            material = Material.valueOf(args[1].equalsIgnoreCase("barrier") ? args[1].toUpperCase() : args[1].toUpperCase()+"_STAINED_GLASS");
                        } catch (Exception ignored) {}
                        DevPlatform currentPlatform = devPlanet.getPlatformInLocation(player.getX(),player.getZ());
                        if (currentPlatform == null) {
                            for (DevPlatform platform : devPlanet.getPlatforms()) {
                                platform.setFloorMaterial(material);
                            }
                        } else {
                            currentPlatform.setFloorMaterial(material);
                        }
                        Sounds.DEV_PLATFORM_COLOR.play(player);
                        break;
                    }
                    case "action": {
                        if (args.length < 2) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        Material material = Material.GRAY_STAINED_GLASS;
                        try {
                            material = Material.valueOf(args[1].equalsIgnoreCase("barrier") ? args[1].toUpperCase() : args[1].toUpperCase()+"_STAINED_GLASS");
                        } catch (Exception ignored) {}
                        DevPlatform currentPlatform = devPlanet.getPlatformInLocation(player.getX(),player.getZ());
                        if (currentPlatform == null) {
                            for (DevPlatform platform : devPlanet.getPlatforms()) {
                                platform.setActionMaterial(material);
                            }
                        } else {
                            currentPlatform.setActionMaterial(material);
                        }
                        Sounds.DEV_PLATFORM_COLOR.play(player);
                        break;
                    }
                    case "event", "executor": {
                        if (args.length < 2) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        Material material = Material.BLUE_STAINED_GLASS;
                        try {
                            material = Material.valueOf(args[1].equalsIgnoreCase("barrier") ? args[1].toUpperCase() : args[1].toUpperCase()+"_STAINED_GLASS");
                        } catch (Exception ignored) {}
                        DevPlatform currentPlatform = devPlanet.getPlatformInLocation(player.getX(),player.getZ());
                        if (currentPlatform == null) {
                            for (DevPlatform platform : devPlanet.getPlatforms()) {
                                platform.setEventMaterial(material);
                            }
                        } else {
                            currentPlatform.setEventMaterial(material);
                        }
                        Sounds.DEV_PLATFORM_COLOR.play(player);
                        break;
                    }
                    case "theme", "settheme", "themes": {
                        if (args.length < 2) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(player);
                        if (devPlanet == null) {
                            sender.sendMessage(getLocaleMessage("only-in-dev-world"));
                            return true;
                        }
                        DevPlatform platform = devPlanet.getPlatformInLocation(player.getX(),player.getZ());
                        if (platform == null) {
                            return true;
                        }
                        if (switch (args[1].toLowerCase()) {
                            case "dark", "black", "darkmode", "space", "night" -> platform.setMaterials(Material.BARRIER,Material.GRAY_STAINED_GLASS,Material.BLACK_STAINED_GLASS);
                            case "light", "white", "lightmode" -> platform.setMaterials(Material.BARRIER,Material.GRAY_STAINED_GLASS,Material.WHITE_STAINED_GLASS);
                            case "pink", "magenta", "purple" -> platform.setMaterials(Material.BARRIER,Material.PINK_STAINED_GLASS,Material.MAGENTA_STAINED_GLASS);
                            case "blue", "ocean", "cyan" -> platform.setMaterials(Material.BARRIER,Material.BLUE_STAINED_GLASS,Material.LIGHT_BLUE_STAINED_GLASS);
                            case "ukraine", "ua", "uk" -> platform.setMaterials(Material.BARRIER,Material.BLUE_STAINED_GLASS,Material.YELLOW_STAINED_GLASS);
                            case "rhombus", "old", "legacy" -> platform.setMaterials(Material.WHITE_STAINED_GLASS,Material.LIGHT_BLUE_STAINED_GLASS,Material.LIGHT_GRAY_STAINED_GLASS);
                            case "just", "planet", "default" -> platform.setMaterials(Material.WHITE_STAINED_GLASS,Material.BLUE_STAINED_GLASS,Material.GRAY_STAINED_GLASS);
                            case "art", "artur" -> platform.setMaterials(Material.WHITE_STAINED_GLASS,Material.BLACK_STAINED_GLASS,Material.CYAN_STAINED_GLASS);
                            case "cloud" -> platform.setMaterials(Material.WHITE_STAINED_GLASS,Material.CYAN_STAINED_GLASS,Material.GRAY_STAINED_GLASS);
                            default -> false;
                        }) {
                            Sounds.DEV_PLATFORM_COLOR.play(player);
                        }
                        break;
                    }
                    case "execute", "exec", "launch", "run": {
                        if (planet.getMode() != Planet.Mode.PLAYING) {
                            sender.sendMessage(getLocaleMessage("world.not-in-play-mode"));
                            return true;
                        }
                        if (args.length < 3) {
                            sender.sendMessage(getLocaleMessage("too-few-args"));
                            return true;
                        }
                        String eventName = args[1];
                        String argument = String.join(" ",Arrays.copyOfRange(args,2,args.length));
                        // /env execute player_join PlayerName
                        // /env execute function Function
                        switch (eventName.toLowerCase()) {
                            case "join", "player_join" -> {
                                Player eventPlayer = Bukkit.getPlayer(argument);
                                if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                    sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                    return true;
                                }
                                new JoinEvent(player).callEvent();
                            }
                            case "quit", "player_quit" -> {
                                Player eventPlayer = Bukkit.getPlayer(argument);
                                if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                    sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                    return true;
                                }
                                new QuitEvent(player).callEvent();
                            }
                            case "liked", "like", "player_like", "player_liked" -> {
                                Player eventPlayer = Bukkit.getPlayer(argument);
                                if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                    sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                    return true;
                                }
                                new LikeEvent(player).callEvent();
                            }
                            case "play", "player_play" -> {
                                Player eventPlayer = Bukkit.getPlayer(argument);
                                if (eventPlayer == null || !planet.getTerritory().getWorld().getPlayers().contains(eventPlayer)) {
                                    sender.sendMessage(getLocaleMessage("environment.execute.offline"));
                                    return true;
                                }
                                new PlayEvent(player).callEvent();
                            }
                            case "world_play" -> new GamePlayEvent(planet).callEvent();
                            case "function", "func" -> {
                                boolean found = false;
                                for (Function function : planet.getTerritory().getScript().getExecutors().getFunctionsList()) {
                                    if (argument.equalsIgnoreCase(function.getName())) {
                                        if (!found) {
                                            /*
                                             * For sending message once and
                                             * before function activation.
                                             */
                                            found = true;
                                            sender.sendMessage(getLocaleMessage("environment.execute.function").replace("%function%",argument));
                                        }
                                        Executors.activate(function, new JoinEvent(player));
                                    }
                                }
                                if (!found) sender.sendMessage(getLocaleMessage("environment.execute.function-not-found"));
                            }
                            case "method", "meth" -> {
                                boolean found = false;
                                for (Method method : planet.getTerritory().getScript().getExecutors().getMethodsList()) {
                                    if (argument.equalsIgnoreCase(method.getName())) {
                                        if (!found) {
                                            found = true;
                                            sender.sendMessage(getLocaleMessage("environment.execute.method").replace("%method%",argument));
                                        }
                                        Executors.activate(method, new JoinEvent(player));
                                    }
                                }
                                if (!found) sender.sendMessage(getLocaleMessage("environment.execute.method-not-found"));
                            }
                            default -> sender.sendMessage(getLocaleMessage("environment.execute.help"));
                        }
                        break;
                    }
                    case "debug": {
                        if (args.length == 1) {
                            player.sendMessage(getLocaleMessage("environment.debug.help"));
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("on")) {
                            for (Player planetPlayer : planet.getPlayers()){
                                planetPlayer.sendMessage(getLocaleMessage("environment.debug.enabled",player));
                            }
                            Sounds.DEV_DEBUG_ON.play(player);
                            planet.setDebug(true);
                        } else if (args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("off")) {
                            for (Player planetPlayer : planet.getPlayers()){
                                planetPlayer.sendMessage(getLocaleMessage("environment.debug.disabled",player));
                            }
                            Sounds.DEV_DEBUG_OFF.play(player);
                            planet.setDebug(false);
                        }
                    }

                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tabCompleter = new ArrayList<>();
        if (args.length == 1) {
            Collections.addAll(tabCompleter,"platform","variables","debug","execute","barrel","floor","action","theme","event","sign","save-location","night-vision","drops");
            return tabCompleter;
        }
        if (args.length == 2) {
            if (List.of("var", "vars", "variables").contains(args[0].toLowerCase())) {
                Collections.addAll(tabCompleter, "set", "get", "size", "clear", "list");
            } else if (List.of("execute", "exec", "run").contains(args[0].toLowerCase())) {
                Collections.addAll(tabCompleter, "function", "method", "player_join", "player_quit", "player_liked", "player_play", "world_play");
            } else if ("debug".equalsIgnoreCase(args[0]) || "drops".equalsIgnoreCase(args[0]) || "night-vision".equalsIgnoreCase(args[0]) || "save-location".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter, "on", "off");
            } else if ("floor".equalsIgnoreCase(args[0]) || "event".equalsIgnoreCase(args[0]) || "action".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "barrier", "black", "blue"
                        , "light_blue", "light_gray", "white"
                        , "red", "orange", "yellow", "purple"
                        , "green", "lime", "magenta", "brown"
                        , "cyan", "pink");
            } else if ("theme".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "default", "dark", "light",
                        "legacy", "cloud", "art", "ukraine",
                        "blue", "purple");
            } else if ("sign".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "oak","acacia","bamboo","cherry",
                        "birch", "jungle");
            } else if ("container".equalsIgnoreCase(args[0])) {
                Collections.addAll(tabCompleter,
                        "barrel", "chest", "black", "blue"
                        , "light_blue", "light_gray", "white"
                        , "red", "orange", "yellow", "purple"
                        , "green", "lime", "magenta", "brown"
                        , "cyan", "pink");
            }
            return tabCompleter;
        }
        if (List.of("execute", "exec", "run").contains(args[0].toLowerCase())) {
            if (sender instanceof Player player) {
                if (PlayerUtils.isEntityInLobby(player)) return tabCompleter;
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                if (planet == null || !planet.getWorldPlayers().canDevelop(player)) return tabCompleter;
                if (List.of("join", "quit", "player_join", "player_quit", "player_play", "play", "player_liked", "liked").contains(args[1].toLowerCase())) {
                    tabCompleter.addAll(planet.getTerritory().getWorld().getPlayers().stream().map(Player::getName).toList());
                } else if (args[1].equalsIgnoreCase("function")) {
                    tabCompleter.addAll(planet.getTerritory().getScript().getExecutors().getFunctionsList().stream().map(Function::getName).toList());
                } else if (args[1].equalsIgnoreCase("method")) {
                    tabCompleter.addAll(planet.getTerritory().getScript().getExecutors().getMethodsList().stream().map(Method::getName).toList());
                }
            }
        }
        if (List.of("var","vars","variables").contains(args[0].toLowerCase())) {
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length == 3) {
                    if (sender instanceof Player player) {
                        if (PlayerUtils.isEntityInLobby(player)) return tabCompleter;
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.getWorldPlayers().canDevelop(player)) return tabCompleter;
                        List<WorldVariable> allVariables = new ArrayList<>(planet.getVariables().getSet());
                        if (allVariables.isEmpty()) return tabCompleter;
                        List<WorldVariable> vars = allVariables.subList(Math.max(0,allVariables.size()-10),allVariables.size());
                        tabCompleter.addAll(vars.stream().map(WorldVariable::getName).toList());
                    }
                }
                if (args.length == 4) {
                    Collections.addAll(tabCompleter, "global", "saved");
                }
                if (args.length == 5) {
                    Collections.addAll(tabCompleter, "text", "number", "location", "item", "boolean", "vector");
                }
                if (args.length == 6) {
                    switch (args[4].toLowerCase()) {
                        case "number", "n", "numb", "num" -> Collections.addAll(tabCompleter, "0","1","16","32","64","100","500");
                        case "boolean", "b", "bool" -> Collections.addAll(tabCompleter, "true","false");
                    }
                }
            }
            if (args[1].equalsIgnoreCase("get")) {
                if (args.length == 3) {
                    if (sender instanceof Player player) {
                        if (PlayerUtils.isEntityInLobby(player)) return tabCompleter;
                        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
                        if (planet == null || !planet.getWorldPlayers().canDevelop(player)) return tabCompleter;
                        List<WorldVariable> allVariables = new ArrayList<>(planet.getVariables().getSet());
                        if (allVariables.isEmpty()) return tabCompleter;
                        List<WorldVariable> vars = allVariables.subList(Math.max(0,allVariables.size()-10),allVariables.size());
                        tabCompleter.addAll(vars.stream().map(WorldVariable::getName).toList());
                    }
                } else if (args.length == 4) {
                    Collections.addAll(tabCompleter, "global","saved");
                } else {
                    return null;
                }
            }
        }
        return tabCompleter;
    }

    private double parseCoordinate(String arg, double current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Double.parseDouble(arg.substring(1));
        } else {
            return Double.parseDouble(arg);
        }
    }

    private float parseCoordinate(String arg, float current) throws NumberFormatException {
        if (arg.startsWith("~")) {
            return arg.equals("~") ? current : current + Float.parseFloat(arg.substring(1));
        } else {
            return Float.parseFloat(arg);
        }
    }
}
