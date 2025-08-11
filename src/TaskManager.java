import java.util.HashMap;

public class TaskManager {
    public static int id = 1;
    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public static HashMap<Integer, Epic> epics = new HashMap<>();


    public void createTask(Task task) {
        task.setId(id);
        tasks.put(task.getId(), task);
        id++;
    }

    public void createEpic(Epic epic) {
        epic.setId(id);
        epics.put(epic.getId(), epic);
        id++;
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getIdEpic()).getStatusTask();
        id++;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getIdEpic()).getStatusTask();
        }
    }

    public void getListTask() {
        for (Task task : tasks.values()) {
            System.out.println(task.getNameTask());
        }
    }

    public void getListEpic() {
        for (Epic epic : epics.values()) {
            System.out.println(epic.getNameTask());
        }
    }

    public void getListSubtask() {
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask.getNameTask());
        }
    }

    public void getIdTask(Task task) {
        System.out.println(tasks.get(task.getId()).getNameTask());
    }

    public void getIdEpic(Epic epic) {
        System.out.println(epics.get(epic.getId()).getNameTask());
    }

    public void getIdSubtask(Subtask subtask) {
        System.out.println(subtasks.get(subtask.getId()).getNameTask());
    }

    public void removeAllTask() {
        tasks.clear();
    }

    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.listSubtask.clear();
            epic.getStatusTask();
        }
        subtasks.clear();
    }

    public void removeIdTask(Task task) {
        tasks.remove(task.getId());
    }

    public void removeIdEpic(Epic epic) {
        for (Subtask subtask : epic.getListSubtask()) {
            subtasks.remove(subtask.getId());
        }

        epics.remove(epic.getId());
    }

    public void removeIdSubtask(Subtask subtask) {
        epics.get(subtask.getIdEpic()).listSubtask.remove(subtask);
        epics.get(subtask.getIdEpic()).getStatusTask();
        subtasks.remove(subtask.getId());
    }

    public void getListSubtaskToEpic(Epic epic) {
        for (Subtask subtask : epic.getListSubtask()) {
            System.out.println(subtask.getNameTask());
        }
    }
}

