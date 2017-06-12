package com.zhitoupc.refreshloadapp;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        Node one = new Node(1);
//        Node two = new Node(2);
//        Node three = new Node(3);
//        Node four = new Node(4);
//
//        one.setNext(two);
//        two.setNext(three);
//        System.out.println(one.toString());
//
//        Node reverse = reverse3(one);
//        System.out.println("final="+reverse.toString());
    }


    private Node reverse(Node node){
        if (node == null || node.getNext() == null) {
            return node;
        }
        Node next = node.getNext();
        node.setNext(null);
        Node reverseNode = reverse(next);
        next.next = node;
        return reverseNode;
    }

    public Node reverse2(Node current) {
        if (current == null || current.next == null)
            return current;
        Node nextNode = current.next;
        current.next = null;
        Node reverseRest = reverse2(nextNode);
        nextNode.next = current;
        return reverseRest;
    }

    private Node reverse3(Node head){
        Node preNode = null;
        Node nextNode;
        while(head != null){
            nextNode = head.next;
            head.next = preNode;
            preNode = head;
            head = nextNode;
        }
        return preNode;
    }


    public class Node{
        private int data;
        private Node next;

        public Node(int data) {
            this.data = data;
        }

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    ", next=" + next +
                    '}';
        }
    }
}