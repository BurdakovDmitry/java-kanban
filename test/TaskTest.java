import Interfases.TaskManager;
import Managers.Managers;
import Tasks.Task;
import Enum.StatusTask;
import Enum.TypeTask;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskTest {
    static TaskManager taskManager = Managers.getDefault();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @Test
    void createNewTask() {
        Task task = new Task("Task1", StatusTask.NEW, "Description1");
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.of(2025,10,10,12,30));
        taskManager.createTask(task);

        assertEquals("Task1", task.getNameTask(), "Имя должно совпадать");
        assertEquals("Description1", task.getDescription(), "Описание должно совпадать");
        assertEquals(StatusTask.NEW, task.getStatusTask(), "Статус должен совпадать");
        assertEquals(TypeTask.TASK, task.getType(), "Тип должен совпадать");
        assertEquals(60, task.getDuration().toMinutes(), "Продолжительность должна совпадать");
        assertEquals("12:30 10.10.2025", task.getStartTime().format(formatter),
                "Дата и время начала задачи должны совпадать");
        assertEquals("13:30 10.10.2025", task.getEndTime().format(formatter),
                "Дата и время окончания задачи должны совпадать");

        task.setStartTime(null);

        assertNull(task.getStartTime(), "Дата и время начала задачи должны быть null");
        assertNull(task.getEndTime(), "Дата и время окончания задачи должны быть null");

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
}