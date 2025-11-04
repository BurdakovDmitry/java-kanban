package tasks;

import enums.TypeTask;
import enums.StatusTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected int id;
    protected TypeTask type;
    protected String nameTask;
    protected String description;
    protected StatusTask statusTask;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String nameTask, StatusTask statusTask, String description) {
        this.nameTask = nameTask;
        this.description = description;
        this.statusTask = statusTask;
        this.type = TypeTask.TASK;
    }

    public Task(String nameTask, StatusTask statusTask, String description,
                LocalDateTime startTime, Duration duration) {
        this.nameTask = nameTask;
        this.description = description;
        this.statusTask = statusTask;
        this.startTime = startTime;
        this.duration = duration;
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
        if (startTime != null && duration != null) {
            return startTime.plusMinutes(duration.toMinutes());
        } else {
            return null;
        }
    }

    public String formaterDateTime(LocalDateTime time) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        if (time == null) {
            return null;
        } else {
            return time.format(dtf);
        }
    }

    public long formaterDuration(Duration duration) {
        if (duration == null) {
            return 0;
        }

        return duration.toMinutes();
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
        return "Task{" +
                "id=" + id +
                ", type=" + type +
                ", nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", statusTask=" + statusTask +
                ", duration=" + formaterDuration(duration) +
                ", startTime=" + formaterDateTime(startTime) +
                '}';
    }

    public String toStringFormatSaveFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n",
                id, type, nameTask, statusTask, description,
                formaterDateTime(startTime), formaterDuration(duration));
    }
}