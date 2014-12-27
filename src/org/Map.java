package org;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

/*
 * Level stores data of the initial setting of each level
 */
public class Map implements Serializable {
	int N; 	// N is the maze's size
	int M;		// M is number of boxes
	int[][] wall;		// 0: empty; 1: wall; 2: destination; 3: box; 4: man;
							// 5: match; 6: outside; 7: man+destination
	// Point[] dest;	// coordinates of destinations
	int x, y;	// initial position of the boxman

	public Map() {}
	
	/*
	 * the file should contain data in the order of the elements listed above
	 */
	public Map(String filename) throws IOException {
		BufferedReader  br = new BufferedReader(new FileReader(filename));
		String line = "";
		/* The first line should be formatted as:
		 * N,M\n 
		 * Default N = 20. */
		if ((line = br.readLine()) != null) {
			N = Integer.parseInt(line.split(",")[0]);
			M = Integer.parseInt(line.split(",")[1]);
		}
		/* The second line is a list of matrix entries ordered by rows.*/
		wall = new int[N][N];
		int k = 0;
		for (int i = 0; i < N; i++) {
			if ((line = br.readLine()) != null) {
				String[] entry = line.split("\t");			
				for (int j = 0; j < N; j++) {
					wall[i][j] = Integer.parseInt(entry[j]);
				}
			}
		}
		/* The third line should be formatted as:
		 * x1,y1\n*/
		if ((line = br.readLine()) != null) {	
			x = Integer.parseInt((line.split(","))[0]);
			y = Integer.parseInt((line.split(","))[1]);
			wall[x][y] = 4;
		}
	}
}
