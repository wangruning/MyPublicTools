package io.l0neman.utils.stay.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by l0neman on 2019/05/30.
 * <p>
 * closeable util.
 */
public class Closer {

  private Closeable[] processStack = new Closeable[10];
  private int index = 0;

  /**
   * make util instance.
   *
   * @return instance.
   */
  public static Closer create() {
    return new Closer();
  }

  /**
   * Register closeables that needs to be closed.
   *
   * @param closeable target closeable.
   * @param <T>       target type.
   * @return target closeable.
   */
  public <T extends Closeable> T register(T closeable) {
    if (index >= processStack.length) {
      throw new AssertionError("so many closeable, max 10.");
    }

    processStack[index++] = closeable;
    return closeable;
  }

  /**
   * Close all closeables that need to be closed.
   *
   * @throws RuntimeException The first exception generated by closeable.
   */
  public void close() throws RuntimeException {
    Throwable first = null;
    for (int i = index - 1; i >= 0; i--) {
      final Closeable closeable = processStack[i];

      try {
        closeIgnoreIOException(closeable);
      } catch (RuntimeException e) {
        first = e;
      }
      processStack[i] = null;
    }

    if (first != null) {
      throw new RuntimeException(first);
    }
  }

  private static void closeIgnoreIOException(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException ignore) {}
    }
  }
}
