import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager listHistory = Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();

    @Test
    void add() {
        Task task = new Task("Task1", "Description1");
        taskManager.createTask(task);

        listHistory.add(task);

        final ArrayList<Task> history = listHistory.getHistory();

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void add2Task() {
        Task task1 = new Task("Task1", "Description1");
        taskManager.createTask(task1);
        listHistory.add(task1);

        Task task2 = new Task("Task2", "Description1");
        taskManager.createTask(task2);
        listHistory.add(task2);

        final Task taskTest1 = listHistory.getHistory().getFirst();
        final Task taskTest2 = listHistory.getHistory().get(1);

        assertEquals(task2, taskTest2, "Задачи должны совпадать");
        assertEquals(task1, taskTest1, "После добавления задачи, предыдущая должна сохраниться");
    }

}