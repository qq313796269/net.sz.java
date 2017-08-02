/*
 * Copyright (c) 2008, Keith Woodward
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of Keith Woodward nor the names
 *    of its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.sz.framework.way.navmesh.util;

import java.io.Serializable;

/**
 * A binary minheap of comparable objects.
 *
 * Source from Donald Chinn, at the University of Washington. Modified by Keith
 * Woodward from www.javagaming.org
 *
 * @author Donald Chinn
 * @author Keith Woodward
 * @version September 19, 2003
 * @param <E>
 */
public class BinaryHeap<E extends Comparable> implements Serializable {

    /* The heap is organized using the implicit array implementation.
     * Array index 0 is not used
     */
    public E[] elements;
    public int size;       // index of last element in the heap

    public BinaryHeap() {
        this(10);
    }

    /**
     * Constructor
     *
     * @param capacity number of active elements the heap can contain
     */
    public BinaryHeap(int capacity) {
        this.elements = (E[]) new Comparable[capacity + 1];
        this.elements[0] = null;
        this.size = 0;
    }

    /**
     * Given an array of Comparables, return a binary heap of those elements.
     *
     * @param data an array of data (no particular order)
     * @return a binary heap of the given data
     */
    public static BinaryHeap buildHeap(Comparable[] data) {
        BinaryHeap newHeap = new BinaryHeap(data.length);
        System.arraycopy(data, 0, newHeap.elements, 1, data.length);
        newHeap.size = data.length;
        for (int i = newHeap.size / 2; i > 0; i--) {
            newHeap.percolateDown(i);
        }
        return newHeap;
    }

    /**
     * Determine whether the heap is empty.
     *
     * @return true if the heap is empty; false otherwise
     */
    public boolean isEmpty() {
        return (size < 1);
    }

    protected void doubleCapacityIfFull() {
        if (size >= elements.length - 1) {
            // not enough room -- create a new array and copy
            // the elements of the old array to the new
            E[] newElements = (E[]) new Comparable[2 * size];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }

    /**
     * Insert an object into the heap.
     *
     * @param key a key
     */
    public void add(E key) {
        doubleCapacityIfFull();
        size++;
        elements[size] = key;
        percolateUp(size);
    }

    /**
     * Remove the object with minimum key from the heap.
     *
     * @return the object with minimum key of the heap
     */
    public E deleteMin() throws ArrayIndexOutOfBoundsException {
        if (!isEmpty()) {
            E returnValue = elements[1];
            elements[1] = elements[size];
            size--;
            percolateDown(1);
            return returnValue;

        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public E peekMin() {
        if (!isEmpty()) {
            return elements[1];
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Given an index in the heap array, percolate that key up the heap.
     *
     * @param index an index into the heap array
     */
    public void percolateUp(int index) {
        E temp = elements[index];  // keep track of the item to be moved
        while (index > 1) {
            if (temp.compareTo(elements[index / 2]) < 0) {
                elements[index] = elements[index / 2];
                index = index / 2;
            } else {
                break;
            }
        }
        elements[index] = temp;
    }

    /**
     * Given an index in the heap array, percolate that key down the heap.
     *
     * @param index an index into the heap array
     */
    public void percolateDown(int index) {
        int child;
        E temp = elements[index];

        while (2 * index <= size) {
            child = 2 * index;
            if ((child != size)
                    && (elements[child + 1].compareTo(elements[child]) < 0)) {
                child++;
            }
            // ASSERT: at this point, elements[child] is the smaller of
            // the two children
            if (elements[child].compareTo(temp) < 0) {
                elements[index] = elements[child];
                index = child;
            } else {
                break;
            }
        }
        elements[index] = temp;

    }

    public void makeEmpty() {
        size = 0;
    }

    public boolean contains(Object x) {
        for (int i = 1; i <= size; i++) {
            if (elements[i] == x) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(Object x) {
        for (int i = 1; i <= size; i++) {
            if (elements[i] == x) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the logical size of the heap. The actual size of the underlying
     * array will be at least as big.
     *
     * @return the size of the heap
     */
    public int size() {
        return size;
    }

//    public static void main(String[] args) {
//        BinaryHeap h = new BinaryHeap();
//        for (int i = 120; i >= 100; i--) {
//            h.add(i);
//        }
//        h.size = 0;
//        for (int i = 10; i >= 0; i--) {
//            h.add(i);
//        }
//        int length = ((Comparable[]) h.elements).length;
//        System.out.println("length == " + length + ", h.size == " + h.size);
//        for (int i = 0; i < ((Comparable[]) h.elements).length; i++) {
//            System.out.println("h.array[" + i + "] == " + ((Comparable[]) h.elements)[i]);
//        }
//    }
}
