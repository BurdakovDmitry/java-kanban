package managers;

import exceptions.ManagerSaveException;
import enums.StatusTask;
import enums.TypeTask;
import tasks.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public FileBackedTaskManager(File saveFile) {
        this.file = saveFile;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeTaskById(int idTask) {
        super.removeTaskById(idTask);
        save();
    }

    @Override
    public void removeEpicById(int idEpic) {
        super.removeEpicById(idEpic);
        save();
    }

    @Override
    public void removeSubtaskById(int idSubtask) {
        super.removeSubtaskById(idSubtask);
        save();
    }

    private void save() {
        String header = "id,type,name,status,description,startTime,duration,epic\n";

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {

            if (file.length() == 0) {
                writer.write(header);
            }

            if (!tasks.isEmpty()) {
                for (Task task : getListTask()) {
                    writer.write(task.toString());
                }
            }

            if (!epics.isEmpty()) {
                for (Task task : getListEpic()) {
                    writer.write(task.toString());
                }
            }

            if (!subtasks.isEmpty()) {
                for (Task task : getListSubtask()) {
                    writer.write(task.toString());
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    private static Task fromString(String value) {
        String[] values = value.split(",");

        int id = Integer.parseInt(values[0]);
        TypeTask type = TypeTask.valueOf(values[1]);
        String name = values[2];
        StatusTask statusTask = StatusTask.valueOf(values[3]);
        String description = values[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(values[6]));
        LocalDateTime time;
        int idEpic = 0;

        try {
            time = LocalDateTime.parse(values[5], dateTimeFormatter);
        } catch (DateTimeParseException e) {
            time = null;
        }

        if (values.length == 8) {
            idEpic = Integer.parseInt(values[7]);
        }

        if (type == TypeTask.SUBTASK) {
            return new Subtask(id, name, statusTask, description, time, duration, idEpic);
        } else if (type == TypeTask.EPIC) {
            return new Epic(id, name, statusTask, description);
        } else {
            return new Task(id, name, statusTask, description, time, duration);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;

            while (reader.ready()) {
                line = reader.readLine();

                if (line.isEmpty()) {
                    return manager;
                }

                if (line.contains("id")) {
                    continue;
                }

                Task task = fromString(line);

                if (task.getType() == TypeTask.EPIC) {
                    manager.epics.put(task.getId(), (Epic) task);
                    manager.id = task.getId() + 1;
                } else if (task.getType() == TypeTask.SUBTASK) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                    manager.epics.get(((Subtask) task).getIdEpic()).listSubtask.add((Subtask) task);
                    manager.id = task.getId() + 1;
                } else {
                    manager.tasks.put(task.getId(), task);
                    manager.id = task.getId() + 1;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return manager;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите абсолютный путь к файлу");

        File file = new File(scanner.nextLine());
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Переезд", StatusTask.NEW, "...");
        task1.setDuration(Duration.ofMinutes(245));
        task1.setStartTime(LocalDateTime.of(2025, 10, 15, 17, 15));
        manager.createTask(task1);

        Task task2 = new Task("Сходить в магазин", StatusTask.NEW, "...");
        task2.setDuration(Duration.ofMinutes(108));
        task2.setStartTime(LocalDateTime.of(2025, 10, 16, 17, 20));
        manager.createTask(task2);

        Task task3 = new Task("Купить билет", StatusTask.NEW, "...");
        task3.setDuration(Duration.ofMinutes(15));
        manager.createTask(task3);

        Epic epic1 = new Epic("Уборка дома", StatusTask.NEW, "...");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Помыть полы", StatusTask.NEW, "...");
        subtask1.setIdEpic(epic1.getId());
        subtask1.setDuration(Duration.ofMinutes(30));
        subtask1.setStartTime(LocalDateTime.of(2025, 10, 12, 9, 30));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Помыть окна", StatusTask.IN_PROGRESS, "...");
        subtask2.setIdEpic(epic1.getId());
        subtask2.setDuration(Duration.ofMinutes(30));
        subtask2.setStartTime(LocalDateTime.of(2025, 10, 12, 13, 0));
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Уборка улицы", StatusTask.NEW, "...");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Подмести", StatusTask.NEW, "...");
        subtask3.setIdEpic(epic2.getId());
        subtask3.setDuration(Duration.ofMinutes(150));
        subtask3.setStartTime(LocalDateTime.of(2025, 10, 17, 17, 15));
        manager.createSubtask(subtask3);

        for (Task value : manager.getPrioritizedTasks()) {
            System.out.println("id - " + value.getId() + ";  timeStart - " + value.getStartTime());
        }

        FileBackedTaskManager manager1 = loadFromFile(file);

        for (Task task : manager1.getListTask()) {
            System.out.println("Задача: id = " + task.getId() + "; название = " + task.getNameTask() +
                    "; Время начала: " + task.getStartTime() + " Длительность: " + task.getDuration());
        }

        for (Task task : manager1.getListEpic()) {
            System.out.println("Эпик: id = " + task.getId() + "; название = " + task.getNameTask() +
                    "; Время начала: " + task.getStartTime() + " Длительность: " + task.getDuration());
        }

        for (Task task : manager1.getListSubtask()) {
            System.out.println("Подзадача: id = " + task.getId() + "; название = " + task.getNameTask() +
                    "; Время начала: " + task.getStartTime() + " Длительность: " + task.getDuration());
        }
    }
}