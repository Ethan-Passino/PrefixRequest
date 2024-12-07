package com.eleriummc.PrefixRequest.commands;

import com.eleriummc.PrefixRequest.Main;
import com.eleriummc.PrefixRequest.storage.DBConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PrefixReq implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Checking if the sender of the command is a player, if it is cast it to a player.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return false;
        }
        Player p = (Player) sender;

        // Check for permission
        if (!(p.hasPermission("prefixrequest.prefixreq")) && !(p.hasPermission("phanaticmc.globalprefix"))) {
            p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        // Check if the user provided a prefix
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "You must provide a prefix (no spaces).");
            return false;
        }

        // Checking if the character length (without colors) exceeds the max char in the config.
        String noColors = ChatColor.stripColor(args[0].replaceAll("&", "§"));
        int maxChar = Main.plugin.getConfig().getInt("max-characters");
        if (noColors.length() > maxChar) {
            p.sendMessage(ChatColor.RED + "You went over the max character limit of " + maxChar + " please try again.");
            return false;
        }

        // Check if this player already created a prefix request, if they did we will delete it.
        final String QUERY_CHECK = "SELECT * FROM prefix_requests WHERE uuid = '" + p.getUniqueId().toString() + "'";
        ResultSet rs = DBConnection.sql.readQuery(QUERY_CHECK);
        try {
            if(rs.next()) {
                DBConnection.sql.modifyQuery("DELETE FROM prefix_requests WHERE uuid = '" + p.getUniqueId().toString() + "';");
            }

            // Send in the prefix request to the database.
            final String QUERY = "INSERT INTO prefix_requests (uuid, prefix, player) VALUES ('" + p.getUniqueId().toString() +"', '" + args[0] + "', '" + p.getName() + "')";
            DBConnection.sql.modifyQuery(QUERY);
            p.sendMessage(ChatColor.GREEN + "Your prefix request has been sent.");
            p.sendMessage(ChatColor.GREEN + "Your requested prefix: " + ChatColor.RESET + "" + args[0].replaceAll("&", "§"));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }
}