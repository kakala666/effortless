package dev.huskuraft.effortless.compat.sable;

import net.neoforged.fml.ModList;

public final class SableSupport {
    public static final boolean LOADED = ModList.get().isLoaded("sable");

    private SableSupport() {}
}
