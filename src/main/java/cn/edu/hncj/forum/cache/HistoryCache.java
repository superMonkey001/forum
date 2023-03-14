package cn.edu.hncj.forum.cache;

import cn.edu.hncj.forum.dto.QuestionDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author FanJian
 * @Date 2023-03-12 16:45
 */
@Data
public class HistoryCache {

    public Map<Long,Node> cacheMap;
    public DoubleLinkedList linkedList;
    int capacity = 10;

    public HistoryCache() {
        cacheMap = new HashMap<>();
        linkedList = new DoubleLinkedList();
    }

    public Node get(Long key) {
        if (cacheMap.containsKey(key)) {
            Node node = cacheMap.get(key);
            linkedList.moveNodeToTail(node);
            return node;
        } else {
            return null;
        }
    }

    public void put(Long key,QuestionDTO dto) {
        // 更新
        if (cacheMap.containsKey(key)) {
            Node node = cacheMap.get(key);
            node.val = dto;
            linkedList.moveNodeToTail(node);
        } else {
            if (cacheMap.size() == capacity) {
                removeLongestUsedNode(linkedList.head.next);
            }
            Node node = new Node(key, dto);
            cacheMap.put(key,node);
            linkedList.addNode(node);
        }
    }

    public void removeLongestUsedNode(Node node) {
        linkedList.removeNode(node);
        cacheMap.remove(node.key);
    }

    public class DoubleLinkedList {
        public Node head;
        public Node tail;
        public DoubleLinkedList() {
            head = new Node();
            tail = new Node();
            head.next = tail;
            tail.pre = head;
        }

        /**
         * 将链表移动到尾部，等同于先删除节点，再添加节点的操作
         */
        public void moveNodeToTail(Node node) {
            // 小小的优化，如果已经是尾部了，那么就不用移动
            if (node == tail.pre) {
                return;
            }
            removeNode(node);
            addNode(node);
        }

        /**
         * 将节点添加到链表尾部
         */
        public void addNode(Node node) {
            node.pre = tail.pre;
            node.next = tail;
            tail.pre.next = node;
            tail.pre = node;
        }

        /**
         * 将节点从链表中删除
         * @param node
         */
        public void removeNode(Node node) {
            node.pre.next = node.next;
            node.next.pre = node.pre;
        }
    }
    public class Node {
        public Node next;
        public Node pre;
        Long key;
        public Object val;
        public Node() {}
        public Node(Long key,Object val) {
            this.key = key;
            this.val = val;
        }
    }
}

