import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private int size = 0;
    Map<Integer, Node> mapNote = new HashMap<>();


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

    private static class Node {
        public Task task;
        public Node next;
        public Node prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);

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
        Node node = head;
        while (node != null) {
            listHistory.add(node.task);
            node = node.next;
        }
        return listHistory;
    }

    private void removeNode(Node node) {
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
            Node prevNode = node.prev;
            Node nextNode = node.next;

            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }
}