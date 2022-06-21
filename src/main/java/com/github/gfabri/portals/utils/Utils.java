package com.github.gfabri.portals.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UtilityClass
public class Utils {


    public static String translate(String input) {
        return ChatColor.translateAlternateColorCodes('&', input.replace("<", "\u00AB").replace(">", "\u00BB").replace('"', '═'));
    }

    public static Location deserialize(String serialized) {
        String[] toDeserialize = serialized.split(";");
        Location location = new Location(Bukkit.getWorld(toDeserialize[0]), Double.parseDouble(toDeserialize[1]), Double.parseDouble(toDeserialize[2]), Double.parseDouble(toDeserialize[3]));
        location.setPitch(Float.parseFloat(toDeserialize[4]));
        location.setYaw(Float.parseFloat(toDeserialize[5]));
        return location;
    }

    public static String serialize(Location location) {
        return (location.getWorld()).getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getPitch() + ";" + location.getYaw();
    }

    public static List<String> translate(List<String> input) {
        List<String> newInput = new ArrayList<String>();
        input.forEach(line -> newInput.add(ChatColor.translateAlternateColorCodes('&', line)));
        return newInput;
    }
}
