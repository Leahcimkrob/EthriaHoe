package de.leahcimkrob.ethriahoe;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WorldGuardRegionChecker {

    public static boolean isTrustedInHighestPriorityRegion(Player player, Location location) {
        World world = BukkitAdapter.adapt(location.getWorld());
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) return false;

        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        if (regions.size() == 0) return false;

        ProtectedRegion highest = null;
        for (ProtectedRegion region : regions) {
            if (highest == null || region.getPriority() > highest.getPriority()) {
                highest = region;
            }
        }
        if (highest == null) return false;

        String regionId = highest.getId().replace("_", "").toLowerCase();
        if (regionId.equals("global")) return false;

        UUID uuid = player.getUniqueId();
        return highest.getOwners().getUniqueIds().contains(uuid) || highest.getMembers().getUniqueIds().contains(uuid);
    }
}