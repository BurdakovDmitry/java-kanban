import java.util.ArrayList;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    ArrayList<Task> getListTask();

    ArrayList<Epic> getListEpic();

    ArrayList<Subtask> getListSubtask();

    Task getTaskById(int idTask);

    Epic getEpicById(int idEpic);

    Subtask getSubtaskById(int idSubtask);

    void removeAllTask();

    void removeAllEpic();

    void removeTaskById(int idTask);

    void removeEpicById(int idEpic);

    void removeSubtaskById(int idSubtask);

    ArrayList<Subtask> getListSubtaskToEpic(int idEpic);

    ArrayList<Task> getHistory();
}
