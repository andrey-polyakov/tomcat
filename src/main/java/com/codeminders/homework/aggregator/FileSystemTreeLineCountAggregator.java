package com.codeminders.homework.aggregator;

import com.codeminders.homework.LineCount;
import com.codeminders.homework.aggregator.structure.Branch;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This Aggregator incrementally builds up a tree of files with a number associated with each node.
 * Each number represent aggregated number of code lines up to this node.
 */
public class FileSystemTreeLineCountAggregator {
    private ExecutorService executor = Executors.newCachedThreadPool();

    public Branch dfs(File startPoint) throws InterruptedException {
        Branch tree;
        Deque<Branch> directoryStack = new LinkedList<>();
        Branch thisNode = tree = new Branch(startPoint, null);
        do {
            if (thisNode.getSourceFile().isFile()) {
                if (thisNode.getSourceFile().toString().toLowerCase().endsWith(".java")) {
                    continue;
                }
                executor.submit(spawnReader(thisNode));
                continue;
            }
            File[] files = thisNode.getSourceFile().listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    if (file.toString().toLowerCase().endsWith(".java")) {
                        executor.submit(spawnReader(new Branch(file, thisNode)));
                    }
                } else {
                    directoryStack.add(new Branch(file, thisNode));
                }
            }
            thisNode = directoryStack.poll();
        } while (!directoryStack.isEmpty());
        executor.shutdown();
        try {
            while (!executor.awaitTermination(2, TimeUnit.MINUTES)) {
                System.err.println("Please wait just a moment longer");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        return tree;
    }

    private Runnable spawnReader(Branch thisNode) {
        Branch finalThisNode = thisNode;
        return () -> {
            try {
                LineCount.of(finalThisNode.getSourceFile()).ifPresent(x -> finalThisNode.addToSum(x));
                return;
            } catch (IOException e) {
                finalThisNode.getError().set(e.getLocalizedMessage());
            }
        };
    }
}
