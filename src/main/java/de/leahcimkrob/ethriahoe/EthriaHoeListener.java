package de.leahcimkrob.ethriahoe;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class EthriaHoeListener implements Listener {
    private final Material toggleItem = Material.WOODEN_HOE; // ggf. konfigurierbar machen

    @EventHandler(ignoreCancelled = true)
    public void onFrameModify(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        Player p = event.getPlayer();

        if (p.getInventory().getItemInMainHand().getType() != toggleItem)
            return;

        FileConfiguration config = EthriaHoe.getInstance().getConfig();

        // PlotPlayer holen (V7)
        PlotPlayer<?> plotPlayer = PlotPlayer.from(p);

        // PlotSquared-Location erzeugen (V7)
        org.bukkit.Location bukkitLoc = event.getRightClicked().getLocation();
        Location plotLoc = Location.at(
                bukkitLoc.getWorld().getName(),
                bukkitLoc.getBlockX(),
                bukkitLoc.getBlockY(),
                bukkitLoc.getBlockZ()
        );

        // Plot abfragen (V7)
        Plot plot = Plot.getPlot(plotLoc);

        if (plot == null) {
            event.setCancelled(true);
            p.sendMessage(config.getString("prefix" + "messages.not_on_plot"));
            return;
        }

        UUID uuid = plotPlayer.getUUID();

        boolean allowTrusted = config.getBoolean("allow_trusted", true);
        boolean allowMember = config.getBoolean("allow_member", true);

        if (plot.isOwner(uuid)) {
            // Owner darf immer
        } else if (plot.getTrusted().contains(uuid) && allowTrusted) {
            // Trusted darf nur, wenn erlaubt
        } else if (plot.getMembers().contains(uuid) && allowMember) {
            // Member darf nur, wenn erlaubt
        } else {
            event.setCancelled(true);
               p.sendMessage(config.getString("prefix" + "messages.no_rights"));
            return;
        }

        // Spieler hat Berechtigung, Frame zu modifizieren
        ItemFrame frame = (ItemFrame) event.getRightClicked();

        if (p.isSneaking()) {
            if (p.hasPermission("ethriahoe.toggleFixed")) {
                event.setCancelled(true);
                frame.setFixed(!frame.isFixed());
                String stateKey = frame.isFixed() ? "fixed" : "unfixed";
                String state = config.getString("messages.state." + stateKey, stateKey);
                String msg = config.getString("messages.set_fixed", "Frame wurde auf %state% gesetzt.").replace("%state%", state);
                p.sendMessage(msg);
            }
        } else if (p.hasPermission("ethriahoe.toggleVisible")) {
            event.setCancelled(true);
            frame.setVisible(!frame.isVisible());
            String stateKey = frame.isVisible() ? "visible" : "invisible";
            String state = config.getString("messages.state." + stateKey, stateKey);
            String msg = config.getString("messages.set_visible", "Frame ist jetzt %state%.").replace("%state%", state);
            p.sendMessage(msg);
        }
    }
}