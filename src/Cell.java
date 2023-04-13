import java.util.List;

public class Cell {
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	
	public Cell(double xMin, double xMax, double yMin, double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	public boolean intersects(double[][] mbr) {
		if (xMax >= mbr[0][0] && mbr[1][0] >= xMin) { 		//an to xMax >= xMin tou mbr kai xMin <= xMax tou mbr
			if (yMax >= mbr[0][1] &&  mbr[1][1] >= yMin) {	//an to yMax >= yMin tou mbr kai yMin <= yMax tou mbr
				return true;
			}
		}
		return false;
	}
	
	public boolean intersects(List<Double> query) {	
		if (xMax >= query.get(0) && query.get(1) >= xMin) { 	//an to xMax >= xMin query kai xMin <= xMax query
			if (yMax >= query.get(2) && query.get(3) >= yMin) {	//an to yMax >= yMin query kai yMin <= yMax query
				return true;
			}
		}
		return false;
	}
	
	public double getXMin() {
		return xMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

	public String toString() {
		return "Cell [xMin=" + xMin + ", xMax=" + xMax + ", yMin=" + yMin + ", yMax=" + yMax + "]";
	}
}
