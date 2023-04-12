import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Record {
	private int id;
	private double[][] mbr;													//stored as [[MBRminX, MBRminY], [MBRmaxX, MBRmaxY]] 
	private List<List<Double>> linestring = new ArrayList<List<Double>>();	//stored as {{X1, Y1}, {X2, Y2},...}

	public Record(int id, double[][] mbr, List<List<Double>> linestring) {
		this.id = id;
		this.mbr = mbr;
		this.linestring = linestring;
	}

	public int getId() {
		return id;
	}

	public double[][] getMbr() {
		return mbr;
	}

	public List<List<Double>> getLinestring() {
		return linestring;
	}

	public String toString() {
		return id + "," + Arrays.deepToString(mbr) + "," + linestring; 
	}
}
