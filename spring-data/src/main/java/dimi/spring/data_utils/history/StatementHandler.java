package dimi.spring.data_utils.history;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Map;

public class StatementHandler implements InvocationHandler
{
    private final Statement delegate;
    private final DatabaseStatementRecorder recorder;


    StatementHandler(Statement delegate, DatabaseStatementRecorder recorder)
    {
        this.delegate = delegate;
        this.recorder = recorder;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        String name = method.getName();
        // intercept execute/executeQuery/executeUpdate methods that take SQL string
        boolean hasSqlParam = args != null && args.length > 0 && args[0] instanceof String;
        if(hasSqlParam && (name.startsWith("execute") || name.equals("addBatch")))
        {
            String sql = (String)args[0];
            long start = System.currentTimeMillis();
            try
            {
                Object res = method.invoke(delegate, args);
                long duration = System.currentTimeMillis() - start;
                recorder.record(new DatabaseStatement(sql, Map.of(), start, duration, true, null, Thread.currentThread().getName()));
                return res;
            }
            catch(Throwable t)
            {
                long duration = System.currentTimeMillis() - start;
                recorder.record(new DatabaseStatement(sql, Map.of(), start, duration, false, t.getMessage(), Thread.currentThread().getName()));
                throw t.getCause() == null ? t : t.getCause();
            }
        }
        return method.invoke(delegate, args);
    }
}
