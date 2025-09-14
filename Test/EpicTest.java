import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    static TaskManager taskManager;
    static int epicId;
    static Epic epic;
    static Subtask subtask1;
    static Subtask subtask2;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
        epic = new Epic("Epic1", "Description1");
        taskManager.createEpic(epic);
        epicId = epic.getId();

        subtask1 = new Subtask("Subtask1", "Description1");
        subtask1.setIdEpic(epic.getId());
        taskManager.createSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Description1");
        subtask2.setIdEpic(epic.getId());
        taskManager.createSubtask(subtask2);
    }

    @Test
    void createNewEpic() {
        assertEquals("Epic1", epic.getNameTask(), "Имя должно совпадать");
        assertEquals("Description1", epic.getDescription(), "Описание должно совпадать");
        assertEquals(StatusTask.NEW, epic.getStatusTask(), "Статус должен совпадать");

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