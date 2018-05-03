package util.observable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import util.ThreadUtils;

public class Observable<T> {

  private final Condition changed;
  private T observed;
  private Lock lock;
  private boolean hasChanged;
  private final List<ObservableListener<T>> listeners;
  
  public Observable(T observed) {
    this.observed = observed;
    this.lock = ThreadUtils.getLock(this);
    this.changed = lock.newCondition();
    this.hasChanged = false;
    this.listeners = new ArrayList<>();
  }
  
  public Observable() {
    this(null);
  }

  public synchronized T await() throws InterruptedException {
    lock.lock();
    changed.await();
    lock.unlock();
    return observed;
  }
  
  public void update(T newValue) {
    lock.lock();
    T oldValue = observed;
    observed = newValue;
    hasChanged = true;
    changed.signalAll();
    for (ObservableListener<T> listener : listeners) {
      listener.listen(oldValue, newValue);
    }
    lock.unlock();
  }

  public synchronized T get() {
    try {
      lock.lock();
      hasChanged = false;
      return observed;
    } finally {
      lock.unlock();
    }
  }
  
  public boolean hasChanged() {
    return hasChanged;
  }
  
  public void addListener(ObservableListener<T> listener) {
    listeners.add(listener);
  }
}
