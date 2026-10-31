package com.fxmind.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Persists {@link Importance} as its numeric level so existing MQL clients keep working. */
@Converter(autoApply = true)
public class ImportanceConverter implements AttributeConverter<Importance, Byte> {

    @Override
    public Byte convertToDatabaseColumn(Importance importance) {
        return importance == null ? null : (byte) importance.level();
    }

    @Override
    public Importance convertToEntityAttribute(Byte level) {
        return level == null ? null : Importance.ofLevel(level);
    }
}
