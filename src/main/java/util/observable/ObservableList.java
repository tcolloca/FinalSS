package util.observable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import util.ThreadUtils;

public class ObservableList<T> {

  private final Condition changed;
  private List<T> observed;
  private Lock lock;
  private boolean hasChanged;
  private final List<ObservableListListener<T>> listeners;
  
  public ObservableList(List<T> observed) {
    this.observed = observed;
    this.lock = ThreadUtils.getLock(this);
    this.changed = lock.newCondition();
    this.hasChanged = false;
    this.listeners = new ArrayList<>();
  }
  
  public ObservableList() {
    this(new ArrayList<>());
  }

  public synchronized List<T> await() throws InterruptedException {
    lock.lock();
    changed.await();
    lock.unlock();
    return observed;
  }
  
  public void add(T newValue) {
    lock.lock();
    observed.add(newValue);
    hasChanged = true;
    changed.signalAll();
    for (ObservableListListener<T> listener : listeners) {
      listener.listen(observed, newValue);
    }
    lock.unlock();
  }

  public synchronized List<T> get() {
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
  
  public void addListener(ObservableListListener<T> listener) {
    listeners.add(listener);
  }
}
