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

package ua.mcchickenstudio.opencreative.indev.modules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.ExecutorType;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ua.mcchickenstudio.opencreative.listeners.player.PlaceBlockListener.placeDevBlock;
import static ua.mcchickenstudio.opencreative.utils.BlockUtils.setSignLine;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendPlanetErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.FileUtils.getModuleConfig;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.parseModuleLines;

/**
 * <h1>Module</h1>
 * This class represents a module, a code that
 * can be pasted in developer world.
 * <p>Modules are stored in ./modules/moduleID.yml files.
 */
@ApiStatus.Experimental
public class Module {

    private final int id;
    private final ModuleInfo info;
    private UUID owner;

    public Module(int id) {
        this.id = id;
        this.info = new ModuleInfo(this);

        String uuid = getModuleConfig(this).getString("owner","");
        try {
            owner = UUID.fromString(uuid);
        } catch (Exception ignored) {
            owner = new UUID(0,0);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                info.updateIcon();
            }
        }.runTaskAsynchronously(OpenCreative.getPlugin());
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
        return offlinePlayer.hasPlayedBefore() ? offlinePlayer.getName() : "Unknown owner";
    }

    public int getId() {
        return id;
    }

    public ModuleInfo getInformation() {
        return info;
    }

    public boolean place(DevPlanet devPlanet, Player player) {
        if (!devPlanet.isLoaded()) return false;
        if (!devPlanet.getWorld().getPlayers().contains(player)) return false;
        FileConfiguration config = getModuleConfig(this);
        System.out.println(config.saveToString());
        ConfigurationSection section = config.getConfigurationSection("code.blocks");
        if (section == null) {
            System.out.println("Can't continue. Blocks are empty!");
            return true;
        }
        int requiredColumns = section.getKeys(false).size();
        List<Location> freeColumns = new ArrayList<>();
        for (DevPlatform platform : devPlanet.getPlatforms()) {
            freeColumns.addAll(platform.getFreeColumns());
            if (freeColumns.size() >= requiredColumns) break;
        }
        if (freeColumns.size() < requiredColumns) {
            player.sendMessage(getLocaleMessage("modules.few-space").replace("%required%",String.valueOf(requiredColumns)));
            return true;
        }
        for (Player planetPlayer : devPlanet.getPlanet().getPlayers()) {
            if (devPlanet.getPlanet().getWorldPlayers().canDevelop(planetPlayer)) {
                planetPlayer.sendMessage(parseModuleLines(this,getLocaleMessage("modules.installed",player)));
            }
        }
        int i = 0;
        for (String key : section.getKeys(false)) {
            ConfigurationSection executor = section.getConfigurationSection(key);
            placeExecutor(freeColumns.get(i), executor, devPlanet);
            i++;
        }
        return true;
    }

    private boolean placeExecutor(Location location, ConfigurationSection config, DevPlanet devPlanet) {
        try {
            ExecutorType type = ExecutorType.valueOf(config.getString("type"));
            Location signLocation = location.getBlock().getRelative(BlockFace.SOUTH).getLocation();
            if (type == ExecutorType.CYCLE) {
                String name = config.getString("name");
                int time = config.getInt("time");
                time = (time >= 5 && time <= 3600 ? time : 20);
                if (name != null) {
                    placeDevBlock(location,type.getCategory().getBlock(),type.getCategory().getAdditionalBlock(),devPlanet.getSignMaterial(),type.getCategory().name().toLowerCase());
                    setSignLine(signLocation,1,name);
                    setSignLine(signLocation,3,String.valueOf(time));
                }
            } else if (type == ExecutorType.FUNCTION || type == ExecutorType.METHOD) {
                String name = config.getString("name");
                if (name != null) {
                    placeDevBlock(location,type.getCategory().getBlock(),type.getCategory().getAdditionalBlock(),devPlanet.getSignMaterial(),type.getCategory().name().toLowerCase());
                    setSignLine(signLocation,3,name);
                }
            } else {
                placeDevBlock(location,type.getCategory().getBlock(),type.getCategory().getAdditionalBlock(),devPlanet.getSignMaterial(),type.getCategory().name().toLowerCase());
                setSignLine(signLocation,3,type.name().toLowerCase());
            }
        } catch (Exception error) {
            sendPlanetErrorMessage(devPlanet.getPlanet(),"Cannot load module",error);
            error.printStackTrace();
        }
        return true;
    }

    /*private Action createAction(YamlConfiguration config) {

        String type = config.getString(path + ".type");
        if (type == null) return null;

        try {
            ActionType actionType = ActionType.valueOf(type);
            Arguments args = new Arguments(executor.getPlanet(),executor);
            Target target = Target.DEFAULT;
            String targetString = config.getString(path + ".target");
            if (targetString != null && !targetString.isEmpty()) {
                target = Target.valueOf(targetString);
            }
            ConfigurationSection section = config.getConfigurationSection(path + ".arguments");
            if (section != null) {
                args.load(section);
            }
            if (actionType == ActionType.LAUNCH_FUNCTION || actionType == ActionType.LAUNCH_METHOD) {
                if (config.getString(path+".name") != null) {
                    args.setArgumentValue("name", ValueType.TEXT,config.getString(path+".name"));
                }
            } else if (actionType == ActionType.SELECTION_SET || actionType == ActionType.SELECTION_ADD || actionType == ActionType.SELECTION_REMOVE) {
                if (config.getConfigurationSection(path+".condition") != null) {
                    boolean isOpposed = config.getBoolean(path+".condition.opposed",false);
                    ActionCategory conditionCategory = ActionCategory.valueOf(config.getString(path+".condition.category"));
                    ActionType conditionType = ActionType.valueOf(config.getString(path+".condition.type"));
                    return actionType.getActionClass().getConstructor(Executor.class,int.class, Arguments.class, ActionCategory.class, ActionType.class, boolean.class).newInstance(executor,config.getInt(path+".location.x"),args,conditionCategory,conditionType,isOpposed);
                } else if (config.getString(path+".target") != null){
                    if (targetString != null && !targetString.isEmpty()) {
                        target = Target.valueOf(targetString);
                    }
                    return actionType.getActionClass().getConstructor(Executor.class,int.class, Arguments.class, Target.class).newInstance(executor,config.getInt(path+".location.x"),args,target);
                }
                if (config.getString(path+".condition.type") != null) {
                    args.setArgumentValue("name", ValueType.TEXT,config.getString(path+".name"));
                }
            }
            if (actionType.getCategory().isMultiAction()) {
                if (config.getConfigurationSection(path+".actions") != null) {
                    if (actionType.getCategory().isCondition()) {
                        boolean isOpposed = config.getBoolean(path+".opposed",false);
                        return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,Arguments.class,List.class,List.class,boolean.class).newInstance(executor,target,config.getInt(path+".location.x"),args,createActionList(executor,path+".actions",config),createActionList(executor,path+".else",config),isOpposed);
                    } else {
                        return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,Arguments.class,List.class).newInstance(executor,target,config.getInt(path+".location.x"),args,createActionList(executor,path+".actions",config));
                    }
                }
            }
            return actionType.getActionClass().getConstructor(Executor.class, Target.class, int.class,Arguments.class).newInstance(executor,target,config.getInt(path+".location.x"),args);
        } catch (Exception error) {
            return null;
        }
    }*/

}
