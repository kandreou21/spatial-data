//Konstantinos Andreou 4316
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GridCreator {
	private static final String csvFile = "tiger_roads.csv";
	double minX = Double.POSITIVE_INFINITY;
	double minY = Double.POSITIVE_INFINITY;
	double maxX = Double.NEGATIVE_INFINITY;
	double maxY = Double.NEGATIVE_INFINITY;
	
	public HashMap<Integer, Record> createRecords() {
		ArrayList<String> fileRows = readFile();
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
	
	public Cell[][] createGrid() {
		HashMap<Integer, Record> records =  createRecords();
		double xInterval = (maxX - minX) / 10;
		double yInterval = (maxY - minY) / 10;
		Cell[][] grid = new Cell[10][10];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				double xLow = minX + i*xInterval;
				double yLow = minY + j*yInterval;
				Cell cell = new Cell(xLow, xLow+xInterval, yLow, yLow+yInterval);
				grid[i][j] = cell;
			}
		}
		return grid;
	}
	
	public HashMap<List<Integer>, List<Integer>> findIntersections(HashMap<Integer, Record> records, Cell[][] grid) {
		HashMap<List<Integer>, List<Integer>> cellContents = new HashMap<List<Integer>, List<Integer>>(); //key:cell(0,0), value:ids that fall in cell		

		for (int j = 0; j < grid.length; j++) {			//for each row of grid
			for (int k = 0; k < grid[j].length; k++) {	//for each cell in row
				List<Integer> ids = new ArrayList<Integer>();	//list that contains the ids of mbr's that drop in a cell
				Cell cell = grid[j][k];
				for (int i = 1; i <= records.size(); i++) {		//for each record
					double[][] mbr = records.get(i).getMbr();	
					if (cell.intersects(mbr)) {
						ids.add(records.get(i).getId());		
						List<Integer> gridID = List.of(j, k);
						cellContents.put(gridID, ids);		
					}
				}
			}
		}
		/*
		cellContents.entrySet().forEach(entry -> {
		    System.out.println(entry.getKey() + " " + entry.getValue());
		});
		*/
		//System.out.println(cellContents.size());
		return cellContents;
	}
	
	public void printGrid(Cell[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print("grid[" + i + "," + j + "]: "  + grid[i][j].toString());
				System.out.print("	");
			}
			System.out.println();
		}
	}
	
	public void writeGridGrd(HashMap<List<Integer>, List<Integer>> cellContents, HashMap<Integer, Record> records) throws IOException {
		PrintWriter gridStream = new PrintWriter(new java.io.FileWriter("grid.grd"));
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				List<Integer> gridID = List.of(i, j);
				if (cellContents.containsKey(gridID) == true) {
					List<Integer> ids = cellContents.get(gridID);
					for (int k = 0; k < ids.size(); k++) {
						Record record = records.get(ids.get(k));
						gridStream.println(record);		
					}
				}
			}
		}
		gridStream.close();	
	}
	
	public void writeGridDir(HashMap<List<Integer>, List<Integer>> cellContents) throws IOException {
		PrintWriter gridStream = new PrintWriter(new java.io.FileWriter("grid.dir"));
		gridStream.println(minX + " " + maxX + " " + minY + " " + maxY);	
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				List<Integer> gridID = List.of(i, j);
				if (cellContents.containsKey(gridID) == false) {
					gridStream.println(i + " " + j + " " + 0);
				} else {
					gridStream.println(i + " " + j + " " + cellContents.get(gridID).size());	//x y ids(that fall in cell)
				}
			}
		}
		gridStream.close();
	}
	
	public static void main(String[] args) { //To create grid.dir and grid.grd files
		GridCreator gridCreator = new GridCreator();
		HashMap<Integer, Record> records = gridCreator.createRecords();
		Cell[][] grid = gridCreator.createGrid();
		//gridCreator.printGrid(grid);
		HashMap<List<Integer>, List<Integer>> cellContents = gridCreator.findIntersections(records, grid);
		try {
			gridCreator.writeGridDir(cellContents);
			gridCreator.writeGridGrd(cellContents, records);
		} catch (IOException e) {}
	}
}
