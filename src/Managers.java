public class Managers {
    public static TaskManager getDefault() {
        return new TaskImplimentation();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
