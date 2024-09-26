package me.fedox.talent.commands;

import me.fedox.talent.Talent;
import me.fedox.talent.utils.Constants;
import me.fedox.talent.worker.QueueWorker;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/26/2024 7:33 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

public class SetCurrentPlayerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (!player.hasPermission("talent.admin")) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "Du hast keine Rechte dazu.");
            return false;
        }

        if (strings.length == 0) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "Bitte gebe einen Spieler an.");
            return false;
        }

        Player target = Bukkit.getPlayer(strings[0]);

        if (target == null) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "Der Spieler ist nicht online.");
            return false;
        }

        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();
        queueWorker.setCurrentSelectedPlayer(target);

        target.sendMessage(Constants.PLUGIN_PREFIX + "Du wurdest als derzeitiger Spieler geforced.");
        player.sendMessage(Constants.PLUGIN_PREFIX + "Der Spieler wurde als derzeitiger Spieler gesetzt.");

        return true;
    }
}
