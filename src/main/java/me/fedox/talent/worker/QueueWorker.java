package me.fedox.talent.worker;

import lombok.Getter;
import lombok.Setter;
import me.fedox.talent.Talent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * © 2024 Florian O. (https://github.com/Fedox-die-Ente)
 * Created on: 9/9/2024 6:36 AM
 * <p>
 * https://www.youtube.com/watch?v=tjBCjfB3Hq8
 */

public class QueueWorker {

    private final Talent plugin;

    @Getter
    private final List<Player> alreadyPlayedPlayers = new ArrayList<>();
    @Getter
    private final List<Player> waitingPlayers = new ArrayList<>();
    private final List<Player> winners = new ArrayList<>();

    @Getter
    @Setter
    private Player currentSelectedPlayer;

    /**
     * Constructor for QueueWorker.
     *
     * @param plugin The Talent plugin instance.
     */
    public QueueWorker(Talent plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a player to the queue.
     *
     * @param player The player to add.
     */
    public void addPlayerToQueue(Player player) {
        waitingPlayers.add(player);
        Talent.getInstance().getLogger().info("Added " + player.getName() + " to the queue.");
    }

    /**
     * Adds a player to the list of already played players.
     *
     * @param player The player to add.
     */
    public void addAlreadyPlayedPlayer(Player player) {
        alreadyPlayedPlayers.add(player);
        Talent.getInstance().getLogger().info("Added " + player.getName() + " to the already played players.");
    }

    /**
     * Removes a player from the queue.
     *
     * @param player The player to remove.
     */
    public void removePlayerFromQueue(Player player) {
        waitingPlayers.remove(player);
        Talent.getInstance().getLogger().info("Removed " + player.getName() + " from the queue.");
    }

    /**
     * Adds a player to the list of winners.
     *
     * @param player The player to add.
     */
    public void addWinner(Player player) {
        winners.add(player);

        Talent.getInstance().addWinPlayer(player);

        Talent.getInstance().getLogger().info("Added " + player.getName() + " to the winners.");
    }

    /**
     * Checks if a player is in the queue.
     *
     * @param player The player to check.
     * @return True if the player is in the queue, false otherwise.
     */
    public boolean isPlayerInQueue(Player player) {
        return waitingPlayers.contains(player);
    }

    /**
     * Selects the next player from the queue.
     *
     * @return The next player.
     */
    public Player nextPlayer() {
        List<Player> shuffledList = new ArrayList<>(waitingPlayers);
        Collections.shuffle(shuffledList);

        Player player = shuffledList.getFirst();
        removePlayerFromQueue(player);
        setCurrentSelectedPlayer(player);

        return player;
    }

    /**
     * Checks if there is a next player in the queue.
     *
     * @return True if there is a next player, false otherwise.
     */
    public boolean hasNextPlayer() {
        return !waitingPlayers.isEmpty();
    }

    /**
     * Checks if a player has already played in this session.
     *
     * @param player The player to check.
     * @return True if the player has already played, false otherwise.
     */
    public boolean hasAlreadyPlayedThisSession(Player player) {
        return alreadyPlayedPlayers.contains(player);
    }

    /**
     * Cleans up the queue by clearing all lists.
     */
    public void cleanUp() {
        waitingPlayers.clear();
        alreadyPlayedPlayers.clear();
        winners.clear();
    }
}