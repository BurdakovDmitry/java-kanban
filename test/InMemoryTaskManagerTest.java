import interfaces.TaskManager;
import managers.Managers;
import tasks.*;
import enums.StatusTask;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    static TaskManager taskManager;
    static Task task;
    static Epic epic;
    static Subtask subtask1;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
        task = new Task("Task1", StatusTask.NEW, "Description1");
        taskManager.createTask(task);

        epic = new Epic("Epic1", StatusTask.NEW, "Description1");
        taskManager.createEpic(epic);

        subtask1 = new Subtask("Subtask1", StatusTask.NEW, "Description1");
        subtask1.setIdEpic(epic.getId());
        taskManager.createSubtask(subtask1);
    }

    @Test
    void equalityId() {
        assertEquals(1, task.getId(), "Id не равны");
        assertEquals(2, epic.getId(), "Id не равны");
        assertEquals(3, subtask1.getId(), "Id не равны");
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
        subtask1.setNameTask("Subtask1.0");
        subtask1.setDescription("Description1.0");
        subtask1.setStatusTask(StatusTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        assertEquals("Subtask1.0", subtask1.getNameTask(), "Имя не обновилось");
        assertEquals("Description1.0", subtask1.getDescription(), "Описание не обновилось");
        assertEquals(StatusTask.IN_PROGRESS, subtask1.getStatusTask(), "Статусы не совпадают");
    }

    @Test
    void updateEpic() {
        subtask1.setStatusTask(StatusTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        epic.setNameTask("Epic1.0");
        epic.setDescription("Description1.0");
        taskManager.updateEpic(epic);

        assertEquals("Epic1.0", epic.getNameTask(), "Имя не обновилось");
        assertEquals("Description1.0", epic.getDescription(), "Описание не обновилось");
        assertEquals(StatusTask.IN_PROGRESS, epic.getStatusTask(), "Статусы не совпадают");
    }

    @Test
    void removeTaskById() {
        Task task2 = new Task("Task2", StatusTask.NEW, "Description1");
        taskManager.createTask(task2);

        final List<Task> tasks1 = taskManager.getListTask();
        assertEquals(2, tasks1.size(), "Задач должно быть 2");

        taskManager.removeTaskById(task2.getId());

        final List<Task> tasks2 = taskManager.getListTask();
        assertEquals(1, tasks2.size(), "Задача должна удалиться");
    }

    @Test
    void removeSubtaskById() {
        Subtask subtask2 = new Subtask("Subtask2", StatusTask.NEW, "Description2");
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
        Epic epic2 = new Epic("Epic2", StatusTask.NEW, "Description2");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Subtask3", StatusTask.NEW, "Description3");
        subtask3.setIdEpic(epic2.getId());
        subtask3.setStartTime(LocalDateTime.of(2025,10,5,12,30));
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

        assertNotNull(subtasks, "В списке должна быть задача");
        assertEquals(1, subtasks.size(),"В списке должна быть 1 задача");
    }

    @Test
    void getPrioritizedTaskElseNullTime() {
        final List<Task> sortedList = taskManager.getPrioritizedTasks();

        assertEquals(0, sortedList.size(), "Список должен быть пуст");
    }

    @Test
    void getPrioritizedOneTask() {
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.of(2025,10,10,12,30));
        taskManager.updateTask(task);

        final List<Task> sortedList = taskManager.getPrioritizedTasks();

        assertFalse(sortedList.isEmpty(), "Список не должен быть пуст");
        assertEquals(1, sortedList.size(),"В списке должна быть 1 задача");
    }

    @Test
    void getPrioritizedTasks() {
        subtask1.setDuration(Duration.ofMinutes(60));
        subtask1.setStartTime(LocalDateTime.of(2025,10,5,12,30));
        taskManager.updateSubtask(subtask1);

        final List<Task> sortedList = taskManager.getPrioritizedTasks();

        assertFalse(sortedList.isEmpty(), "Список не должен быть пуст");
        assertEquals(2, sortedList.size(),"В списке должно быть 2 задачи");

        final Task newSubtask = sortedList.getFirst();
        final Task newTask = sortedList.getLast();

        assertEquals("12:30 05.10.2025", newSubtask.getStartTime().format(TaskTest.formatter),
                "Дата и время начала задачи должны совпадать");
        assertEquals("12:30 10.10.2025", newTask.getStartTime().format(TaskTest.formatter),
                "Дата и время начала задачи должны совпадать");
    }

    @AfterAll
    static void removeAllTask() {
        taskManager.removeAllTask();
        assertEquals(0, taskManager.getListTask().size(), "Список задач не пуст");
    }

    @AfterAll
    static void removeAllEpic() {
        taskManager.removeAllEpic();
        assertEquals(0, taskManager.getListEpic().size(), "Список эпиков не пуст");
        assertEquals(0, taskManager.getListSubtask().size(), "Список подзадач не пуст");
    }
}