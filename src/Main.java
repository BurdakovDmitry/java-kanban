public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Переезд", "...");
        manager.createTask(task1);
        Task task2 = new Task("Сходить в магазин", "...");
        manager.createTask(task2);

        Epic epic1 = new Epic("Уборка дома", "...");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Помыть полы", "...");
        subtask1.setIdEpic(epic1.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Помыть окна", "...");
        subtask2.setIdEpic(epic1.getId());
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Уборка улицы", "...");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Подмести", "...");
        subtask3.setIdEpic(epic2.getId());
        manager.createSubtask(subtask3);

        System.out.println("Задача №" + task1.getId() + ". " + task1.getNameTask() + " Status: " + task1.getStatusTask());
        System.out.println("Задача №" + task2.getId() + ". " + task2.getNameTask() + " Status: " + task2.getStatusTask());
        System.out.println("Эпик №" + epic1.getId() + ". " + epic1.getNameTask() + " Status: " + epic1.getStatusTask());
        System.out.println("Подзадача №" + subtask1.getId() + ". " + subtask1.getNameTask() + " Status: " + subtask1.getStatusTask());
        System.out.println("Подзадача №" + subtask2.getId() + ". " + subtask2.getNameTask() + " Status: " + subtask2.getStatusTask());
        System.out.println("Эпик №" + epic2.getId() + ". " + epic2.getNameTask() + " Status: " + epic2.getStatusTask());
        System.out.println("Подзадача №" + subtask3.getId() + ". " + subtask3.getNameTask() + " Status: " + subtask3.getStatusTask());
        System.out.println("___".repeat(20));


        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(6);
        manager.getSubtaskById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(4);
        manager.getSubtaskById(7);
        manager.getSubtaskById(4);

        for (Task task : manager.getHistory()) {
            System.out.println(task.getNameTask());
        }

        System.out.println("___".repeat(20));

        manager.getSubtaskById(4);

        for (Task task : manager.getHistory()) {
            System.out.println(task.getNameTask());
        }
    }
}
