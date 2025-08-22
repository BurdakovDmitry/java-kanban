import java.util.ArrayList;
import java.util.HashMap;

public class TaskImplimentation implements TaskManager {
    public static int id = 1;
    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public static HashMap<Integer, Epic> epics = new HashMap<>();
    public static HistoryManager listHistory = Managers.getDefaultHistory();


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
    public ArrayList<Task> getListTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getListEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getListSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int idTask) {
        listHistory.add(tasks.get(idTask));
        return tasks.get(idTask);
    }

    @Override
    public Epic getEpicById(int idEpic) {
        listHistory.add(epics.get(idEpic));
        return epics.get(idEpic);
    }

    @Override
    public Subtask getSubtaskById(int idSubtask) {
        listHistory.add(subtasks.get(idSubtask));
        return subtasks.get(idSubtask);
    }

    @Override
    public void removeAllTask() {
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeTaskById(int idTask) {
        tasks.remove(idTask);
    }

    @Override
    public void removeEpicById(int idEpic) {
        for (Subtask subtask : epics.get(idEpic).getListSubtask()) {
            subtasks.remove(subtask.getId());
        }

        epics.remove(idEpic);
    }

    @Override
    public void removeSubtaskById(int idSubtask) {
        epics.get(subtasks.get(idSubtask).getIdEpic()).listSubtask.remove(subtasks.get(idSubtask));
        epics.get(subtasks.get(idSubtask).getIdEpic()).getStatusTask();
        subtasks.remove(idSubtask);
    }

    @Override
    public ArrayList<Subtask> getListSubtaskToEpic(int idEpic) {
        return new ArrayList<>(epics.get(idEpic).getListSubtask());
    }


    public ArrayList<Task> getHistory() {
        return listHistory.getHistory();
    }
}

