package de.aelpecyem.runes.common.reg;

import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.util.RegistryUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class RunesSounds {
    public static final SoundEvent ITEM_ROCK_THROWN = new SoundEvent(RunesMod.id("item.rock.throw"));
    public static void init(){
        RegistryUtil.register(Registry.SOUND_EVENT, "item.rock.throw", ITEM_ROCK_THROWN);
    }
}
