package dev.huskuraft.effortless.compat.sable.mixin;

import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkCache.class)
public interface ChunkCacheAccessor {
    @Invoker("clearCache")
    void effortlessSable$clearRecentChunks();
}
