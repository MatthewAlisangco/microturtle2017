#include <Stepper.h>
#include <SharpIR.h>
#define irL A0
#define irC A1
#define irR A2
#define model 1080

const int stepsPerRevolution = 200;  // change this to fit the number of steps per revolution
// for your motor
// initialize the stepper library on pins 8 through 11:
Stepper leftStepper(stepsPerRevolution, 8, 9, 10, 11);
// initialize the stepper library on pins 4 through 7:
Stepper rightStepper(stepsPerRevolution, 4, 5, 6, 7);

SharpIR leftSensor(irL, 25, 93, model);
SharpIR centerSensor(irC, 25, 93, model);
SharpIR rightSensor(irR, 25, 93, model);

enum {
  NORTH = 0,
  EAST = 1,
  SOUTH = 2,
  WEST = 3,
};
const int board = 5;// board size
int mazeAdjList[board * board][4]; //Stores graph edges  {North,East,South,West}
int current = EAST; //Start and current facing
const int listSize = 4; //graph array column size
int row = 0; //tracks current coordinate
int col = 0; //track current coordinate
//const int goal[4][2] = {{7,7},{7,8},{8,7},{8,8}}; //Stores winning squares
const int goal[1][2] = {2,2};
int index = 0; //Tracks current Vertex
int left = 0; // Holds Left IR Sensor
int right = 0; // Holds Right IR Sensor
int forward = 0; // Hold Center IR Sensor
int weight[] = {6,2,1,8}; //Holds Movement weights {North,East,South,West}
int wMazeList[board * board][4]; //Tracks each vertices weights {North,East,South,West}
int stack[board * board]; //Stack used to track correct route to center
int sIndex = -1;  //Tracks stack index

void setup() {
  push(-1); //push value onto stack, because first update method needs to values on stack in order to avoid index out of bounds
  push(0); //Push starting vertex onto stack

  fillArray();  //Setup Adjacency list
}

void loop() {  
   findMiddle(); //Find center
   findHome(); //Return home
   return;
}

void push(int v) {
  stack[++sIndex] = v;
}
   
int pop() {
  return stack[sIndex--];
}
   
int peek() {
  int stack = pop();
  push(stack);
  return stack;
}

void findMiddle() {
  while(found) {
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

void findHome() {
   index = pop();
   while(index != 0) {
      int choice = pop();
      rotate(choice);
      move(choice);
   }
}

void updateChoice(int choice) {
  for(int i = 0; i < 4; ++i) {
     if (mazeAdjList[choice][i] == index)
        wMazeList[choice][i] = wMazeList[choice][i] + 10;
  }
}

void backTrack(int choice) {
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

int better_abs(int v) {
  return (v < 0 ? -v : v);
}

int found() {
  for(int i = 0; i < 4; ++i) {
     if (better_abs(row) == goal[i][0] && better_abs(col) == goal[i][1])
        return 1;
  }
  return 0;
}

void rotate(int choice) {
  int direction = -1;
  for(int i = 0; i < 4; ++i) {
     if (choice == mazeAdjList[index][i])
        direction = i;
  }
  
  if ((current + 1) % listSize == direction)
     go_right();
  else if ((current + listSize - 1) % listSize == direction)
     go_left();
  else if ((current + 2) % listSize == direction) {
     go_left();
     go_left();
  }
}
void move(int choice) {
  if (current == 0)
     col++;
  else if (current == 1)
     row++;
  else if (current == 2)
     col--;
  else
     row--;
  
  go_forward();
  index = choice;
}

void updateAdjList() {
  for(int i = 0; i < 4; ++i) {
     if (mazeAdjList[index][i] != -1 && wMazeList[index][i] == 0) 
        wMazeList[index][i] = weight[i];
     else if (mazeAdjList[index][i] == -1)
        wMazeList[index][i] = 9999;
  }
}

int makeDecision() {
  int choice = 0;
  for(int i = 1; i < 4; ++i)
     if (wMazeList[index][choice] > wMazeList[index][i])
        choice = i;
  return mazeAdjList[index][choice];
}

void fillArray() {
  for(int i = 0; i < board * board; ++i) {
     if (i == 0) {                       //Top-left Corner
        mazeAdjList[i][0] = -1;  
        mazeAdjList[i][1] = i+1;
        mazeAdjList[i][2] = i + board;
        mazeAdjList[i][3] = -1;
     } else if (i == board - 1) {               //Top-right corner
        mazeAdjList[i][0] = -1;  
        mazeAdjList[i][1] = -1;
        mazeAdjList[i][2] = i + board;
        mazeAdjList[i][3] = i - 1;
     } else if (i == board * board - board) {              //Bottom-left corner
        mazeAdjList[i][0] = i - board;  
        mazeAdjList[i][1] = i + 1;
        mazeAdjList[i][2] = -1;
        mazeAdjList[i][3] = -1;
     } else if (i == board*board - 1) {              //Bottom-right corner
        mazeAdjList[i][0] = i - board;  
        mazeAdjList[i][1] = -1;
        mazeAdjList[i][2] = -1;
        mazeAdjList[i][3] = i - 1;
     } else if (i / board == 0) {        //Top wall
        mazeAdjList[i][0] = -1;  
        mazeAdjList[i][1] = i + 1;
        mazeAdjList[i][2] = i + board;
        mazeAdjList[i][3] = i - 1;
     } else if (i / board == board - 1) {       //bottom wall                            
        mazeAdjList[i][0] = i - board;  
        mazeAdjList[i][1] = i + 1;
        mazeAdjList[i][2] = -1;
        mazeAdjList[i][3] = i - 1;
     } else if (i % board == 0) {        //Left wall
        mazeAdjList[i][0] = i - board;  
        mazeAdjList[i][1] = i + 1;
        mazeAdjList[i][2] = i + board;
        mazeAdjList[i][3] = -1;
      } else if (i % board == board - 1) {      //Right wall
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

void gatherSensor() {
 //for the row, index matches up with the predefined directions
 //if between 0-9, update that valid to turn left
 left = leftSensor.distance();
 right = rightSensor.distance();
 forward = centerSensor.distance();
 if(left < 30)
     mazeAdjList[index][(current + listSize -1)% listSize] = -1;    
 // check right and update list
 if(right < 30)
     mazeAdjList[index][(current + listSize +1)% listSize] = -1;     
 // checks sensor for center
 if(forward < 16)
     mazeAdjList[index][(current + listSize)% listSize] = -1;
}

void go_forward()
{
  for (int i = 1; i <= 191; i++)
  {
    leftStepper.step(1);
    rightStepper.step(1); 
    delay(15);
  }
}

void go_left()
{
  for (int i = 1; i <= 90; i++)
  {
    leftStepper.step(-1);
    rightStepper.step(1);
    delay(15);
  }
  delay(1000);
  for (int i = 1; i <=24; i++){
    leftStepper.step(-1);
    rightStepper.step(-1);
    delay(15);
  }
}

void go_right()
{
  for (int i = 1; i <= 90; i++)
  {
    leftStepper.step(1);
    rightStepper.step(-1); 
    delay(15);
  }
  delay(1000);
  for (int i = 1; i <=24; i++){
  leftStepper.step(-1);
  rightStepper.step(-1);
  delay(15);
  }
}

/*
void go_backwards()                 
{
  for (int i = 1; i <= 180; i++)
  {
    leftStepper.step(-1);
    rightStepper.step(-1); 
    delay(15);
  }
}
*/

