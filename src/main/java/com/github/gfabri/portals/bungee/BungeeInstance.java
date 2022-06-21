package com.github.gfabri.portals.bungee;

import com.github.gfabri.portals.ConfigHandler;
import com.github.gfabri.portals.Portals;
import com.github.gfabri.portals.portals.Portal;
import com.github.gfabri.portals.utils.Utils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class BungeeInstance {

    private static BungeeInstance instance;

    @Getter
    @Setter
    private String localServer = ConfigHandler.Configs.CONFIG.getConfig().getString("serverName");

    public static BungeeInstance getInstance() {
        if (instance == null) {
            synchronized (BungeeInstance.class) {
                if (instance == null) {
                    instance = new BungeeInstance();
                }
            }
        }
        return instance;
    }

    public void sendToServer(Player player, Portal portal) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(portal.getTarget().split(":")[0]);
        player.sendPluginMessage(Portals.getInstance(), "BungeeCord", out.toByteArray());
    }
}
