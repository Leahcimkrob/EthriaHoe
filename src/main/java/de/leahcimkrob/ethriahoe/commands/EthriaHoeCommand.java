package de.leahcimkrob.ethriahoe.commands;

import de.leahcimkrob.ethriahoe.EthriaHoe;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EthriaHoeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EthriaHoe plugin = EthriaHoe.getInstance();

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("ethriahoe.admin")) {
                String noPermMsg = plugin.getConfig().getString("messages.reload_no_perm", "Du hast keine Berechtigung, diesen Befehl auszuführen.");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermMsg));
                return true;
            }
            plugin.reloadConfig();
            plugin.registerAliases(); // Aliasse auch beim Reload neu setzen!
            String reloadMsg = plugin.getConfig().getString("messages.reload", "&4Die EthriaHoe-Konfiguration wurde neu geladen!");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', reloadMsg));
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Verwendung: /ethriahoe reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if ("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("ethriahoe.admin")) {
                completions.add("reload");
            }
            return completions;
        }
        return Collections.emptyList();
    }
}