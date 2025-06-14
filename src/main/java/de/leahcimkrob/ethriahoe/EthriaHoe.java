package de.leahcimkrob.ethriahoe;

import de.leahcimkrob.ethriahoe.commands.EthriaHoeCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;

public class EthriaHoe extends JavaPlugin {
    private static EthriaHoe instance;
    private boolean plotsquaredAvailable;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        EthriaHoeCommand cmd = new EthriaHoeCommand();
        getCommand("ethriahoe").setExecutor(cmd);
        getCommand("ethriahoe").setTabCompleter(cmd);

        instance = this;
        plotsquaredAvailable = getServer().getPluginManager().getPlugin("PlotSquared") != null;

        if (plotsquaredAvailable) {
            getServer().getPluginManager().registerEvents(new EthriaHoeListener(), this);
            getLogger().info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("messages.plugin_enabled"))));
        } else {
            getLogger().warning(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("no_plotsquared"))));
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("messages.plugin_disabled"))));
    }

    public void registerAliases() {
        List<String> aliases = getConfig().getStringList("command-aliases");
        if (!aliases.isEmpty()) {
            try {
                Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                bukkitCommandMap.setAccessible(true);
                SimpleCommandMap commandMap = (SimpleCommandMap) bukkitCommandMap.get(Bukkit.getServer());
                Command cmd = getCommand("ethriahoe");
                if (cmd != null) {
                    cmd.setAliases(aliases);
                    commandMap.register(getDescription().getName(), cmd);
                }
            } catch (Exception e) {
                getLogger().warning(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("no_alias"))));
            }
        }
    }

    public static EthriaHoe getInstance() {
        return instance;
    }

    public boolean isPlotsquaredAvailable() {
        return plotsquaredAvailable;
    }
}