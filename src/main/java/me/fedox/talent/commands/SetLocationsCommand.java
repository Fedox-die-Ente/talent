package me.fedox.talent.commands;

import me.fedox.talent.Talent;
import me.fedox.talent.utils.Constants;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.fedox.talent.utils.Constants.*;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/26/2024 7:41 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

public class SetLocationsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender.hasPermission("talent.admin")) {

            Player player = (Player) commandSender;

            Location location = player.getLocation();

            if (strings.length == 0) {
                commandSender.sendMessage(Constants.PLUGIN_PREFIX + "Bitte gebe einen Location Typen an.");
                return false;
            }

            switch (strings[0]) {
                case "sound":

                    Talent.getInstance().getConfig().set(LOCATIONS_SOUND_WORLD, location.getWorld().getName());
                    Talent.getInstance().getConfig().set(LOCATIONS_SOUND_X, location.getX());
                    Talent.getInstance().getConfig().set(LOCATIONS_SOUND_Y, location.getY());
                    Talent.getInstance().getConfig().set(LOCATIONS_SOUND_Z, location.getZ());

                    Talent.getInstance().saveConfig();

                    player.sendMessage(Constants.PLUGIN_PREFIX + "Sound/Partikel Location gesetzt!");

                    break;
                case "stage":

                    Talent.getInstance().getConfig().set(LOCATIONS_STAGE_WORLD, location.getWorld().getName());
                    Talent.getInstance().getConfig().set(LOCATIONS_STAGE_X, location.getX());
                    Talent.getInstance().getConfig().set(LOCATIONS_STAGE_Y, location.getY());
                    Talent.getInstance().getConfig().set(LOCATIONS_STAGE_Z, location.getZ());
                    Talent.getInstance().getConfig().set(LOCATIONS_STAGE_YAW, location.getYaw());
                    Talent.getInstance().getConfig().set(LOCATIONS_STAGE_PITCH, location.getPitch());

                    Talent.getInstance().saveConfig();

                    player.sendMessage(Constants.PLUGIN_PREFIX + "Stage Location gesetzt!");

                    break;
                case "end":

                    Talent.getInstance().getConfig().set(LOCATIONS_END_WORLD, location.getWorld().getName());
                    Talent.getInstance().getConfig().set(LOCATIONS_END_X, location.getX());
                    Talent.getInstance().getConfig().set(LOCATIONS_END_Y, location.getY());
                    Talent.getInstance().getConfig().set(LOCATIONS_END_Z, location.getZ());
                    Talent.getInstance().getConfig().set(LOCATIONS_END_YAW, location.getYaw());
                    Talent.getInstance().getConfig().set(LOCATIONS_END_PITCH, location.getPitch());

                    Talent.getInstance().saveConfig();

                    player.sendMessage(Constants.PLUGIN_PREFIX + "End Location gesetzt!");

                    break;
                case "spawn":

                    Talent.getInstance().getConfig().set(LOCATIONS_SPAWN_WORLD, location.getWorld().getName());
                    Talent.getInstance().getConfig().set(LOCATIONS_SPAWN_X, location.getX());
                    Talent.getInstance().getConfig().set(LOCATIONS_SPAWN_Y, location.getY());
                    Talent.getInstance().getConfig().set(LOCATIONS_SPAWN_Z, location.getZ());
                    Talent.getInstance().getConfig().set(LOCATIONS_SPAWN_YAW, location.getYaw());
                    Talent.getInstance().getConfig().set(LOCATIONS_SPAWN_PITCH, location.getPitch());

                    Talent.getInstance().saveConfig();

                    player.sendMessage(Constants.PLUGIN_PREFIX + "Spawn Location gesetzt!");

                    break;
                case "onstage":

                    Talent.getInstance().getConfig().set(LOCATIONS_ON_STAGE_WORLD, location.getWorld().getName());
                    Talent.getInstance().getConfig().set(LOCATIONS_ON_STAGE_X, location.getX());
                    Talent.getInstance().getConfig().set(LOCATIONS_ON_STAGE_Y, location.getY());
                    Talent.getInstance().getConfig().set(LOCATIONS_ON_STAGE_Z, location.getZ());

                    Talent.getInstance().saveConfig();

                    player.sendMessage(Constants.PLUGIN_PREFIX + "OnStage Location gesetzt!");

                    break;
                default:
                    commandSender.sendMessage(Constants.PLUGIN_PREFIX + "Bitte gebe einen gültigen Location Typen an.");
                    return false;
            }

            return true;
        } else {
            commandSender.sendMessage(Constants.PLUGIN_PREFIX + "Du hast keine Rechte dazu.");
            return false;
        }

    }
}
