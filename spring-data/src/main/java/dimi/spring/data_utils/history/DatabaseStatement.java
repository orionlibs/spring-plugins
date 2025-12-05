package dimi.spring.data_utils.history;

import java.util.Map;

public record DatabaseStatement(String sql,
                                Map<Integer, Object> parameters,
                                long startTimeMillis,
                                long durationMillis,
                                boolean success,
                                String errorMessage,
                                String threadName)
{
}
