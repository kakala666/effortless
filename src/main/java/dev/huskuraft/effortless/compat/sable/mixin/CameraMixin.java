package dev.huskuraft.effortless.compat.sable.mixin;

import dev.huskuraft.effortless.compat.sable.OutlineFrames;
import dev.huskuraft.universal.api.math.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="dev.huskuraft.effortless.neoforge.renderer.MinecraftCamera", remap=false)
abstract class CameraMixin {
    @Inject(method="position", at=@At("HEAD"), cancellable=true)
    private void localPosition(CallbackInfoReturnable<Vector3d> ci) {
        var camera = OutlineFrames.localCamera();
        if (camera != null) ci.setReturnValue(camera);
    }
}
