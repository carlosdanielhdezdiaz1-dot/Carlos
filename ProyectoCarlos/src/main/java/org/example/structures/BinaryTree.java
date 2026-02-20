package org.example.structures;

public class BinaryTree {
    private class TreeNode {
        String value;
        int count;
        TreeNode left;
        TreeNode right;

        TreeNode(String value) {
            this.value = value;
            this.count = 1;
            this.left = null;
            this.right = null;
        }
    }

    private TreeNode root;

    public BinaryTree() {
        root = null;
    }


    public void insert(String value) {
        root = insertRecursive(root, value);
    }

    private TreeNode insertRecursive(TreeNode node, String value) {
        if (node == null) {
            return new TreeNode(value);
        }

        int comparison = value.compareTo(node.value);
        if (comparison < 0) {
            node.left = insertRecursive(node.left, value);
        } else if (comparison > 0) {
            node.right = insertRecursive(node.right, value);
        } else {
            node.count++;
        }

        return node;
    }


    public boolean search(String value) {
        return searchRecursive(root, value);
    }

    private boolean searchRecursive(TreeNode node, String value) {
        if (node == null) {
            return false;
        }

        if (value.equals(node.value)) {
            return true;
        }

        return value.compareTo(node.value) < 0
                ? searchRecursive(node.left, value)
                : searchRecursive(node.right, value);
    }


    public void inOrder() {
        System.out.println("🌳 Árbol Binario (Inorden):");
        inOrderRecursive(root);
    }

    private void inOrderRecursive(TreeNode node) {
        if (node != null) {
            inOrderRecursive(node.left);
            System.out.println("  " + node.value + " (" + node.count + ")");
            inOrderRecursive(node.right);
        }
    }


    public void preOrder() {
        preOrderRecursive(root);
    }

    private void preOrderRecursive(TreeNode node) {
        if (node != null) {
            System.out.println(node.value);
            preOrderRecursive(node.left);
            preOrderRecursive(node.right);
        }
    }


    public void postOrder() {
        postOrderRecursive(root);
    }

    private void postOrderRecursive(TreeNode node) {
        if (node != null) {
            postOrderRecursive(node.left);
            postOrderRecursive(node.right);
            System.out.println(node.value);
        }
    }


    public int countNodes() {
        return countNodesRecursive(root);
    }

    private int countNodesRecursive(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodesRecursive(node.left) + countNodesRecursive(node.right);
    }


    public int height() {
        return heightRecursive(root);
    }

    private int heightRecursive(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(heightRecursive(node.left), heightRecursive(node.right));
    }
}