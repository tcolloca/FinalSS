package util.observable;

import java.util.List;

@FunctionalInterface
public interface ObservableListListener<T> {

  public void listen(List<T> list, T newValue);
}
