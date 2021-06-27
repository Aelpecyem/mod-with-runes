package de.aelpecyem.runes.common.item;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.aelpecyem.runes.RunesMod;
import de.aelpecyem.runes.common.misc.RuneParticleEffect;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class BifrostAmuletItem extends TrinketItem {
    public static final List<ColorScheme> COLOR_SCHEMES = new ArrayList<>();
    public static final EntityAttributeModifier DAMAGE_MOD = new EntityAttributeModifier(UUID.fromString("d2b5f6b2-cede-4d73-b8fb-a4bf1b25dedf"), "Bifrost Amulet Damage Modifier", 2, EntityAttributeModifier.Operation.ADDITION);
    public static final EntityAttributeModifier SPEED_MOD = new EntityAttributeModifier(UUID.fromString("e73cf52d-679d-4a21-937e-7107a64ec9cd"), "Bifrost Amulet Speed Modifier", 0.06F, EntityAttributeModifier.Operation.ADDITION);
    public static final EntityAttributeModifier ARMOR_TOUGHNESS_MOD = new EntityAttributeModifier(UUID.fromString("0f27755e-759c-455e-ae36-2511d52acd21"), "Bifrost Amulet Armor Toughness Modifier", 4, EntityAttributeModifier.Operation.ADDITION);
    private static final Multimap<EntityAttribute, EntityAttributeModifier> MODIFIERS = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);
    static {
        MODIFIERS.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_MOD);
        MODIFIERS.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, DAMAGE_MOD);
        MODIFIERS.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_MOD);
    }
    public BifrostAmuletItem() {
        super(new FabricItemSettings().group(RunesMod.GROUP).maxCount(1));
        registerColorScheme(ColorScheme.BI);
        registerColorScheme(new ColorScheme(0.1F, "queer", new Vec3f(1F, 0F, 0F), new Vec3f(1F, 0.5F, 0F),
                new Vec3f(1F, 1F, 0F), new Vec3f(0F, 1F, 0F), new Vec3f(0F, 1F, 0.5F), new Vec3f(0F, 1F, 1F),
                new Vec3f(0F, 0F, 1F), new Vec3f(0.5F, 0F, 1), new Vec3f(1F, 0F, 1F)));
        registerColorScheme(new ColorScheme(0.2F, "lesbian", new Vec3f(1F, 1F, 1F), new Vec3f(0.9F, 0.25F, 0.15F),
                new Vec3f(0.95F, 0.3F, 0.4F)));
        registerColorScheme(new ColorScheme(0.3F, "gay", new Vec3f(1F, 1F, 1F), new Vec3f(0.01F, 0.85F, 0.5F),
                new Vec3f(0.05F, 0.3F, 0.9F)));
        registerColorScheme(new ColorScheme(0.4F, "trans", new Vec3f(1F, 1F, 1F), new Vec3f(0.6F, 0.9F, 1F),
                new Vec3f(1F, 0.6F, 0.9F)));
        registerColorScheme(new ColorScheme(0.5F, (string) -> string.toLowerCase().contains("enby") || string.toLowerCase().contains("nonbinary"),
                new Vec3f(1F, 1F, 1F), new Vec3f(0.25F, 0.25F, 0.25F), new Vec3f(1F, 1F, 0F), new Vec3f(0.5F, 0F, 1F)));
        registerColorScheme(new ColorScheme(0.6F, (string) -> string.toLowerCase().contains("ace") || string.toLowerCase().contains("asexual"),
                new Vec3f(1F, 1F, 1F), new Vec3f(0.5F, 0.5F, 0.5F), new Vec3f(0.5F, 0F, 1F)));
        registerColorScheme(new ColorScheme(0.7F, (string) -> string.toLowerCase().contains("aro") || string.toLowerCase().contains("aromantic"),
                new Vec3f(1F, 1F, 1F), new Vec3f(0F, 0.8F, 0F), new Vec3f(0.5F, 0.5F, 0.5F)));
        registerColorScheme(new ColorScheme(0.8F, "pan",
                new Vec3f(1F, 1F, 0F), new Vec3f(0F, 1F, 1F), new Vec3f(1F, 0F, 1F)));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        boolean active = isActive(entity);
        if (active){
            if (entity.world.isClient && entity.age % 4 == 0){
                entity.world.addParticle(new RuneParticleEffect(getColorScheme(stack).getColor(entity.getRandom()), 1.5F),
                        entity.getParticleX(1.25), entity.getRandomBodyY(), entity.getParticleZ(1.25),
                        0, 0, 0);
            }
            entity.flyingSpeed *= 2;
        }
        if (entity.getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_MOD.getId()) && !active){
            entity.getAttributes().removeModifiers(MODIFIERS);
        }else if (active){
            entity.getAttributes().addTemporaryModifiers(MODIFIERS);
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifierMultimap = super.getModifiers(stack, slot, entity, uuid);
        modifierMultimap.putAll(MODIFIERS);
        return modifierMultimap;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("tooltip." + RunesMod.MOD_ID + ".bifrost_amulet").formatted(Formatting.AQUA));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public static ColorScheme getColorScheme(ItemStack stack){
        return COLOR_SCHEMES.stream().filter(colorScheme -> colorScheme.matches(stack.getName().asString())).findFirst().orElse(ColorScheme.BI);
    }

    public boolean isActive(LivingEntity entity){
        return entity.getHealth() / entity.getMaxHealth() > 0.75F;
    }

    public static ColorScheme registerColorScheme(ColorScheme colorScheme){
        COLOR_SCHEMES.add(colorScheme);
        return colorScheme;
    }

    public static class ColorScheme {
        public static final ColorScheme BI = new ColorScheme(0, "bi",
                new Vec3f(1, 0, 1), new Vec3f(0, 0, 0.8F), new Vec3f(0.6F, 0, 0.8F));
        private final float threshold;
        private final Predicate<String> prefixPredicate;
        private final List<Vec3f> colors;

        public ColorScheme(float threshold, String prefix, Vec3f... colors) {
            this.threshold = threshold;
            this.colors = Arrays.asList(colors);
            this.prefixPredicate = (string) -> string.toLowerCase().contains(prefix);
        }

        public ColorScheme(float threshold, Predicate<String> prefixPredicate, Vec3f... colors) {
            this.threshold = threshold;
            this.colors = Arrays.asList(colors);
            this.prefixPredicate = prefixPredicate;
        }
        public Vec3f getColor(Random random) {
            return colors.get(random.nextInt(colors.size()));
        }

        public float getThreshold() {
            return threshold;
        }

        public boolean matches(String name){
            return prefixPredicate.test(name);
        }
    }
}
