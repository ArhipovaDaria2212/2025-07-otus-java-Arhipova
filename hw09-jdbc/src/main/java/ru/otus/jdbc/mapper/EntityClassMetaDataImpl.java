package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import lombok.SneakyThrows;
import ru.otus.jdbc.mapper.annotation.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    @SneakyThrows
    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        this.constructor = clazz.getDeclaredConstructor();
        this.allFields = List.of(clazz.getDeclaredFields());
        this.idField = allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No @Id field found in class " + clazz.getName()));
        this.fieldsWithoutId =
                allFields.stream().filter(field -> !field.equals(idField)).toList();
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
