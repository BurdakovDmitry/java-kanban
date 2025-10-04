import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    List<Subtask> listSubtask;

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
}