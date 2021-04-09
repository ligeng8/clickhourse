package com.example.demo.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.example.demo.annotation.ColumnName;
import com.example.demo.annotation.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

@Service
public class BaseClickHourceService {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    DataSource clickHourceDataSource;

    /**
     * ALTER TABLE 表名 UPDATE column1 = expr1 [, ...] WHERE filter_expr
     *
     * @param data
     * @param filter
     * @return
     * @throws Exception
     */
    public Boolean update(Object data, Object filter) throws Exception {
        String database = buildDatabase(data);
        String table = buildTableName(data);
        HashMap<String, Field> fieldsMap = querymapFields(data.getClass());
        ArrayList<String> columnNames = new ArrayList<>();
        ArrayList<String> filterNames = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER Table ").append(database).append(".").append(table).append(" UPDATE  ");
        StringBuilder columnValues = new StringBuilder();
        StringBuilder filterValues = new StringBuilder();
        for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();
            field.setAccessible(true);
            if (field.get(data) != null) {
                columnValues.append(columnName).append(" = ").append("?").append(" , ");
                columnNames.add(columnName);
            }
            if (field.get(filter) != null) {
                filterValues.append(columnName).append(" = ").append("?").append(" and ");
                filterNames.add(columnName);
            }
        }
        sql.append(columnValues.substring(0, columnValues.length() - 2)).append(" ")
                .append(filterValues.substring(0, filterValues.length() - 6));
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());) {
            prePareSetValues(false, data, fieldsMap, 0, columnNames, preparedStatement);
            prePareSetValues(true, filter, fieldsMap, columnNames.size(), filterNames, preparedStatement);
            return preparedStatement.execute();
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return false;
    }

    private void prePareSetValues(Boolean iswhere, Object data, HashMap<String, Field> fieldsMap, int skip,
                                  ArrayList<String> columnNames, PreparedStatement preparedStatement) throws Exception {
        for (int i = 0; i < columnNames.size(); i++) {
            Field field = fieldsMap.get(columnNames.get(i));
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(Date.class) && field.get(data) != null) {
                Date date = (Date) field.get(data);
                preparedStatement.setObject(i + 1 + skip, new Timestamp(date.getTime()));
            } else {
                Object value = field.get(data);
                //Decimal 字段不能作为NUmber处理，负责会报 No operation equals between Decimal(32, 10) and Float64:
                if (value instanceof BigDecimal && iswhere) {
                    preparedStatement.setObject(i + 1 + skip, value.toString());
                } else {
                    preparedStatement.setObject(i + 1 + skip, value);
                }

            }
        }
    }

    public int delete(Object data) throws IllegalAccessException, SQLException {
        String database = buildDatabase(data);
        String table = buildTableName(data);
        HashMap<String, Field> fieldsMap = querymapFields(data.getClass());
        ArrayList<String> columnNames = new ArrayList<>();
        StringBuilder sql1 = new StringBuilder();
        sql1.append("ALTER Table ").append(database).append(".").append(table).append(" delete where ");
        for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();
            field.setAccessible(true);
            if (field.get(data) != null) {
                //Decimal 字段不能作为NUmber处理，负责会报 No operation equals between Decimal(32, 10) and Float64:
                sql1.append(columnName).append(" = ").append("?").append(" and ");
                columnNames.add(columnName);
            }
        }
        String sql = sql1.substring(0, sql1.length() - 5);
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());) {
            prePareSetValues(false, data, fieldsMap, 0, columnNames, preparedStatement);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return 0;
    }


    public Boolean insert(Object data) throws IllegalAccessException, SQLException {
        String database = buildDatabase(data);
        String table = buildTableName(data);
        HashMap<String, Field> fieldsMap = querymapFields(data.getClass());
        ArrayList<String> columnNames = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(database).append(".").append(table).append(" (");
        StringBuilder values = new StringBuilder();
        for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();
            field.setAccessible(true);
            if (field.get(data) != null) {
                sql.append(columnName).append(",");
                values.append("?").append(",");
                columnNames.add(columnName);
            }
        }
        sql.deleteCharAt(sql.length() - 1).append(") VALUES (").append(values.deleteCharAt(values.length() - 1)).append(")");
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());) {
            prePareSetValues(false, data, fieldsMap, 0, columnNames, preparedStatement);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return false;
    }


    public JSONArray queryByStatement(String sql) throws Exception {
        JSONArray result = new JSONArray();
        try (Connection connection = clickHourceDataSource.getConnection();
             Statement statement = connection.createStatement();) {
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }
            return result;
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return result;
    }

    public Boolean excuteByStatement(String sql) throws Exception {
        try (Connection connection = clickHourceDataSource.getConnection();
             Statement statement = connection.createStatement();) {
            return statement.execute(sql);
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return false;
    }

    public Boolean excuteByPrepareStatement(String sql, Object... params) throws Exception {
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                preparedStatement.setObject(i, params[i - 1]);
            }
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return false;
    }

    public JSONArray queryByPreparedStatement(String sql, Object... params) {
        JSONArray result = new JSONArray();
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                preparedStatement.setObject(i, params[i - 1]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return result;
    }

    public <T> List<T> queryByStatement(Class<T> clazz, String sql, Object... params) throws Exception {
        List<T> result = new ArrayList<>();
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                preparedStatement.setObject(i, params[i - 1]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return buildResult(clazz, result, resultSet);
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return result;
    }

    public <T> List<T> queryPrepareStatement(Class<T> clazz, String sql, Object... params) throws Exception {
        List<T> result = new ArrayList<>();
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return buildResult(clazz, result, resultSet);
        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return result;
    }

    private <T> List<T> buildResult(Class<T> clazz, List<T> result, ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        HashMap<String, Field> mapFields = querymapFields(clazz);
        while (resultSet.next()) {
            T instance = clazz.newInstance();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Field field = mapFields.get(columnName);
                field.setAccessible(true);
                Object data = resultSet.getObject(i);
                Class<?> type = field.getType();
                if (type.isInstance(data)) {
                    field.set(instance, data);
                }
            }
            result.add(instance);
        }
        return result;
    }

    private <T> HashMap<String, Field> querymapFields(Class<T> clazz) {
        HashMap<String, Field> mapFields = new HashMap<>();
        Class clazz1 = clazz;
        do {
            Field[] declaredFields = clazz1.getDeclaredFields();
            for (Field field : declaredFields) {
                Class<?> type = field.getType();
                ColumnName annotation = field.getAnnotation(ColumnName.class);
                if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
                    mapFields.put(annotation.value(), field);
                } else {
                    mapFields.put(field.getName(), field);
                }
            }
            clazz1 = clazz1.getSuperclass();
        } while (!Objects.equals(Object.class, clazz1));
        return mapFields;
    }

    public JSONArray queryByStatement(String sql, List<?> params) throws Exception {
        JSONArray result = new JSONArray();
        try (Connection connection = clickHourceDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.size(); i++) {
                preparedStatement.setObject(i, params.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }

        } catch (Exception e) {
            logger.error("未知异常", e);
        }
        return result;
    }


    private String buildDatabase(Object data) {
        Table annotation = data.getClass().getAnnotation(Table.class);
        String database = "default";
        if (annotation != null && StringUtils.isNotBlank(annotation.database())) {
            database = annotation.database();
        }
        return database;
    }

    private String buildTableName(Object data) {
        Table annotation = data.getClass().getAnnotation(Table.class);
        String table;
        if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
            table = annotation.value();
        } else {
            String simpleName = data.getClass().getSimpleName();
            final char[] chars = simpleName.toCharArray();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                if (Character.isUpperCase(chars[i])) {
                    if (i > 0) sb.append("_");
                    sb.append(Character.toLowerCase(chars[i]));
                } else {
                    sb.append(chars[i]);
                }
            }
            table = sb.toString();
        }
        return table;
    }

}
