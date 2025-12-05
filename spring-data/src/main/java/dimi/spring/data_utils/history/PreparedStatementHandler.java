package dimi.spring.data_utils.history;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class PreparedStatementHandler implements InvocationHandler
{
    private final PreparedStatement delegate;
    private final String sql;
    private final DatabaseStatementRecorder recorder;
    private final Map<Integer, Object> params = new HashMap<>();


    PreparedStatementHandler(PreparedStatement delegate, String sql, DatabaseStatementRecorder recorder)
    {
        this.delegate = delegate;
        this.sql = sql;
        this.recorder = recorder;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        String name = method.getName();
        // capture setX(int index, Object) methods (and variants) to record params
        if(name.startsWith("set") && args != null && args.length >= 2 && args[0] instanceof Integer index)
        {
            params.put(index, args[1]);
            return method.invoke(delegate, args);
        }
        // intercept execute* (no SQL param) - record using sql + params snapshot
        if((name.equals("execute") || name.equals("executeQuery") || name.equals("executeUpdate") || name.equals("addBatch"))
                        && (args == null || args.length == 0))
        {
            long start = System.currentTimeMillis();
            Map<Integer, Object> paramsSnapshot = Map.copyOf(params);
            try
            {
                Object res = method.invoke(delegate, args);
                long duration = System.currentTimeMillis() - start;
                recorder.record(new DatabaseStatement(sql, paramsSnapshot, start, duration, true, null, Thread.currentThread().getName()));
                return res;
            }
            catch(Throwable t)
            {
                long duration = System.currentTimeMillis() - start;
                recorder.record(new DatabaseStatement(sql, paramsSnapshot, start, duration, false, t.getMessage(), Thread.currentThread().getName()));
                throw t.getCause() == null ? t : t.getCause();
            }
        }
        return method.invoke(delegate, args);
    }
}
