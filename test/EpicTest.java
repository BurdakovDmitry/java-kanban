import interfaces.TaskManager;
import managers.Managers;
import tasks.Subtask;
import tasks.Epic;
import enums.StatusTask;
import enums.TypeTask;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EpicTest {
    static TaskManager taskManager;
    static int epicId;
    static Epic epic;
    static Subtask subtask1;
    static Subtask subtask2;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
        epic = new Epic("Epic1", StatusTask.NEW,"Description1");
        taskManager.createEpic(epic);
        epicId = epic.getId();

        subtask1 = new Subtask("Subtask1", StatusTask.NEW, "Description1");
        subtask1.setIdEpic(epic.getId());
        subtask1.setDuration(Duration.ofMinutes(60));
        subtask1.setStartTime(LocalDateTime.of(2025,10,10,12,30));
        taskManager.createSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", StatusTask.NEW, "Description1");
        subtask2.setIdEpic(epic.getId());
        subtask2.setDuration(Duration.ofMinutes(60));
        subtask2.setStartTime(LocalDateTime.of(2025,10,11,12,30));
        taskManager.createSubtask(subtask2);
    }

    @Test
    void createNewEpic() {
        assertEquals("Epic1", epic.getNameTask(), "Имя должно совпадать");
        assertEquals("Description1", epic.getDescription(), "Описание должно совпадать");
        assertEquals(StatusTask.NEW, epic.getStatusTask(), "Статус должен совпадать");
        assertEquals(TypeTask.EPIC, epic.getType(), "Тип должен совпадать");
        assertEquals(120, epic.getDuration().toMinutes(), "Продолжительность должна совпадать");
        assertEquals("12:30 10.10.2025", epic.getStartTime().format(TaskTest.formatter),
                "Дата и время начала задачи должны совпадать");
        assertEquals("13:30 11.10.2025", epic.getEndTime().format(TaskTest.formatter),
                "Дата и время окончания задачи должны совпадать");

        subtask1.setStartTime(null);

        assertEquals(120, epic.getDuration().toMinutes(), "Продолжительность должна совпадать");
        assertEquals("12:30 11.10.2025", epic.getStartTime().format(TaskTest.formatter),
                "Дата и время начала задачи должны совпадать");
        assertEquals("13:30 11.10.2025", epic.getEndTime().format(TaskTest.formatter),
                "Дата и время окончания задачи должны совпадать");


        subtask2.setStartTime(null);

        assertNull(epic.getStartTime(), "Дата и время начала задачи должны быть null");
        assertNull(epic.getEndTime(), "Дата и время окончания задачи должны быть null");

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getListEpic();

        assertNotNull(epics, "Задачи не записываются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addSubtaskToListSubtasksToEpic() {
        final List<Subtask> subtasks = epic.getListSubtask();

        assertNotNull(subtasks, "Задачи не записываются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void StatusEpicInProgressIfSubtaskInProgress() {
        subtask1.setStatusTask(StatusTask.IN_PROGRESS);
        subtask2.setStatusTask(StatusTask.IN_PROGRESS);

        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask(), "Статусы не совпадают.");
    }

    @Test
    void StatusEpicDoneIfSubtaskDone() {
        subtask1.setStatusTask(StatusTask.DONE);
        subtask2.setStatusTask(StatusTask.DONE);

        assertEquals(StatusTask.DONE, epic.getStatusTask(), "Статусы не совпадают.");
    }

    @Test
    void StatusEpicInProgressIfSubtaskNewAndDone() {
        subtask1.setStatusTask(StatusTask.NEW);
        subtask2.setStatusTask(StatusTask.DONE);

        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask(), "Статусы не совпадают.");
    }

    @Test
    void StatusEpicNewIfSubtaskNew() {
        subtask1.setStatusTask(StatusTask.NEW);
        subtask2.setStatusTask(StatusTask.NEW);

        assertEquals(StatusTask.NEW, epic.getStatusTask(), "Статусы не совпадают.");
    }

    @AfterAll
    static void StatusEpicNewIfListSubtaskNull() {
        final List<Subtask> subtasks = epic.getListSubtask();
        subtasks.clear();

        assertEquals(StatusTask.NEW, epic.getStatusTask(), "Статусы не совпадают.");
    }
}