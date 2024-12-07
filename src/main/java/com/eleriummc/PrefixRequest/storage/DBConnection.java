package com.eleriummc.PrefixRequest.storage;

import com.eleriummc.PrefixRequest.Main;

import java.sql.Connection;

public class DBConnection {

    public static Database sql;

    private static String host;
    private static int port;
    private static String db;
    private static String user;
    private static String pass;
    private static boolean isOpen = false;

    public static void init() {
        host = Main.plugin.getConfig().getString("storage.mysql.host");
        port = Main.plugin.getConfig().getInt("storage.mysql.port");
        pass = Main.plugin.getConfig().getString("storage.mysql.password");
        db = Main.plugin.getConfig().getString("storage.mysql.database");
        user = Main.plugin.getConfig().getString("storage.mysql.username");
        sql = new MySQL(Main.log, "Establishing MySQL Connection...", host, port, user, pass, db);
        if (((MySQL)sql).open() == null) {
            Main.log.severe("Disabling due to database error");
            Main.plugin.getServer().getPluginManager().disablePlugin(Main.plugin);
            return;
        }
        isOpen = true;
        Main.log.info("Database connection established.");
        if (!sql.tableExists("prefix_requests")) {
            Main.log.info("Creating prefix requests table");
            String query = "CREATE TABLE `prefix_requests` (`uuid` varchar(36) NOT NULL,`prefix` varchar(32), `player` varchar(16));";
            sql.modifyQuery(query, false);
        }
    }

    public static boolean isOpen() {
        return isOpen;
    }

    public static void openConnection() {
        if(sql == null) return;
        if(((MySQL)sql).open() == null) {
            Main.log.severe("Disabling due to database error.");
            Main.plugin.getServer().getPluginManager().disablePlugin(Main.plugin);
            return;
        }
        isOpen = true;

    }
 }
