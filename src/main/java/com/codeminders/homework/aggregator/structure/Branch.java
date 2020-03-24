package com.codeminders.homework.aggregator.structure;

import java.io.File;
import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents an abstract tree node and incorporates source file/folder with associated information for
 * aggregation/print-out.
 */
public final class Branch {

    /**
     * Required for nice indentation.
     */
    private final long level;
    private final File sourceFile;
    private final Branch parent;
    /**
     * Indicates empty sub-branches to be truncated during pretty print.
     */
    private final AtomicBoolean eligibleForGarbageCollection = new AtomicBoolean(true);
    /**
     * Aggregate number of lines descending from children. Happens in asynchronous fashion.
     */
    private final AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<String> error = new AtomicReference<>(null);
    /**
     * In order for Directories to come first they inserted first, for this matter Linked list was chosen
     * because of its asymptotic performance.
     */
    private final Deque<Branch> children = new LinkedList<>();

    public Branch(File sourceFile, Branch parent) {
        this.parent = parent;
        this.sourceFile = sourceFile;
        if (sourceFile == null) {
            throw new IllegalArgumentException("Empty source file not allowed");
        }
        if (parent == null) {
            level = 1;
            return;
        } else {
            level = parent.level + 1;
        }
        if (sourceFile.isFile()) {
            parent.children.addFirst(this);
        } else {
            parent.children.addLast(this);
        }
    }

    public void addToSum(BigDecimal amount) {
        Branch thisLevel = this;
        while (thisLevel != null) {// drill down loop to the root
            thisLevel.sum.getAndUpdate((x) -> amount.add(x));
            thisLevel.eligibleForGarbageCollection.set(false);
            thisLevel = thisLevel.parent;
        }
    }

    public Deque<Branch> getChildren() {
        return children;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public AtomicReference<BigDecimal> getSum() {
        return sum;
    }

    public AtomicReference<String> getError() {
        return error;
    }

    public long getLevel() {
        return level;
    }

    public AtomicBoolean getEligibleForGarbageCollection() {
        return eligibleForGarbageCollection;
    }
}
