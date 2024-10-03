package me.fedox.talent.listener;

import me.fedox.talent.Talent;
import me.fedox.talent.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * © 2024 Florian O and Fabian W.
 * Created on: 10/3/2024 5:08 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

public class OtherListener implements Listener {


    // queue message

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var config = Talent.getInstance().getConfig();

        Location spawnLoc = new Location(
                Bukkit.getWorld(config.getString(Constants.LOCATIONS_SPAWN_WORLD)),
                config.getDouble(Constants.LOCATIONS_SPAWN_X),
                config.getDouble(Constants.LOCATIONS_SPAWN_Y),
                config.getDouble(Constants.LOCATIONS_SPAWN_Z),
                (float) config.getDouble(Constants.LOCATIONS_SPAWN_YAW),
                (float) config.getDouble(Constants.LOCATIONS_SPAWN_PITCH)
        );

        event.getPlayer().teleport(spawnLoc);

        event.setJoinMessage(Constants.PLUGIN_PREFIX + "Der Spieler §a" + event.getPlayer().getName() + " §7hat den Server betreten!");
        event.getPlayer().sendMessage(Constants.PLUGIN_PREFIX + "Führe §a/queue §7aus, um dich in die Warteschlange einzutragen.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(Constants.PLUGIN_PREFIX + "Der Spieler §a" + event.getPlayer().getName() + " §7hat den Server verlassen!");
    }

}
