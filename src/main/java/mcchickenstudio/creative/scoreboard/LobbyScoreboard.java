/*
Creative+, Minecraft plugin.
(C) 2022-2024, McChicken Studio, mcchickenstudio@gmail.com

Creative+ is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Creative+ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*//*


package mcchickenstudio.creative.scoreboard;

import mcchickenstudio.creative.utils.ErrorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import java.util.HashMap;
import java.util.Map;

// скорборд не работает, он не отсылает ошибок и при этом ничего не показывает игроку

public class LobbyScoreboard {

    public static Map<Player, Integer> scoreboardTasks = new HashMap<>();
    static final Plugin plugin = Main.getPlugin();

    public static void show(Player player) {

        Integer taskID = new BukkitRunnable() {

            public void run() {
                if (player.isOnline()) {
                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                    org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
                    Objective objective = board.registerNewObjective("", "dummy");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    try {
                        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("scoreboards.lobby.name")));
                        int configScoreboardSize = plugin.getConfig().getStringList("scoreboards.lobby.lines").size();
                        int inGameScoreboardIndex = configScoreboardSize;
                        for (int configScoreboardIndex = 0; configScoreboardIndex < configScoreboardSize; configScoreboardIndex++) {
                            inGameScoreboardIndex--;
                            Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getStringList("scoreboards.lobby.lines").get(configScoreboardIndex).replace("%player%",player.getName()).replace("%online%",String.valueOf(Bukkit.getOnlinePlayers().size()))));
                            score.setScore(inGameScoreboardIndex);
                        }
                    } catch (NullPointerException error) {
                        ErrorUtils.sendWarningErrorMessage("При попытке показать скорборд лобби произошла ошибка: " + error.getMessage());
                        cancel();
                    } catch (IllegalArgumentException error) {
                        ErrorUtils.sendWarningErrorMessage("При попытке показать скорборд скорее всего получилась строка длинее 40 символов, укоротите её. " + error.getMessage());
                        cancel();
                    }
                    player.setScoreboard(board);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L * plugin.getConfig().getInt("scoreboards.lobby.update-every-seconds")).getTaskId();
        scoreboardTasks.put(player, taskID);

    }

    public static void hide(Player player) {
        try {
            plugin.getServer().getScheduler().cancelTask(scoreboardTasks.get(player));
        } catch (NullPointerException error) {
            ErrorUtils.sendWarningErrorMessage("Ошибка при попытке скрыть скорборд, плагин не получил сервер? "+error.getMessage());
        }
    }

}
*/
