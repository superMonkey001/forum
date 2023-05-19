package cn.edu.hncj.forum.ac_automation;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @Author FanJian
 * @Date 2023-02-04 21:40
 */


@Component
public class ACAutomation {
    private Node root;

    private final String fileName = "sensitiveWord.txt";
    public ACAutomation() {
        root = new Node();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // 使用逗号拆分每行内容为数组
                String[] parts = line.split(",");
                for (String part : parts) {
                    // 将拆分后的每个元素添加到列表中
                    insert(part);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        build();
    }

    public void insert(String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        char[] chs = str.toCharArray();
        Node node = root;
        for (int i = 0; i < chs.length; i++) {
            Node[] nexts = node.nexts;
            int path = chs[i];
            if (nexts[path] == null) {
                nexts[path] = new Node();
            }
            node = nexts[path];
        }
        node.end = str;
    }

    public void build() {
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        Node cur = null;
        Node cFail = null;
        while (!queue.isEmpty()) {
            cur = queue.poll();
            for (int i = 0; i < 65536; i++) {
                // 如果有i号子树
                if (cur.nexts[i] != null) {
                    cur.nexts[i].fail = root;
                    cFail = cur.fail;
                    while (cFail != null) {
                        // 当前节点一直沿着fail指针找，如果真的找到了一个节点，那么就让当前节点的下一个节点的fail指针指向这个及诶单
                        if (cFail.nexts[i] != null) {
                            cur.nexts[i].fail = cFail.nexts[i];
                            break;
                        }
                        cFail = cFail.fail;
                    }
                    queue.add(cur.nexts[i]);
                }
            }
        }
    }

    public Set<String> containsWords(String content) {
        char[] chs = content.toCharArray();
        Node cur = root;
        Node follow = null;
        int index = 0;
        Set<String> ans = new HashSet<>();
        for (int i = 0; i < chs.length; i++) {
            index = chs[i];
            // 意味着失败了
            // cur一直沿着fail指针往下走，会出现两种情况，一种是走到根节点，一种是找到了一个可以往下走的节点
            // (详细点说就是，当前字符是文章中的某一个字符嘛，那我就要看一下我下面是否能够有这条路径，如果有，那么就可以往下走，如果没有，就不能往下走，也就是回到root)
            while (cur.nexts[index] == null && cur != root) {
                cur = cur.fail;
            }

            cur = cur.nexts[index] != null ? cur.nexts[index] : root;
            follow = cur;
            while (follow != root) {
                if (follow.end != null) {
                    ans.add(follow.end);
                }
                follow = follow.fail;
            }
        }
        return ans;
    }
}

class Node {
    String end;
    Node fail;
    Node[] nexts;

    public Node() {
        this.end = null;
        this.fail = null;
        this.nexts = new Node[65536];
    }
}
