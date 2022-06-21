package com.github.gfabri.portals.commands;

import com.github.gfabri.portals.Portals;
import com.github.gfabri.portals.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand
        implements CommandExecutor
{

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
            Player player = (Player) sender;

//        if (Portals.getInstance().getPortalManager().getPortals().containsKey(player.getUniqueId())) {
//            Location location = Utils.deserialize(Portals.getInstance().getPortalManager().getPortals().get(player.getUniqueId()).getTarget().split(":")[1]);
//            location.setY(location.getY() + 1);
//            player.teleport(location);
//        }

        return true;
    }
}
