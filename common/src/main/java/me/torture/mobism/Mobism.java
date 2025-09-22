package me.torture.mobism;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

public final class Mobism {
    public static final String MOD_ID = "mobism";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Mobism init()");
    }
}
