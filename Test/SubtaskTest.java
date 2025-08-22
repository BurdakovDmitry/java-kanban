import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    static TaskManager taskManager = Managers.getDefault();

    @Test
    void createNewSubtask() {
        Epic epic = new Epic("Epic1", "Description1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Description1");
        subtask.setIdEpic(epic.getId());
        taskManager.createSubtask(subtask);

        assertEquals("Subtask1", subtask.getNameTask(), "Имя должно совпадать");
        assertEquals("Description1", subtask.getDescription(), "Описание должно совпадать");
        assertEquals(StatusTask.NEW, subtask.getStatusTask(), "Статус должен совпадать");

        final int subtaskId = subtask.getId();

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.getListSubtask();

        assertNotNull(subtasks, "Задачи не записываются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают.");

        assertEquals(StatusTask.NEW, subtask.getStatusTask(), "Статусы не совпадают.");
    }
}