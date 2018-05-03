package model;

public class Cell {

	private final double value;
	private final Direction direction;
	
	public Cell(double value, Direction direction) {
		super();
		this.value = value;
		this.direction = direction;
	}

	public Cell(double value) {
		this(value, null);
	}

	public double getValue() {
		return value;
	}

	public Direction getDirection() {
		return direction;
	}
}	
