package me.fedox.talent.commands;

import me.fedox.talent.Talent;
import me.fedox.talent.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/26/2024 8:39 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

public class BuildCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (!player.hasPermission("talent.admin")) {
            player.sendMessage(Constants.PLUGIN_PREFIX + "Du hast keine Rechte dazu.");
            return false;
        }

        if (Talent.getInstance().getBuildModePlayers().contains(player)) {
            Talent.getInstance().getBuildModePlayers().remove(player);
            player.sendMessage(Constants.PLUGIN_PREFIX + "Du bist nicht mehr im Baumodus.");
        } else {
            Talent.getInstance().getBuildModePlayers().add(player);
            player.sendMessage(Constants.PLUGIN_PREFIX + "Du bist nun im Baumodus.");
        }

        return true;
    }
}
