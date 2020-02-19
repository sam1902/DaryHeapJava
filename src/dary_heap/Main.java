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

import java.util.Arrays;

public class Main {
    // Ref https://www.wikiwand.com/en/D-ary_heap
    public static void main(String[] args) {
        int arity = 4;
        PriorityQueue<Integer> myQueue = new PriorityQueue<>(arity);
        Integer[] vals = new Integer[]{9, 1, 5, 2, 0, 19, 24, 17};
        myQueue.addAll(Arrays.asList(vals));

        System.out.println("The " + arity + "-ary heap looks like:\n" + myQueue);

        System.out.println("For now it has capacity " + myQueue.getCapacity());
        System.out.println("And its size is " + myQueue.size());

        System.out.print("You can add values to it.. ");
        myQueue.offer(-4);

        System.out.println(" and then get the minimum without deleting it: " + myQueue.peek());
        System.out.println("And then remove it: " + myQueue.poll());

        System.out.println("Iterates over all elements in any order:");
        for (Integer i : myQueue){
            System.out.print(i + " ,");
        }
        System.out.println();
        System.out.println("Now its capacity is still " + myQueue.getCapacity());
        System.out.println("and its size is still " + myQueue.size());

        System.out.println("Iterates over all elements in priority order and dequeues them whilst doing so:");
        for (Integer i : myQueue.dequeuer()){
            System.out.print(i + " ,");
        }
        System.out.println();

        System.out.println("Now its capacity is still " + myQueue.getCapacity());
        System.out.println("but its size is " + myQueue.size());


    }
}
