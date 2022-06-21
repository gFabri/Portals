package com.github.gfabri.portals.portals;

import com.github.gfabri.portals.ConfigHandler;
import com.github.gfabri.portals.Portals;
import com.github.gfabri.portals.events.PortalCreateEvent;
import com.github.gfabri.portals.utils.CustomItem;
import com.github.gfabri.portals.utils.Utils;
import com.github.gfabri.portals.utils.WorldGuardAPI;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PortalManager {

    @Getter
    private final HashMap<UUID, HashMap<ItemStack, Portal>> portals = new HashMap<>();

    @Getter
    private final MysqlDataSource dataSource;

    public PortalManager() {
        dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(ConfigHandler.Configs.CONFIG.getConfig().getString("MySQL.ip"));
        dataSource.setPortNumber(ConfigHandler.Configs.CONFIG.getConfig().getInt("MySQL.port"));
        dataSource.setDatabaseName(ConfigHandler.Configs.CONFIG.getConfig().getString("MySQL.database"));
        dataSource.setUser(ConfigHandler.Configs.CONFIG.getConfig().getString("MySQL.username"));
        dataSource.setPassword(ConfigHandler.Configs.CONFIG.getConfig().getString("MySQL.password"));
        try {
            dataSource.setAllowMultiQueries(true);
            dataSource.setAutoReconnect(true);
            dataSource.setAutoReconnectForPools(true);
            dataSource.setCharacterEncoding("utf8");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getConsoleSender().sendMessage(Utils.translate("&7[&bPortals&7] &eConnecting to database..."));

        try {
            if (!getConnection().isClosed()) {
                Bukkit.getConsoleSender().sendMessage(Utils.translate("&7[&bPortals&7] &aSuccessfully connection"));
            } else {
                Bukkit.getConsoleSender().sendMessage(Utils.translate("&7[&bPortals&7] &cAn error occurred with the connection"));
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTable();
    }

    public void createTable() {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `portals` (id int NOT NULL AUTO_INCREMENT, owner varchar(38) NOT NULL, location varchar(100), target varchar(100), safe varchar(10), pending varchar(10), PRIMARY KEY(id));");
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeConnection() {
        try {
            if (getConnection() != null) {
                getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disable() {
        if (dataSource != null) {
            try {
                dataSource.getConnection().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getLastPortalID() {
        int id = 1;
        try {
            Connection con = getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "SELECT id FROM portals ORDER BY id DESC LIMIT 1;");

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                id = rs.getInt(1);
            }
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void createPortal(Player player, ItemStack stack) {
        if (player.getItemInHand().getItemMeta().getLore().contains("Target")) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Portals.getInstance(), () -> {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `portals` (`owner`, `location`, `target`, `safe`, `pending`) VALUES (?, ?, ?, ?, ?);");

                statement.setString(1, String.valueOf(player.getUniqueId()));
                statement.setString(2, null);
                statement.setString(3, null);
                statement.setString(4, "true");
                statement.setString(5, "false");
                statement.executeUpdate();

                HashMap<ItemStack, Portal> newPortals = new HashMap<>();
                newPortals.put(stack, new Portal(getLastPortalID(), player.getUniqueId(), null, null, false));
                getPortals().put(player.getUniqueId(), newPortals);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isPending(Player player) {
        boolean pending = false;
            try {
                Connection con = getConnection();
                PreparedStatement statement = con.prepareStatement(
                        "SELECT pending FROM `portals` where owner=?;");

                statement.setString(1, String.valueOf(player.getUniqueId())
                );

                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                   pending = Boolean.parseBoolean(rs.getString(1));
                }
                closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return pending;
    }

    public void load(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(Portals.getInstance(), () -> {
            try {
                Connection con = getConnection();
                PreparedStatement statement = con.prepareStatement(
                        "SELECT * FROM `portals` where owner=?;");

                statement.setString(1, String.valueOf(uuid)
                );

                ResultSet rs = statement.executeQuery();

                HashMap<ItemStack, Portal> portals = new HashMap<>();

                while (rs.next()) {
                    Portal portal = new Portal(rs.getInt(1), uuid, rs.getString(3), rs.getString(4), Boolean.parseBoolean(rs.getString(5)));
                    ItemStack customItem = new CustomItem(Material.getMaterial(ConfigHandler.Configs.CONFIG.getConfig().getString("PortalItem.material")), 1, ConfigHandler.Configs.CONFIG.getConfig().getInt("PortalItem.material-data")).setName(ConfigHandler.Configs.CONFIG.getConfig().getString("PortalItem.displayName")).
                            addLore(Utils.translate("&fTarget&7: &c" + portal.getTarget()), Utils.translate("&fLast Location&7: &c" + portal.getLocation())).create();
                    portals.put(customItem, portal);
                }
                getPortals().put(uuid, portals);
                closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void update(Player player, String location, String target, int id) {
        Bukkit.getScheduler().runTaskAsynchronously(Portals.getInstance(), () -> {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE portals SET `location`=?, `target`=?, `safe`=? WHERE owner=? and id=?;");

                boolean safe = true;

                if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
                    safe = !WorldGuardAPI.isPvPZone(Utils.deserialize(target.split(":")[1]));
                }

                statement.setString(1, location);
                statement.setString(2, target);
                statement.setString(3, String.valueOf(safe));
                statement.setString(4, String.valueOf(player.getUniqueId()));
                statement.setInt(5, id);

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setPending(Player player, boolean pending, int id)  {
        Bukkit.getScheduler().runTaskAsynchronously(Portals.getInstance(), () -> {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE portals SET `pending`=? WHERE owner=? and id=?;");

                statement.setString(1, String.valueOf(pending));
                statement.setString(2, String.valueOf(player.getUniqueId()));
                statement.setInt(3, id);

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setTarget(Player player, String location, ItemStack itemStack) {
        if (getPortals().containsKey(player.getUniqueId())) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> lore = itemMeta.getLore();

            lore.set(0, Utils.translate("&fTarget&7: &c" + location));

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            player.sendMessage(Utils.translate(ConfigHandler.Configs.CONFIG.getConfig().getString("MESSAGES.target-set")));
        }
    }

    public void setLocation(Player player, String location, ItemStack stack) {
        if (getPortals().containsKey(player.getUniqueId())) {
            ItemStack item = player.getItemInUse();
            ItemMeta itemMeta = item.getItemMeta();

            List<String> lore = itemMeta.getLore();

            lore.set(1, Utils.translate("&fLast Location&7: &c" + location));

            itemMeta.setLore(lore);
            stack.setItemMeta(itemMeta);


            if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
                portal.setSafe(!WorldGuardAPI.isPvPZone(Utils.deserialize(location.split(":")[1])));
            }

            HashMap<ItemStack, Portal> portals = getPortals().get(player.getUniqueId());
            Portal portal = portals.get(stack);
            portal.setLocation(location);
//
//
//            Bukkit.getPluginManager().callEvent(new PortalCreateEvent(player, portal));
        }
    }

    public ItemStack getPortal(Player player, ItemStack stack) {
        if (getPortals().containsKey(player.getUniqueId()) && getPortals().get(player.getUniqueId()).get(stack).isSafe()) {
            ItemStack item = new ItemStack(Material.getMaterial(ConfigHandler.Configs.CONFIG.getConfig().getString("armor_stand.in-safe-zone.material")));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(ConfigHandler.Configs.CONFIG.getConfig().getInt("armor_stand.in-safe-zone.data"));
            item.setItemMeta(itemMeta);
            return item;
        } else {
            ItemStack item = new ItemStack(Material.getMaterial(ConfigHandler.Configs.CONFIG.getConfig().getString("armor_stand.in-pvp-zone.material")));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(ConfigHandler.Configs.CONFIG.getConfig().getInt("armor_stand.in-pvp-zone.data"));
            item.setItemMeta(itemMeta);
            return item;
        }
    }
}
