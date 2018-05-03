package util.observable;

@FunctionalInterface
public interface ObservableListener<T> {

  public void listen(T oldValue, T newValue);
}
