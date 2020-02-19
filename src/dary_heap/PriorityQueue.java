/*
 * Copyright (c) 2020 Samuel Prevost.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dary_heap;

import java.util.*;

/***
 * Implements a priority queue using a d-ary heap.
 * This implementation is compatible with the other abstract data type Java provides
 * since it tries to follow the basic java.util.dary_heap.PriorityQueue interface and method name
 * @param <T>
 */
public class PriorityQueue<T> extends AbstractQueue<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 10;

    private final int arity;
    // All things consider, using an ArrayList to manage the growth is better (c.f. DaryHeapSimple)
    private int maxIndex;
    private Object[] queue;
    private Comparator<? super T> comparator;

    public PriorityQueue(int arity){
        this(arity, DEFAULT_INITIAL_CAPACITY, null);
    }

    public PriorityQueue(int arity, int initialCapacity){
        this(arity, initialCapacity, null);
    }

    public PriorityQueue(int arity, int initialCapacity, Comparator<? super T> comparator){
        if (arity < 2 || initialCapacity < 0)
            throw new IllegalArgumentException();
        this.arity = arity;
        this.comparator = comparator == null ? (Comparator<? super T>)Comparator.naturalOrder() : comparator;
        this.queue = new Object[initialCapacity];
        this.maxIndex = -1;
    }



    /**
     * Returns an iterator over the elements contained in this collection.
     * The element are iterated over IN NO PARTICULAR ORDERED !
     *
     * @return an unordered iterator over the elements contained in this collection
     */
    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    private final class Itr implements Iterator<T> {
        private int index = 0;

        Itr() {}

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return index <= maxIndex;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return (T) queue[index++];
        }
    }

    /***
     * Creates an Iterable that will dequeue (in priority order) each and every element and return it.
     * Pretty useful in combination with a for loop such as
     *      for(T elem : myqueue.dequeuer()){
     *          ...
     *      }
     * Of course, the elements from `myqueue` will be dequeued when you iterate over them as such.
     * @return an Iterable which dequeues elements one by one.
     */
    public Iterable<T> dequeuer() {
        return new PQWithDequeue<>(this);
    }
    /* A subclass that it just an Iterable for the elements,
        as a way to provide a second "iterator" method to the PQ */
    private final class PQWithDequeue<T> implements Iterable<T> {
        PriorityQueue<T> pq;
        public PQWithDequeue(PriorityQueue<T> pq) {
            this.pq = pq;
        }

        public Iterator<T> iterator() { return new ItrDequeue(); }
        /* A subsubclass that *is* the dequeueing Iterator itself
        *  pretty obscure stuff I gotta admit */
        private final class ItrDequeue implements Iterator<T> {

            /**
             * Returns {@code true} if the iteration has more elements.
             * (In other words, returns {@code true} if {@link #next} would
             * return an element rather than throwing an exception.)
             *
             * @return {@code true} if the iteration has more elements
             */
            @Override
            public boolean hasNext() {
                return pq.peek() != null;
            }

            /**
             * Returns the next element in the iteration.
             *
             * @return the next element in the iteration
             * @throws NoSuchElementException if the iteration has no more elements
             */
            @Override
            public T next() {
                return pq.poll();
            }
        }
    }

    @Override
    public int size() {
        return maxIndex+1;
    }

    /**
     * The maximum current capacity of the queue.
     * The queue will try to expand when inserting an element
     * that it hasn't yet the capacity to handle.
     *
     * @return the maximum current capacity of the queue.
     */
    public int getCapacity(){
        return queue.length;
    }

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions.
     * When using a capacity-restricted queue, this method is generally
     * preferable to {@link #add}, which can fail to insert an element only
     * by throwing an exception.
     *
     * @param o the element to add
     * @return {@code true} if the element was added to this queue, else
     * {@code false}
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null and
     *                                  this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *                                  prevents it from being added to this queue
     * @throws OutOfMemoryError         if the queue is full
     *
     */
    @Override
    public boolean offer(Object o) {
        if (o == null){
            throw new NullPointerException();
        }
        try {
            // Just try to cast the element and see if it fails at runtime
            T dummy = (T) o;
        } catch (ClassCastException e){
            throw new ClassCastException();
        }
        grow(); // throws OutOfMemoryError
        swim(o, maxIndex);
        return true;
    }

    /**
     * Retrieves and removes the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    @Override
    public T poll() {
        if (maxIndex <= -1){
            return null;
        }
        T root = (T)queue[0];
        Object last = queue[maxIndex];
        queue[maxIndex] = null;
        maxIndex--;
        if (maxIndex > -1)
            sink(last, 0);
        return root;
    }

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    @Override
    public T peek() {
        if (maxIndex == -1) {
            return null;
        } else {
            return (T)queue[0];
        }
    }

    /***
     * Return i's parent's index.
     *
     * @param i the child's index which to get the parent of.
     * @return the parent's index, or -1 if there is no parent.
     */
    private int getParent(int i) {
        return i==0 ? -1 : (i-1)/arity;
    }

    /***
     * Returns the k-th child's index of the element at inner_array[i].
     *
     * @param i index of the element which to get the child of.
     * @param k relative index of the child to get, between 0 included and d excluded.
     * @return i's k-th child's index, or -1 if there is no k-th child.
     */
    private int getChild(int i, int k){
        int index = arity*i + k + 1;
        return index > maxIndex ? -1 : index;
    }

    /***
     * Returns i's child which has minimum value compared to the others.
     * @param i index of the element which to get the child of.
     * @return i's minimum child's index, or -1 if there is no child whatsoever.
     */
    private int getMinChild(int i){
        int minChild = getChild(i, 0);
        if (minChild <= -1)
            return -1;

        T minChildValue = (T)queue[minChild];
        for (int k = 1; k < arity; k++) {
            int otherChild = getChild(i, k);
            if (otherChild <= -1)
                break;
            T otherChildValue = (T)queue[otherChild];
            if (comparator.compare(otherChildValue, minChildValue) < 0){
                minChild = otherChild;
                minChildValue = otherChildValue;
            }
        }
        return minChild;
    }

    private void sink(Object value, int i){
        int minChild = getMinChild(i);
        while (minChild > -1 && comparator.compare((T)queue[minChild], (T)value) < 0){
            queue[i] = queue[minChild];
            i = minChild;
            minChild = getMinChild(i);
        }
        queue[i] = value;
    }

    private void swim(Object value, int i){
        int parent = getParent(i);
        while (parent > -1 && comparator.compare((T)queue[parent], (T)value) > 0){
            queue[i] = queue[parent];
            i = parent;
            parent = getParent(i);
        }
        queue[i] = value;
    }

    public void heapify(){
        for (int i = getParent(maxIndex+1)-1; i > -1; i--) {
            sink(queue[i], i);
        }
    }

    public void shuffle_inner_array(){
        // Just to test heapify
        queue = Arrays.copyOf(queue, maxIndex+1);
        Collections.shuffle(Arrays.asList(queue));
    }

    /**
     * Increases the capacity of the array.
     */
    private void grow() throws OutOfMemoryError {
        maxIndex++;
        if (maxIndex >= queue.length) {
            // Double size if small; else grow by 50%
            int newCapacity = queue.length
                    + (queue.length < 64 ? queue.length + 1 : queue.length >> 1);
            queue = Arrays.copyOf(queue, newCapacity);
        }
    }

    private void printQ(){
        System.out.print("[ ");
        for (Object e : queue){
            System.out.print(e + ", ");
        }
        System.out.println(" ]");
    }

    public String toString(){
        // n = \sum^{depth}_{i=0}{d^i} = \frac{1-d^{depth+1}}{1-d}
        // <=>
        // depth = \frac{\ln(n(d-1)+1)}{\ln(d)} - 1
        // then floor it cause it seems to work well when you do.
        // That's how you get the following horror:
        int maxDepth = (int)Math.floor(Math.log((maxIndex+1)*(arity-1) + 1)/Math.log(arity) - 1);
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < maxDepth*(arity-1); i++) {
            str.append('\t');
        }
        str.append(queue[0]).append('\n');
        ArrayList<Integer> parents = new ArrayList<>(Collections.singletonList(0));

        for (int depth = maxDepth; depth > -1; depth--) {
            ArrayList<Integer> children = new ArrayList<>();
            // Jolie indentation
            for (int i = 0; i < depth*(arity-1); i++) { str.append('\t'); }

            for (int p : parents){
                for (int c = 0; c < arity; c++) {
                    int child_index = getChild(p, c);
                    if (child_index <= -1) {
                        str.append("x ");
                    } else {
                        children.add(child_index);
                        str.append(queue[child_index]).append(" ");
                    }
                }
                str.append('\t');
            }
            str.append('\n');
            parents = children;
        }
        return str.toString();
    }
}
