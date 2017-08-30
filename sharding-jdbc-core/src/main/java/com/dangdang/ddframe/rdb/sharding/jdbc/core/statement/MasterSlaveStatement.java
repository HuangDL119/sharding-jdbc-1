/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.jdbc.core.statement;

import com.dangdang.ddframe.rdb.sharding.jdbc.adapter.AbstractStatementAdapter;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.connection.MasterSlaveConnection;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;

/**
 * Statement that support master-slave.
 * 
 * @author zhangliang
 */
@RequiredArgsConstructor
@Getter
public final class MasterSlaveStatement extends AbstractStatementAdapter {
    
    private final MasterSlaveConnection connection;
    
    private final int resultSetType;
    
    private final int resultSetConcurrency;
    
    private final int resultSetHoldability;
    
    @Getter(AccessLevel.NONE)
    private Statement routedStatement;
    
    public MasterSlaveStatement(final MasterSlaveConnection connection) {
        this(connection, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }
    
    public MasterSlaveStatement(final MasterSlaveConnection connection, final int resultSetType, final int resultSetConcurrency) {
        this(connection, resultSetType, resultSetConcurrency, ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }
    
    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        Collection<Connection> connections = connection.getConnection(sql);
        Preconditions.checkState(1 == connections.size());
        routedStatement = connections.iterator().next().createStatement();
        return routedStatement.executeQuery(sql);
    }
    
    @Override
    public int executeUpdate(final String sql) throws SQLException {
        Collection<Connection> connections = connection.getConnection(sql);
        Preconditions.checkState(1 == connections.size());
        routedStatement = connections.iterator().next().createStatement();
        return routedStatement.executeUpdate(sql);
    }
    
    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        Collection<Connection> connections = connection.getConnection(sql);
        Preconditions.checkState(1 == connections.size());
        routedStatement = connections.iterator().next().createStatement();
        return routedStatement.executeUpdate(sql, autoGeneratedKeys);
    }
    
    @Override
    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        Collection<Connection> connections = connection.getConnection(sql);
        Preconditions.checkState(1 == connections.size());
        routedStatement = connections.iterator().next().createStatement();
        return routedStatement.executeUpdate(sql, columnIndexes);
    }
    
    @Override
    public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
        Collection<Connection> connections = connection.getConnection(sql);
        Preconditions.checkState(1 == connections.size());
        routedStatement = connections.iterator().next().createStatement();
        return routedStatement.executeUpdate(sql, columnNames);
    }
    
    @Override
    public boolean execute(final String sql) throws SQLException {
        boolean result = false;
        for (Connection each : connection.getConnection(sql)) {
            routedStatement = each.createStatement();
            result = routedStatement.execute(sql);
        }
        return result;
    }
    
    @Override
    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        boolean result = false;
        for (Connection each : connection.getConnection(sql)) {
            routedStatement = each.createStatement();
            result = routedStatement.execute(sql, autoGeneratedKeys);
        }
        return result;
    }
    
    @Override
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        boolean result = false;
        for (Connection each : connection.getConnection(sql)) {
            routedStatement = each.createStatement();
            result = routedStatement.execute(sql, columnIndexes);
        }
        return result;
    }
    
    @Override
    public boolean execute(final String sql, final String[] columnNames) throws SQLException {
        boolean result = false;
        for (Connection each : connection.getConnection(sql)) {
            routedStatement = each.createStatement();
            result = routedStatement.execute(sql, columnNames);
        }
        return result;
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return routedStatement.getGeneratedKeys();
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        return routedStatement.getResultSet();
    }
    
    @Override
    protected Collection<Statement> getRoutedStatements() {
        return Collections.singletonList(routedStatement);
    }
}
