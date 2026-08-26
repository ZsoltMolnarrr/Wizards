package net.wizards.effect;

import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.spell_engine.rpg_series.config.EffectConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

    public static Effects.Entry frozen = add(new Effects.Entry(Identifier.fromNamespaceAndPath(WizardsMod.ID, "frozen"),
            "Frozen",
            "Prevents movement, removed upon taking damage, vulnerable to frost magic",
            new FrozenStatusEffect(MobEffectCategory.HARMFUL, 0x99ccff)
                    .setVulnerability(SpellSchools.FROST, new SpellPower.Vulnerability(0, 1F, 0F)),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    Attributes.MOVEMENT_SPEED.getRegisteredName(),
                                    -10,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            new AttributeModifier(
                                    Attributes.JUMP_STRENGTH.getRegisteredName(),
                                    -10,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry frostShield = add(new Effects.Entry(Identifier.fromNamespaceAndPath(WizardsMod.ID, "frost_shield"),
            "Frost Shield",
            "Blocks incoming attacks while active, but slows down movement",
            new FrostShieldStatusEffect(MobEffectCategory.BENEFICIAL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    Attributes.MOVEMENT_SPEED.getRegisteredName(),
                                    -0.5F,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry frostSlowness = add(new Effects.Entry(Identifier.fromNamespaceAndPath(WizardsMod.ID, "frost_slowness"),
            "Slowness",
            "Reduces movement speed",
            new FrozenStatusEffect(MobEffectCategory.HARMFUL, 0x99ccff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    Attributes.MOVEMENT_SPEED.getRegisteredName(),
                                    -0.15F,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry evocation = add(new Effects.Entry(Identifier.fromNamespaceAndPath(WizardsMod.ID, "arcane_evocation"),
            "Evocation",
            "Increases spell critical strike and speed, but also damage you take",
            new CustomStatusEffect(MobEffectCategory.BENEFICIAL, 0xcc44ff),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    "spell_power:critical_chance",
                                    0.03F,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            new AttributeModifier(
                                    "spell_power:haste",
                                    0.03F,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            new AttributeModifier(
                                    "spell_engine:damage_taken",
                                    0.1F,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            )
                    )
            )
    ));

    public static Effects.Entry arcaneCharge = add(new Effects.Entry(Identifier.fromNamespaceAndPath(WizardsMod.ID, "arcane_charge"),
            "Arcane Charge",
            "Increases Arcane spell damage done",
            new CustomStatusEffect(MobEffectCategory.BENEFICIAL, 0xff4bdd),
            new EffectConfig(
                    List.of(
                            new AttributeModifier(
                                    SpellSchools.ARCANE.id.toString(),
                                    0.15F,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
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
