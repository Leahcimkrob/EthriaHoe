package de.leahcimkrob.ethriahoe;

import de.leahcimkrob.ethriahoe.commands.EthriaHoeCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;

public class EthriaHoe extends JavaPlugin {
    private static EthriaHoe instance;

    public static EthriaHoe getInstance() {
        return instance;
    }

    private boolean plotsquaredAvailable = false;
    private boolean worldGuardAvailable = false;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Softdepend-Prüfung: PlotSquared
        Plugin plotSquared = Bukkit.getPluginManager().getPlugin("PlotSquared");
        plotsquaredAvailable = (plotSquared != null && plotSquared.isEnabled());
        if (plotsquaredAvailable) {
            getLogger().warning(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("plotsquared"))));
        } else {
            getLogger().warning(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("no_plotsquared"))));
        }

        // Softdepend-Prüfung: WorldGuard
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        worldGuardAvailable = (worldGuard != null && worldGuard.isEnabled());
        if (worldGuardAvailable) {
            getLogger().warning(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("worldguard"))));
        } else {
            getLogger().warning(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "") + getConfig().getString("no_worldguard"))));
        }

        // Plugin-aktiviert-Meldung (unabhängig von Softdepends)
        getLogger().info(ChatColor.stripColor(
                ChatColor.translateAlternateColorCodes('&',
                        config.getString("prefix", "") +
                                config.getString("messages.plugin_enabled", "EthriaHoe wurde aktiviert!")
                )
        ));

        // Listener registrieren
        getServer().getPluginManager().registerEvents(new EthriaHoeListener(), this);
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



    public boolean isPlotsquaredAvailable() {
        return plotsquaredAvailable;
    }

    public boolean isWorldGuardAvailable() { return worldGuardAvailable; }
}