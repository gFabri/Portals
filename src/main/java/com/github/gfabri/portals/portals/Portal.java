package com.github.gfabri.portals.portals;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Portal {

    @Getter
    private final int id;

    @Getter
    private final UUID owner;

    @Getter @Setter
    private boolean open;

    @Getter @Setter
    private boolean pending;

    @Getter @Setter
    private boolean safe;

    @Setter
    @Getter
    private String location;

    @Getter @Setter
    private String target;

    public Portal(int id, UUID owner) {
        this.id = id;
        this.owner = owner;
        this.open = false;
        this.safe = true;
        this.pending = false;
    }

    public Portal(int id, UUID owner, String location, String target, boolean safe) {
        this.id = id;
        this.owner = owner;
        this.location = location;
        this.target = target;
        this.safe = safe;
    }
}
