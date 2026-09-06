package net.wizards.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.spell_engine.rpg_series.config.EffectConfig;
import net.spell_engine.api.effect.CustomStatusEffect;
import net.spell_engine.api.effect.Effects;
import net.spell_engine.api.effect.RemoveOnHit;
import net.spell_engine.api.effect.Synchronized;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;

import java.util.ArrayList;
import java.util.List;

public class WizardsEffects {
    /// 1.20.1 `EntityAttribute` has no `getIdAsString()` — resolve through the registry instead.
    private static String attributeId(net.minecraft.entity.attribute.EntityAttribute attribute) {
        return Registries.ATTRIBUTE.getId(attribute).toString();
    }

    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static Effects.Entry frozen = add(new Effects.Entry(new Identifier(WizardsMod.ID, "frozen"),
            "Frozen",
            "Prevents movement, removed upon taking damage, vulnerable to frost magic",
            new FrozenStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff)
                    .setVulnerability(SpellSchools.FROST, new SpellPower.Vulnerability(0, 1F, 0F)),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                                    -10,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                            // 1.20.1 has no living-entity jump-strength attribute (only HORSE_JUMP_STRENGTH),
                            // so the jump lock-out is carried entirely by LivingEntityFrozen#jump.
                    )
            )
    ));

    public static Effects.Entry frostShield = add(new Effects.Entry(new Identifier(WizardsMod.ID, "frost_shield"),
            "Frost Shield",
            "Blocks incoming attacks while active, but slows down movement",
            new FrostShieldStatusEffect(StatusEffectCategory.BENEFICIAL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                                    -0.5F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry frostSlowness = add(new Effects.Entry(new Identifier(WizardsMod.ID, "frost_slowness"),
            "Slowness",
            "Reduces movement speed",
            new FrozenStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                                    -0.15F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry evocation = add(new Effects.Entry(new Identifier(WizardsMod.ID, "arcane_evocation"),
            "Evocation",
            "Increases spell critical strike and speed, but also damage you take",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xcc44ff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    "spell_power:critical_chance",
                                    0.03F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            ),
                            new AttributeModifier(
                                    "spell_power:haste",
                                    0.03F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            ),
                            new AttributeModifier(
                                    "spell_engine:damage_taken",
                                    0.1F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry arcaneCharge = add(new Effects.Entry(new Identifier(WizardsMod.ID, "arcane_charge"),
            "Arcane Charge",
            "Increases Arcane spell damage done",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff4bdd),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellSchools.ARCANE.id.toString(),
                                    0.15F,
                                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                            )
                    )
            )
    ));

    public static void register(ConfigFile.Effects config) {
        RemoveOnHit.configure(frozen.effect, RemoveOnHit.Trigger.DIRECT_HIT, 1, 1);
        Synchronized.configure(frostSlowness.effect, true);
        Synchronized.configure(frozen.effect, true);
        Synchronized.configure(frostShield.effect, true);
        Synchronized.configure(evocation.effect, true);
        Synchronized.configure(arcaneCharge.effect, true);

        Effects.register(entries, config.effects);
    }
}
