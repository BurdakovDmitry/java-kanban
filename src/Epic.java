import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    ArrayList<Subtask> listSubtask;

    public Epic(String nameTask, String description) {
        super(nameTask, description);
        listSubtask = new ArrayList<>();
    }

    public ArrayList<Subtask> getListSubtask() {
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

    private boolean statusNew(ArrayList<Subtask> list) {
        for (Subtask subtask : list) {
            if (!Objects.equals(subtask.getStatusTask(), StatusTask.NEW)) {
                return false;
            }
        }
        return true;
    }

    private boolean statusDone(ArrayList<Subtask> list) {
        for (Subtask subtask : list) {
            if (!subtask.getStatusTask().equals(StatusTask.DONE)) {
                return false;
            }
        }
        return true;
    }
}
