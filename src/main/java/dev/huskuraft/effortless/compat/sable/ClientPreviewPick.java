package dev.huskuraft.effortless.compat.sable;

import dev.huskuraft.effortless.building.Context;
import dev.huskuraft.universal.api.core.BlockInteraction;
import dev.huskuraft.universal.api.core.Player;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class ClientPreviewPick {
    private ClientPreviewPick() {}

    private static Frame frame(BlockInteraction hit) {
        return hit == null ? null : Frame.tag(hit).effortlessSable$get();
    }

    private static boolean onShip(Frame frame) {
        return frame != null && frame.owner() != null;
    }

    public static boolean needsUpdate(Context base, BlockInteraction last) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;
        return onShip(Frame.first(base)) || onShip(frame(last))
                || Sable.HELPER.getTrackingOrVehicleSubLevel(mc.player) != null
                || (mc.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK
                    && Sable.HELPER.getContaining(mc.level, hit.getBlockPos()) != null);
    }

    public static BlockInteraction trace(Player player, Context base) {
        var mc = Minecraft.getInstance();
        float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
        var eye = Sable.HELPER.getEyePositionInterpolated(mc.player, partialTick);
        var hit = mc.getCameraEntity() == mc.player && mc.hitResult instanceof BlockHitResult block
                && block.getType() == HitResult.Type.BLOCK ? block : null;
        return TraceScope.trace(base.structure(), player, base, eye, mc.player.getViewVector(partialTick), hit);
    }

    public static boolean sameCell(BlockInteraction a, BlockInteraction b) {
        if (a == null || b == null) return a == b;
        return a.isMiss() == b.isMiss() && a.direction() == b.direction()
                && a.blockPosition().equals(b.blockPosition()) && java.util.Objects.equals(frame(a), frame(b));
    }
}
