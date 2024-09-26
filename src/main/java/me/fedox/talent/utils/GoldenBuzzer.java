package me.fedox.talent.utils;

import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import me.fedox.talent.Talent;
import me.fedox.talent.listener.VoteListener;
import me.fedox.talent.worker.QueueWorker;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoldenBuzzer implements Listener {

    private final Talent plugin;
    private final List<Block> redstoneLamps = new ArrayList<>();
    private boolean isGoldenBuzzerActive = false;
    private boolean currentLampState = false;
    private BukkitRunnable effectsTask;
    private Location soundLoc;

    public GoldenBuzzer() {
//        this.buzzedPlayer = buzzedPlayer;
        this.plugin = Talent.getInstance();
    }

    public void triggerGoldenBuzzer(Player player) {
        var config = plugin.getConfig();

        soundLoc = new Location(Bukkit.getWorld(config.getString(Constants.LOCATIONS_SOUND_WORLD)),
                config.getDouble(Constants.LOCATIONS_SOUND_X),
                config.getDouble(Constants.LOCATIONS_SOUND_Y),
                config.getDouble(Constants.LOCATIONS_SOUND_Z));
        File file = new File("plugins/talent/songs/golden_buzzer.nbs");

        Song song = NBSDecoder.parse(file);

        PositionSongPlayer songPlayer = new PositionSongPlayer(song);
        songPlayer.setTargetLocation(soundLoc);
        songPlayer.setDistance(70);
        songPlayer.setPlaying(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            songPlayer.addPlayer(p);
        }

        isGoldenBuzzerActive = true;
        currentLampState = false;
        findRedstoneLamps();
        startEffects();
    }

    private void findRedstoneLamps() {
        World world = soundLoc.getWorld();
        int radius = 80;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = world.getBlockAt(soundLoc.getBlockX() + x, soundLoc.getBlockY() + y, soundLoc.getBlockZ() + z);
                    if (block.getType() == Material.REDSTONE_LAMP) {
                        redstoneLamps.add(block);
                    }
                }
            }
        }
    }

    private void startEffects() {
        effectsTask = new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!isGoldenBuzzerActive) {
                    this.cancel();
                    return;
                }

                currentLampState = !currentLampState;

                spawnFireworks(soundLoc);
                spawnParticles(soundLoc);
                toggleRedstoneLamps(currentLampState);
                tick++;
            }
        };
        effectsTask.runTaskTimer(plugin, 0L, 20L);
    }

    private void spawnFireworks(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();

        Random r = new Random();
        int rt = r.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;

        switch (rt) {
            case 1:
                type = FireworkEffect.Type.BALL;
                break;
            case 2:
                type = FireworkEffect.Type.BALL_LARGE;
                break;
            case 3:
                type = FireworkEffect.Type.BURST;
                break;
            case 4:
                type = FireworkEffect.Type.STAR;
                break;
        }

        Color c1 = getRandomBrightColor(r);
        Color c2 = getRandomBrightColor(r);

        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean())
                .withColor(c1).withFade(c2).with(type)
                .trail(r.nextBoolean()).build();

        fwm.addEffect(effect);
        fwm.setPower(r.nextInt(2) + 1);
        fw.setFireworkMeta(fwm);
    }

    private Color getRandomBrightColor(Random r) {
        Color[] brightColors = {
                Color.fromRGB(255, 223, 186),
                Color.fromRGB(255, 255, 153),
                Color.fromRGB(204, 255, 204),
                Color.fromRGB(153, 204, 255),
                Color.fromRGB(255, 153, 204),
                Color.fromRGB(255, 255, 255)
        };

        return brightColors[r.nextInt(brightColors.length)];
    }

    private void spawnParticles(Location location) {
        location.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, location, 50, 3, 3, 3);
        location.getWorld().spawnParticle(Particle.FLAME, location, 30, 2, 2, 2, 0.05);
        location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 30, 2, 2, 2, 0.05);
        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, location, 30, 2, 2, 2, 0.05);
        location.getWorld().spawnParticle(Particle.END_ROD, location, 30, 2, 2, 2, 0.05);
    }

    private void toggleRedstoneLamps(boolean on) {
        for (Block lamp : redstoneLamps) {
            Lightable lightable = (Lightable) lamp.getBlockData();
            lightable.setLit(on);
            lamp.setBlockData(lightable);
        }
    }

    @EventHandler
    public void onSongEnd(SongEndEvent e) {
        Bukkit.broadcastMessage(Constants.PLUGIN_PREFIX + "Buzzer Song vorbei!");

        toggleRedstoneLamps(false);

        Bukkit.getScheduler().cancelTasks(plugin);
        isGoldenBuzzerActive = false;
        effectsTask = null;
    }

    @EventHandler
    public void onSongEndSecond(SongEndEvent e) {
        QueueWorker queueWorker = Talent.getInstance().getQueueWorker();
        Player buzzedPlayer = queueWorker.getCurrentSelectedPlayer();

        var config = plugin.getConfig();

        Location endLoc = new Location(buzzedPlayer.getWorld(),
                config.getDouble(Constants.LOCATIONS_END_X),
                config.getDouble(Constants.LOCATIONS_END_Y),
                config.getDouble(Constants.LOCATIONS_END_Z));

        buzzedPlayer.teleport(endLoc);

        queueWorker.addWinner(buzzedPlayer);
        queueWorker.setCurrentSelectedPlayer(null);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!queueWorker.hasNextPlayer()) {
                VoteListener.handleLastVote();
            }
        }, 20L);

    }
}