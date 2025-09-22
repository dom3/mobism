package me.torture.mobism.neoforge;

import me.torture.mobism.Mobism;
import net.neoforged.fml.common.Mod;

@Mod(Mobism.MOD_ID)
public final class MobismNeoForge {
    public MobismNeoForge() {
        // Run our common setup.
        Mobism.init();
    }
}
