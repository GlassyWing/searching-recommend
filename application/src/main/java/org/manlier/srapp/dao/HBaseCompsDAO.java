package org.manlier.srapp.dao;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.manlier.srapp.entities.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.manlier.common.schemes.HBaseComponentsQuery.*;


@Repository
public class HBaseCompsDAO implements CompsDAO {

    private Connection connection;

    @Inject
    public HBaseCompsDAO( Connection connection) {
        this.connection = connection;
    }


    @Override
    public Optional<Component> getComponentByName(String compName) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            Get get = new Get(Bytes.toBytes(compName));
            Result result = table.get(get);
            if (result.isEmpty()) {
                return Optional.empty();
            }
            String id = Bytes.toString(result.getRow());
            String desc = Bytes.toString(result.getValue(
                    INFO_COLUMNFAMILY, DESC_QUALIFIER
            ));
            Component component = new Component(id, desc);
            return Optional.of(component);
        }
    }

    @Override
    public void addComponent(Component component) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            Put put = new Put(Bytes.toBytes(component.getId()));
            put.addColumn(INFO_COLUMNFAMILY, DESC_QUALIFIER, Bytes.toBytes(component.getDesc()));
            table.put(put);
        }
    }

    @Override
    public int addComponents(List<Component> components) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            List<Put> puts = components.parallelStream()
                    .map(component -> {
                        Put put = new Put(Bytes.toBytes(component.getId()));
                        put.addColumn(INFO_COLUMNFAMILY, DESC_QUALIFIER, Bytes.toBytes(component.getDesc()));
                        return put;
                    }).collect(Collectors.toList());
            table.put(puts);
            return puts.size();
        }
    }

    @Override
    public void updateComponent(Component component) throws IOException {
        this.addComponent(component);
    }

    @Override
    public int updateComponents(List<Component> components) throws IOException {
        return this.addComponents(components);
    }

    @Override
    public void deleteComponentByName(String compName) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            Delete delete = new Delete(Bytes.toBytes(compName));
            delete.addFamily(INFO_COLUMNFAMILY);
            table.delete(delete);
        }
    }

    @Override
    public int deleteComponentsByName(String... names) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(TABLE_NAME))) {
            List<Delete> deletes = Arrays.asList(names).parallelStream()
                    .map(name -> {
                        Delete delete = new Delete(Bytes.toBytes(name));
                        delete.addFamily(INFO_COLUMNFAMILY);
                        return delete;
                    }).collect(Collectors.toList());
            table.delete(deletes);
            return names.length;
        }
    }
}
