package com.github.gfabri.portals.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

import java.util.Objects;

public class WorldGuardAPI
{
    public static boolean isPvPZone(Location location) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld())));
        assert regionManager != null;
        for(ProtectedRegion r : regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location))) {
            if (r.getFlag(Flags.PVP) != null) {
                if (Objects.equals(r.getFlag(Flags.PVP), StateFlag.State.ALLOW)) {
                    return true;
                }
            }
        }
        return false;
  }
  
  public static boolean portalsAllowedHere(Location location) {
      RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld())));
      assert regionManager != null;
      for(ProtectedRegion r : regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location))) {
          if (r.getFlag(Flags.BUILD) != null) {
              if (Objects.equals(r.getFlag(Flags.BUILD), StateFlag.State.DENY)) {
                  return false;
              }
          }
      }
      return true;
  }
}
