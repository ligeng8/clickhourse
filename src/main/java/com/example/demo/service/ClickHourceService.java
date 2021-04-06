package com.example.demo.service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.annotation.ColumnName;

@Service
public class ClickHourceService {

    @Resource
    DataSource clickHourceDataSource;

    public JSONArray queryByStatement(String sql) throws Exception {
        JSONArray result = new JSONArray();
        try {
            Connection connection = clickHourceDataSource.getConnection();
            Statement statement = connection.createStatement();
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
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public JSONArray queryByStatement(String sql, Object... params) throws Exception {
        JSONArray result = new JSONArray();
        try {
            Connection connection = clickHourceDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
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
            return result;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public <T> List<T> queryyPrepareStatement(Class<T> clazz, String sql, Object... params) throws Exception {
        List<T> result = new ArrayList<>();
        try {
            Connection connection = clickHourceDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            HashMap<String, Field> mapFields = new HashMap<>();
            Class clazz1 = clazz;
            do {
                Field[] declaredFields = clazz1.getDeclaredFields();
                for (Field field : declaredFields) {
                    Class<?> type = field.getType();
                    ColumnName annotation = field.getAnnotation(ColumnName.class);
                    if (annotation != null && StringUtils.hasText(annotation.value())) {
                        mapFields.put(annotation.value(), field);
                    } else {
                        mapFields.put(field.getName(), field);
                    }
                }
                clazz1 = clazz1.getSuperclass();
            } while (!Objects.equals(Object.class, clazz1));
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
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public JSONArray queryByStatement(String sql, List<?> params) throws Exception {
        JSONArray result = new JSONArray();
        try {
            Connection connection = clickHourceDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= params.size(); i++) {
                preparedStatement.setObject(i , params.get(i));
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
            return result;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
