import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> listHistory = new ArrayList<>();

    @Override
    public void add(Task task){
        if (!listHistory.isEmpty() && listHistory.size() > 9) {
            listHistory.removeFirst();
        }
        listHistory.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return listHistory;
    }
}
