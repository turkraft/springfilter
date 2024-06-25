package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.converter.StringCustomDateConverter;
import com.turkraft.springfilter.converter.StringCustomObjectIdConverter.CustomObjectId;
import com.turkraft.springfilter.converter.StringCustomUUIDConverter.CustomUUID;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

@Service
class FieldTypeResolverImpl implements FieldTypeResolver {

    @Override
    public Class<?> resolve(Class<?> klass, String path) {
        return normalize(getField(klass, path));
    }

    @Override
    public Field getField(Class<?> klass, final String path) {

        String[] fieldNames = path.split("\\.");

        Field lastField = null;

        for (String fieldName : fieldNames) {

            lastField = ReflectionUtils.findField(klass, fieldName);

            if (lastField != null) {
                klass = normalize(lastField);
            } else {
                throw new IllegalArgumentException("Could not find field '" + fieldName + "' in " + klass);
            }

        }

        return lastField;

    }

    private Class<?> normalize(Field field) {

        if (field.isAnnotationPresent(Id.class) && field.getType().equals(String.class)) {
            return CustomObjectId.class;
        }

        if (field.getType().equals(UUID.class)) {
            return CustomUUID.class;
        }
        if (field.getType().equals(LocalDateTime.class)
                || field.getType().equals(ZonedDateTime.class)
                || field.getType().equals(LocalDate.class)
                || field.getType().equals(java.util.Date.class)
                || field.getType().equals(Instant.class)) {
            return StringCustomDateConverter.CustomDate.class;
        }

        if (Collection.class.isAssignableFrom(field.getType())) {
            return getFirstTypeParameterOf(field);
        } else if (field.getType().isArray()) {
            return field.getType().getComponentType();
        } else {
            return field.getType();
        }

    }

    public boolean isIterable(Class<?> klass, String path) {
        return isIterable(getField(klass, path));
    }

    private boolean isIterable(Field field) {
        return Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray();
    }

    private Class<?> getFirstTypeParameterOf(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

}
