import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileBackedTaskManager extends TaskImplimentation {
    private final File file;

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
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {

            if (file.length() == 0) {
                writer.write("id,type,name,status,description,epic\n");
            }

            if (!tasks.isEmpty()) {
                for (Task task : getListTask()) {
                    writer.write(toString(task));
                }
            }

            if (!epics.isEmpty()) {
                for (Task task : getListEpic()) {
                    writer.write(toString(task));
                }
            }

            if (!subtasks.isEmpty()) {
                for (Task task : getListSubtask()) {
                    writer.write(toString(task));
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    private String toString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%d,%s,%s,%s,%s,%d\n",
                    subtask.getId(), TypeTask.SUBTASK, subtask.getNameTask(), subtask.getStatusTask(),
                    subtask.getDescription(), subtask.getIdEpic());
        } else if (task instanceof Epic epic) {
            return String.format("%d,%s,%s,%s,%s\n",
                    epic.getId(), TypeTask.EPIC, epic.getNameTask(), epic.getStatusTask(), epic.getDescription());
        } else {
            return String.format("%d,%s,%s,%s,%s\n",
                    task.getId(), TypeTask.TASK, task.getNameTask(), task.getStatusTask(), task.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] values = value.split(",");

        if (values[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(values[2], StatusTask.valueOf(values[3]), values[4]);
            subtask.setId(Integer.parseInt(values[0]));
            subtask.setIdEpic(Integer.parseInt(values[5]));
            return subtask;
        } else if (values[1].equals("EPIC")) {
            Epic epic = new Epic(values[2], StatusTask.valueOf(values[3]), values[4]);
            epic.setId(Integer.parseInt(values[0]));
            return epic;
        } else {
            Task task = new Task(values[2], StatusTask.valueOf(values[3]), values[4]);
            task.setId(Integer.parseInt(values[0]));
            return task;
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
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

                if (task instanceof Epic epic) {
                    manager.epics.put(epic.getId(), epic);
                    manager.id = epic.getId() + 1;
                } else if (task instanceof Subtask subtask) {
                    manager.subtasks.put(subtask.getId(), subtask);
                    manager.id = subtask.getId() + 1;
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
        manager.createTask(task1);
        Task task2 = new Task("Сходить в магазин", StatusTask.NEW, "...");
        manager.createTask(task2);

        Epic epic1 = new Epic("Уборка дома", StatusTask.NEW, "...");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Помыть полы", StatusTask.NEW, "...");
        subtask1.setIdEpic(epic1.getId());
        manager.createSubtask(subtask1);

        FileBackedTaskManager manager1 = loadFromFile(file);

        for (Task task : manager1.getListTask()) {
            System.out.println("Задача: id = " + task.getId() + "; название = " + task.getNameTask());
        }

        for (Task task : manager1.getListEpic()) {
            System.out.println("Эпик: id = " + task.getId() + "; название = " + task.getNameTask());
        }

        for (Task task : manager1.getListSubtask()) {
            System.out.println("Подзадача: id = " + task.getId() + "; название = " + task.getNameTask());
        }
    }
}