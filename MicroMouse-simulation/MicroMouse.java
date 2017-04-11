import java.util.Arrays;

public class MicroMouse {
  
   static int[][] mazeAdjList = new int[256][4];
   static int current = Direction.EAST;
   static int listSize = 4;
   static int board = 16;
   static int row = 0;
   static int col = 0;
   static int[][] goal = {{7,7},{7,8},{8,7},{8,8}};
   static int index = 0;
   static int left = 0;
   static int right = 0;
   static int forward = 0;
   static int weight[] = {6,2,1,8};
   static LoadMaze maze;
   static int[][] wMazeList = new int[256][4];
   static int[] stack = new int[256];
   static int sIndex = -1;
   static int oSIndex = 0;
   public static void main(String[] args) {

      try {
         maze = new LoadMaze();  
         push(-1);
         push(0);
         fillArray();   
         findMiddle();
         findHome();
      } catch(Exception e) {
         System.out.println(Arrays.toString(stack));
         System.out.println(e);
         System.out.println(row);
         System.out.println(col);
         return;
      }
   }
   private static void push(int v) {
      stack[++sIndex] = v;
   }
   
   private static int pop() {
      return stack[sIndex--];
   }
   
   private static int peek() {
      int stack = pop();
      push(stack);
      return stack;
   }
   
   private static void findMiddle() {
      System.out.println("Searching for middle");
      while(!goal()) {
         int prev = peek();
         gatherSensor();
         updateAdjList();
         int choice = makeDecision();
         rotate(choice);
         updateChoice(choice);
         backTrack(choice);
         move(choice);
         push(choice);
      }
   }
     
   private static void findHome() {
         System.out.println("Returning Home");
         index = pop();
         while(index != 0) {
            int choice = pop();
            rotate(choice);
            move(choice);
         }
   }
   
   private static void updateChoice(int choice) {
      for(int i = 0; i < 4; ++i) {
         if (mazeAdjList[choice][i] == index)
            wMazeList[choice][i] = wMazeList[choice][i] + 10;
      }
   }
   
   private static void backTrack(int choice) {
      int backTrack = pop();
      if (peek() == choice) { 
         for(int i = 0; i < 4; ++i) {
            if (mazeAdjList[choice][i] == index) {
               wMazeList[choice][i] = wMazeList[choice][i] + 50;
               pop();
            }
          }
      } else
          push(backTrack);
   }
   private static int abs(int v) {
      return (v < 0 ? -v : v);
   }
   
   private static boolean goal() {
      for(int i = 0; i < 4; ++i) {
         if (abs(row) == goal[i][0] && abs(col) == goal[i][1])
            return true;
      }
      return false;
   }
   private static void rotate(int choice) {
      int direction = -1;
      for(int i = 0; i < 4; ++i) {
         if (choice == mazeAdjList[index][i])
            direction = i;
      }
      
      if ((current + 1) % listSize == direction)
         rotateRight();
      else if ((current + listSize - 1) % listSize == direction)
         rotateLeft();
      else if ((current + 2) % listSize == direction) {
         rotateLeft();
         rotateLeft();
      }
   }
   
   private static void rotateRight() {
      current = (current + 1) % listSize;
   }
   
   private static void rotateLeft() {
      current = (current + listSize - 1) % listSize;
   }
   
   private static void move(int choice) {
      if (current == 0)
         col++;
      else if (current == 1)
         row++;
      else if (current == 2)
         col--;
      else
         row--;
      
      //MoveForward();
      index = choice;
   }
   
   private static void updateAdjList() {
      for(int i = 0; i < 4; ++i) {
         if (mazeAdjList[index][i] != -1 && wMazeList[index][i] == 0) 
            wMazeList[index][i] = weight[i];
         else if (mazeAdjList[index][i] == -1)
            wMazeList[index][i] = 9999;
      }
   }
   
   private static int makeDecision() {
      int choice = 0;
      for(int i = 1; i < 4; ++i)
         if (wMazeList[index][choice] > wMazeList[index][i])
            choice = i;
      return mazeAdjList[index][choice];
   }
   
   private static void fillArray() {
      for(int i = 0; i < 256; ++i) {
         if (i == 0) {                       //Top-left Corner
            mazeAdjList[i][0] = -1;  
            mazeAdjList[i][1] = i+1;
            mazeAdjList[i][2] = i + board;
            mazeAdjList[i][3] = -1;
         } else if (i == 15) {               //Top-right corner
            mazeAdjList[i][0] = -1;  
            mazeAdjList[i][1] = -1;
            mazeAdjList[i][2] = i + board;
            mazeAdjList[i][3] = i - 1;
         } else if (i == 240) {              //Bottom-left corner
            mazeAdjList[i][0] = i - board;  
            mazeAdjList[i][1] = i + 1;
            mazeAdjList[i][2] = -1;
            mazeAdjList[i][3] = -1;
         } else if (i == 255) {              //Bottom-right corner
            mazeAdjList[i][0] = i - board;  
            mazeAdjList[i][1] = -1;
            mazeAdjList[i][2] = -1;
            mazeAdjList[i][3] = i - 1;
         } else if (i / board == 0) {        //Top wall
            mazeAdjList[i][0] = -1;  
            mazeAdjList[i][1] = i + 1;
            mazeAdjList[i][2] = i + board;
            mazeAdjList[i][3] = i - 1;
         } else if (i / board == 15) {       //bottom wall                            
            mazeAdjList[i][0] = i - board;  
            mazeAdjList[i][1] = i + 1;
            mazeAdjList[i][2] = -1;
            mazeAdjList[i][3] = i - 1;
         } else if (i % board == 0) {        //Left wall
            mazeAdjList[i][0] = i - board;  
            mazeAdjList[i][1] = i + 1;
            mazeAdjList[i][2] = i + board;
            mazeAdjList[i][3] = -1;
          } else if (i % board == 15) {      //Right wall
            mazeAdjList[i][0] = i - board;  
            mazeAdjList[i][1] = -1;
            mazeAdjList[i][2] = i + board;
            mazeAdjList[i][3] = i - 1;
         } else {                            //All center pieces
            mazeAdjList[i][0] = i - board;  
            mazeAdjList[i][1] = i + 1;
            mazeAdjList[i][2] = i + board;
            mazeAdjList[i][3] = i - 1;
         }  
      }
   }
   
   private static void gatherSensor() {
      for(int i = 0; i < 4; ++i) {
         boolean found = false;
         for(int j = 0; j < 4; ++j) {
            if (mazeAdjList[index][i] == maze.getEdge(index, j))
                found = true;
         }
         if (!found)
         mazeAdjList[index][i] = -1;
      }
   }
  /* public static void gatherSensor() {
		
		 //for the row, index matches up with the predefined directions
		 //if between 0-9, update that valid to turn left
		 int off = 0;
       if(left < 9)  { 
         off = (current + listSize -1)% listSize;
		   mazeAdjList[index][off] = weight[off];
		  
		 }
		 else { 
		     mazeAdjList[index][(current + listSize -1)% listSize] = -1;
		    
		    }
		 // check right and update list
		 if(right < 9)  { 
         off = (current + listSize + 1)% listSize;
		   mazeAdjList[index][off] = weight[off];
		  
		 } else { 
		     mazeAdjList[index][(current + listSize +1)% listSize] = -1;
		
		     
		  }
		 // checks sensor for center
		 if(forward < 9)  { 
         off = (current + listSize)% listSize;
		   mazeAdjList[index][off] = weight[off];
		 } else { 
		     mazeAdjList[index][(current + listSize)% listSize] = -1;
		 } 
   } */
}
            
