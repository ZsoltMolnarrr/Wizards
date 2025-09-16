package net.wizards.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.config.EffectConfig;
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
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static Effects.Entry frozen = add(new Effects.Entry(Identifier.of(WizardsMod.ID, "frozen"),
            "Frozen",
            "Frozen in place",
            new FrozenStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff)
                    .setVulnerability(SpellSchools.FROST, new SpellPower.Vulnerability(0, 1F, 0F)),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                                    -10,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_JUMP_STRENGTH.getIdAsString(),
                                    -10,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry frostShield = add(new Effects.Entry(Identifier.of(WizardsMod.ID, "frost_shield"),
            "Frost Shield",
            "Protected by frost",
            new FrostShieldStatusEffect(StatusEffectCategory.BENEFICIAL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                                    -0.5F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry frostSlowness = add(new Effects.Entry(Identifier.of(WizardsMod.ID, "frost_slowness"),
            "Slowness",
            "Slowed by frost magic",
            new FrozenStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                                    -0.15F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry arcaneCharge = add(new Effects.Entry(Identifier.of(WizardsMod.ID, "arcane_charge"),
            "Arcane Charge",
            "Empowered by arcane magic",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff4bdd),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellSchools.ARCANE.id.toString(),
                                    0.15F,
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static void register(ConfigFile.Effects config) {
        RemoveOnHit.configure(frozen.effect, RemoveOnHit.Trigger.DIRECT_HIT, 1, 1);
        Synchronized.configure(frostSlowness.effect, true);
        Synchronized.configure(frozen.effect, true);
        Synchronized.configure(frostShield.effect, true);
        Synchronized.configure(arcaneCharge.effect, true);

        Effects.register(entries, config.effects);
    }
}
