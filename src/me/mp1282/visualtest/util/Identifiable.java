package me.mp1282.visualtest.util;

import java.util.UUID;

public abstract class Identifiable {

    protected final UUID uuid;

    public Identifiable() {
        this(UUID.randomUUID());
    }

    public Identifiable(UUID uuid) {
        this.uuid = uuid;
    }

    public final UUID getUniqueId() {
        return uuid;
    }
}
