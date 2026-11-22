package dev.huskuraft.effortless.compat.sable;

import java.util.UUID;
import dev.huskuraft.universal.api.networking.NetByteBuf;
import dev.huskuraft.universal.api.core.BlockInteraction;
import dev.huskuraft.universal.api.core.BlockPosition;
import dev.huskuraft.effortless.building.Context;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public record Frame(String dimension, UUID owner) {
    public interface Tagged {
        Frame effortlessSable$get();
        void effortlessSable$set(Frame frame);
    }

    public static Frame first(Context context) {
        if (context.interactionsSize() == 0 || context.getInteraction(0) == null) return null;
        return tag(context.getInteraction(0)).effortlessSable$get();
    }

    public static Tagged tag(BlockInteraction hit) {
        return (Tagged)(Object) hit;
    }

    public static void write(NetByteBuf buf, BlockInteraction hit) {
        var frame = tag(hit).effortlessSable$get();
        buf.writeBoolean(frame != null);
        if (frame == null) return;
        buf.writeString(frame.dimension());
        buf.writeBoolean(frame.owner() != null);
        if (frame.owner() != null) buf.writeUUID(frame.owner());
    }

    public static void read(NetByteBuf buf, BlockInteraction hit) {
        if (buf.readBoolean()) tag(hit).effortlessSable$set(new Frame(buf.readString(), buf.readBoolean() ? buf.readUUID() : null));
    }

    public static BlockPos pos(BlockPosition p) {
        return new BlockPos(p.x(), p.y(), p.z());
    }

    public static Frame at(Level level, BlockPos p) {
        var ship = SableCompanion.INSTANCE.getContaining(level, p);
        if (ship == null && SableCompanion.INSTANCE.isInPlotGrid(level, p)) return null;
        return new Frame(level.dimension().location().toString(), ship == null ? null : ship.getUniqueId());
    }

    public boolean contains(Level level, BlockPosition pos) {
        return dimension.equals(level.dimension().location().toString())
                && containsChunk(level, pos.x() >> 4, pos.z() >> 4);
    }

    boolean containsChunk(Level level, int x, int z) {
        var ship = SableCompanion.INSTANCE.getContaining(level, x, z);
        if (ship != null) return ship.getUniqueId().equals(owner);
        return owner == null && !SableCompanion.INSTANCE.isInPlotGrid(level, x, z);
    }

    public SubLevelAccess ship(Level level, BlockPosition pos) {
        return SableCompanion.INSTANCE.getContaining(level, pos(pos));
    }
}
