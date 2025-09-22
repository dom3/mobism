package me.torture.mobism.mixin;

import me.torture.mobism.HarshWords;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public class VillagerMixin {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private GossipContainer gossips;
    @Unique
    private static final int UPDATE_INTERVAL = 60*20;
    @Unique
    private int mobism$lastUpdateTrades = UPDATE_INTERVAL;
    @Unique
    private boolean mobism$wantsToTrade = false;

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    public void finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        mobism$updateTrades();
    }

    @Inject(method = "tick", at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        if (villager.isAlive() && !villager.isTrading() && !villager.isSleeping()) {
            mobism$lastUpdateTrades++;
            if (mobism$lastUpdateTrades >= UPDATE_INTERVAL) {
                mobism$updateTrades();
            }
        }
    }

    @Unique
    private void mobism$updateTrades() {
        Villager villager = (Villager) (Object) this;
        if (villager.level().isClientSide) return;
        RandomSource random = villager.getRandom();
        mobism$lastUpdateTrades = 0;
        mobism$wantsToTrade = (random.nextInt(4) <= 1);
    }

    @Inject(method = "mobInteract", at=@At("HEAD"), cancellable = true)
    public void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.level().isClientSide) return;
        if (interactionHand != InteractionHand.MAIN_HAND) return;

        Villager villager = (Villager) (Object) this;
        Holder<VillagerProfession> profession = villager.getVillagerData().profession();
        if (profession.is(VillagerProfession.NONE) || profession.is(VillagerProfession.NITWIT)) return;
        if (!mobism$wantsToTrade) {
            HarshWords.sayHarshWord(player, villager.getName(), HarshWords.getVillagerHarshWord());
            cir.setReturnValue(InteractionResult.FAIL);
            cir.cancel();
        }
    }

    /**
     * @author domohyes
     * @reason mobist villagers
     */
    @Overwrite
    public int getPlayerReputation(Player player) {
        return this.gossips.getReputation(player.getUUID(), (gossipType) -> true)-100;
    }

}