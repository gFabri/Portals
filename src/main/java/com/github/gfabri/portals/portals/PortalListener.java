package com.github.gfabri.portals.portals;

import com.github.gfabri.portals.ConfigHandler;
import com.github.gfabri.portals.Portals;
import com.github.gfabri.portals.bungee.BungeeInstance;
import com.github.gfabri.portals.events.PortalCreateEvent;
import com.github.gfabri.portals.utils.Cuboid;
import com.github.gfabri.portals.utils.CustomItem;
import com.github.gfabri.portals.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class PortalListener implements Listener {
    private final ItemStack isPortalItem = new CustomItem(Material.getMaterial(ConfigHandler.Configs.CONFIG.getConfig().getString("PortalItem.material")), 1, ConfigHandler.Configs.CONFIG.getConfig().getInt("PortalItem.material-data")).setName(ConfigHandler.Configs.CONFIG.getConfig().getString("PortalItem.displayName")).addLore(ConfigHandler.Configs.CONFIG.getConfig().getStringList("PortalItem.lore")).create();
    private final double RADIUS = ConfigHandler.Configs.CONFIG.getConfig().getDouble("disable-portal-in-radius");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getItem() != null && event.getItem().equals(isPortalItem)) {

            if (canCreatePortal(event.getPlayer())) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Portals.getInstance().getPortalManager().createPortal(event.getPlayer(), event.getItem());
                    Portals.getInstance().getPortalManager().setTarget(event.getPlayer(), BungeeInstance.getInstance().getLocalServer() + ":" + Utils.serialize(event.getClickedBlock().getLocation()), event.getItem());
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (!Portals.getInstance().getPortalManager().getPortals().containsKey(event.getPlayer().getUniqueId()) || !event.getItem().getItemMeta().getLore().contains("Target")) {
                        event.getPlayer().sendMessage(Utils.translate(ConfigHandler.Configs.CONFIG.getConfig().getString("MESSAGES.first-target")));
                        return;
                    }
                    Portals.getInstance().getPortalManager().setLocation(event.getPlayer(), BungeeInstance.getInstance().getLocalServer() + ":" + Utils.serialize(event.getClickedBlock().getLocation()), event.getItem());
                }
            } else {
                event.getPlayer().sendMessage(Utils.translate(ConfigHandler.Configs.CONFIG.getConfig().getString("MESSAGES.cantcreateportal").replace("%blocks%", String.valueOf(RADIUS))));
            }
        }
    }

    public boolean canCreatePortal(Player player) {
        Collection<Entity> nearby = player.getNearbyEntities(RADIUS, RADIUS, RADIUS);
        for (final Entity entity : nearby) {
            if (entity instanceof Player) {
                final Player target = (Player) entity;
                if (!target.canSee(player)) {
                    continue;
                }
                if (!player.canSee(target)) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

//    @EventHandler(priority = EventPriority.NORMAL)
//    public void onPlayerJoin(PlayerJoinEvent event) {
//            Portals.getInstance().getPortalManager().load(event.getPlayer().getUniqueId());
//
//            Bukkit.getScheduler().runTask(Portals.getInstance(), () -> {
//                if (Portals.getInstance().getPortalManager().isPending(event.getPlayer())) {
//                    Location location = Utils.deserialize(Portals.getInstance().getPortalManager().getPortals().get(event.getPlayer().getUniqueId()).getTarget().split(":")[1]);
//                    ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
//
//                    stand.setGravity(false);
//                    stand.setVisible(false);
//                    stand.setCustomName("");
//                    stand.setCustomNameVisible(false);
//                    stand.setMetadata("portals", new FixedMetadataValue(Portals.getInstance(), "1"));
//                    stand.getEquipment().setHelmet(Portals.getInstance().getPortalManager().getPortal(event.getPlayer()));
//
//                    location.setY(location.getY() + 1);
//
//                    event.getPlayer().performCommand("tpportal");
//
//                    Portals.getInstance().getPortalManager().setPending(event.getPlayer(), false);
//
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            stand.remove();
//                            this.cancel();
//                        }
//                    }.runTaskLater(Portals.getInstance(), 20L * ConfigHandler.Configs.CONFIG.getConfig().getLong("portal-time"));
//                }
//            });
//    }

//    @EventHandler(priority = EventPriority.LOW)
//    public void onPlayerLeave(PlayerQuitEvent event) {
//        if (Portals.getInstance().getPortalManager().getPortals().containsKey(event.getPlayer().getUniqueId())) {
//            Portal portal = Portals.getInstance().getPortalManager().getPortals().get(event.getPlayer().getUniqueId());
//            Portals.getInstance().getPortalManager().update(event.getPlayer(), portal.getLocation(), portal.getTarget());
//        }
//    }

//    @EventHandler
//    public void onPortalCreate(PortalCreateEvent event) {
//        Portal portal = event.getPortal();
//        ItemStack portalItemstack = Portals.getInstance().getPortalManager().getPortal(event.getPlayer());
//        portal.setOpen(true);
//
//        if (Objects.equals(portal.getLocation().split(":")[0], portal.getTarget().split(":")[0])) {
//                Location location2 = Utils.deserialize(portal.getTarget().split(":")[1]);
//                ArmorStand stand = (ArmorStand) location2.getWorld().spawnEntity(location2, EntityType.ARMOR_STAND);
//
//                stand.setGravity(false);
//                stand.setVisible(false);
//                stand.setCustomName("");
//                stand.setCustomNameVisible(false);
//                stand.setMetadata("portals", new FixedMetadataValue(Portals.getInstance(), "1"));
//
//                stand.getEquipment().setHelmet(portalItemstack);
//
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        stand.remove();
//                    }
//                }.runTaskLater(Portals.getInstance(), 20L * ConfigHandler.Configs.CONFIG.getConfig().getLong("portal-time"));
//            }
//
//            Location location = Utils.deserialize(portal.getLocation().split(":")[1]);
//            ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
//
//            stand.setGravity(false);
//            stand.setVisible(false);
//            stand.setCustomName("");
//            stand.setCustomNameVisible(false);
//            stand.setMetadata("portals", new FixedMetadataValue(Portals.getInstance(), "1"));
//
//            stand.getEquipment().setHelmet(portalItemstack);
//
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    stand.remove();
//                    portal.setOpen(false);
//                }
//            }.runTaskLater(Portals.getInstance(), 20L * ConfigHandler.Configs.CONFIG.getConfig().getLong("portal-time"));
//
//    }
//
//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent event) {
//        Portal portal = Portals.getInstance().getPortalManager().getPortals().get(event.getPlayer().getUniqueId());
//
//        if (portal == null || !portal.isOpen()) {
//            return;
//        }
//
//
//        if (portal.isOpen() && portal.getLocation() != null && portal.getTarget() != null) {
//
//            Location location = Utils.deserialize(portal.getLocation().split(":")[1]);
//            Location location2 = Utils.deserialize(portal.getLocation().split(":")[1]);
//
//            location.setY(location.getY() + 5);
//            location2.setY(location2.getY() -5);
//
//            if (new Cuboid(location, location2).contains(event.getPlayer().getLocation())) {
//                if (portal.getLocation().split(":")[0].equalsIgnoreCase(portal.getTarget().split(":")[0])) {
//                    Location target = Utils.deserialize(portal.getTarget().split(":")[1]);
//                    target.setY(target.getY() + 1);
//                    event.getPlayer().teleport(target);
//                } else {
//                    Portals.getInstance().getPortalManager().setPending(event.getPlayer(), true);
//                    BungeeInstance.getInstance().sendToServer(event.getPlayer(), portal);
//                }
//            }
//        }
//    }

    @EventHandler
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        if (!event.getRightClicked().isVisible()) {
            event.setCancelled(true);
        }
    }
}
