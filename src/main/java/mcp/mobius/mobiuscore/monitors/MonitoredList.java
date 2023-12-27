package mcp.mobius.mobiuscore.monitors;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MonitoredList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 8542841497500505801L;

    abstract void addCount(E e);

    abstract void removeCount(int index);

    abstract void removeCount(Object o);

    abstract void printCount();

    abstract void clearCount();

    @Override
    public boolean add(E e) {
        this.addCount(e);
        return super.add(e);
    }

    @Override
    public void add(int index, E e) {
        this.addCount(e);
        super.add(index, e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) this.addCount(e);

        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        for (E e : c) this.addCount(e);

        return super.addAll(index, c);
    }

    @Override
    public void clear() {
        this.clearCount();
        super.clear();
    }

    @Override
    public E remove(int index) {
        this.removeCount(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        this.removeCount(o);
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) this.removeCount(o);

        return super.removeAll(c);
    }
}
