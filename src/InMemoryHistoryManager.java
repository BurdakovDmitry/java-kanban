import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> listHistory = new ArrayList<>();

    @Override
    public void add(Task task){
        if (!listHistory.isEmpty() && listHistory.size() > 9) {
            listHistory.removeFirst();
        }
        listHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return listHistory;
    }
}
