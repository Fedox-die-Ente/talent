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

public class VoteListener implements Listener {

    private final Talent plugin;

    public VoteListener(Talent plugin) {
        this.plugin = plugin;
    }

    public static void handleLastVote() {
        ShowManager showManager = Talent.getInstance().getShowManager();
        showManager.startEndSequence();

        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();
        queueWorker.cleanUp();
    }

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

    private void spawnParticles(Location location) {
        location.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, location, 50, 1, 1, 1, 0.1);
        location.getWorld().spawnParticle(Particle.WITCH, location, 100, 1, 1, 1, 0.1);
        location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 50, 1, 1, 1, 0.1);
    }

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