import java.util.List;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getListTask();

    List<Epic> getListEpic();

    List<Subtask> getListSubtask();

    Task getTaskById(int idTask);

    Epic getEpicById(int idEpic);

    Subtask getSubtaskById(int idSubtask);

    void removeAllTask();

    void removeAllEpic();

    void removeTaskById(int idTask);

    void removeEpicById(int idEpic);

    void removeSubtaskById(int idSubtask);

    List<Subtask> getListSubtaskToEpic(int idEpic);

    List<Task> getHistory();
}