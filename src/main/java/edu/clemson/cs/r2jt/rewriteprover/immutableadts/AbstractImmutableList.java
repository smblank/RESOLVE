/*
 * AbstractImmutableList.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.immutableadts;

import java.util.Iterator;

public abstract class AbstractImmutableList<E> implements ImmutableList<E> {

    @Override
    public ImmutableList<E> appended(E e) {
        return appended(new SingletonImmutableList<E>(e));
    }

    @Override
    public ImmutableList<E> appended(ImmutableList<E> l) {
        return new ImmutableListConcatenation<E>(this, l);
    }

    @Override
    public ImmutableList<E> appended(Iterable<E> i) {
        return appended(new ArrayBackedImmutableList<E>(i));
    }

    @Override
    public E first() {
        return get(0);
    }

    @Override
    public ImmutableList<E> removed(int index) {
        ImmutableList<E> retval;

        if (index == 0) {
            retval = tail(1);
        }
        else if (index == size() - 1) {
            retval = head(index);
        }
        else {
            retval = new ImmutableListConcatenation<E>(head(index),
                    tail(index + 1));
        }

        return retval;
    }

    @Override
    public ImmutableList<E> set(int index, E e) {
        ImmutableList<E> first, second;

        ImmutableList<E> insertedList = new SingletonImmutableList<E>(e);

        if (index == 0) {
            first = insertedList;
            second = tail(1);
        }
        else if (index == size() - 1) {
            first = head(index);
            second = insertedList;
        }
        else {
            first = new ImmutableListConcatenation<E>(head(index),
                    insertedList);
            second = tail(index + 1);
        }

        return new ImmutableListConcatenation<E>(first, second);
    }

    @Override
    public ImmutableList<E> insert(int index, E e) {
        return insert(index, new SingletonImmutableList<E>(e));
    }

    @Override
    public ImmutableList<E> insert(int index, ImmutableList<E> l) {
        ImmutableList<E> first, second;

        if (index == 0) {
            first = l;
            second = this;
        }
        else if (index == size()) {
            first = this;
            second = l;
        }
        else {
            first = new ImmutableListConcatenation<E>(head(index), l);
            second = tail(index);
        }

        return new ImmutableListConcatenation<E>(first, second);
    }

    @Override
    public ImmutableList<E> subList(int startIndex, int length) {
        return tail(startIndex).head(length);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("[");

        int sizeSanityCheck = 0;

        boolean first = true;
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            if (!first) {
                buffer.append(", ");
            }

            buffer.append(iterator.next());

            first = false;
            sizeSanityCheck++;
        }

        buffer.append("]");

        if (sizeSanityCheck != size()) {
            throw new RuntimeException();
        }

        return buffer.toString();
    }
}
