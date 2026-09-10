package com.bgsoftware.superiorskyblock.nms.v26_2_folia.dragon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.dimension.end.DragonRespawnStage;
import net.minecraft.world.level.levelgen.feature.EndSpikeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndSpikeConfiguration;
import org.bukkit.event.entity.EntityRemoveEvent;

import java.util.List;

public final class IslandDragonRespawn {
    private IslandDragonRespawn() {
    }

    public static void tick(IslandDragonFight fight, List<EndCrystal> crystals, int time) {
        ServerLevel level = fight.level;
        BlockPos beam = fight.beamPosition();
        switch (fight.respawnStage) {
            case START -> {
                crystals.forEach(crystal -> crystal.setBeamTarget(beam));
                fight.setRespawnStage(DragonRespawnStage.PREPARING_TO_SUMMON_PILLARS);
            }
            case PREPARING_TO_SUMMON_PILLARS -> {
                if (time >= 100) {
                    fight.setRespawnStage(DragonRespawnStage.SUMMONING_PILLARS);
                } else if (time == 0 || time == 50 || time == 51 || time == 52 || time >= 95) {
                    level.levelEvent(LevelEvent.ANIMATION_DRAGON_SUMMON_ROAR, beam, 0);
                }
            }
            case SUMMONING_PILLARS -> {
                int index = time / 40;
                if (index >= fight.getSpikes().size()) {
                    if (time % 40 == 0)
                        fight.setRespawnStage(DragonRespawnStage.SUMMONING_DRAGON);
                    return;
                }
                EndSpikeFeature.EndSpike spike = fight.getSpikes().get(index);
                if (time % 40 == 0) {
                    BlockPos spikeBeam = new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ());
                    crystals.forEach(crystal -> crystal.setBeamTarget(spikeBeam));
                } else if (time % 40 == 39) {
                    for (BlockPos pos : BlockPos.betweenClosed(
                            new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10),
                            new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10)))
                        level.removeBlock(pos, false);
                    level.explode(null, spike.getCenterX() + 0.5, spike.getHeight(),
                            spike.getCenterZ() + 0.5, 5.0F, Level.ExplosionInteraction.BLOCK);
                    Feature.END_SPIKE.place(new EndSpikeConfiguration(true, List.of(spike), beam), level,
                            level.getChunkSource().getGenerator(), RandomSource.create(),
                            new BlockPos(spike.getCenterX(), 45, spike.getCenterZ()));
                    fight.markSpikeCrystals(spike);
                }
            }
            case SUMMONING_DRAGON -> {
                if (time >= 100) {
                    fight.setRespawnStage(DragonRespawnStage.END);
                    fight.resetSpikeCrystals();
                    for (EndCrystal crystal : crystals) {
                        crystal.setBeamTarget(null);
                        level.explode(crystal, crystal.getX(), crystal.getY(), crystal.getZ(),
                                6.0F, Level.ExplosionInteraction.NONE);
                        crystal.discard(EntityRemoveEvent.Cause.EXPLODE);
                    }
                } else if (time == 0) {
                    crystals.forEach(crystal -> crystal.setBeamTarget(beam));
                } else if (time >= 80 || time < 5) {
                    level.levelEvent(LevelEvent.ANIMATION_DRAGON_SUMMON_ROAR, beam, 0);
                }
            }
            case END -> {
            }
        }
    }
}
