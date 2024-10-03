package me.fedox.talent.commands;

import me.fedox.talent.Talent;
import me.fedox.talent.utils.Constants;
import me.fedox.talent.worker.QueueWorker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/26/2024 9:06 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

/**
 * Command executor for handling the "next" command in the Talent plugin.
 */
public class NextCommand implements CommandExecutor {

    /**
     * Executes the given command, returning its success.
     *
     * @param commandSender Source of the command.
     * @param command       Command which was executed.
     * @param s             Alias of the command which was used.
     * @param strings       Passed command arguments.
     * @return true if the command was successful, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        var config = Talent.getInstance().getConfig();

        var stageLoc = new Location(
                Bukkit.getWorld(config.getString(Constants.LOCATIONS_STAGE_WORLD)),
                config.getDouble(Constants.LOCATIONS_STAGE_X),
                config.getDouble(Constants.LOCATIONS_STAGE_Y),
                config.getDouble(Constants.LOCATIONS_STAGE_Z),
                (float) config.getDouble(Constants.LOCATIONS_STAGE_YAW),
                (float) config.getDouble(Constants.LOCATIONS_STAGE_PITCH)
        );

        if (!player.hasPermission("talent.admin")) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "§cDu hast keine Rechte dazu.");
            return false;
        }

        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();

        if (!queueWorker.hasNextPlayer()) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "§cEs gibt keinen nächsten Spieler.");
            return false;
        }

        if (queueWorker.getCurrentSelectedPlayer() != null) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "§7Bitte warte bis der aktuelle Spieler fertig ist.");
            return false;
        }

        Player nextPlayer = queueWorker.nextPlayer();
        
        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "Der nächste Spieler ist " + nextPlayer.getName() + ".");
        nextPlayer.teleport(stageLoc);
        nextPlayer.playSound(nextPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        queueWorker.addAlreadyPlayedPlayer(nextPlayer);

        return true;
    }
}