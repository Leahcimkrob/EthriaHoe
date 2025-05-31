package de.leahcimkrob.ethriahoe;

import org.bukkit.plugin.java.JavaPlugin;

public class EthriaHoe extends JavaPlugin {
    @Override
    public void onEnable() {
        // Registriere den Event-Listener
        getServer().getPluginManager().registerEvents(new EthriaHoeListener(), this);
        getLogger().info("EthriaHoe Plugin wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("EthriaHoe Plugin wurde deaktiviert!");
    }
}