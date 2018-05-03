package model;

public enum Direction {
	  N, W, S, E, NW, NE, SW, SE;

    public static Direction parseDirection(String string) {
      for (Direction direction : values()) {
        if (direction.name().equals(string)) {
          return direction;
        }
      }
      throw new IllegalArgumentException("Unknown direction: " + string);
    }
}