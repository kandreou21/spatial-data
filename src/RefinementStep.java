//Konstantinos Andreou 4316
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class RefinementStep {
	private GridCreator gridCreator;
	private Cell[][] grid;
	private HashMap<List<Integer>, List<Integer>> cellContents;
	
	public RefinementStep() {
		this.gridCreator = new GridCreator();
		cellContents = new HashMap<List<Integer>, List<Integer>>();
	}
	
	public void loadGrid() throws IOException {	 
		Scanner dirScanner = new Scanner(new File("grid.dir"));
		Scanner grdScanner = new Scanner(new File("grid.grd"));
		
		String[] firstLine = dirScanner.nextLine().split(" ");
		double xInterval = (Double.parseDouble(firstLine[1]) - Double.parseDouble(firstLine[0])) / 10;
		double yInterval = (Double.parseDouble(firstLine[3]) - Double.parseDouble(firstLine[2])) / 10;
		grid = new Cell[10][10];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				double xLow = Double.parseDouble(firstLine[0]) + i*xInterval;
				double yLow = Double.parseDouble(firstLine[2]) + j*yInterval;
				Cell cell = new Cell(xLow, xLow+xInterval, yLow, yLow+yInterval);
				grid[i][j] = cell;
			}
		}
		while (dirScanner.hasNextLine()) { 
			String[] dirLineFields = dirScanner.nextLine().split(" ");
			int x = Integer.parseInt(dirLineFields[0]);
			int y = Integer.parseInt(dirLineFields[1]);
			int numberOfRecords = Integer.parseInt(dirLineFields[2]);
			
			List<Integer> ids = new ArrayList<Integer>();
			List<Integer> gridID = List.of(x, y);
			for (int i = 0; i < numberOfRecords; i++) {
				int id = Integer.parseInt(grdScanner.nextLine().split(",\\[")[0]);
				ids.add(id);
			}
			cellContents.put(gridID, ids);
		}
		/*
		cellContents.entrySet().forEach(entry -> {
		    System.out.println(entry.getKey() + " " + entry.getValue()); 
		});		
		*/
		dirScanner.close();
		grdScanner.close();
	}

	private List<Double> readQueries() throws IOException {	
		Scanner queryScanner = new Scanner(new File("queries.txt"));
		List<Double> queries = new ArrayList<Double>();
		while (queryScanner.hasNextLine()) { 
			String[] querySplits = queryScanner.nextLine().split(",");
			String[] queryBounds = querySplits[1].split(" ");
			for (int i = 0; i < queryBounds.length; i++) {
				queries.add(Double.parseDouble(queryBounds[i]));
			}
		}
		queryScanner.close();
		return queries;		//each query is 4 elements in list(xmin, xmax, ymin, ymax)
	}
	
	public void answerQueries() {
		List<Double> queries = new ArrayList<Double>();
		try {
			queries = readQueries();
		} catch (IOException e) {System.out.println("Unable to read queries.txt");}
		
		System.out.println("Meros 3\n");
		HashMap<Integer, Record> records = gridCreator.createRecords();
		int queryNum = 1;
		for (int i = 0; i < queries.size(); i+=4) {
			int numCells = 0;
			List<Integer> ids = new ArrayList<Integer>();
			List<Integer> refinementStepIds = new ArrayList<Integer>();
			for (List<Integer> key : cellContents.keySet()) {	//for each key(where key is the cell's coordinates, ie (0,0))
			    int x = key.get(0);
			    int y = key.get(1);
				if (cellContents.get(key).size() != 0) {	//if cell has a mapping to at least one id
					List<Double> query = List.of(queries.get(i), queries.get(i+1), queries.get(i+2), queries.get(i+3)); //xmin, xmax, ymin, ymax
					if (grid[x][y].intersects(query)) {
						numCells++;
						for (Integer id : cellContents.get(key)) {	
							double[][] mbr = records.get(id).getMbr();
							if (mbr[1][0] >= queries.get(i) && queries.get(i+1) >= mbr[0][0]) { 			//an to xMax mbr >= xMin query kai xMin mbr <= xMax query
								if (mbr[1][1] >= queries.get(i+2) && queries.get(i+3) >= mbr[0][1]) {		//an to yMax mbr >= yMin query kai yMin mbr <= yMax query
									double[] referencePoint = {Math.max(mbr[0][0], queries.get(i)), Math.max(mbr[0][1], queries.get(i+2))};  
									if (referencePoint[0] >= grid[x][y].getXMin() && referencePoint[0] <= grid[x][y].getXMax() && referencePoint[1] >= grid[x][y].getYMin() && referencePoint[1] <= grid[x][y].getYMax()) {
										ids.add(id);	
										if ((mbr[0][0] >= queries.get(i) && mbr[1][0] <= queries.get(i+1)) || (mbr[0][1] >= queries.get(i+2) && mbr[1][1] <= queries.get(i+3))) { //query contains x or y axis of mbr
											refinementStepIds.add(id);
										} else {
											List<List<Double>> linestring = records.get(id).getLinestring();
											for (int j = 0; j < linestring.size()-1; j++) {
												List<List<Double>> lineSegment = List.of(linestring.get(j), linestring.get(j+1));	
												if (intersects(lineSegment, query)) {
													refinementStepIds.add(id);
													break;
												}
											}
										}
									}
								}
							}
						}
			    	}
			    }
			}
			System.out.println("Query " + queryNum + " results: \n" + refinementStepIds);			
			System.out.println("Cells: " + numCells);
			System.out.println("Results: " + refinementStepIds.size());
			System.out.println("----------");
			queryNum++;
		}
	}
	
	private boolean intersects(List<List<Double>> lineSegment, List<Double> query) {
		double t = (((lineSegment.get(0).get(0)-query.get(0)) * (query.get(3)-query.get(3))) - ((lineSegment.get(0).get(1)-query.get(3)) * (query.get(0)-query.get(1)))) / 
				   (((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(3)-query.get(3))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(0)-query.get(1))));
		
		double u = (((lineSegment.get(0).get(0)-query.get(0)) * (lineSegment.get(0).get(1)-lineSegment.get(1).get(1))) - ((lineSegment.get(0).get(1)-query.get(3)) * (lineSegment.get(0).get(0)-lineSegment.get(1).get(0)))) / 
				   (((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(3)-query.get(3))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(0)-query.get(1))));
		if ((t >= 0 && t <= 1) && (u >= 0 && u <= 1)) {
			return true;
		} //window's upper line segment
		
		t = (((lineSegment.get(0).get(0)-query.get(0)) * (query.get(2)-query.get(2))) - ((lineSegment.get(0).get(1)-query.get(2)) * (query.get(0)-query.get(1)))) / 
			(((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(2)-query.get(2))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(0)-query.get(1))));
		
		u = (((lineSegment.get(0).get(0)-query.get(0)) * (lineSegment.get(0).get(1)-lineSegment.get(1).get(1))) - ((lineSegment.get(0).get(1)-query.get(2)) * (lineSegment.get(0).get(0)-lineSegment.get(1).get(0)))) / 
			(((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(2)-query.get(2))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(0)-query.get(1))));
		if ((t >= 0 && t <= 1) && (u >= 0 && u <= 1)) {
			return true;
		} //window's lower line	segment
		
		t = (((lineSegment.get(0).get(0)-query.get(0)) * (query.get(2)-query.get(3))) - ((lineSegment.get(0).get(1)-query.get(2)) * (query.get(0)-query.get(0)))) / 
			(((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(2)-query.get(3))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(0)-query.get(0))));	
		
		u = (((lineSegment.get(0).get(0)-query.get(0)) * (lineSegment.get(0).get(1)-lineSegment.get(1).get(1))) - ((lineSegment.get(0).get(1)-query.get(2)) * (lineSegment.get(0).get(0)-lineSegment.get(1).get(0)))) / 
			(((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(2)-query.get(3))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(0)-query.get(0))));
		if ((t >= 0 && t <= 1) && (u >= 0 && u <= 1)) {
			return true;
		} //window's left line segment
		
		t = (((lineSegment.get(0).get(0)-query.get(1)) * (query.get(2)-query.get(3))) - ((lineSegment.get(0).get(1)-query.get(2)) * (query.get(1)-query.get(2)))) / 
			(((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(2)-query.get(3))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(1)-query.get(2))));	
		
		u = (((lineSegment.get(0).get(0)-query.get(1)) * (lineSegment.get(0).get(1)-lineSegment.get(1).get(1))) - ((lineSegment.get(0).get(1)-query.get(2)) * (lineSegment.get(0).get(0)-lineSegment.get(1).get(0)))) / 
			(((lineSegment.get(0).get(0)-lineSegment.get(1).get(0)) * (query.get(2)-query.get(3))) - ((lineSegment.get(0).get(1)-lineSegment.get(1).get(1)) * (query.get(1)-query.get(1))));
		if ((t >= 0 && t <= 1) && (u >= 0 && u <= 1)) {
			return true;
		} //window's right line segment

		return false;
	}
	
	public static void main(String[] args) {
		RefinementStep refinementQuery = new RefinementStep();
		try {
			refinementQuery.loadGrid();
			refinementQuery.answerQueries();
		} catch (IOException e) {}
	}
}