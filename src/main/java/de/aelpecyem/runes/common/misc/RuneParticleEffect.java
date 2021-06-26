package de.aelpecyem.runes.common.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.aelpecyem.runes.common.reg.RunesParticles;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;

public class RuneParticleEffect extends AbstractDustParticleEffect {
   public static final ParticleEffect.Factory<RuneParticleEffect> PARAMETERS_FACTORY;

    public RuneParticleEffect(Vec3f vec3f, float f) {
        super(vec3f, f);
    }

    @Override
    public ParticleType<?> getType() {
        return RunesParticles.RUNE;
    }

    static {
        PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
            public RuneParticleEffect read(ParticleType<RuneParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
                Vec3f vec3f = AbstractDustParticleEffect.readColor(stringReader);
                stringReader.expect(' ');
                float f = stringReader.readFloat();
                return new RuneParticleEffect(vec3f, f);
            }

            public RuneParticleEffect read(ParticleType<RuneParticleEffect> particleType, PacketByteBuf packetByteBuf) {
                return new RuneParticleEffect(AbstractDustParticleEffect.readColor(packetByteBuf), packetByteBuf.readFloat());
            }
        };
    }
}
