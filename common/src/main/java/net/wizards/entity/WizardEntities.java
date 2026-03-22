package net.wizards.entity;

public class WizardEntities {
    public static EntityConfig defaultEntityConfig() {
        var c = new EntityConfig();
        var entry = new EntityConfig.Entry();
        entry.common = new EntityConfig.CommonAttributes(30, 0.25, 4);
        c.entries.put(FrostElementalEntity.ID.getPath(), entry);
        return c;
    }
}
