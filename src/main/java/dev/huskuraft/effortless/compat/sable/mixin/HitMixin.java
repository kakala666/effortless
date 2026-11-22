package dev.huskuraft.effortless.compat.sable.mixin;

import dev.huskuraft.effortless.compat.sable.Frame;
import dev.huskuraft.universal.api.core.BlockInteraction;
import org.spongepowered.asm.mixin.*;

@Mixin(value = BlockInteraction.class, remap = false)
abstract class HitMixin implements Frame.Tagged {
    @Unique private Frame effortlessSable$frame;
    public Frame effortlessSable$get() { return effortlessSable$frame; }
    public void effortlessSable$set(Frame frame) { effortlessSable$frame = frame; }
}
