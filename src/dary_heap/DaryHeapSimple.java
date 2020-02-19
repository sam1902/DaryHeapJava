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

import java.util.ArrayList;
import java.util.Collections;

/***
 * Implements a simple d-ary heap but it's not compatible with the other abstract data type Java provides
 * @param <T>
 */
public class DaryHeapSimple<T extends Comparable<? super T>> {
    private ArrayList<T> innerArray;
    private final int d;

    public DaryHeapSimple(int d){
        this.d = d;
        this.innerArray = new ArrayList<>();
    }

    public int size(){
        return innerArray.size();
    }

    public void print(){
        // n = \sum^{depth}_{i=0}{d^i} = \frac{1-d^{depth+1}}{1-d}
        // <=>
        // depth = \frac{\ln(n(d-1)+1)}{\ln(d)} - 1
        // then floor it cause it seems to work well when you do.
        // That's how you get the following horror:
        int depth = (int)Math.floor(Math.log(innerArray.size()*(d-1) + 1)/Math.log(d) - 1);
        for (int i = 0; i < depth*(d-1); i++) { System.out.print("\t"); }
        System.out.println(innerArray.get(0));
        _print(new ArrayList<>(Collections.singletonList(0)), depth);
    }

    private void _print(ArrayList<Integer> parents, int depth){
        ArrayList<Integer> children = new ArrayList<>();
        // Jolie indentation
        for (int i = 0; i < depth*(d-1); i++) { System.out.print("\t"); }

        for (int p : parents){
            for (int c = 0; c < d; c++) {
                int child_index = getChild(p, c);
                if (child_index >= innerArray.size()) {
                    System.out.print("x ");
                } else {
                    children.add(child_index);
                    System.out.print(innerArray.get(child_index) + " ");
                }
            }
            System.out.print("\t");
        }
        System.out.println();
        if (depth > 0){
            _print(children, depth-1);
        }
    }

    public void insert(T x){
        innerArray.add(null);
        swim(x, innerArray.size()-1);
    }

    public T deleteMin(){
        assert innerArray.size() > 0;
        T root = innerArray.get(0);
        T last_value = innerArray.get(innerArray.size()-1);
        innerArray.set(innerArray.size()-1, null);
        innerArray.remove(innerArray.size()-1);
        if(innerArray.size() > 0){
            sink(last_value, 0);
        }
        return root;
    }

    /***
     * Return i's parent's index.
     * @param i the child's index which to get the parent of.
     * @return the parent's index.
     */
    private int getParent(int i) {
        // return (int)Math.floor(((double)i-1)/d);
        // equiv to
        return i==0 ? -1 : (i-1)/d;
    }

    /***
     * Returns the k-th child's index of the element at inner_array[i].
     * @param i index of the element which to get the child of.
     * @param k relative index of the child to get, between 0 included and d excluded.
     * @return i's k-th child's index.
     */
    private int getChild(int i, int k){
        return d*i + k + 1;
    }

    /***
     * Returns the index of the smallest child, in the sense of a.compareTo(b)
     * @param i index of the element which to get the children of.
     * @return i's smallest child index.
     */
    private int getMinChild(int i){
        int first_child = getChild(i, 0);
        if (first_child >= innerArray.size()){
            return -1;
        }
        int minChild = first_child;

        int maxIndex = Math.min(first_child+d, innerArray.size());
        for (int j = first_child+1; j < maxIndex; j++) {
            // if minChild > jth child
            if (innerArray.get(minChild).compareTo(innerArray.get(j)) > 0){
                minChild = j;
            }
        }
        return minChild;
    }

    private void sink(T x, int i){
        // placement de x dans innerArray[i, ...]
        // remplacer le nœud par une «bulle», enlever une feuille
        // et pousser la bulle vers les feuilles jusqu’à ce qu’on trouve la place pour la nouvelle valeur
        int child = getMinChild(i);
        while (child > -1 && innerArray.get(child).compareTo(x) < 0){
            innerArray.set(i, innerArray.get(child));
            i = child;
            child = getMinChild(i);
        }
        innerArray.set(i, x);
    }

    private void swim(T x, int i){
        // placement de x dans innerArray[... i]
        // ajouter un nœud vide («bulle») au dernier niveau + monter la bulle vers la
        // racine jusqu’à ce qu’on trouve la place pour la nouvelle valeur (où le parent aune clé inférieure)

        int parent = getParent(i);
        while (parent > -1 && innerArray.get(parent).compareTo(x) > 0){
            innerArray.set(i, innerArray.get(parent));
            i = parent;
            parent = getParent(i);
        }
        innerArray.set(i, x);
    }

    // Met les elements du tableau en ordre de tas.
    private void slow_heapify(){
        // O(nlogn)
        for (int i = 0; i < innerArray.size(); i++) {
            swim(innerArray.get(i), i);
        }
    }

    public void shuffle_inner_array(){
        // Just to test heapify
        Collections.shuffle(innerArray);
    }

    // Met les elements du tableau en ordre de tas.
    public void heapify() {
        // O(n)
        for (int i = getParent(innerArray.size())-1; i > -1; i--) {  // Automatic rounding down of int divided by int
            sink(innerArray.get(i), i);
        }
    }
}
