package Tasks;

import Enum.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    public List<Subtask> listSubtask;

    public Epic(String nameTask, StatusTask statusTask, String description) {
        super(nameTask, statusTask, description);
        listSubtask = new ArrayList<>();
    }

    public List<Subtask> getListSubtask() {
        return listSubtask;
    }

    @Override
    public StatusTask getStatusTask() {
        if (listSubtask.isEmpty()) {
            return StatusTask.NEW;
        }

        if (statusNew(listSubtask)) {
            return StatusTask.NEW;
        } else if (statusDone(listSubtask)) {
            return StatusTask.DONE;
        }
        return StatusTask.IN_PROGRESS;
    }

    private boolean statusNew(List<Subtask> list) {
        for (Subtask subtask : list) {
            if (!Objects.equals(subtask.getStatusTask(), StatusTask.NEW)) {
                return false;
            }
        }
        return true;
    }

    private boolean statusDone(List<Subtask> list) {
        for (Subtask subtask : list) {
            if (!subtask.getStatusTask().equals(StatusTask.DONE)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s\n",
                getId(), TypeTask.EPIC, getNameTask(), getStatusTask(), getDescription());
    }
}