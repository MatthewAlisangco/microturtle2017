import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class LoadMaze {
   int[][] maze = new int[256][4];
   Scanner read;
   
   public LoadMaze() throws FileNotFoundException {
      read = new Scanner(new File("adjList.txt"));
      for(int i = 0; i < 256; ++i) {
         if(i == 254)
            System.out.println("Halt");
         Scanner line =new Scanner(read.nextLine());
         read.nextInt();
         for (int j = 0; j < 4; ++j) {
            if (line.hasNextInt())
               maze[i][j] = line.nextInt();
            else 
               maze[i][j] = -1;
          }
       }
   }
   
   public int getEdge(int vertex, int direction) {
      return maze[vertex][direction];
   }
   
   public String toString() {
      String s = "";
      for(int i = 0; i < maze.length; ++i) 
         s += String.format("%3d %3d %3d %3d\n", maze[i][0],maze[i][1], maze[i][2], maze[i][3]);
      return s;
   }
}