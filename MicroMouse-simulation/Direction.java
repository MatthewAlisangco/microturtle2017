public class Direction {
   public static final int NORTH = 0;
   public static final int EAST = 1;
   public static final int SOUTH = 2;
   public static final int WEST = 3;
   
   public static String getDirection(int v) {
      if (v == 0)
         return "North";
      else if (v == 1)
         return "East";
      else if (v == 2)
         return "South";
      else if (v == 3)
         return "West";
      else
         return "FAIL";
   }
}