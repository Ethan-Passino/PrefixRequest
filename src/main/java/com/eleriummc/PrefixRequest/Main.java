package com.eleriummc.PrefixRequest;

import com.eleriummc.PrefixRequest.commands.*;
import com.eleriummc.PrefixRequest.storage.DBConnection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static Main plugin;
    public static Logger log;

    @Override
    public void onEnable() {
        // Declaring useful variables
        plugin = this;
        log = this.getLogger();

        // Setting up default config values.
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("storage.mysql.host", "localhost");
        config.addDefault("storage.mysql.port", 3306);
        config.addDefault("storage.mysql.database", "database");
        config.addDefault("storage.mysql.username", "root");
        config.addDefault("storage.mysql.password", "");
        config.addDefault("max-characters", 16);
        config.options().copyDefaults(true);
        this.saveConfig();

        // Open Database connect/create database table
        DBConnection.init();
        if(!DBConnection.isOpen()) {
            return;
        }

        // Add listeners
        this.getServer().getPluginManager().registerEvents(new PrefixRequests(), this);

        // Register commands
        this.getCommand("prefix").setExecutor(new Prefix());
        this.getCommand("prefixtest").setExecutor(new PrefixTest());
        this.getCommand("prefixreq").setExecutor(new PrefixReq());
        this.getCommand("prefixrequests").setExecutor(new PrefixRequests());
    }

    @Override
    public void onDisable() {
        // Check if DB Connection is open, if it is close it.
        if(DBConnection.isOpen()) {
            DBConnection.sql.close();
        }
    }
}
