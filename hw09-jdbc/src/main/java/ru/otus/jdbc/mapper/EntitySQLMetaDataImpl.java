package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> entityClassMetaDataClient;
    private final String table;

    public <T> EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaDataClient) {
        this.entityClassMetaDataClient = entityClassMetaDataClient;
        this.table = entityClassMetaDataClient.getName().toLowerCase();
    }

    @Override
    public String getSelectAllSql() {
        return String.format("select * from %s", table);
    }

    @Override
    public String getSelectByIdSql() {
        String id = entityClassMetaDataClient.getIdField().getName();
        return String.format("select * from %s where %s = ?", table, id);
    }

    @Override
    public String getInsertSql() {
        List<Field> fields = entityClassMetaDataClient.getFieldsWithoutId();
        String columns = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
        String placeholders = fields.stream().map(field -> "?").collect(Collectors.joining(", "));

        return String.format("insert into %s(%s) values (%s)", table, columns, placeholders);
    }

    @Override
    public String getUpdateSql() {
        String id = entityClassMetaDataClient.getIdField().getName();
        String fields = entityClassMetaDataClient.getFieldsWithoutId().stream()
                .map(Field::getName)
                .collect(Collectors.joining(" = ?, "));
        return String.format("update %s set %s where %s = ?", table, fields, id);
    }
}
