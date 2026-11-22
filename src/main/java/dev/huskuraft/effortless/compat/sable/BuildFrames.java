package dev.huskuraft.effortless.compat.sable;

import dev.huskuraft.effortless.building.Context;
import dev.huskuraft.effortless.building.operation.batch.BatchOperation;
import dev.huskuraft.effortless.building.operation.batch.DeferredBatchOperation;
import dev.huskuraft.effortless.building.operation.block.BlockOperation;
import dev.huskuraft.effortless.compat.sable.mixin.ChunkCacheAccessor;
import dev.huskuraft.universal.api.core.World;
import dev.ryanhcode.sable.Sable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

public final class BuildFrames {
    private BuildFrames() {}

    public static BatchOperation restrict(World world, Context context, BatchOperation operations) {
        Level level = world.reference();
        var frame = Frame.first(context);
        if (frame == null || !context.getInteractions().stream().allMatch(hit ->
                hit != null && frame.equals(Frame.tag(hit).effortlessSable$get()) && frame.contains(level, hit.blockPosition()))) {
            return new DeferredBatchOperation(context, Stream::empty);
        }
        return operations.filter(op -> op instanceof BlockOperation block && frame.contains(level, block.getBlockPosition()));
    }

    public static boolean withinBorder(BlockOperation op) {
        var frame = Frame.first(op.getContext());
        Level level = op.getWorld().reference();
        if (frame == null || op.getPlayer().getWorld().refs() != level || !frame.contains(level, op.getBlockPosition())) {
            return false;
        }
        if (frame.owner() == null) return op.getWorld().getWorldBorder().isInBounds(op.getBlockPosition());
        var center = Vec3.atCenterOf(Frame.pos(op.getBlockPosition()));
        var actual = frame.ship(level, op.getBlockPosition()).logicalPose().transformPosition(center);
        return level.getWorldBorder().isWithinBounds(net.minecraft.core.BlockPos.containing(actual));
    }

    public static boolean preparePlacement(BlockOperation op) {
        var frame = Frame.first(op.getContext());
        if (frame == null) return false;
        if (frame.owner() == null || !(op.getWorld().refs() instanceof ServerLevel level)) return true;
        if (op.getPlayer().getWorld().refs() != level || !frame.contains(level, op.getBlockPosition())) return false;
        var pos = Frame.pos(op.getBlockPosition());
        var ship = Sable.HELPER.getContaining(level, pos);
        if (ship == null || ship.isRemoved() || !frame.owner().equals(ship.getUniqueId())) return false;
        var plot = ship.getPlot();
        var chunk = new ChunkPos(pos);
        if (plot.getChunkHolder(plot.toLocal(chunk)) == null) {
            plot.newEmptyChunk(chunk);
            // Preflight can cache Sable's empty placeholder before the first write.
            ((ChunkCacheAccessor) level.getChunkSource()).effortlessSable$clearRecentChunks();
        }
        return true;
    }
}
