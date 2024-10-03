package me.fedox.talent.commands;

import me.fedox.talent.Talent;
import me.fedox.talent.utils.Constants;
import me.fedox.talent.worker.QueueWorker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/26/2024 8:52 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

/**
 * Command executor for handling queue-related commands in the Talent plugin.
 */
public class QueueCommand implements CommandExecutor {

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

        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();

        if (strings.length == 0) {
            player.sendMessage("§8» §7Bitte begebe dich in den Discord, in die §a§lStreamerwarteschlange.");
            player.sendMessage("§8» §7Und benenne dich wie in Minecraft, also §a§l" + player.getName() + "§7.");
            player.sendMessage("§8» §7Dannach kannst du den Befehl §a§l/queue confirm§7 nutzen.");
            return false;
        }

        if (strings[0].equalsIgnoreCase("confirm")) {

            if (queueWorker.hasAlreadyPlayedThisSession(player)) {
                player.sendMessage(Constants.PLUGIN_PREFIX + "§cDu hast bereits in dieser Session gespielt.");
                return false;
            }

            if (queueWorker.isPlayerInQueue(player)) {
                player.sendMessage(Constants.PLUGIN_PREFIX + "§cDu bist bereits in der Warteschlange.");
                return false;
            }

            player.sendMessage("§7Du wurdest in die Warteschlange eingereiht.");
            player.sendMessage("§7Bitte warte geduldig bis du dran bist, beachte den Server nicht zu verlassen!");
            player.sendMessage("§7Falls du doch gehen musst, und wieder kommen möchtest, nutze §a/queue confirm§7 erneut.");

            queueWorker.addPlayerToQueue(player);
            return true;
        }

        if (strings[0].equalsIgnoreCase("debug")) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "§7In der Warteschlange: " + queueWorker.getWaitingPlayers().size());
            player.sendMessage(Constants.PLUGIN_PREFIX + "§7Warteschlange: " + queueWorker.getWaitingPlayers());

            if (queueWorker.getCurrentSelectedPlayer() == null) {
                player.sendMessage(Constants.PLUGIN_PREFIX + "§7Derzeitiger Spieler: §cNiemand");
                return true;
            }
            player.sendMessage(Constants.PLUGIN_PREFIX + "§7Derzeitiger Spieler: " + queueWorker.getCurrentSelectedPlayer().getName());

            return true;
        }

        if (strings[0].equalsIgnoreCase("add")) {
            queueWorker.addPlayerToQueue(player);

            return true;
        }

        return false;
    }
}