package de.leahcimkrob.ethriahoe;

import org.bukkit.plugin.java.JavaPlugin;

public class EthriaHoe extends JavaPlugin {
    private static EthriaHoe instance;
    private boolean plotsquaredAvailable;

    @Override
    public void onEnable() {
        instance = this;
        plotsquaredAvailable = getServer().getPluginManager().getPlugin("PlotSquared") != null;
        saveDefaultConfig();

        if (plotsquaredAvailable) {
            getServer().getPluginManager().registerEvents(new EthriaHoeListener(), this);
            getLogger().info(getConfig().getString("prefix", "") + getConfig().getString("messages.plugin_enabled"));
        } else {
            getLogger().warning("[EthriaHoe] PlotSquared nicht gefunden! Plot-Funktionen sind deaktiviert.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(getConfig().getString("prefix", "") + getConfig().getString("messages.plugin_disabled"));
    }

    public static EthriaHoe getInstance() {
        return instance;
    }

    public boolean isPlotsquaredAvailable() {
        return plotsquaredAvailable;
    }
}