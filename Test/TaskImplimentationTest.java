import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskImplimentationTest {
    static TaskManager taskManager;
    static Task task;
    static Epic epic;
    static Subtask subtask;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
        task = new Task("Task1", "Description1");
        taskManager.createTask(task);

        epic = new Epic("Epic1", "Description1");
        taskManager.createEpic(epic);

        subtask = new Subtask("Subtask1", "Description1");
        subtask.setIdEpic(epic.getId());
        taskManager.createSubtask(subtask);
    }

    @Test
    void equalityId() {
        assertEquals(1, task.getId(), "Id не равны");
        assertEquals(2, epic.getId(), "Id не равны");
        assertEquals(3, subtask.getId(), "Id не равны");
    }

    @Test
    void updateTask() {
        task.setNameTask("Task1.0");
        task.setDescription("Description1.0");
        task.setStatusTask(StatusTask.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals("Task1.0", task.getNameTask(), "Имя не обновилось");
        assertEquals("Description1.0", task.getDescription(), "Описание не обновилось");
        assertEquals(StatusTask.IN_PROGRESS, task.getStatusTask(), "Статус не обновился");
    }

    @Test
    void updateSubtask() {
        subtask.setNameTask("Subtask1.0");
        subtask.setDescription("Description1.0");
        subtask.setStatusTask(StatusTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask);

        assertEquals("Subtask1.0", subtask.getNameTask(), "Имя не обновилось");
        assertEquals("Description1.0", subtask.getDescription(), "Описание не обновилось");
        assertEquals(StatusTask.IN_PROGRESS, subtask.getStatusTask(), "Статусы не совпадают");
    }

    @Test
    void updateEpic() {
        subtask.setStatusTask(StatusTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask);

        epic.setNameTask("Epic1.0");
        epic.setDescription("Description1.0");
        taskManager.updateEpic(epic);

        assertEquals("Epic1.0", epic.getNameTask(), "Имя не обновилось");
        assertEquals("Description1.0", epic.getDescription(), "Описание не обновилось");
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask(), "Статусы не совпадают");
    }

    @Test
    void removeTaskById() {
        Task task2 = new Task("Task2", "Description1");
        taskManager.createTask(task2);

        final List<Task> tasks1 = taskManager.getListTask();
        assertEquals(2, tasks1.size(), "Задач должно быть 2");

        taskManager.removeTaskById(task2.getId());

        final List<Task> tasks2 = taskManager.getListTask();
        assertEquals(1, tasks2.size(), "Задача должна удалиться");
    }

    @Test
    void removeSubtaskById() {
        Subtask subtask2 = new Subtask("Subtask2", "Description1");
        subtask2.setIdEpic(epic.getId());
        taskManager.createSubtask(subtask2);

        final List<Subtask> subtasks1 = taskManager.getListSubtask();
        assertEquals(2, subtasks1.size(), "Задач должно быть 2");

        taskManager.removeSubtaskById(subtask2.getId());

        final List<Subtask> subtasks2 = taskManager.getListSubtask();
        assertEquals(1, subtasks2.size(), "Задача должна удалиться");
    }

    @Test
    void removeEpicById() {
        Epic epic2 = new Epic("Epic2", "Description1");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Subtask2", "Description1");
        subtask3.setIdEpic(epic2.getId());
        taskManager.createSubtask(subtask3);

        final List<Epic> epics1 = taskManager.getListEpic();
        final List<Subtask> subtasks1 = taskManager.getListSubtask();

        assertEquals(2, epics1.size(), "Задач должно быть 2");
        assertEquals(2, subtasks1.size(), "Задач должно быть 2");

        taskManager.removeEpicById(epic2.getId());

        final List<Epic> epics2 = taskManager.getListEpic();
        final List<Subtask> subtasks2 = taskManager.getListSubtask();

        assertEquals(1, epics2.size(), "Задача должна удалиться");
        assertEquals(1, subtasks2.size(), "Задача должна удалиться");
    }

    @Test
    void getListSubtaskToEpic() {
        final List<Subtask> subtasks = taskManager.getListSubtaskToEpic(epic.getId());

        assertNotNull(subtasks, "В списке должна бить задача");
        assertEquals(1, subtasks.size(),"В списке должна бить 1 задача");
    }

    @AfterAll
    static void removeAllTask() {
        taskManager.removeAllTask();
        assertEquals(0, TaskImplimentation.tasks.size(), "Список задач не пуст");
    }

    @AfterAll
    static void removeAllEpic() {
        taskManager.removeAllEpic();
        assertEquals(0, TaskImplimentation.epics.size(), "Список эпиков не пуст");
        assertEquals(0, TaskImplimentation.subtasks.size(), "Список подзадач не пуст");
    }
}