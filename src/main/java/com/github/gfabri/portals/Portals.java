package com.github.gfabri.portals;

import com.github.gfabri.portals.bungee.BungeeInstance;
import com.github.gfabri.portals.commands.PortalCommand;
import com.github.gfabri.portals.commands.TeleportCommand;
import com.github.gfabri.portals.portals.PortalListener;
import com.github.gfabri.portals.portals.PortalManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Portals extends JavaPlugin {

    @Getter @Setter
    public static Portals instance;

    @Getter
    private PortalManager portalManager;

    @Override
    public void onEnable() {
        setInstance(this);
        new ConfigHandler(this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("portal").setExecutor(new PortalCommand());
        getCommand("tpportal").setExecutor(new TeleportCommand());
        Bukkit.getPluginManager().registerEvents(new PortalListener(), this);

        portalManager = new PortalManager();

        Bukkit.getOnlinePlayers().forEach(player -> portalManager.load(player.getUniqueId()));

        if (BungeeInstance.getInstance().getLocalServer() == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "config ´serverName´ not defined in config.yml.");
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        portalManager.disable();

        Bukkit.getWorlds().forEach(worlds -> worlds.getEntities().forEach(entity -> {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                ArmorStand stand = (ArmorStand) entity;

                if (!stand.isVisible() && stand.hasMetadata("portals")) {
                    entity.remove();
                }
            }
        }));

        setInstance(null);
    }
}
