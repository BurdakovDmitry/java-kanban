import tasks.Task;
import exceptions.ManagerSaveException;
import exceptions.ManagerAddTaskException;
import enums.StatusTask;
import enums.TypeTask;
import managers.FileBackedTaskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    static File file;
    static Task task = new Task("Task1", StatusTask.NEW, "Description1");

    @BeforeEach
    void createTemporaryFile() {
        try {
            file = File.createTempFile("testFile", ".txt");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания файла");
        }
    }

    @Test
    void loadingEmptyFile() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(manager, "Менеджер должен быть проинициализирован");
        assertTrue(file.exists(), "Файл должен быть создан");
        assertEquals(0, file.length(), "Файл должен быть пустым");
    }

    @Test
    void savingTaskFile() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        assertTrue(file.exists(), "Файл должен быть создан");
        assertEquals(0, file.length(), "Файл должен быть пустым");

        manager.createTask(task);
        final List<String> listTask = Files.readAllLines(file.toPath());

        assertNotNull(listTask, "Список не должен быть пустым");
        assertEquals(2, listTask.size(), "Строк должно быть 2");
    }

    @Test
    void loadingTaskFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.of(2025, 10, 10, 12, 30));
        manager.createTask(task);

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        final List<Task> listTask = newManager.getListTask();

        assertNotNull(listTask, "Список не должен быть пустым");
        assertEquals(1, listTask.size(), "Должна быть 1 задача");

        final Task task1 = listTask.getFirst();

        assertEquals(1, task1.getId(), "id должны совпадать");
        assertEquals("Task1", task1.getNameTask(), "Имя должно совпадать");
        assertEquals(StatusTask.NEW, task1.getStatusTask(), "Статус должен совпадать");
        assertEquals("Description1", task1.getDescription(), "Описание должно совпадать");
        assertEquals(TypeTask.TASK, task.getType(), "Тип должен совпадать");
        assertEquals(60, task.getDuration().toMinutes(), "Продолжительность должна совпадать");
        assertEquals("12:30 10.10.2025", task.getStartTime().format(TaskTest.formatter),
                "Дата и время начала задачи должны совпадать");
        assertEquals("13:30 10.10.2025", task.getEndTime().format(TaskTest.formatter),
                "Дата и время окончания задачи должны совпадать");
    }

    @Test
    void loadingTaskFileElseNullTime() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        task.setStartTime(null);
        manager.createTask(task);

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        final List<Task> listTask = newManager.getListTask();

        assertNotNull(listTask, "Список не должен быть пустым");
        assertEquals(1, listTask.size(), "Должна быть 1 задача");

        final Task task1 = listTask.getFirst();

        assertEquals(1, task1.getId(), "id должны совпадать");
        assertEquals("Task1", task1.getNameTask(), "Имя должно совпадать");
        assertEquals(StatusTask.NEW, task1.getStatusTask(), "Статус должен совпадать");
        assertEquals("Description1", task1.getDescription(), "Описание должно совпадать");
        assertEquals(TypeTask.TASK, task.getType(), "Тип должен совпадать");
        assertEquals(60, task.getDuration().toMinutes(), "Продолжительность должна совпадать");
        assertNull(task.getStartTime(), "Дата и время начала задачи должны быть null");
        assertNull(task.getEndTime(), "Дата и время окончания задачи должны быть null");
    }

    @Test
    void crossingTime() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task("Task1", StatusTask.NEW, "Description1");
        task1.setDuration(Duration.ofMinutes(60));
        task1.setStartTime(LocalDateTime.of(2025,10,10,12,25));
        manager.createTask(task1);

        Task task2 = new Task("Task2", StatusTask.NEW, "Description2");
        task2.setDuration(Duration.ofMinutes(60));
        task2.setStartTime(LocalDateTime.of(2025,10,10,12,45));

        assertThrows(ManagerAddTaskException.class, () -> manager.createTask(task2));

        final List<Task> sortedList = manager.getPrioritizedTasks();

        assertFalse(sortedList.isEmpty(), "Список не должен быть пуст");
        assertEquals(1, sortedList.size(),"В списке должна быть 1 задача");
    }
}