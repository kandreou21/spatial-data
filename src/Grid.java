import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Grid {
	private static final String csvFile = "tiger_roads.csv";
	private double minX = Double.POSITIVE_INFINITY;
	private double minY = Double.POSITIVE_INFINITY;
	private double maxX = Double.NEGATIVE_INFINITY;
	private double maxY = Double.NEGATIVE_INFINITY;
	
	public HashMap<Integer, Record> parseFile() {
		ArrayList<String> fileRows = readFile();
		HashMap<Integer, Record> records = createRecords(fileRows);
		double xInterval = (maxX - minX) / 10;
		double yInterval = (maxY - minY) / 10;
		String[][] grid = new String[11][11];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				double x = minX + i*xInterval;
				double y = minY + j*yInterval;
				grid[i][j] = (x + "," + y);
			}
		}
		/*
		System.out.println("minX: " + minX + " minY: " + minY + " maxX: " + maxX + " maxY: " + maxY);
		//System.out.println(xRange + "  " + yRange);
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print("grid[" + i + "," + j + "]: "  + grid[i][j]);
				System.out.print("	");
			}
			System.out.println();
		}
		*/
		return records;
	}

	private ArrayList<String> readFile() {
		BufferedReader br;
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		try {
			br = new BufferedReader(new FileReader(csvFile));	
			line = br.readLine();
			while ((line = br.readLine()) != null) {
			    lines.add(line);		
			}
		} catch (IOException e) {  
			e.printStackTrace();
		}
		return lines;
	}	
	
	private HashMap<Integer, Record> createRecords(ArrayList<String> fileRows) {
		List<List<Double>> linestring;
		HashMap<Integer, Record> records = new HashMap<Integer, Record>();
		
		for (int i = 0; i < fileRows.size(); i++) {
			linestring = new ArrayList<List<Double>>();
			double MBRminX = Double.POSITIVE_INFINITY;
            double MBRminY = Double.POSITIVE_INFINITY;
            double MBRmaxX = Double.NEGATIVE_INFINITY;
            double MBRmaxY = Double.NEGATIVE_INFINITY;
            
			String[] points = fileRows.get(i).split(",");
			for (int j = 0; j < points.length; j++) {
				String[] coords = points[j].split(" ");
				double x = Double.parseDouble(coords[0]);
				double y = Double.parseDouble(coords[1]);
				if (x < MBRminX) {
					MBRminX = x;
					if (x < minX)
						minX = x;
                }
                if (y < MBRminY) {
                	MBRminY = y;
                	if (y < minY)	
                		minY = y;
                }
                if (x > MBRmaxX) {
                	MBRmaxX = x;
                	if (x > maxX)
                		maxX = x;
                }
                if (y > MBRmaxY) {
                	MBRmaxY = y;
                	if (y > maxY)
                		maxY = y;
                }
                List<Double> point = List.of(x, y);
                linestring.add(point);        
			}
			double[][] mbr = {{MBRminX, MBRminY}, {MBRmaxX, MBRmaxY}};
			Record record = new Record(i+1, mbr, linestring);
			records.put(record.getId(), record);
		}
		return records;
	}	
}
