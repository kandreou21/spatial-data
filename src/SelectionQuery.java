import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SelectionQuery {
	private GridCreator gridCreator;
	private HashMap<List<Integer>, List<Integer>> cellContents;
	
	public SelectionQuery() {
		this.gridCreator = new GridCreator();
		cellContents = new HashMap<List<Integer>, List<Integer>>();
	}
	
	public void loadGrid() throws IOException {	 
		Scanner dirScanner = new Scanner(new File("grid.dir"));
		Scanner grdScanner = new Scanner(new File("grid.grd"));
		
		dirScanner.nextLine(); //skip first line
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
		
		System.out.println("Meros 2\n");
		Cell[][] grid = gridCreator.createGrid();
		HashMap<Integer, Record> records = gridCreator.createRecords();
		int queryNum = 1;
		for (int i = 0; i < queries.size(); i+=4) {
			int numCells = 0;
			List<Integer> ids = new ArrayList<Integer>();
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
									}
								}
							}
						}
			    	}
			    }
			}
			System.out.println("Query " + queryNum + " results: \n" + ids);			
			System.out.println("Cells: " + numCells);
			System.out.println("Results: " + ids.size());
			System.out.println("----------");
			queryNum++;
		}
	}
	
	public static void main(String[] args) {
		SelectionQuery query = new SelectionQuery();
		try {
			query.loadGrid();
			query.answerQueries();
		} catch (IOException e) {}
	}
}