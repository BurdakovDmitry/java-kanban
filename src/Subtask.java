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
}
