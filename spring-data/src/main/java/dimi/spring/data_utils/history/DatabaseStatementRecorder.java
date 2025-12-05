package dimi.spring.data_utils.history;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class DatabaseStatementRecorder
{
    private final Deque<DatabaseStatement> buffer;
    private final ReentrantLock lock = new ReentrantLock();


    public DatabaseStatementRecorder()
    {
        this(1000); // default capacity
    }


    public DatabaseStatementRecorder(int capacity)
    {
        this.buffer = new ArrayDeque<>(Math.max(16, capacity));
    }


    public void record(DatabaseStatement stmt)
    {
        lock.lock();
        try
        {
            if(buffer.size() == buffer.toArray().length && buffer.size() == ((ArrayDeque<DatabaseStatement>)buffer).size())
            {
                // defensive — though ArrayDeque does not expose capacity; we'll simply maintain size cap below
            }
            // maintain bounded size: if full remove oldest
            int max = 1000; // default; optionally make configurable
            if(buffer.size() >= max)
            {
                buffer.removeFirst();
            }
            buffer.addLast(stmt);
        }
        finally
        {
            lock.unlock();
        }
    }


    // get last N statements (most recent first)
    public List<DatabaseStatement> getLast(int n)
    {
        if(n <= 0)
        {
            return List.of();
        }
        lock.lock();
        try
        {
            List<DatabaseStatement> out = new ArrayList<>(Math.min(n, buffer.size()));
            var it = buffer.descendingIterator();
            while(it.hasNext() && out.size() < n)
            {
                out.add(it.next());
            }
            return out;
        }
        finally
        {
            lock.unlock();
        }
    }


    public List<DatabaseStatement> getAll()
    {
        lock.lock();
        try
        {
            return new ArrayList<>(buffer);
        }
        finally
        {
            lock.unlock();
        }
    }


    public void clear()
    {
        lock.lock();
        try
        {
            buffer.clear();
        }
        finally
        {
            lock.unlock();
        }
    }
}
