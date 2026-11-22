package dev.huskuraft.effortless.test;

import java.util.List;
import dev.huskuraft.effortless.compat.sable.Frame;
import dev.huskuraft.effortless.Effortless;
import dev.huskuraft.effortless.api.core.*;
import dev.huskuraft.effortless.building.*;
import dev.huskuraft.effortless.building.session.BatchBuildSession;
import dev.huskuraft.effortless.building.structure.builder.standard.Single;
import dev.huskuraft.effortless.neoforge.core.MinecraftPlayer;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.*;

@Mod("effortless_sable_test")
@GameTestHolder("effortless_sable_test")
public final class IsolationTest {
    @PrefixGameTestTemplate(false)
    @GameTest(template="empty",timeoutTicks=120)
    public static void buildInMissingPlotChunks(GameTestHelper t) {
        var level = t.getLevel();
        var start = t.absolutePos(new BlockPos(2,2,2));
        level.setBlockAndUpdate(start,Blocks.STONE.defaultBlockState());
        var ship = SubLevelAssemblyHelper.assembleBlocks(level,start,List.of(start),new BoundingBox3i(start,start));
        var anchor = BlockPos.containing(ship.logicalPose().transformPositionInverse(Vec3.atCenterOf(start)));
        var origin = anchor.above();
        var far = origin.offset(32,0,27);
        var plot = ship.getPlot();
        var farChunk = plot.toLocal(new net.minecraft.world.level.ChunkPos(far));
        var frame = Frame.at(level,anchor);
        var fake = FakePlayerFactory.get(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"PlotFloorTest"));
        fake.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
        fake.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.item.ItemStack(Blocks.STONE_BRICKS,64));
        var player = new MinecraftPlayer(fake);
        var first = hit(origin);
        var last = hit(far);
        Frame.tag(first).effortlessSable$set(frame);
        Frame.tag(last).effortlessSable$set(frame);
        var base = Context.defaultSet().withBuildState(BuildState.PLACE_BLOCK).withPlayerExtras(player);
        var square = base.withStructure(new dev.huskuraft.effortless.building.structure.builder.standard.Square())
                .withNextInteraction(first).withNextInteraction(last);
        new BatchBuildSession(Effortless.getInstance(),player,square.withBuildType(BuildType.PREVIEW)).commit();
        t.assertTrue(plot.getChunkHolder(farChunk)==null,"Preview allocated a plot chunk");
        // Write a disconnected cell first so that normal adjacent-block expansion cannot mask the failure.
        var isolated = base.withStructure(new Single()).withNextInteraction(last);
        var one = new BatchBuildSession(Effortless.getInstance(),player,isolated).commit();
        t.assertTrue(level.getBlockState(far).is(Blocks.STONE_BRICKS),"First remote write failed: holder="+plot.getChunkHolder(farChunk)+" result="+
                one.getResults().stream().map(r->((dev.huskuraft.effortless.building.operation.block.BlockOperationResult)r).result()).toList()+
                " owner="+Frame.at(level,far)+" anchor="+frame);
        one.getReverseOperation().commit();
        t.assertTrue(level.getBlockState(far).isAir(),"Remote undo failed");
        level.setBlockAndUpdate(start.above(),Blocks.GOLD_BLOCK.defaultBlockState());
        var floor = new BatchBuildSession(Effortless.getInstance(),player,square).commit();
        for (var p:BlockPos.betweenClosed(origin,far)) t.assertTrue(level.getBlockState(p).is(Blocks.STONE_BRICKS),"Missing floor cell: "+p);
        floor.getReverseOperation().commit();
        for (var p:BlockPos.betweenClosed(origin,far)) t.assertTrue(level.getBlockState(p).isAir(),"Undo left a floor cell: "+p);
        t.assertTrue(level.getBlockState(anchor).is(Blocks.STONE) && level.getBlockState(start.above()).is(Blocks.GOLD_BLOCK),"Anchor or terrain changed");
        t.succeed();
    }
    @PrefixGameTestTemplate(false)
    @GameTest(template="empty",timeoutTicks=120)
    public static void overlappingTargets(GameTestHelper t) {
        var level = t.getLevel();
        BlockPos start = t.absolutePos(new BlockPos(2,2,2));
        BlockPos other = start.east(3);
        level.setBlockAndUpdate(start, Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(start.east(), Blocks.STONE.defaultBlockState());
        var a = SubLevelAssemblyHelper.assembleBlocks(level,start,List.of(start,start.east()),new BoundingBox3i(start,start.east()));
        level.setBlockAndUpdate(other, Blocks.GOLD_BLOCK.defaultBlockState());
        var b = SubLevelAssemblyHelper.assembleBlocks(level,other,List.of(other),new BoundingBox3i(other,other));
        BlockPos plotA = BlockPos.containing(a.logicalPose().transformPositionInverse(Vec3.atCenterOf(start)));
        BlockPos plotB = BlockPos.containing(b.logicalPose().transformPositionInverse(Vec3.atCenterOf(other)));
        level.setBlockAndUpdate(start, Blocks.IRON_BLOCK.defaultBlockState());
        var fake = FakePlayerFactory.get(level, new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "ShipUndoTest"));
        fake.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
        fake.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(Blocks.STONE));
        var player = new MinecraftPlayer(fake);
        player.refs().setPos(Vec3.atCenterOf(start).add(0,2,0));
        var base = Context.defaultSet().withStructure(new Single()).withBuildState(BuildState.BREAK_BLOCK).withPlayerExtras(player);
        var shipHit = hit(plotA);
        Frame shipFrame = Frame.at(level,plotA);
        Frame.tag(shipHit).effortlessSable$set(shipFrame);
        var terrainHit = hit(start);
        Frame.tag(terrainHit).effortlessSable$set(Frame.at(level,start));
        t.assertTrue(shipFrame.owner().equals(a.getUniqueId()),"Wrong primary ship");
        var context = base.withNextInteraction(shipHit);
        var bytes = io.netty.buffer.Unpooled.buffer();
        try {
            var buf = new dev.huskuraft.effortless.api.networking.NetByteBuf(bytes);
            var serializer = new dev.huskuraft.effortless.networking.serializer.BlockInteractionSerializer();
            serializer.write(buf, shipHit);
            var restored = serializer.read(buf);
            t.assertTrue(shipFrame.equals(Frame.tag(restored).effortlessSable$get()) && !bytes.isReadable(), "Frame packet round trip failed");
        } finally {
            bytes.release();
        }
        var result = new BatchBuildSession(Effortless.getInstance(),player,context).commit();
        t.assertTrue(level.getBlockState(plotA).isAir(),"Ship A block was not removed: " + result.getResults().stream()
                .map(r -> ((dev.huskuraft.effortless.building.operation.block.BlockOperationResult)r).result()).toList());
        t.assertTrue(level.getBlockState(start).is(Blocks.IRON_BLOCK) && level.getBlockState(plotB).is(Blocks.GOLD_BLOCK),"Terrain or B changed");
        var destination = new org.joml.Vector3d(a.logicalPose().position()).add(8, 2, 0);
        dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle.of(a).teleport(destination, new org.joml.Quaterniond().rotateY(Math.PI / 2));
        t.runAfterDelay(3, () -> {
        t.assertTrue(a.logicalPose().transformPosition(Vec3.atCenterOf(plotA)).distanceTo(Vec3.atCenterOf(start)) > 5,
                "Ship did not move before undo");
        result.getReverseOperation().commit();
        t.assertTrue(level.getBlockState(plotA).is(Blocks.STONE),"Ship undo failed");
        var wrong = hit(plotB);
        Frame.tag(wrong).effortlessSable$set(shipFrame);
        new BatchBuildSession(Effortless.getInstance(),player,base.withNextInteraction(wrong)).commit();
        t.assertTrue(level.getBlockState(plotB).is(Blocks.GOLD_BLOCK),"Stale owner modified B");
        new BatchBuildSession(Effortless.getInstance(),player,base.withNextInteraction(terrainHit)).commit();
        t.assertTrue(level.getBlockState(start).isAir() && level.getBlockState(plotA).is(Blocks.STONE),"Terrain edit crossed into A");
        t.succeed();
        });
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template="empty",timeoutTicks=120)
    public static void firstClickAndExtension(GameTestHelper t) {
        var level = t.getLevel();
        BlockPos start = t.absolutePos(new BlockPos(2,3,2));
        level.setBlockAndUpdate(start, Blocks.STONE.defaultBlockState());
        var ship = SubLevelAssemblyHelper.assembleBlocks(level,start,List.of(start),new BoundingBox3i(start,start));
        BlockPos plot = BlockPos.containing(ship.logicalPose().transformPositionInverse(Vec3.atCenterOf(start)));
        var fake = FakePlayerFactory.get(level, new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "ShipPickTest"));
        fake.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
        fake.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(Blocks.BRICKS));
        var player = new MinecraftPlayer(fake);
        t.runAfterDelay(3, () -> {
        Vec3 feet = ship.logicalPose().transformPosition(Vec3.atCenterOf(plot)).add(0,2,0);
        fake.moveTo(feet.x,feet.y,feet.z,0,90);
        fake.setOldPosAndRot();
        var base = Context.defaultSet().withStructure(new Single()).withBuildState(BuildState.PLACE_BLOCK).withPlayerExtras(player);
        var first = base.trace(player);
        t.assertTrue(first != null && !first.isMiss() && Frame.pos(first.blockPosition()).equals(plot.above()), "First click missed ship top: " + first + " bounds=" + ship.boundingBox() + " plot=" + plot + " feet=" + feet
                + " state=" + level.getBlockState(plot));
        t.assertTrue(ship.getUniqueId().equals(Frame.tag(first).effortlessSable$get().owner()),"First click lost owner");
        var diagonal = new dev.huskuraft.effortless.building.structure.builder.standard.DiagonalLine();
        var locked = base.withStructure(diagonal).withNextInteraction(first);
        BlockPos upper = start.above();
        level.setBlockAndUpdate(upper, Blocks.DIAMOND_BLOCK.defaultBlockState());
        var crossing = SubLevelAssemblyHelper.assembleBlocks(level,upper,List.of(upper),new BoundingBox3i(upper,upper));
        var plotCrossing = BlockPos.containing(crossing.logicalPose().transformPositionInverse(Vec3.atCenterOf(upper)));
        t.runAfterDelay(3, () -> {
        Vec3 a = ship.logicalPose().transformPosition(Vec3.atCenterOf(plot));
        Vec3 b = crossing.logicalPose().transformPosition(Vec3.atCenterOf(plotCrossing));
        Vec3 aim = a.subtract(b).normalize();
        Vec3 eye = b.subtract(aim.scale(0.85));
        fake.moveTo(eye.x, eye.y - fake.getEyeHeight(), eye.z,
                (float)Math.toDegrees(Math.atan2(-aim.x, aim.z)), (float)-Math.toDegrees(Math.asin(aim.y)));
        fake.setOldPosAndRot();
        var nearest = base.trace(player);
        t.assertTrue(nearest != null && crossing.getUniqueId().equals(Frame.tag(nearest).effortlessSable$get().owner()),
                "Fresh click should hit the intervening ship: " + nearest + " A=" + a + " B=" + b
                        + " eye=" + player.getEyePosition() + " look=" + player.getEyeDirection());
        var second = locked.trace(player);
        t.assertTrue(second != null && Frame.pos(second.blockPosition()).equals(plot.above()),"Second click switched ships");
        level.setBlockAndUpdate(start.above(), Blocks.GOLD_BLOCK.defaultBlockState());
        second = locked.trace(player);
        t.assertTrue(second != null && Frame.pos(second.blockPosition()).equals(plot.above()),"Terrain intercepted locked ray");
        var ctx = base.withNextInteraction(first);
        var placed = new BatchBuildSession(Effortless.getInstance(),player,ctx).commit();
        t.assertTrue(level.getBlockState(plot.above()).is(Blocks.BRICKS) && level.getBlockState(start.above()).is(Blocks.GOLD_BLOCK),
                "Placement failed: plot=" + level.getBlockState(plot.above()) + " terrain=" + level.getBlockState(start.above()) + " results="
                        + placed.getResults().stream().map(r -> ((dev.huskuraft.effortless.building.operation.block.BlockOperationResult)r).result()).toList());
        t.succeed();
        });
        });
    }

    private static BlockInteraction hit(BlockPos pos) {
        var p = new BlockPosition(pos.getX(),pos.getY(),pos.getZ());
        return new BlockInteraction(p.getCenter(),Direction.UP,p,false);
    }
}
