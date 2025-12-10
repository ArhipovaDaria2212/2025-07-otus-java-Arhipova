package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    @SneakyThrows
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createObject(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    @SneakyThrows
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    var objects = new ArrayList<T>();
                    try {
                        while (rs.next()) {
                            objects.add(createObject(rs));
                        }
                        return objects;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T object) {
        try {
            List<Object> values = entityClassMetaData.getFieldsWithoutId().stream()
                    .map(field -> {
                        try {
                            field.setAccessible(true);
                            return field.get(object);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), values);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T object) {
        try {
            Object id = entityClassMetaData.getIdField().get(object);
            List<Object> values = entityClassMetaData.getFieldsWithoutId().stream()
                    .map(field -> {
                        try {
                            field.setAccessible(true);
                            return field.get(object);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), List.of(values, id));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @SneakyThrows
    private T createObject(ResultSet rs) {
        Constructor<T> constructor = entityClassMetaData.getConstructor();
        constructor.setAccessible(true);
        T object = constructor.newInstance();
        entityClassMetaData.getAllFields().forEach(field -> {
            try {
                field.setAccessible(true);
                field.set(object, rs.getObject(field.getName()));
            } catch (IllegalAccessException | SQLException e) {
                throw new DataTemplateException(e);
            }
        });
        return object;
    }
}
