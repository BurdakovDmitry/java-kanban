import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;
    Map<Integer, Node<Task>> mapNote = new HashMap<>();


    @Override
    public void add(Task task) {
        if (mapNote.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(mapNote.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node<Task> {
        public Task task;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);

        mapNote.put(task.getId(), newNode);
        size++;

        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
    }

    private List<Task> getTasks() {
        List<Task> listHistory = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            listHistory.add(node.task);
            node = node.next;
        }
        return listHistory;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        if (node == head) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
        } else if (node == tail) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            Node<Task> prevNode = node.prev;
            Node<Task> nextNode = node.next;

            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }
}