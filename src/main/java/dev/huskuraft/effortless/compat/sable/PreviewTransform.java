package dev.huskuraft.effortless.compat.sable;

import dev.huskuraft.effortless.building.operation.block.BlockOperation;
import dev.huskuraft.universal.api.math.Quaternionf;
import dev.huskuraft.universal.api.math.Vector3d;
import dev.huskuraft.universal.api.renderer.Renderer;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.world.level.Level;

public record PreviewTransform(Vector3d position, Pose3dc pose) {
    public static PreviewTransform forBlock(BlockOperation op) {
        var frame = Frame.first(op.getContext());
        if (frame == null || frame.owner() == null) return null;
        Level level = op.getWorld().reference();
        if (!frame.dimension().equals(level.dimension().location().toString())) return null;
        var ship = frame.ship(level, op.getBlockPosition());
        if (ship == null || !frame.owner().equals(ship.getUniqueId())) return null;
        var pose = PreviewPoses.get(ship);
        var position = pose.transformPosition(TraceScope.vec(op.getBlockPosition().toVector3d()));
        return new PreviewTransform(TraceScope.vector(position), pose);
    }

    public void apply(Renderer renderer) {
        var q = pose.orientation();
        renderer.rotate(new Quaternionf((float) q.x(), (float) q.y(), (float) q.z(), (float) q.w()));
        renderer.scale(pose.scale().x(), pose.scale().y(), pose.scale().z());
    }
}
