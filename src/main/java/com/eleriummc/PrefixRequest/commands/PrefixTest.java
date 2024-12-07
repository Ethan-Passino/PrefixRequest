package com.eleriummc.PrefixRequest.commands;

import com.eleriummc.PrefixRequest.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PrefixTest implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Checking if the sender of the command is a player, if it is cast it to a player.
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return false;
        }
        Player p = (Player) sender;

        // Check for permission
        if (!(p.hasPermission("prefixrequest.prefixtest"))) {
            p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        // Check if the user provided a prefix
        if(args.length != 1) {
            p.sendMessage(ChatColor.RED + "You must provide a prefix (no spaces).");
            return false;
        }

        // Checking if the character length (without colors) exceeds the max char in the config.
        String noColors = ChatColor.stripColor(args[0].replaceAll("&", "§"));
        int maxChar = Main.plugin.getConfig().getInt("max-characters");
        if(noColors.length() > maxChar) {
            p.sendMessage(ChatColor.RED + "You went over the max character limit of " + maxChar + " please try again.");
            return false;
        }

        // Send the prefix into chat
        p.sendMessage(ChatColor.GREEN + "Keep in mind max character limit is: " + maxChar + ". Prefix:");
        p.sendMessage(args[0].replaceAll("&", "§") + ChatColor.RESET + " " + p.getName() + ":");
        return true;
    }

}
