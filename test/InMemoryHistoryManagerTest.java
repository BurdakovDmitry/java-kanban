import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();

    @Test
    void add() {
        Task task = new Task("Task1", StatusTask.NEW, "Description1");
        taskManager.createTask(task);

        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void add2Task() {
        Task task1 = new Task("Task1", StatusTask.NEW, "Description1");
        taskManager.createTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2", StatusTask.NEW, "Description1");
        taskManager.createTask(task2);
        historyManager.add(task2);

        final Task taskTest1 = historyManager.getHistory().getFirst();
        final Task taskTest2 = historyManager.getHistory().get(1);

        assertEquals(task2, taskTest2, "Задачи должны совпадать");
        assertEquals(task1, taskTest1, "После добавления задачи, предыдущая должна сохраниться");
    }

    @Test
    void UpdateTask1AfterAddingTask2() {
        Task task1 = new Task("Task1", StatusTask.NEW, "Description1");
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", StatusTask.NEW, "Description1");
        taskManager.createTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        final List<Task> listHistory = historyManager.getHistory();
        final Task taskTest1 = historyManager.getHistory().getFirst();
        final Task taskTest2 = historyManager.getHistory().get(1);

        assertEquals(2, listHistory.size(), "После обновления задачи, длина списка не меняется");
        assertEquals(task2, taskTest1, "После обновления задачи, старая версия удаляется");
        assertEquals(task1, taskTest2, "После обновления задачи, она добавляется в конец списка");
    }

    @Test
    void removeTaskById() {
        Task task1 = new Task("Task1", StatusTask.NEW, "Description1");
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", StatusTask.NEW, "Description1");
        taskManager.createTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);

        final List<Task> listHistory = historyManager.getHistory();
        final Task taskTest1 = historyManager.getHistory().getFirst();
        final Task taskTest2 = historyManager.getHistory().get(1);

        assertEquals(2, listHistory.size(), "Должно добавиться 2 задачи");
        assertEquals(task1, taskTest1, "Задачи должны совпадать");
        assertEquals(task2, taskTest2, "Задачи должны совпадать");

        historyManager.remove(task1.getId());

        final List<Task> listHistoryRemove = historyManager.getHistory();
        final Task taskTest3 = historyManager.getHistory().getFirst();

        assertEquals(1, listHistoryRemove.size(), "После удаления задачи, длина списка уменьшается");
        assertEquals(task2, taskTest3, "Задачи должны совпадать");
    }
}