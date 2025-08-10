public class Subtask extends Task {
    private int idEpic;

    public Subtask(String nameTask, String description) {
        super(nameTask, description);
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }
}
