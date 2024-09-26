package me.fedox.talent.worker;

import me.fedox.talent.Talent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/26/2024 9:00 PM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

/**
 * Listener class for handling player quit events.
 */
public class QueueListener implements Listener {

    /**
     * Event handler for PlayerQuitEvent.
     * Removes the player from the queue if they are in it.
     *
     * @param event The PlayerQuitEvent triggered when a player quits.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();

        if (queueWorker.isPlayerInQueue(event.getPlayer())) {
            queueWorker.removePlayerFromQueue(event.getPlayer());
        }

        if (queueWorker.getCurrentSelectedPlayer() == event.getPlayer()) {
            queueWorker.setCurrentSelectedPlayer(null);
        }

    }
}