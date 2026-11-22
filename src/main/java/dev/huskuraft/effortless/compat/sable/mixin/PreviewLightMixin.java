package dev.huskuraft.effortless.compat.sable.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.huskuraft.universal.api.core.BlockPosition;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.huskuraft.effortless.compat.sable.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="dev.huskuraft.effortless.neoforge.renderer.MinecraftRenderer", remap=false)
abstract class PreviewLightMixin {
    @ModifyArg(method="renderBlockState", at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI)V"), index=0)
    private BlockAndTintGetter previewLighting(BlockAndTintGetter world, @Local(argsOnly=true) BlockPosition position) {
        if (world instanceof ClientLevel level && Sable.HELPER.getContaining(level, Frame.pos(position)) instanceof ClientSubLevel ship)
            return PreviewPoses.lighting(level,ship);
        return world;
    }
}
