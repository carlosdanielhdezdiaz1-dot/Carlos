package org.example.structures;

import java.util.LinkedList;

public class CustomQueue<T> {
    private LinkedList<T> elements;

    public CustomQueue() {
        elements = new LinkedList<>();
    }


    public void enqueue(T element) {
        elements.addLast(element);
    }


    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        return elements.removeFirst();
    }


    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return elements.getFirst();
    }


    public boolean isEmpty() {
        return elements.isEmpty();
    }


    public int size() {
        return elements.size();
    }


    public void clear() {
        elements.clear();
    }


    public boolean searchRecursive(T element, int index) {
        if (index >= elements.size()) {
            return false;
        }
        if (elements.get(index).equals(element)) {
            return true;
        }
        return searchRecursive(element, index + 1);
    }


    public void displayRecursive(int index) {
        if (index >= elements.size()) {
            return;
        }
        System.out.println("Posición " + index + ": " + elements.get(index));
        displayRecursive(index + 1);
    }


    public void display() {
        System.out.println("Cola actual:");
        for (int i = 0; i < elements.size(); i++) {
            System.out.println("  [" + i + "] " + elements.get(i));
        }
    }

    @Override
    public String toString() {
        return "Cola: " + elements.toString();
    }
}