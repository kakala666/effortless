package dev.huskuraft.effortless.compat.sable;

import dev.huskuraft.universal.api.core.*;
import dev.huskuraft.universal.api.math.Vector3d;
import dev.huskuraft.effortless.building.Context;
import dev.huskuraft.effortless.building.structure.builder.Structure;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

public final class TraceScope {
    private static final ThreadLocal<TraceScope> CURRENT = new ThreadLocal<>();
    private final Level level;
    private final Vec3 previewEye;
    private final Vec3 previewLook;
    private final BlockHitResult crosshair;
    private Frame frame;
    private Pose3dc pose;

    private TraceScope(Player player, Vec3 previewEye, Vec3 previewLook, BlockHitResult crosshair) {
        level = player.getWorld().reference();
        this.previewEye = previewEye;
        this.previewLook = previewLook;
        this.crosshair = crosshair;
    }

    public static BlockInteraction trace(Structure structure, Player player, Context context) {
        return trace(structure, player, context, null, null, null);
    }

    static BlockInteraction trace(Structure structure, Player player, Context context, Vec3 eye, Vec3 look, BlockHitResult crosshair) {
        var scope = new TraceScope(player, eye, look, crosshair);
        if (context.interactionsSize() != 0) {
            scope.frame = Frame.first(context);
            if (scope.frame == null || !scope.frame.contains(scope.level, context.getPosition(0))) return null;
            var ship = scope.frame.ship(scope.level, context.getPosition(0));
            if (ship != null) scope.pose = ship instanceof ClientSubLevelAccess client ? client.renderPose() : ship.logicalPose();
        }
        var previous = CURRENT.get();
        CURRENT.set(scope);
        try {
            var hit = structure.trace(player, context);
            if (hit == null || hit.isMiss()) return hit;
            if (scope.frame == null || !scope.frame.contains(scope.level, hit.blockPosition())) return null;
            Frame.tag(hit).effortlessSable$set(scope.frame);
            return hit;
        } finally {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        }
    }

    public static Vector3d vector(Vec3 p) { return new Vector3d(p.x, p.y, p.z); }
    public static Vec3 vec(Vector3d p) { return new Vec3(p.x(), p.y(), p.z()); }

    public static Vector3d eye(Player player) {
        var s = CURRENT.get();
        var eye = s != null && s.previewEye != null ? vector(s.previewEye) : player.getEyePosition();
        return s == null || s.pose == null ? eye : vector(s.pose.transformPositionInverse(vec(eye)));
    }

    public static Vector3d look(Player player) {
        var s = CURRENT.get();
        var look = s != null && s.previewLook != null ? vector(s.previewLook) : player.getEyeDirection();
        return s == null || s.pose == null ? look : vector(s.pose.transformNormalInverse(vec(look)).normalize());
    }

    public static BlockInteraction pick(Player player, double reach, float tick, boolean fluids) {
        var s = CURRENT.get();
        if (s == null) return player.raytrace(reach, tick, fluids);
        Vec3 worldStart = s.previewEye != null ? s.previewEye : vec(player.getEyePosition());
        Vec3 direction = s.previewLook != null ? s.previewLook : vec(player.getEyeDirection());
        Vec3 worldEnd = worldStart.add(direction.scale(reach));
        if (s.frame != null) return interaction(s.clip(player, fluids, worldStart, worldEnd, s.frame, s.pose));

        // The client already picked the closest outline at this render pose.
        // Reuse it only for an unlocked solid-block ray within the tool's reach.
        if (!fluids && s.crosshair != null) {
            var pos = s.crosshair.getBlockPos();
            var ship = SableCompanion.INSTANCE.getContaining(s.level, pos);
            if (ship instanceof ClientSubLevelAccess client) {
                var worldHit = client.renderPose().transformPosition(s.crosshair.getLocation());
                if (worldStart.distanceToSqr(worldHit) <= reach * reach) {
                    s.frame = Frame.at(s.level, pos);
                    return interaction(s.crosshair);
                }
            }
        }

        String dimension = s.level.dimension().location().toString();
        var selected = new Frame(dimension, null);
        var closest = s.clip(player, fluids, worldStart, worldEnd, selected, null);
        double distance = closest.getType() == HitResult.Type.MISS ? Double.POSITIVE_INFINITY : closest.getLocation().distanceToSqr(worldStart);
        var bounds = new BoundingBox3d(new AABB(worldStart, worldEnd).inflate(1));
        for (var ship : SableCompanion.INSTANCE.getAllIntersecting(s.level, bounds)) {
            var pose = ship instanceof ClientSubLevelAccess client ? client.renderPose() : ship.logicalPose();
            var candidate = new Frame(dimension, ship.getUniqueId());
            var hit = s.clip(player, fluids, worldStart, worldEnd, candidate, pose);
            if (hit.getType() == HitResult.Type.MISS) continue;
            double d = pose.transformPosition(hit.getLocation()).distanceToSqr(worldStart);
            if (d < distance) {
                selected = candidate;
                closest = hit;
                distance = d;
            }
        }
        s.frame = selected;
        return interaction(closest);
    }

    private BlockHitResult clip(Player player, boolean fluids, Vec3 worldStart, Vec3 worldEnd, Frame target, Pose3dc pose) {
        Vec3 start = pose == null ? worldStart : pose.transformPositionInverse(worldStart);
        Vec3 end = pose == null ? worldEnd : pose.transformPositionInverse(worldEnd);
        var clip = new ClipContext(start, end, ClipContext.Block.OUTLINE,
                fluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, (net.minecraft.world.entity.Entity) player.refs());
        // Both Companion ownership methods depend only on chunk X/Z. The cache
        // belongs to this single synchronous traversal of a validated dimension.
        class ChunkOwnership {
            int lastX = Integer.MIN_VALUE;
            int lastZ;
            boolean matches;
            boolean accepts(BlockPos pos) {
                int x = pos.getX() >> 4, z = pos.getZ() >> 4;
                if (x != lastX || z != lastZ) {
                    matches = target.containsChunk(level, x, z);
                    lastX = x;
                    lastZ = z;
                }
                return matches;
            }
        }
        var ownership = new ChunkOwnership();
        return BlockGetter.traverseBlocks(start, end, clip, (ctx, pos) -> {
            if (!ownership.accepts(pos)) return null;
            var block = level.getBlockState(pos);
            var solid = level.clipWithInteractionOverride(start, end, pos, ctx.getBlockShape(block, level, pos), block);
            var liquid = fluids ? ctx.getFluidShape(level.getFluidState(pos), level, pos).clip(start, end, pos) : null;
            return liquid != null && (solid == null || start.distanceToSqr(liquid.getLocation()) < start.distanceToSqr(solid.getLocation())) ? liquid : solid;
        }, ctx -> BlockHitResult.miss(end, net.minecraft.core.Direction.getNearest(start.x-end.x,start.y-end.y,start.z-end.z), BlockPos.containing(end)));
    }

    private static BlockInteraction interaction(BlockHitResult hit) {
        var p = hit.getBlockPos();
        return new BlockInteraction(vector(hit.getLocation()), Direction.valueOf(hit.getDirection().name()),
                new BlockPosition(p.getX(), p.getY(), p.getZ()), hit.isInside(), hit.getType() == HitResult.Type.MISS);
    }
}
