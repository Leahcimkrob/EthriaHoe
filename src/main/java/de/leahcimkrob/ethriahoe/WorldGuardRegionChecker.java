package de.leahcimkrob.ethriahoe;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class WorldGuardRegionChecker {

    /**
     * Prüft, ob der Spieler an der Location in mindestens einer WorldGuard-Region Member oder Owner ist.
     * Gibt true zurück, wenn der Spieler in einer Region Member/Owner ist, andernfalls false.
     */
    public static boolean isTrustedInAnyRegion(Player player, Location location) {
        World world = BukkitAdapter.adapt(location.getWorld());
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) return true; // Keine Regionsverwaltung im Welt -> Zugriff erlauben

        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        if (regions.size() == 0) return true; // Keine Region -> Zugriff erlauben

        String playerName = player.getName();
        for (ProtectedRegion region : regions) {
            if (region.isOwner(playerName) || region.isMember(playerName)) {
                return true; // Mindestens eine Region erlaubt Zugriff
            }
        }
        return false; // In keiner Region Member/Owner
    }
}