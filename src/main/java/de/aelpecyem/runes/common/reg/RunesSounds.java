package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.util.RegistryUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class RunesSounds {
    public static final SoundEvent ITEM_ROCK_THROWN = new SoundEvent(RunesMod.id("item.rock.throw"));
    public static final SoundEvent STASIS_HIT = new SoundEvent(RunesMod.id("entity.stasis.hit"));
    public static void init(){
        RegistryUtil.register(Registry.SOUND_EVENT, "item.rock.throw", ITEM_ROCK_THROWN);
        RegistryUtil.register(Registry.SOUND_EVENT, "entity.stasis.hit", STASIS_HIT);
    }
}
