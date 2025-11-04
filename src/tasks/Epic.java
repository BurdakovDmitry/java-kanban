package tasks;

import enums.StatusTask;
import enums.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    public List<Subtask> listSubtask;

    public Epic(String nameTask, StatusTask statusTask, String description) {
        super(nameTask, statusTask, description);
        listSubtask = new ArrayList<>();
        this.type = TypeTask.EPIC;
    }

    public Epic(int id, String nameTask, StatusTask statusTask, String description,
                LocalDateTime startTime, Duration duration) {
        super(nameTask, statusTask, description, startTime, duration);
        this.id = id;
        listSubtask = new ArrayList<>();
        this.type = TypeTask.EPIC;
    }

    public List<Subtask> getListSubtask() {
        return listSubtask;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public LocalDateTime getStartTime() {
        startTime = LocalDateTime.MAX;

        for (Subtask subtask : listSubtask) {
            if (subtask.getStartTime() != null) {
                if (startTime.isAfter(subtask.getStartTime())) {
                    startTime = subtask.getStartTime();
                }
            }
        }

        if (startTime == LocalDateTime.MAX) {
            startTime = null;
        }

        return startTime;
    }

    @Override
    public Duration getDuration() {
        duration = Duration.ofMinutes(0);

        for (Subtask subtask : listSubtask) {
            if (subtask.getDuration() != null) {
                duration = duration.plusMinutes(subtask.getDuration().toMinutes());
            }
        }
        return duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime endTime = LocalDateTime.MIN;

        for (Subtask subtask : listSubtask) {
            if (subtask.getStartTime() != null) {
                if (endTime.isBefore(subtask.getEndTime())) {
                    endTime = subtask.getEndTime();
                }
            }
        }

        if (endTime == LocalDateTime.MIN) {
            endTime = null;
        }

        return endTime;
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
        return "Epic{" +
                "listSubtask=" + listSubtask +
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
        return String.format("%s,%s,%s,%s,%s,%s,%s\n", getId(), getType(), getNameTask(), getStatusTask(),
                getDescription(), formaterDateTime(getStartTime()), formaterDuration(getDuration()));
    }
}