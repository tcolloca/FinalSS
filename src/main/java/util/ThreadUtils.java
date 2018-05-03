package util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadUtils {

  private static final ThreadUtils instance;

  private final ExecutorService execService;
  private final Map<Object, Lock> lockMap;
  private final Map<Object, CountDownLatch> countDownLatchMap;
  private final Map<Object, Semaphore> semaphoreMap;
  private final Map<Lock, Condition> conditionMap;


  static {
    instance = new ThreadUtils();
  }

  private ThreadUtils() {
    execService = Executors.newCachedThreadPool();
    lockMap = new HashMap<>();
    countDownLatchMap = new HashMap<>();
    semaphoreMap = new HashMap<>();
    conditionMap = new HashMap<>();
  }

  public static synchronized ExecutorService getExecutorService() {
    return instance.execService;
  }

  public static synchronized void submit(Runnable runnable) {
    instance.execService.submit(() -> {
      try {
        runnable.run();
      } catch (Throwable th) {
        th.printStackTrace();
      }
    });
  }

  public static synchronized void submit(Callable<?> callable) {
    instance.execService.submit(() -> {
      try {
        callable.call();
      } catch (Throwable th) {
        th.printStackTrace();
      }
    });
  }

  public static Condition getCondition(Object o) {
    instance.conditionMap.putIfAbsent(getLock(o), getLock(o).newCondition());
    System.out.println(instance.conditionMap.get(getLock(o)));
    return instance.conditionMap.get(getLock(o));
  }

  public static synchronized Lock getLock(Object o) {
    instance.lockMap.putIfAbsent(o, new ReentrantLock());
    return instance.lockMap.get(o);
  }

  public static synchronized CountDownLatch getCountDownLatch(Object o, int n) {
    instance.countDownLatchMap.putIfAbsent(o, new CountDownLatch(n));
    return instance.countDownLatchMap.get(o);
  }

  public static Semaphore getSemaphore(Object o, int n) {
    instance.semaphoreMap.putIfAbsent(o, new Semaphore(n));
    return instance.semaphoreMap.get(o);
  }
}
