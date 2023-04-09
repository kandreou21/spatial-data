import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
	public static void main(String args[]) {
		/*
		double[][] mbr = {{0, 1},
						  {1, 0}};
		
		ArrayList<Double> list1 = new ArrayList<Double>();
		list1.add(5.0);
		list1.add(5.0);
		
		ArrayList<Double> list2 = new ArrayList<Double>();
		list2.add(10.0);
		list2.add(10.0);
		
		List<List<Double>> linestring = new ArrayList<List<Double>>();
		linestring.add(list1);
		linestring.add(list2);
				
		Record record = new Record(1, mbr, linestring);
		System.out.println(record.toString());
		*/
		
		Grid grid = new Grid();
		HashMap<Integer, Record> records = grid.parseFile();
		/*
		records.entrySet().forEach(entry -> {
		    System.out.println(entry.getKey() + " " + entry.getValue());
		});
		*/
	}
}
