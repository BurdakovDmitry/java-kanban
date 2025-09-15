import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TaskImplimentation implements TaskManager {
    public static int id = 1;
    public static Map<Integer, Task> tasks = new HashMap<>();
    public static Map<Integer, Subtask> subtasks = new HashMap<>();
    public static Map<Integer, Epic> epics = new HashMap<>();
    public static HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public void createTask(Task task) {
        task.setId(id);
        tasks.put(task.getId(), task);
        id++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(id);
        epics.put(epic.getId(), epic);
        id++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getIdEpic()).listSubtask.add(subtask);
        epics.get(subtask.getIdEpic()).getStatusTask();
        id++;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getIdEpic()).getStatusTask();
        }
    }

    @Override
    public List<Task> getListTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getListEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getListSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int idTask) {
        historyManager.add(tasks.get(idTask));
        return tasks.get(idTask);
    }

    @Override
    public Epic getEpicById(int idEpic) {
        historyManager.add(epics.get(idEpic));
        return epics.get(idEpic);
    }

    @Override
    public Subtask getSubtaskById(int idSubtask) {
        historyManager.add(subtasks.get(idSubtask));
        return subtasks.get(idSubtask);
    }

    @Override
    public void removeAllTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }

        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeTaskById(int idTask) {
        tasks.remove(idTask);
        historyManager.remove(idTask);
    }

    @Override
    public void removeEpicById(int idEpic) {
        for (Subtask subtask : epics.get(idEpic).getListSubtask()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }

        epics.remove(idEpic);
        historyManager.remove(idEpic);
    }

    @Override
    public void removeSubtaskById(int idSubtask) {
        epics.get(subtasks.get(idSubtask).getIdEpic()).listSubtask.remove(subtasks.get(idSubtask));
        epics.get(subtasks.get(idSubtask).getIdEpic()).getStatusTask();
        subtasks.remove(idSubtask);
        historyManager.remove(idSubtask);
    }

    @Override
    public List<Subtask> getListSubtaskToEpic(int idEpic) {
        return new ArrayList<>(epics.get(idEpic).getListSubtask());
    }


    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}

