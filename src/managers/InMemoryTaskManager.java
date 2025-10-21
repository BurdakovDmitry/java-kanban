package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import tasks.Task;
import tasks.Subtask;
import tasks.Epic;
import enums.TypeTask;
import exceptions.ManagerAddTaskException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    public int id = 1;
    public Map<Integer, Task> tasks = new HashMap<>();
    public Map<Integer, Subtask> subtasks = new HashMap<>();
    public Map<Integer, Epic> epics = new HashMap<>();
    public HistoryManager historyManager = Managers.getDefaultHistory();
    public Set<Task> sortedListByTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public void createTask(Task task) {
        task.setId(id);
        addTasks(task);
        id++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(id);
        addTasks(epic);
        id++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(id);
        addTasks(subtask);
        epics.get(subtask.getIdEpic()).getStatusTask();
        id++;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            addTasks(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            addTasks(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            addTasks(subtask);
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
        tasks.values().forEach((task) -> historyManager.remove(task.getId()));
        tasks.values().forEach(task -> sortedListByTime.remove(task));
        tasks.clear();
    }

    @Override
    public void removeAllEpic() {
        subtasks.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtasks.values().forEach(subtask -> sortedListByTime.remove(subtask));
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeTaskById(int idTask) {
        sortedListByTime.remove(tasks.get(idTask));
        tasks.remove(idTask);
        historyManager.remove(idTask);
    }

    @Override
    public void removeEpicById(int idEpic) {
        epics.get(idEpic).getListSubtask().forEach(subtask -> subtasks.remove(subtask.getId()));
        epics.get(idEpic).getListSubtask().forEach(subtask -> historyManager.remove(subtask.getId()));
        epics.get(idEpic).getListSubtask().forEach(subtask -> sortedListByTime.remove(subtask));
        epics.remove(idEpic);
        historyManager.remove(idEpic);
    }

    @Override
    public void removeSubtaskById(int idSubtask) {
        epics.get(subtasks.get(idSubtask).getIdEpic()).listSubtask.remove(subtasks.get(idSubtask));
        epics.get(subtasks.get(idSubtask).getIdEpic()).getStatusTask();
        sortedListByTime.remove(subtasks.get(idSubtask));
        subtasks.remove(idSubtask);
        historyManager.remove(idSubtask);
    }

    @Override
    public List<Subtask> getListSubtaskToEpic(int idEpic) {
        return new ArrayList<>(epics.get(idEpic).getListSubtask());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedListByTime);
    }

    private void addTasks(Task task) {

        if (sortedListByTime.isEmpty() && task.getStartTime() != null) {
            sortedListByTime.add(task);
        }

        if (!crossingTime(task)) {
            if (task.getStartTime() != null && task.getType() != TypeTask.EPIC) {
                sortedListByTime.add(task);
            }

            if (task.getType() == TypeTask.TASK) {
                tasks.put(task.getId(), task);
            } else if (task.getType() == TypeTask.SUBTASK) {
                subtasks.put(task.getId(), (Subtask) task);
                epics.get(((Subtask) task).getIdEpic()).listSubtask.add((Subtask) task);
            } else {
                epics.put(task.getId(), (Epic) task);
            }
        } else {
            throw new ManagerAddTaskException("Ошибка добавления задачи. Задача пересекается по времени.");
        }
    }

    private boolean crossingTime(Task task) {
        return isCrossingStartTime(task) || isCrossingEndTime(task)
                || taskWithinTaskFromSet(task) || taskFromSetWithinTask(task);
    }

    private boolean isCrossingStartTime(Task task) {
        for (Task taskFromSet : sortedListByTime) {
            if (task.getStartTime() != null) {
                return task.getStartTime().isAfter(taskFromSet.getStartTime())
                        && task.getStartTime().isBefore(taskFromSet.getEndTime());
            }
        }
        return false;
    }

    private boolean isCrossingEndTime(Task task) {
        for (Task taskFromSet : sortedListByTime) {
            if (task.getStartTime() != null) {
                return task.getStartTime().isBefore(taskFromSet.getStartTime())
                        && task.getEndTime().isAfter(taskFromSet.getStartTime());
            }
        }
        return false;
    }

    private boolean taskWithinTaskFromSet(Task task) {
        for (Task taskFromSet : sortedListByTime) {
            if (task.getStartTime() != null) {
                return task.getStartTime().isAfter(taskFromSet.getStartTime())
                        && task.getEndTime().isBefore(taskFromSet.getEndTime());
            }
        }
        return false;
    }

    private boolean taskFromSetWithinTask(Task task) {
        for (Task taskFromSet : sortedListByTime) {
            if (task.getStartTime() != null) {
                return task.getStartTime().isBefore(taskFromSet.getStartTime())
                        && task.getEndTime().isAfter(taskFromSet.getEndTime());
            }
        }
        return false;
    }
}