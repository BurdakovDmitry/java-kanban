package Tasks;

import Enum.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private final TypeTask type;
    protected String nameTask;
    protected String description;
    protected int id;
    protected StatusTask statusTask;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public Task(String nameTask, StatusTask statusTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
        this.statusTask = statusTask;
        this.type = TypeTask.TASK;
    }

    public Task(int id, String nameTask, StatusTask statusTask, String description,
                LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.nameTask = nameTask;
        this.description = description;
        this.statusTask = statusTask;
        this.startTime = startTime;
        this.duration = duration;
        this.type = TypeTask.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusTask getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(StatusTask statusTask) {
        this.statusTask = statusTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public TypeTask getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration.toMinutes());
        } else {
            return null;
        }
    }

    public String formaterDateTime(LocalDateTime time, DateTimeFormatter formatter) {
        if (time == null) {
            return null;
        } else {
            return time.format(formatter);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n",
                id, type, nameTask, statusTask, description,
                formaterDateTime(startTime, dateTimeFormatter), duration.toMinutes());
    }
}