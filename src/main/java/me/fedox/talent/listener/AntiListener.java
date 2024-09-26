package me.fedox.talent.listener;

import me.fedox.talent.Talent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/26/2024 8:38 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

/**
 * Listener class for handling various anti-griefing events.
 */
public class AntiListener implements Listener {

    /**
     * Checks if a player is in build mode.
     *
     * @param player The player to check.
     * @return True if the player is in build mode, false otherwise.
     */
    private boolean isBuildMode(Player player) {
        return Talent.getInstance().getBuildModePlayers().contains(player);
    }

    /**
     * Event handler for EntityDamageEvent.
     * Cancels all damage events.
     *
     * @param event The EntityDamageEvent triggered when an entity takes damage.
     */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    /**
     * Event handler for BlockBreakEvent.
     * Cancels block breaking if the player is not in build mode.
     *
     * @param event The BlockBreakEvent triggered when a player breaks a block.
     */
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!isBuildMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /**
     * Event handler for BlockPlaceEvent.
     * Cancels block placing if the player is not in build mode.
     *
     * @param event The BlockPlaceEvent triggered when a player places a block.
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!isBuildMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /**
     * Event handler for FoodLevelChangeEvent.
     * Cancels all food level changes.
     *
     * @param event The FoodLevelChangeEvent triggered when an entity's food level changes.
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

}