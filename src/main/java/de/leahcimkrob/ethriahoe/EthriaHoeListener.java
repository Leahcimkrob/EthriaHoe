package de.leahcimkrob.ethriahoe;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EthriaHoeListener implements Listener {
    private final Material toggleItem = Material.WOODEN_HOE; // Kann bei Bedarf konfigurierbar gemacht werden

    @EventHandler(ignoreCancelled = true)
    public void onToggleFrameProperty(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        ItemFrame frame = (ItemFrame) event.getRightClicked();
        Player p = event.getPlayer();

        if (p.getInventory().getItemInMainHand().getType() != toggleItem)
            return;

        if (p.isSneaking()) {
            if (p.hasPermission("imagemaps.toggleFixed")) {
                event.setCancelled(true);
                frame.setFixed(!frame.isFixed());
                p.sendMessage("Frame set to " + (frame.isFixed() ? "fixed" : "unfixed"));
            }
        } else if (p.hasPermission("imagemaps.toggleVisible")) {
            event.setCancelled(true);
            frame.setVisible(!frame.isVisible());
            p.sendMessage("Frame set to " + (frame.isVisible() ? "visible" : "invisible"));
        }
    }
}