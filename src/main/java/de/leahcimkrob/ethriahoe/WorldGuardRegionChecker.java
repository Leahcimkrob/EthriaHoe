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
     * Prüft, ob der Spieler an der Location in der WorldGuard-Region Member oder Owner ist, welche die höchste Priorität hat.
     * Gibt true zurück, wenn der Spieler in einer Region Member/Owner ist, andernfalls false.
     */
    public static boolean isTrustedInHighestPriorityRegion(Player player, Location location) {
        World world = BukkitAdapter.adapt(location.getWorld());
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) return true;
        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        if (regions.size() == 0) return true;
        String playerName = player.getName();
        ProtectedRegion highest = null;
        for (ProtectedRegion region : regions) {
            if (highest == null || region.getPriority() > highest.getPriority()) {
                highest = region;
            }
        }
        if (highest == null) return false;
        return highest.isOwner(playerName) || highest.isMember(playerName);
    }
}