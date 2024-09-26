package me.fedox.talent.utils;

import me.fedox.talent.Talent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

/**
 * Manages the show events and sequences for the Talent plugin.
 */
public class ShowManager {

    private final Talent plugin;
    private final Location centerLocation;
    private final Random random;
    private final List<Location> tourLocations;

    /**
     * Constructor for ShowManager.
     * Initializes the manager with the plugin instance and configuration settings.
     *
     * @param plugin The Talent plugin instance.
     */
    public ShowManager(Talent plugin) {
        this.plugin = plugin;
        this.random = new Random();

        var config = plugin.getConfig();
        this.centerLocation = new Location(
                Bukkit.getWorld(config.getString(Constants.LOCATIONS_SOUND_WORLD)),
                config.getDouble(Constants.LOCATIONS_SOUND_X),
                config.getDouble(Constants.LOCATIONS_SOUND_Y),
                config.getDouble(Constants.LOCATIONS_SOUND_Z)
        );

        this.tourLocations = Talent.getInstance().getCameraLocations();
    }

    /**
     * Starts the end sequence of the show, including sounds, titles, and teleporting winners.
     */
    public void startEndSequence() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            player.sendTitle("§8§k!!! §aVORBEI §8§k!!!", "", 10, 70, 20);
        }

        // Get all winners
        for (String winner : Talent.getInstance().getWinners()) {
            Player target = Bukkit.getPlayer(winner);
            if (target != null) {
                Location stageLocation = new Location(
                        Bukkit.getWorld(plugin.getConfig().getString(Constants.LOCATIONS_ON_STAGE_WORLD)),
                        plugin.getConfig().getDouble(Constants.LOCATIONS_ON_STAGE_X),
                        plugin.getConfig().getDouble(Constants.LOCATIONS_ON_STAGE_Y),
                        plugin.getConfig().getDouble(Constants.LOCATIONS_ON_STAGE_Z)
                );

                target.teleport(stageLocation);
            }
        }

        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "Die Show ist vorbei!");

        playEndMusic();

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                switch (step) {
                    case 0:
                        startFireworkShow();
                        startParticleShow();
                        break;
                    case 1:
                        announceWinners();
                        break;
                    case 2:
                        finalLightShow();
                        break;
                    case 3:
                        startEpicTour();
                        this.cancel();
                        break;
                }
                step++;
            }
        }.runTaskTimer(plugin, 0L, 200L);
    }

    /**
     * Starts a firework show at the center location.
     */
    private void startFireworkShow() {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 20) {
                    this.cancel();
                    return;
                }
                launchFirework();
                count++;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    /**
     * Launches a single firework at a random location near the center.
     */
    private void launchFirework() {
        Location loc = centerLocation.clone().add(random.nextInt(21) - 10, 0, random.nextInt(21) - 10);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                .trail(true)
                .flicker(true)
                .build();

        fwm.addEffect(effect);
        fwm.setPower(random.nextInt(3) + 1);
        fw.setFireworkMeta(fwm);
    }

    /**
     * Starts a particle show at the center location.
     */
    private void startParticleShow() {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 400) {
                    this.cancel();
                    return;
                }
                spawnParticles();
                count++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Spawns particles at random locations near the center.
     */
    private void spawnParticles() {
        Particle[] particles = {Particle.TOTEM_OF_UNDYING, Particle.WITCH, Particle.HAPPY_VILLAGER, Particle.NOTE, Particle.HEART};
        for (Particle particle : particles) {
            centerLocation.getWorld().spawnParticle(particle,
                    centerLocation.clone().add(random.nextInt(41) - 20, random.nextInt(11), random.nextInt(41) - 20),
                    15, 0.5, 0.5, 0.5, 0);
        }
    }

    /**
     * Plays the end music for all online players.
     */
    private void playEndMusic() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.MUSIC_CREDITS, 2, 1);
        }
    }

    /**
     * Announces the winners to all online players.
     */
    private void announceWinners() {
        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "Die Gewinner sind:");

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= Talent.getInstance().getWinners().size()) {
                    this.cancel();
                    return;
                }
                String winner = Talent.getInstance().getWinners().get(index);
                Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "§8● §a" + winner);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    player.spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.1);
                }
                index++;
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }

    /**
     * Starts the final light show with lightning effects.
     */
    private void finalLightShow() {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 100) {
                    this.cancel();
                    return;
                }
                lightningEffect();
                count++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Creates a lightning effect at a random location near the center.
     */
    private void lightningEffect() {
        Location loc = centerLocation.clone().add(random.nextInt(41) - 20, 0, random.nextInt(41) - 20);
        loc.getWorld().strikeLightningEffect(loc);
    }

    /**
     * Starts an epic tour for all online players, teleporting them to various locations.
     */
    private void startEpicTour() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
        }

        new BukkitRunnable() {
            int locationIndex = 0;
            int step = 0;

            @Override
            public void run() {
                if (locationIndex >= tourLocations.size()) {
                    endTour();
                    this.cancel();
                    return;
                }

                Location targetLocation = tourLocations.get(locationIndex);

                if (step == 0) {
                    teleportPlayersToLocation(targetLocation);
                } else if (step >= 100) {
                    locationIndex++;
                    step = -1;
                }

                createAmbientEffects(targetLocation);
                step++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Teleports all online players to the specified location.
     *
     * @param location The location to teleport players to.
     */
    private void teleportPlayersToLocation(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(location);
        }
    }

    /**
     * Creates ambient effects at the specified location.
     *
     * @param location The location to create effects at.
     */
    private void createAmbientEffects(Location location) {
        World world = location.getWorld();
        world.spawnParticle(Particle.END_ROD, location, 50, 5, 5, 5, 0.1);
        world.spawnParticle(Particle.TOTEM_OF_UNDYING, location, 30, 3, 3, 3, 0.05);

        if (random.nextInt(20) == 0) {
            launchFirework(location);
        }
    }

    /**
     * Launches a firework at the specified location.
     *
     * @param location The location to launch the firework at.
     */
    private void launchFirework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                .trail(true)
                .flicker(true)
                .build();

        fwm.addEffect(effect);
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);
    }

    /**
     * Ends the tour and teleports all players back to the stage.
     */
    private void endTour() {
        Location endLocation = new Location(
                Bukkit.getWorld(plugin.getConfig().getString(Constants.LOCATIONS_ON_STAGE_WORLD)),
                plugin.getConfig().getDouble(Constants.LOCATIONS_ON_STAGE_X),
                plugin.getConfig().getDouble(Constants.LOCATIONS_ON_STAGE_Y),
                plugin.getConfig().getDouble(Constants.LOCATIONS_ON_STAGE_Z)
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(endLocation);
        }

        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "Vielen Dank fürs mitmachen!");
        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "Der Server wird in 120 Sekunden gestoppt.");

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.shutdown();
            }
        }.runTaskLater(plugin, 2400L);
    }
}