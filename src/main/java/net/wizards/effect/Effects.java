package net.wizards.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.RemoveOnHit;
import net.spell_engine.api.effect.Synchronized;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.wizards.WizardsMod;

import java.util.ArrayList;

public class Effects {
    private static final ArrayList<Entry> entries = new ArrayList<>();
    public static class Entry {
        public final Identifier id;
        public final StatusEffect effect;
        public RegistryEntry<StatusEffect> registryEntry;

        public Entry(String name, StatusEffect effect) {
            this.id = Identifier.of(WizardsMod.ID, name);
            this.effect = effect;
            entries.add(this);
        }

        public void register() {
            registryEntry = Registry.registerReference(Registries.STATUS_EFFECT, id, effect);
        }

        public Identifier modifierId() {
            return Identifier.of(WizardsMod.ID, "effect." + id.getPath());
        }
    }

    public static Entry frozen = new Entry("frozen",
            new FrozenStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff)
            .setVulnerability(SpellSchools.FROST, new SpellPower.Vulnerability(0, 1F, 0F))
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    Identifier.of(WizardsMod.ID, "effect.frozen"),
                    -1F,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(EntityAttributes.GENERIC_JUMP_STRENGTH,
                    Identifier.of(WizardsMod.ID, "effect.frozen"),
                    -1F,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static Entry frostShield = new Entry("frost_shield",
            new FrostShieldStatusEffect(StatusEffectCategory.BENEFICIAL, 0x99ccff)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    Identifier.of(WizardsMod.ID, "effect.frost_shield"),
                    -0.5F,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static Entry frostSlowness = new Entry("frost_slowness",
            new FrozenStatusEffect(StatusEffectCategory.HARMFUL, 0x99ccff)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    Identifier.of(WizardsMod.ID, "effect.frost_slowness"),
                    -0.15,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static Entry arcaneCharge = new Entry("arcane_charge",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff4bdd)
    );

    public static void register() {
        /// Adding attribute modifier here due to relying on config value
        arcaneCharge.effect.addAttributeModifier(
                SpellSchools.ARCANE.attributeEntry,
                arcaneCharge.modifierId(),
                WizardsMod.tweaksConfig.value.arcane_charge_damage_per_stack,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        RemoveOnHit.configure(frozen.effect, true);
        Synchronized.configure(frostSlowness.effect, true);
        Synchronized.configure(frozen.effect, true);
        Synchronized.configure(frostShield.effect, true);
        Synchronized.configure(arcaneCharge.effect, true);

        for (var entry: entries) {
            entry.register();
        }
    }
}
