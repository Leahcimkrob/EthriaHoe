package de.leahcimkrob.ethriahoe;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EthriaHoe extends JavaPlugin {
    private static EthriaHoe instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new EthriaHoeListener(), this);
        String enabledMsg = getConfig().getString("prefix" + "messages.plugin_enabled");
        getLogger().info(enabledMsg);
    }

    @Override
    public void onDisable() {
        String disabledMsg = getConfig().getString("prefix" + "messages.plugin_disabled");
        getLogger().info(disabledMsg);
    }

    public static EthriaHoe getInstance() {
        return instance;
    }
}