package dimi.spring.utils.rest_recorder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class RESTCallRecorder
{
    private static final int DEFAULT_CAPACITY = 1000;
    private final Deque<RESTCall> buffer;
    private final ReentrantLock lock = new ReentrantLock();
    private final int capacity;


    public RESTCallRecorder()
    {
        this(DEFAULT_CAPACITY);
    }


    public RESTCallRecorder(int capacity)
    {
        this.capacity = Math.max(8, capacity);
        this.buffer = new ArrayDeque<>(this.capacity);
    }


    public void record(RESTCall call)
    {
        lock.lock();
        try
        {
            if(buffer.size() >= capacity)
            {
                buffer.removeFirst();
            }
            buffer.addLast(call);
        }
        finally
        {
            lock.unlock();
        }
    }


    /**
     * Get last N calls (most recent first).
     */
    public List<RESTCall> getLast(int n)
    {
        if(n <= 0)
        {
            return List.of();
        }
        lock.lock();
        try
        {
            List<RESTCall> out = new ArrayList<>(Math.min(n, buffer.size()));
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


    public List<RESTCall> getAll()
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


    public int capacity()
    {
        return capacity;
    }
}
