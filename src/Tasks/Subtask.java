package Tasks;

import Enum.*;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameTask, StatusTask statusTask, String description) {
        super(nameTask, statusTask, description);
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d\n",
                getId(), TypeTask.SUBTASK, getNameTask(), getStatusTask(), getDescription(), idEpic);
    }
}
