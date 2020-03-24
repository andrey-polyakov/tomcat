package com.codeminders.homework;

import com.codeminders.homework.aggregator.FileSystemTreeLineCountAggregator;
import com.codeminders.homework.aggregator.structure.Branch;

import java.io.File;
import java.util.Deque;
import java.util.LinkedList;

public class Launcher {

    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.err.println("Please provide file/folder name for traversal to start from");
            return;
        }
        File startingDir = new File(args[0]);
        FileSystemTreeLineCountAggregator aggregator = new FileSystemTreeLineCountAggregator();
        Branch thisNode = aggregator.dfs(startingDir);
        Deque<Branch> stack = new LinkedList<>();
        stack.addLast(thisNode);
        do {
            thisNode = stack.remove();
            if (thisNode.getEligibleForGarbageCollection().get()) {
                continue;
            }
            printOut(thisNode);
            if (!thisNode.getChildren().isEmpty()) {
                for (Branch child : thisNode.getChildren()) {
                    if (child.getSourceFile().isFile()) {
                        printOut(child);
                    } else {
                        if (stack.peek() == null || stack.peek().getLevel() < child.getLevel()) {
                            stack.addFirst(child);
                        } else {
                            stack.addFirst(child);
                        }
                    }
                }
            }
        } while (!stack.isEmpty());

    }

    private static void printOut(Branch thisNode) {
        Object error = thisNode.getError().get();
        String associatedValue = error == null ? thisNode.getSum().get().toString() : error.toString();
        String padded = String.format("%s %s : %s", pad(thisNode.getLevel() * 2),
                thisNode.getSourceFile().getName(),
                associatedValue);
        System.out.println(padded);
    }

    public static String pad(long length) {
        return String.format("%1$" + length + "s", " ");
    }

}
