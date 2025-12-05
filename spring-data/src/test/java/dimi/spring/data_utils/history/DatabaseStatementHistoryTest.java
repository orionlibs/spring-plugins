package dimi.spring.data_utils.history;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DbRecorderTestApplication.class)
@ActiveProfiles("test")
public class DatabaseStatementHistoryTest
{
    @Autowired UserDAO userDAO;
    @Autowired DatabaseStatementRecorder recorder;


    @Test
    void repositoryOperationsProduceRecordedStatements()
    {
        recorder.clear();
        assertThat(recorder.getAll()).isEmpty();
        UserModel saved = userDAO.save(new UserModel("Alice"));
        assertThat(saved.getId()).isNotNull();
        userDAO.findById(saved.getId()).orElseThrow();
        userDAO.save(new UserModel("Bob"));
        long count = userDAO.count();
        assertThat(count).isGreaterThanOrEqualTo(2);
        List<DatabaseStatement> last = recorder.getLast(10);
        assertThat(last).isNotEmpty();
        last.forEach(s -> System.out.println(s));
        boolean hasInsertOrSelect = last.stream()
                        .map(DatabaseStatement::sql)
                        .anyMatch(sql -> sql != null && (sql.toLowerCase().contains("insert") || sql.toLowerCase().contains("select")));
        assertThat(hasInsertOrSelect).isTrue();
    }
}
