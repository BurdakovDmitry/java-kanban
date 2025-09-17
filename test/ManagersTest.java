import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void TaskManagerGetDefaultNotNull() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Объект должен быть проинициализирован");
    }

    @Test
    void HistoryManagerGetDefaultHistoryNotNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Объект должен быть проинициализирован");
    }
}