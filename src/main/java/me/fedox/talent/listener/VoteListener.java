package me.fedox.talent.listener;

import me.fedox.talent.Talent;
import me.fedox.talent.utils.Constants;
import me.fedox.talent.utils.GoldenBuzzer;
import me.fedox.talent.utils.ShowManager;
import me.fedox.talent.worker.QueueWorker;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Listener class for handling player votes.
 */
public class VoteListener implements Listener {

    private final Talent plugin;

    /**
     * Constructor for VoteListener.
     * Initializes the listener with the plugin instance.
     *
     * @param plugin The Talent plugin instance.
     */
    public VoteListener(Talent plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the last vote by starting the end sequence and cleaning up the queue.
     */
    public static void handleLastVote() {
        ShowManager showManager = Talent.getInstance().getShowManager();
        showManager.startEndSequence();

        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();
        queueWorker.cleanUp();
    }

    /**
     * Event handler for PlayerInteractEvent.
     * Handles player votes based on the block they interact with.
     *
     * @param event The PlayerInteractEvent triggered when a player interacts with a block.
     */
    @EventHandler
    public void onVote(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        QueueWorker queueWorker = plugin.getQueueWorker();

        Player jury = event.getPlayer();
        Player currentPlayer = queueWorker.getCurrentSelectedPlayer();

        if (jury.hasPermission("talent.jury")) {
            if (event.getClickedBlock().getType() == Material.STONE_BUTTON) {
                switch (event.getClickedBlock().getRelative(0, -1, 0).getType()) {
                    case REDSTONE_BLOCK:
                        handleNegativeVote(currentPlayer, jury);
                        break;
                    case EMERALD_BLOCK:
                        handlePositiveVote(currentPlayer, jury);
                        break;
                    case GOLD_BLOCK:
                        handleGoldenBuzzer(event, currentPlayer, jury);
                        break;
                }
            }
        }
    }

    /**
     * Handles the Golden Buzzer event.
     * Triggers the Golden Buzzer effects and updates the queue.
     *
     * @param event         The PlayerInteractEvent triggered when a player interacts with a block.
     * @param currentPlayer The current player being judged.
     * @param jury          The jury player who triggered the event.
     */
    private void handleGoldenBuzzer(PlayerInteractEvent event, Player currentPlayer, Player jury) {
        if (currentPlayer == null) {
            jury.sendMessage(Constants.PLUGIN_PREFIX + "Es ist kein Spieler an der Reihe.");
            return;
        }

        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();

        event.getClickedBlock().setType(Material.AIR);
        event.getClickedBlock().getRelative(0, -1, 0).setType(Material.AIR);

        GoldenBuzzer goldenBuzzer = new GoldenBuzzer();
        goldenBuzzer.triggerGoldenBuzzer(queueWorker.getCurrentSelectedPlayer());
    }

    /**
     * Handles a negative vote.
     * Creates effects and updates the queue for a negative vote.
     *
     * @param currentPlayer The current player being judged.
     * @param jury          The jury player who cast the vote.
     */
    private void handleNegativeVote(Player currentPlayer, Player jury) {
        if (currentPlayer == null) {
            jury.sendMessage(Constants.PLUGIN_PREFIX + "Es ist kein Spieler an der Reihe.");
            return;
        }

        var config = plugin.getConfig();
        Location endLoc = new Location(currentPlayer.getWorld(),
                config.getDouble(Constants.LOCATIONS_END_X),
                config.getDouble(Constants.LOCATIONS_END_Y),
                config.getDouble(Constants.LOCATIONS_END_Z));

        currentPlayer.getWorld().createExplosion(currentPlayer.getLocation(), 0.0f, false);
        currentPlayer.getWorld().spawnParticle(Particle.ASH, currentPlayer.getLocation(), 100, 0.5, 0.5, 0.5, 0.0);
        currentPlayer.getWorld().spawnParticle(Particle.CLOUD, currentPlayer.getLocation(), 100, 0.5, 0.5, 0.5, 0.0);

        currentPlayer.setVelocity(currentPlayer.getVelocity().setY(2.0));

        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + currentPlayer.getName() + " §7ist nicht weitergekommen!");
        currentPlayer.sendMessage(Constants.PLUGIN_PREFIX + "§7Du wurdest rausgeworfen, viel Erfolg beim nächsten Mal!");

        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();
        queueWorker.setCurrentSelectedPlayer(null);

        new BukkitRunnable() {
            @Override
            public void run() {
                currentPlayer.teleport(endLoc);

                if (!queueWorker.hasNextPlayer()) {
                    handleLastVote();
                }
            }
        }.runTaskLater(plugin, 20L);
    }

    /**
     * Handles a positive vote.
     * Creates effects and updates the queue for a positive vote.
     *
     * @param currentPlayer The current player being judged.
     * @param jury          The jury player who cast the vote.
     */
    private void handlePositiveVote(Player currentPlayer, Player jury) {
        if (currentPlayer == null) {
            jury.sendMessage(Constants.PLUGIN_PREFIX + "Es ist kein Spieler an der Reihe.");
            return;
        }

        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "§a" + currentPlayer.getName() + " §7hat den Vote von §a" + jury.getName() + " §7erhalten!");
        currentPlayer.sendMessage(Constants.PLUGIN_PREFIX + "§7Du hast den Vote von §a" + jury.getName() + " §7erhalten!");

        Location effectsLoc = currentPlayer.getLocation().add(0, 2, 0);

        startCoolEffects(currentPlayer, effectsLoc);
    }

    /**
     * Starts cool effects for a positive vote.
     * Includes particles, fireworks, and teleportation.
     *
     * @param player   The player who received the positive vote.
     * @param location The location to create effects at.
     */
    private void startCoolEffects(Player player, Location location) {
        player.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 3) {

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            var config = plugin.getConfig();
                            Location endLoc = new Location(player.getWorld(),
                                    config.getDouble(Constants.LOCATIONS_END_X),
                                    config.getDouble(Constants.LOCATIONS_END_Y),
                                    config.getDouble(Constants.LOCATIONS_END_Z));

                            player.teleport(endLoc);

                            QueueWorker queueWorker = Talent.getInstance().getQueueWorker();

                            queueWorker.addWinner(player);
                            queueWorker.setCurrentSelectedPlayer(null);

                            if (!queueWorker.hasNextPlayer()) {
                                handleLastVote();
                            }
                        }
                    }.runTaskLater(plugin, 20L);

                    this.cancel();
                    return;
                }
                spawnParticles(location);
                spawnFirework(location);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 5L);

        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.GLOWING, 200, 1));
    }

    /**
     * Spawns particles at the specified location.
     *
     * @param location The location to spawn particles at.
     */
    private void spawnParticles(Location location) {
        location.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, location, 50, 1, 1, 1, 0.1);
        location.getWorld().spawnParticle(Particle.WITCH, location, 100, 1, 1, 1, 0.1);
        location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 50, 1, 1, 1, 0.1);
    }

    /**
     * Spawns a firework at the specified location.
     *
     * @param location The location to spawn the firework at.
     */
    private void spawnFirework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();

        Random random = new Random();
        FireworkEffect.Type type = FireworkEffect.Type.BALL_LARGE;
        Color color1 = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color color2 = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        FireworkEffect effect = FireworkEffect.builder()
                .flicker(true)
                .withColor(color1)
                .withFade(color2)
                .with(type)
                .trail(true)
                .build();

        fwm.addEffect(effect);
        fwm.setPower(0);
        fw.setFireworkMeta(fwm);
    }
}