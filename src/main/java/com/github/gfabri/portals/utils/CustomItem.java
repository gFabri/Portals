package com.github.gfabri.portals.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public CustomItem(Material material, int amount, int shorts) {
        this.itemStack = new ItemStack(material, amount, (short) shorts);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public CustomItem setName(String displayName) {
        this.itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName).replace("<", "\u00AB").replace(">", "\u00BB"));
        return this;
    }

    public CustomItem addLore(List<String> strings) {
        List<String> newLore = new ArrayList<>();
        strings.forEach(nowLore -> newLore.add(ChatColor.translateAlternateColorCodes('&', nowLore.replace("<", "\u00AB").replace(">", "\u00BB"))));
        this.itemMeta.setLore(newLore);
        return this;
    }

    public CustomItem addLore(String... strings) {
        List<String> newLore = new ArrayList<>();
        for (String nowLore : strings) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', nowLore.replace("<", "\u00AB").replace(">", "\u00BB")));
        }
        this.itemMeta.setLore(newLore);
        return this;
    }

    public ItemStack createHead(Player player, String name) {
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(player.getName());
        skullMeta.setDisplayName(name);
        this.itemStack.setItemMeta(skullMeta);
        return this.itemStack;
    }

    public ItemStack create() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
