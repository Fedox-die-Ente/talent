package me.fedox.talent;

import lombok.Getter;
import lombok.Setter;
import me.fedox.talent.commands.*;
import me.fedox.talent.listener.AntiListener;
import me.fedox.talent.listener.VoteListener;
import me.fedox.talent.utils.Constants;
import me.fedox.talent.utils.GoldenBuzzer;
import me.fedox.talent.utils.ShowManager;
import me.fedox.talent.worker.QueueListener;
import me.fedox.talent.worker.QueueWorker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static me.fedox.talent.utils.Constants.CAMERA_LOCATIONS;
import static me.fedox.talent.utils.Constants.NEXT_PLAYERS;

/**
 * Main class for the Talent plugin.
 * Extends JavaPlugin to integrate with the Bukkit plugin system.
 */
public final class Talent extends JavaPlugin {

    @Getter
    @Setter
    public static String creatorUUID; // UUID of the creator
    @Getter
    private static Talent instance; // Singleton instance of the plugin
    @Getter
    private QueueWorker queueWorker; // Worker for handling queue operations
    @Getter
    private ShowManager showManager; // Manager for handling show operations

    @Getter
    private List<Player> buildModePlayers = new ArrayList<>(); // List of players in build mode

    /**
     * Called when the plugin is enabled.
     * Initializes the plugin, checks for dependencies, and registers commands and events.
     */
    @Override
    public void onEnable() {
        instance = this;

        loadConfig();
        copySongs();

        Location soundLoc = new Location(
                Bukkit.getWorld(getConfig().getString(Constants.LOCATIONS_SOUND_WORLD)),
                getConfig().getDouble(Constants.LOCATIONS_SOUND_X),
                getConfig().getDouble(Constants.LOCATIONS_SOUND_Y),
                getConfig().getDouble(Constants.LOCATIONS_SOUND_Z)
        );

        if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
            return;
        }

        queueWorker = new QueueWorker(this);
        showManager = new ShowManager(this, soundLoc);

        System.out.println("Cam Locs:");
        System.out.println(getCameraLocations());
        
        register();
    }

    /**
     * Called when the plugin is disabled.
     * Used for cleanup operations.
     */
    @Override
    public void onDisable() {
        System.out.println("Talent plugin disabled");
    }

    /**
     * Registers commands and event listeners for the plugin.
     */
    public void register() {
        this.getCommand("setlocations").setExecutor(new SetLocationsCommand());
        this.getCommand("setcurrentplayer").setExecutor(new SetCurrentPlayerCommand());

        this.getCommand("build").setExecutor(new BuildCommand());
        this.getCommand("next").setExecutor(new NextCommand());
        this.getCommand("queue").setExecutor(new QueueCommand());
        this.getCommand("addcamera").setExecutor(new AddCameraLocationCommand());

        Bukkit.getPluginManager().registerEvents(new GoldenBuzzer(), this);
        Bukkit.getPluginManager().registerEvents(new VoteListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AntiListener(), this);
        Bukkit.getPluginManager().registerEvents(new QueueListener(), this);
    }

    /**
     * Loads the plugin configuration. Copies default values and saves the configuration.
     */
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Copies the songs from the resources to the plugin folder.
     */
    private void copySongs() {
        File songsDir = new File(getDataFolder(), "songs");
        if (!songsDir.exists()) {
            songsDir.mkdirs();
        }

        String[] songFiles = {"golden_buzzer.nbs"};

        for (String song : songFiles) {
            File songFile = new File(songsDir, song);
            if (!songFile.exists()) {
                try (InputStream in = getResource("songs/" + song);
                     FileOutputStream out = new FileOutputStream(songFile)) {
                    if (in != null) {
                        Files.copy(in, songFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        getLogger().info("Kopiere " + song + " nach " + songFile.getAbsolutePath());
                    } else {
                        getLogger().warning("Die Datei " + song + " konnte nicht gefunden werden!");
                    }
                } catch (IOException e) {
                    getLogger().severe("Fehler beim Kopieren der Datei " + song + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Adds a player to the list of winners.
     *
     * @param player The player to add.
     */
    public void addWinPlayer(Player player) {
        List<String> nextPlayers = getConfig().getStringList(NEXT_PLAYERS);
        nextPlayers.add(player.getName());

        getConfig().set(NEXT_PLAYERS, nextPlayers);
        saveConfig();
    }

    /**
     * Retrieves the list of winners.
     *
     * @return List of winner names.
     */
    public List<String> getWinners() {
        return getConfig().getStringList(NEXT_PLAYERS);
    }

    /**
     * Adds a camera location to the configuration.
     *
     * @param location The location to add.
     */
    public void addCameraLocation(Location location) {
        List<String> cameraLocations = getConfig().getStringList(CAMERA_LOCATIONS);
        cameraLocations.add(location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch());

        getConfig().set(CAMERA_LOCATIONS, cameraLocations);
        saveConfig();
    }

    /**
     * Retrieves the list of camera locations.
     *
     * @return List of camera locations.
     */
    public List<Location> getCameraLocations() {
        List<Location> cameraLocations = new ArrayList<>();
        List<String> cameraLocationsString = getConfig().getStringList(CAMERA_LOCATIONS);

        for (String locationString : cameraLocationsString) {
            String[] locationParts = locationString.split(",");
            Location location = new Location(Bukkit.getWorld(locationParts[0]), Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2]), Double.parseDouble(locationParts[3]), Float.parseFloat(locationParts[4]), Float.parseFloat(locationParts[5]));
            cameraLocations.add(location);
        }

        return cameraLocations;
    }
}