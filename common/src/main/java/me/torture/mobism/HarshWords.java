package me.torture.mobism;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class HarshWords {
    public static Component[] HARSH_WORDS = {
            Component.translatable("entity.harsh_words.1"),
            Component.translatable("entity.harsh_words.2"),
            Component.translatable("entity.harsh_words.3"),
            Component.translatable("entity.harsh_words.4"),
            Component.translatable("entity.harsh_words.5"),
            Component.translatable("entity.harsh_words.6"),
            Component.translatable("entity.harsh_words.7"),
            Component.translatable("entity.harsh_words.8"),
            Component.translatable("entity.harsh_words.9"),
            Component.translatable("entity.harsh_words.10"),
            Component.translatable("entity.harsh_words.11"),
            Component.translatable("entity.harsh_words.12"),
            Component.translatable("entity.harsh_words.13"),
    };

    public static Component[] VILLAGER_HARSH_WORDS = {
            Component.translatable("entity.harsh_words.villager.1"),
            Component.translatable("entity.harsh_words.villager.2"),
            Component.translatable("entity.harsh_words.villager.3"),
            Component.translatable("entity.harsh_words.villager.4"),
    };

    public static Component[] BABY_ASK_HARSH_WORDS = {
            Component.translatable("entity.harsh_words.baby.ask.1"),
            Component.translatable("entity.harsh_words.baby.ask.2")
    };

    public static Component[] BABY_HARSH_WORDS = {
            Component.translatable("entity.harsh_words.baby.1"),
            Component.translatable("entity.harsh_words.baby.2"),
            Component.translatable("entity.harsh_words.baby.3"),
            Component.translatable("entity.harsh_words.baby.4"),
    };

    public static String getHarshWord(@NotNull Player player) {
        return HARSH_WORDS[(int) (Math.random() * HARSH_WORDS.length)].getString()
                .replace("%p%", player.getDisplayName().getString());
    }

    public static String getVillagerHarshWord() {
        return VILLAGER_HARSH_WORDS[(int) (Math.random() * VILLAGER_HARSH_WORDS.length)].getString();
    }

    public static String getBabyAskHarshWord() {
        return BABY_ASK_HARSH_WORDS[(int) (Math.random() * BABY_ASK_HARSH_WORDS.length)].getString();
    }

    public static String getBabyHarshWord() {
        return BABY_HARSH_WORDS[(int) (Math.random() * BABY_HARSH_WORDS.length)].getString();
    }

    public static void sayHarshWord(@NotNull Player player, Component sender, String content) {
        player.displayClientMessage(
                Component.literal("<").append(sender).append("> %s".formatted(
                        content
                )),
                false
        );
    }
}
