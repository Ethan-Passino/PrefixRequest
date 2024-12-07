package com.eleriummc.PrefixRequest.commands;

import com.eleriummc.PrefixRequest.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Prefix implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Sender must be a player, players can only have prefixes in chat.
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return false;
        }
        Player p = (Player) sender;

        // Check for permissions
        if(!(p.hasPermission("prefixrequest.prefix"))) {
            p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        // Check if the player provided a prefix
        if(args.length != 1) {
            p.sendMessage(ChatColor.RED + "You must provide a prefix.");
            return false;
        }

        // Checking if the character length (without colors) exceeds the max char in the config.
        String noColors = ChatColor.stripColor(args[0].replaceAll("&", "§"));
        int maxChar = Main.plugin.getConfig().getInt("max-characters");
        if(noColors.length() > maxChar) {
            p.sendMessage(ChatColor.RED + "You went over the max character limit of " + maxChar + " please try again.");
            return false;
        }

        // Set the prefix. This is simple with luckperms.
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + p.getName() + " meta setprefix \"&r" + args[0] + "&b \"");
        p.sendMessage(ChatColor.GREEN + "Set your prefix to " + args[0].replaceAll("&", "§"));
        return true;
    }
}
