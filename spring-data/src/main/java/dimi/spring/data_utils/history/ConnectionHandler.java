package dimi.spring.data_utils.history;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class ConnectionHandler implements InvocationHandler
{
    private final Connection delegate;
    private final DatabaseStatementRecorder recorder;


    ConnectionHandler(Connection delegate, DatabaseStatementRecorder recorder)
    {
        this.delegate = delegate;
        this.recorder = recorder;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        String name = method.getName();
        // intercept prepareStatement(String sql, ...)
        if("prepareStatement".equals(name) && args != null && args.length >= 1 && args[0] instanceof String sql)
        {
            PreparedStatement ps = (PreparedStatement)method.invoke(delegate, args);
            return createPreparedStatementProxy(ps, sql);
        }
        // intercept createStatement(...) -> wrap the returned Statement
        if("createStatement".equals(name))
        {
            Statement s = (Statement)method.invoke(delegate, args);
            return createStatementProxy(s);
        }
        // Delegate other calls
        return method.invoke(delegate, args);
    }


    private Statement createStatementProxy(Statement real)
    {
        InvocationHandler h = new StatementHandler(real, recorder);
        return (Statement)Proxy.newProxyInstance(Statement.class.getClassLoader(), new Class[] {Statement.class}, h);
    }


    private PreparedStatement createPreparedStatementProxy(PreparedStatement real, String sql)
    {
        InvocationHandler h = new PreparedStatementHandler(real, sql, recorder);
        return (PreparedStatement)Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(),
                        new Class[] {PreparedStatement.class}, h);
    }
}
