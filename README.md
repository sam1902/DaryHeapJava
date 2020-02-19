# D-ary Heap in Java

The d-ary heap or d-heap is a priority queue data structure, a generalization of the binary heap in which 
the nodes have d children instead of 2. 
Thus, a binary heap is a 2-heap, and a ternary heap is a 3-heap.

This data structure allows decrease priority operations to be performed more quickly than binary heaps,
at the expense of slower delete minimum operations. 
This tradeoff leads to better running times for algorithms such as Dijkstra's algorithm 
in which decrease priority operations are more common than delete min operations. 

Additionally, d-ary heaps have better memory cache behavior than binary heaps, 
allowing them to run more quickly in practice despite having a theoretically larger worst-case running time.

Like binary heaps, d-ary heaps are an in-place data structure that uses no additional storage beyond that needed to store the array of items in the heap.

## Optimality

<table>
  <tr>
    <th></th>
    <th>Worst time complexity</th>
  </tr>
  <tr>
    <td><code>insert</code></td>
    <td><code>O(log n / log d)</code></td>
  </tr>
  <tr>
    <td><code>deleteMin</code></td>
    <td><code>O(d log n / log d)</code></td>
  </tr>
    <tr>
      <td><code>heapify</code></td>
      <td><code>O(n)</code></td>
    </tr>
</table>

## Main underlying operations

Sink replaces the node `i` by a bubble, removes its value and pushes the bubble down 
until it finds a suitable spot (where `v` is respecting the heap ordering) for the value `v` to insert.

<img src="/images/sink.png?raw=true" alt="Example sink procedure" width="400"/>

---
Swim adds an empty node (bubble) at the last level and make that bubble rise to the root until
it finds a suitable spot for the new value (where the parent has a lower value than our `v` value).

<img src="/images/swim.png?raw=true" alt="Example swim procedure" width="400"/>


## Formulae necessary for implementation

<table>
  <tr>
    <th></th>
    <th>Index</th>
  </tr>
  <tr>
    <td>child <code>k</code> of node <code>i</code></td>
    <td><code>d*i + k + 1</code></td>
  </tr>
  <tr>
    <td>parent of node <code>i</code></td>
    <td><code>floor((i-1)/d)</code></td>
  </tr>
</table>

Where `0 <= k < d`.

The elements of the heap can therefore be stored in a regular array.

<img src="/images/array_representation.png?raw=true" alt="Example underlying structure" width="400"/>

Example of the underlying structure of the array for a binary heap.

## *HeapSort* sorting
The elements of the heap making the priority queue are in what's called "heap order": every child is 
of higher priority than the parent.

<img src="/images/heap_ordering.png?raw=true" alt="Example heap ordering" width="400"/>

Example of heap ordering with natural integers

If one sets the inner array data structure to an arbitrary array of comparable elements,
in any order. 
One can then call `heapify`, and finally calls `deleteMin` for each element of the queue, the
elements coming will be the sorted elements of the initial array, hence *HeapSort* is born.