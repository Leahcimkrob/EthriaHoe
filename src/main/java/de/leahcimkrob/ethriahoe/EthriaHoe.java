package de.leahcimkrob.ethriahoe;

import org.bukkit.plugin.java.JavaPlugin;

public class EthriaHoe extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin-Start-Logik
        getServer().getPluginManager().registerEvents(new EthriaHoeListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin-Ende-Logik (optional)
    }
}