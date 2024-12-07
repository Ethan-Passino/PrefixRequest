package com.eleriummc.PrefixRequest.commands;

import com.eleriummc.PrefixRequest.Main;
import com.eleriummc.PrefixRequest.PrefixPlayer;
import com.eleriummc.PrefixRequest.storage.DBConnection;
import com.eleriummc.PrefixRequest.storage.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PrefixRequests implements CommandExecutor, Listener {
    private static Inventory gui;
    private static Inventory acceptGui;
    private static ArrayList<PrefixPlayer> skulls;
    private static PrefixPlayer prefixChosen;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Checking if Sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return false;
        }
        Player p = (Player) sender;

        // Checking for permissions
        if(!(p.hasPermission("prefixrequest.prefixrequests"))) {
            p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        // Opening GUI
        try {
            openNewGUI(p);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }


    public void openNewGUI(Player p) throws SQLException {
        gui = Bukkit.createInventory(null, 54, "Prefixes"); // Chest type is a large chest.
        skulls = new ArrayList<>();

        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Close GUI");
        close.setItemMeta(closeMeta);
        gui.setItem(49, close);

        // Validate query
        if(!(DBConnection.sql.getConnection().isValid(28800))) {
            DBConnection.sql.close();
            DBConnection.openConnection();
        }

        // Skull player requests
        ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM prefix_requests"); // pulls all prefix requests
        int requestCount = 0;
        while(rs.next()) {
            requestCount++;
            // Puiling data from database query
            String prefix = rs.getString("prefix");
            String player = rs.getString("player");
            String uuid = rs.getString("uuid");

            // Creating a skull for the itemslot.
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(player);
            skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + prefix.replaceAll("&", "§") + ChatColor.RESET + "" + ChatColor.WHITE + " " + player);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.RESET + "" + ChatColor.RED + "Click on this skull to accept/deny this request.");
            skullMeta.setLore(lore);
            skull.setItemMeta(skullMeta);

            // Adding a skull we can grab later if this skull is clicked.
            skulls.add(new PrefixPlayer(player, prefix, uuid));

            // Putting the skull into the gui
            gui.setItem(requestCount-1, skull);

            // If the request count is larger than 36, we only include the first 36, so we break this loop.
            if(requestCount == 36) {
                break;
            }
        }

        p.openInventory(gui);

    }

    public void openAcceptDenyGUI(Player p, PrefixPlayer prefixPlayer) {
        prefixChosen = prefixPlayer;
        acceptGui = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, "Accept or Deny a Prefix");

        // Accept button
        ItemStack accept = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta accMeta = accept.getItemMeta();
        accMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Accept");
        accept.setItemMeta(accMeta);

        // Deny button
        ItemStack deny = new ItemStack(Material.RED_CONCRETE);
        ItemMeta denyMeta = deny.getItemMeta();
        denyMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Deny");
        deny.setItemMeta(denyMeta);

        // Back button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Back to Main Page");
        back.setItemMeta(backMeta);

        // Prefix Player Skull
        ItemStack prefixDisplay = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) prefixDisplay.getItemMeta();
        skullMeta.setOwner(prefixPlayer.getName());
        skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE +
                prefixPlayer.getPrefix().replaceAll("&", "§") + ChatColor.RESET + "" + ChatColor.WHITE +
                " " + prefixPlayer.getName());
        prefixDisplay.setItemMeta(skullMeta);

        // Setting the GUI slots
        acceptGui.setItem(13, prefixDisplay); // middle location
        acceptGui.setItem(11, accept); // mid left
        acceptGui.setItem(15, deny);  // mid right
        acceptGui.setItem(22, back); // bottom middle

        p.openInventory(acceptGui);

    }

    @EventHandler
    public void guiClickEvent(InventoryClickEvent e) throws SQLException {
        // This event fires when any item is clicked in an inventory
        // Check which gui you are clicking in
        if(!(e.getInventory().equals(acceptGui)) && !(e.getInventory().equals(gui))) {
            return;
        }

        e.setCancelled(true); // cancels you clicking on the item, you cannot take it.

        Player p = (Player) e.getWhoClicked();

        if(e.getInventory().equals(acceptGui)) {
            // In the case it is the accept gui we are in
            switch(e.getSlot()) {
                case 11: {
                    // Accept button
                    // remove the request from the database and set the prefix to the player.
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " +
                            prefixChosen.getUUID() + " meta setprefix \"&r" + prefixChosen.getPrefix() + "&r \"");
                    DBConnection.sql.modifyQuery("DELETE FROM prefix_requests WHERE uuid = '" + prefixChosen.getUUID() + "';");
                    p.closeInventory();
                    p.sendMessage(ChatColor.GREEN + "Successfully accepted prefix.");
                    p.closeInventory();
                    break;
                }
                case 15: {
                    // Deny button
                    // remove the request from the database.
                    DBConnection.sql.modifyQuery("DELETE FROM prefix_requests WHERE uuid = '" + prefixChosen.getUUID() + "';");
                    p.sendMessage(ChatColor.GREEN + "Successfully denied prefix.");
                    p.closeInventory();
                    break;
                }
                case 22: {
                    // Back button
                    p.closeInventory();
                    openNewGUI(p);
                    break;
                }
            }
        } else {
            // In the case it is the normal gui we are in
            if(e.getCurrentItem() != null) {
                if (e.getCurrentItem().getType() == Material.BARRIER) {
                    // Close button
                    p.closeInventory();
                }

                if (e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                    // Any skull clicked.
                    p.closeInventory();
                    openAcceptDenyGUI(p, skulls.get(e.getSlot()));
                }
            }
        }
    }

}
