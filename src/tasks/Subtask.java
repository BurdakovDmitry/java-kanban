package tasks;

import enums.TypeTask;
import enums.StatusTask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameTask, StatusTask statusTask, String description) {
        super(nameTask, statusTask, description);
        this.type = TypeTask.SUBTASK;
    }

    public Subtask(int idEpic, String nameTask, StatusTask statusTask, String description,
                   LocalDateTime startTime, Duration duration) {
        super(nameTask, statusTask, description, startTime, duration);
        this.idEpic = idEpic;
        this.type = TypeTask.SUBTASK;
    }

    public Subtask(int id, String nameTask, StatusTask statusTask, String description,
                   LocalDateTime startTime, Duration duration, int idEpic) {
        super(id, nameTask, statusTask, description, startTime, duration);
        this.idEpic = idEpic;
        this.type = TypeTask.SUBTASK;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "idEpic=" + idEpic +
                ", id=" + id +
                ", type=" + getType() +
                ", nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", statusTask=" + statusTask +
                ", duration=" + formaterDuration(duration) +
                ", startTime=" + formaterDateTime(startTime) +
                '}';
    }

    @Override
    public String toStringFormatSaveFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                getId(), getType(), getNameTask(), getStatusTask(), getDescription(),
                formaterDateTime(getStartTime()), formaterDuration(getDuration()), idEpic);
    }
}