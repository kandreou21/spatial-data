import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Record {
	private int id;
	private double[][] mbr;													//stored as [[MBRminX MBRminY],[MBRmaxX MBRmaxY]] 
	private List<List<Double>> linestring = new ArrayList<List<Double>>();	//stored as {{X1, Y1},{X2, Y2},...}

	public Record(int id, double[][] mbr, List<List<Double>> linestring) {
		this.id = id;
		this.mbr = mbr;
		this.linestring = linestring;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double[][] getMbr() {
		return mbr;
	}

	public void setMbr(double[][] mbr) {
		this.mbr = mbr;
	}

	public List<List<Double>> getLinestring() {
		return linestring;
	}

	public void setLinestring(List<List<Double>> linestring) {
		this.linestring = linestring;
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", mbr=" + Arrays.deepToString(mbr) + ", linestring=" + linestring + "]";
	}
}
