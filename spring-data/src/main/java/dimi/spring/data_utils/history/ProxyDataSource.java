package dimi.spring.data_utils.history;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class ProxyDataSource implements DataSource
{
    private final DataSource delegate;
    private final DatabaseStatementRecorder recorder;


    public ProxyDataSource(DataSource delegate, DatabaseStatementRecorder recorder)
    {
        this.delegate = delegate;
        this.recorder = recorder;
    }


    @Override
    public Connection getConnection() throws SQLException
    {
        Connection conn = delegate.getConnection();
        return createConnectionProxy(conn);
    }


    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        Connection conn = delegate.getConnection(username, password);
        return createConnectionProxy(conn);
    }


    private Connection createConnectionProxy(Connection real)
    {
        InvocationHandler h = new ConnectionHandler(real, recorder);
        return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] {Connection.class}, h);
    }


    // --- delegate other DataSource methods ---
    @Override public PrintWriter getLogWriter() throws SQLException
    {
        return delegate.getLogWriter();
    }


    @Override public void setLogWriter(PrintWriter out) throws SQLException
    {
        delegate.setLogWriter(out);
    }


    @Override public void setLoginTimeout(int seconds) throws SQLException
    {
        delegate.setLoginTimeout(seconds);
    }


    @Override public int getLoginTimeout() throws SQLException
    {
        return delegate.getLoginTimeout();
    }


    @Override public Logger getParentLogger()
    {
        try
        {
            return delegate.getParentLogger();
        }
        catch(SQLFeatureNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return delegate.unwrap(iface);
    }


    @Override public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return delegate.isWrapperFor(iface);
    }
}
