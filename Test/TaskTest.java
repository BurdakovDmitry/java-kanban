import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    static TaskManager taskManager = Managers.getDefault();

    @Test
    void createNewTask() {
        Task task = new Task("Task1", "Description1");
        taskManager.createTask(task);

        assertEquals("Task1", task.getNameTask(), "Имя должно совпадать");
        assertEquals("Description1", task.getDescription(), "Описание должно совпадать");
        assertEquals(StatusTask.NEW, task.getStatusTask(), "Статус должен совпадать");

        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListTask();

        assertNotNull(tasks, "Задачи не записываются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");

        assertEquals(StatusTask.NEW, task.getStatusTask(), "Статусы не совпадают.");
    }

    @AfterAll
    static void removeAllTask() {
        taskManager.removeAllTask();
    }
}
