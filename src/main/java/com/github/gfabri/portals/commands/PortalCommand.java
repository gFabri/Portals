package com.github.gfabri.portals.commands;

import com.github.gfabri.portals.ConfigHandler;
import com.github.gfabri.portals.utils.CustomItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class PortalCommand
        implements CommandExecutor
{

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (sender.hasPermission("portals.manage")) {

            Player player = (Player) sender;

            player.getInventory().addItem(new CustomItem(Material.getMaterial(ConfigHandler.Configs.CONFIG.getConfig().getString("PortalItem.material")), 1, ConfigHandler.Configs.CONFIG.getConfig().getInt("PortalItem.material-data")).setName(ConfigHandler.Configs.CONFIG.getConfig().getString("PortalItem.displayName")).addLore(ConfigHandler.Configs.CONFIG.getConfig().getStringList("PortalItem.lore")).create());
            return false;
        }

        return true;
    }
}
