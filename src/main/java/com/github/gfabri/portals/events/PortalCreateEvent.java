package com.github.gfabri.portals.events;

import com.github.gfabri.portals.portals.Portal;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PortalCreateEvent extends PlayerEvent {

    private static final HandlerList handlerList = new HandlerList();

    @Getter
    private final Portal portal;

    public PortalCreateEvent(Player player, Portal portal) {
        super(player);

        this.portal = portal;
    }


    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
