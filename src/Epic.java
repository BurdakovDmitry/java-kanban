import java.util.ArrayList;

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

        for (Subtask subtask : listSubtask) {
            if (subtask.getStatusTask().equals(StatusTask.NEW)) {
                return StatusTask.NEW;
            } else if (subtask.getStatusTask().equals(StatusTask.DONE)) {
                return StatusTask.DONE;
            }
        }
        return StatusTask.IN_PROGRESS;
    }
}
