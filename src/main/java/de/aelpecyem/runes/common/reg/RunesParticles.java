package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.common.misc.RuneParticleEffect;
import de.aelpecyem.runes.util.RegistryUtil;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class RunesParticles {
    public static final ParticleType<RuneParticleEffect> RUNE = FabricParticleTypes.complex(RuneParticleEffect.PARAMETERS_FACTORY);
    public static void init(){
        RegistryUtil.register(Registry.PARTICLE_TYPE, "rune", RUNE);
    }
}
