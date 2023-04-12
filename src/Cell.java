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
		if (xMax >= mbr[0][0] && mbr[1][0] >= xMin) { 		//an to xMax cell >= xMin tou mbr kai xMin cell <= xMax tou mbr
			if (yMax >= mbr[0][1] &&  mbr[1][1] >= yMin) {	//an to yMax cell >= yMin tou mbr kai yMin cell <= yMax tou mbr
				return true;
			}
		}
		return false;
	}
	
	public boolean intersects(List<Double> query, int index) {	
		if (xMax >= query.get(index) && query.get(index+1) >= xMin) { 		//an to xMax cell >= xMin query kai xMin cell <= xMax query
			if (yMax >= query.get(index+2) && query.get(index+3) >= yMin) {	//an to yMax cell >= yMin query kai yMin cell <= yMax query
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
