package de.leahcimkrob.ethriahoe;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class WorldGuardRegionChecker {

    public static boolean isTrustedInHighestPriorityRegion(Player player, Location location) {
        World world = BukkitAdapter.adapt(location.getWorld());
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) return true;

        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        if (regions.size() == 0) return true;

        ProtectedRegion highest = null;
        for (ProtectedRegion region : regions) {
            if (highest == null || region.getPriority() > highest.getPriority()) {
                highest = region;
            }
        }
        if (highest == null) return true;

        // DEBUG
        System.out.println("[EthriaHoe-DEBUG] Region(en) an der Position:");
        for (ProtectedRegion region : regions) {
            System.out.println("  - " + region.getId() + " (Prio: " + region.getPriority() + ")");
        }
        System.out.println("[EthriaHoe-DEBUG] Höchste Region: " + highest.getId());

        if (highest.getId().equalsIgnoreCase("__global__")) {
            System.out.println("[EthriaHoe-DEBUG] In __global__ -> Rückgabe: false");
            return false;
        }

        UUID uuid = player.getUniqueId();
        boolean isOwner = highest.getOwners().getUniqueIds().contains(uuid);
        boolean isMember = highest.getMembers().getUniqueIds().contains(uuid);

        System.out.println("[EthriaHoe-DEBUG] Owner? " + isOwner + " | Member? " + isMember);
        return isOwner || isMember;
    }
}