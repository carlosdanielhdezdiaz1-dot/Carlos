package org.example.structures;

import java.util.ArrayList;
import java.util.EmptyStackException;

public class CustomStack<T> {
    private ArrayList<T> elements;
    private int capacity;
    private int top;

    public CustomStack() {
        this(100); // Capacidad por defecto
    }

    public CustomStack(int capacity) {
        this.elements = new ArrayList<>(capacity);
        this.capacity = capacity;
        this.top = -1;
    }

    public void push(T element) {
        if (isFull()) {
            throw new StackOverflowError("Pila llena");
        }
        elements.add(element);
        top++;
    }


    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T element = elements.get(top);
        elements.remove(top);
        top--;
        return element;
    }


    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.get(top);
    }


    public boolean isEmpty() {
        return top == -1;
    }


    public boolean isFull() {
        return top == capacity - 1;
    }


    public int size() {
        return top + 1;
    }


    public void clear() {
        elements.clear();
        top = -1;
    }


    public boolean searchRecursive(T element, int index) {
        if (index > top) {
            return false;
        }
        if (elements.get(index).equals(element)) {
            return true;
        }
        return searchRecursive(element, index + 1);
    }


    public void displayRecursive(int index) {
        if (index > top) {
            return;
        }
        System.out.println("Nivel " + index + ": " + elements.get(index));
        displayRecursive(index + 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pila [");
        for (int i = 0; i <= top; i++) {
            sb.append(elements.get(i));
            if (i < top) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}