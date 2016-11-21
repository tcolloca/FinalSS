package model;

public interface CellularAutomaton {

	boolean next();
	
	Cell getCell(int i, int j);
}
