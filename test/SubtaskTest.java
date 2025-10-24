import interfaces.TaskManager;
import managers.Managers;
import tasks.Subtask;
import tasks.Epic;
import enums.StatusTask;
import enums.TypeTask;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubtaskTest {
    static TaskManager taskManager = Managers.getDefault();

    @Test
    void createNewSubtask() {
        Epic epic = new Epic("Epic1", StatusTask.NEW, "Description1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask1", StatusTask.NEW, "Description1");
        subtask.setIdEpic(epic.getId());
        subtask.setDuration(Duration.ofMinutes(60));
        subtask.setStartTime(LocalDateTime.of(2025,10,10,12,30));
        taskManager.createSubtask(subtask);

        assertEquals("Subtask1", subtask.getNameTask(), "Имя должно совпадать");
        assertEquals("Description1", subtask.getDescription(), "Описание должно совпадать");
        assertEquals(StatusTask.NEW, subtask.getStatusTask(), "Статус должен совпадать");
        assertEquals(TypeTask.SUBTASK, subtask.getType(), "Тип должен совпадать");
        assertEquals(60, subtask.getDuration().toMinutes(), "Продолжительность должна совпадать");
        assertEquals("12:30 10.10.2025", subtask.getStartTime().format(TaskTest.formatter),
                "Дата и время начала задачи должны совпадать");
        assertEquals("13:30 10.10.2025", subtask.getEndTime().format(TaskTest.formatter),
                "Дата и время окончания задачи должны совпадать");

        subtask.setStartTime(null);

        assertNull(subtask.getStartTime(), "Дата и время начала задачи должны быть null");
        assertNull(subtask.getEndTime(), "Дата и время окончания задачи должны быть null");

        final int subtaskId = subtask.getId();

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getListSubtask();

        assertNotNull(subtasks, "Задачи не записываются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают.");

        assertEquals(StatusTask.NEW, subtask.getStatusTask(), "Статусы не совпадают.");
    }
}