import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void TaskManagerGetDefaultNotNull() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Объект должен быть проинициализирован");
    }

    @Test
    void HistoryManagerGetDefaultHistoryNotNull() {
        HistoryManager listHistory = Managers.getDefaultHistory();

        assertNotNull(listHistory, "Объект должен быть проинициализирован");
    }

}