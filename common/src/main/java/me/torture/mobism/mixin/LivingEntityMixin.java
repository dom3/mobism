package me.torture.mobism.mixin;

import me.torture.mobism.HarshWords;
import me.torture.mobism.Mobism;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin{
    @Unique
    public int mobism$lastCheck = 0;
    @Unique
    private static final int CHECK_INTERVAL = 20;

    @Unique
    public List<ServerPlayer> mobism$cachedPlayers = List.of();
    @Unique
    public int mobism$lastCachedPlayers = 0;
    @Unique
    private static final int CACHE_INTERVAL = 40;

    @Unique
    public HashMap<Player, Integer> mobism$lastTalkedTo = new HashMap<>();
    @Unique
    private static final int TALK_INTERVAL = 20*20;

    @Unique
    public HashMap<Player, Integer> mobism$lastTalkedToBaby = new HashMap<>();
    @Unique
    private static final int TALK_INTERVAL_BABY = 2*20;

    @Unique
    private static final int TALK_DISTANCE = 10;

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {

        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity.getType() == EntityType.PLAYER) return;

        mobism$lastCachedPlayers++;
        if (mobism$lastCachedPlayers >= CACHE_INTERVAL) {
            mobism$cacheNearbyPlayers();
        }

        mobism$lastTalkedToBaby.forEach( (player, integer) -> mobism$lastTalkedToBaby.replace(player, integer+1));

        for (HashMap.Entry<Player, Integer> entry : mobism$lastTalkedToBaby.entrySet()) {
            if (entry.getValue() > TALK_INTERVAL) {
                mobism$lastTalkedToBaby.remove(entry.getKey());
                HarshWords.sayHarshWord(entry.getKey(), livingEntity.getName(), HarshWords.getBabyHarshWord());
            }
        }

        mobism$lastCheck++;
        if (mobism$lastCheck >= CHECK_INTERVAL && !mobism$cachedPlayers.isEmpty()) {
            mobism$check();
        }
    }

    @Unique
    private void mobism$cacheNearbyPlayers() {
        mobism$lastCachedPlayers = 0;
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        MinecraftServer server = livingEntity.getServer();
        if (server == null) return;
        mobism$cachedPlayers = server.getPlayerList().getPlayers();
    }

    @Unique
    public void mobism$check() {
        mobism$lastCheck = 0;
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        for (ServerPlayer player : mobism$cachedPlayers) {
            if (player.position().closerThan(livingEntity.position(), TALK_DISTANCE)) {
                if (livingEntity instanceof OwnableEntity ownableEntity) {
                    if (Objects.equals(ownableEntity.getOwner(), player)) return;
                }

                Integer lastTalked = mobism$lastTalkedTo.get(player);

                if (lastTalked == null) {
                    lastTalked = 0;
                    mobism$talkTo(player);
                } else {
                    lastTalked++;
                    if (lastTalked > TALK_INTERVAL) {
                        lastTalked = 0;
                        mobism$talkTo(player);
                    }
                }

                mobism$lastTalkedTo.put(player, lastTalked);
            }
        }
    }

    /**
     * Does not reset last talked to
     * @param serverPlayer the player to talk to
     */
    @Unique
    public void mobism$talkTo(@NotNull ServerPlayer serverPlayer) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity.isBaby()) {
            HarshWords.sayHarshWord(serverPlayer, Component.literal("Baby ").append(livingEntity.getName()), HarshWords.getBabyAskHarshWord());
            mobism$lastTalkedToBaby.put(serverPlayer, 0);
            return;
        }

        HarshWords.sayHarshWord(serverPlayer, livingEntity.getName(), HarshWords.getHarshWord(serverPlayer));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {

    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {

    }
}
