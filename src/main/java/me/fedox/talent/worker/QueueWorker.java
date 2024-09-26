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

    public QueueWorker(Talent plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToQueue(Player player) {
        waitingPlayers.add(player);
        Talent.getInstance().getLogger().info("Added " + player.getName() + " to the queue.");
    }

    public void addAlreadyPlayedPlayer(Player player) {
        alreadyPlayedPlayers.add(player);
        Talent.getInstance().getLogger().info("Added " + player.getName() + " to the already played players.");
    }

    public void removePlayerFromQueue(Player player) {
        waitingPlayers.remove(player);
        Talent.getInstance().getLogger().info("Removed " + player.getName() + " from the queue.");
    }

    public void addWinner(Player player) {
        winners.add(player);

        Talent.getInstance().addWinPlayer(player);

        Talent.getInstance().getLogger().info("Added " + player.getName() + " to the winners.");
    }

    public boolean isPlayerInQueue(Player player) {
        return waitingPlayers.contains(player);
    }

    public Player nextPlayer() {
        List<Player> shuffledList = new ArrayList<>(waitingPlayers);
        Collections.shuffle(shuffledList);

        Player player = shuffledList.getFirst();
        removePlayerFromQueue(player);
        setCurrentSelectedPlayer(player);

        return player;
    }

    public boolean hasNextPlayer() {
        return !waitingPlayers.isEmpty();
    }

    public boolean hasAlreadyPlayedThisSession(Player player) {
        return alreadyPlayedPlayers.contains(player);
    }

    public void cleanUp() {
        waitingPlayers.clear();
        alreadyPlayedPlayers.clear();
        winners.clear();
    }
}
