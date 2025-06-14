package de.leahcimkrob.ethriahoe;

import com.plotsquared.core.player.PlotPlayer;
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

    @EventHandler(ignoreCancelled = true)
    public void onFrameModify(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        FileConfiguration config = EthriaHoe.getInstance().getConfig();
        String prefix = config.getString("prefix", "");

        // Hole das Item aus der Config als lokale Variable
        Material toggleItem;
        try {
            toggleItem = Material.valueOf(config.getString("toggle_item", "WOODEN_HOE"));
        } catch (IllegalArgumentException e) {
            toggleItem = Material.WOODEN_HOE;
        }
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        if (p.getInventory().getItemInMainHand().getType() != toggleItem)
            return;

        // Prüfen ob Item stimmt
        if (p.getInventory().getItemInMainHand().getType() != Material.WOODEN_HOE)
            return;

        // Prüfen ob PlotSquared installiert ist
        if (EthriaHoe.getInstance().isPlotsquaredAvailable()) {
            // PlotSquared-Logik wie gehabt
            PlotPlayer<?> plotPlayer = PlotPlayer.from(p);
            org.bukkit.Location bukkitLoc = event.getRightClicked().getLocation();
            com.plotsquared.core.location.Location plotLoc = com.plotsquared.core.location.Location.at(
                    bukkitLoc.getWorld().getName(),
                    bukkitLoc.getBlockX(),
                    bukkitLoc.getBlockY(),
                    bukkitLoc.getBlockZ()
            );
            Plot plot = Plot.getPlot(plotLoc);

            if (plot == null) {
                event.setCancelled(true);
                p.sendMessage(prefix + config.getString("messages.not_on_plot", ""));
                return;
            }

            UUID uuid = plotPlayer.getUUID();
            boolean allowTrusted = config.getBoolean("allow_trusted", true);
            boolean allowMember = config.getBoolean("allow_member", true);

            if (
                    plot.isOwner(uuid) ||
                            (plot.getTrusted().contains(uuid) && allowTrusted) ||
                            (plot.getMembers().contains(uuid) && allowMember)
            ) {
                // Zugriff erlaubt, unten normal weiter
            } else {
                event.setCancelled(true);
                p.sendMessage(prefix + config.getString("messages.no_rights", ""));
                return;
            }
        }
        // Wenn PlotSquared nicht installiert ist, gibt es keine Rechte-Prüfung!
        // Ab hier: Funktionalität für alle Spieler

        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        ItemFrame frame = (ItemFrame) event.getRightClicked();

        if (p.isSneaking()) {
            if (p.hasPermission("ethriahoe.toggleFixed")) {
                event.setCancelled(true);
                frame.setFixed(!frame.isFixed());
                String stateKey = frame.isFixed() ? "fixed" : "unfixed";
                String state = config.getString("messages.state." + stateKey, stateKey);
                String msg = prefix + config.getString("messages.set_fixed", "Frame wurde auf %state% gesetzt.").replace("%state%", state);
                p.sendMessage(msg);
            }
        } else if (p.hasPermission("ethriahoe.toggleVisible")) {
            event.setCancelled(true);
            frame.setVisible(!frame.isVisible());
            String stateKey = frame.isVisible() ? "visible" : "invisible";
            String state = config.getString("messages.state." + stateKey, stateKey);
            String msg = prefix + config.getString("messages.set_visible", "Frame ist jetzt %state%.").replace("%state%", state);
            p.sendMessage(msg);
        }
    }
}