package simulator;

import java.util.ArrayList;
import processing.core.*;


public class RobotSimulation extends PApplet {

    public static void main(String[] args){
		String[] processingArgs = {"MySketch"};
		RobotSimulation mySketch = new RobotSimulation();
		PApplet.runSketch(processingArgs, mySketch);
	}
}

abstract class Drawable {
    PApplet sketch;
    
    Drawable(PApplet sketch) {
        this.sketch = sketch;
    }

    public void draw(Position pos) {

    }
}

class Robot extends Drawable{
    PShape robotShape;
    Position pos = new Position(0, 0);
    double acceleration;
    double velocity;

    Robot(PApplet sketch, PShape robotShape, double acceleration) {
        super(sketch);
        this.robotShape = robotShape;
        this.acceleration = acceleration;
    }

    public void move(PVector vector, double velocity, boolean rotateFirst) {

    }

}



class Position {
    double x;
    double y;

    Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Field {

    ArrayList<FieldObject> fieldObjects = new ArrayList<>();
    PShape fieldShape;
    PApplet sketch;

    public void setField(PShape fieldShape, PApplet sketch) {
        this.fieldShape = fieldShape;
        this.sketch = sketch;
    }

    public void draw() {
        

    }
}

class FieldObject extends Drawable {
    Position pos;
    PShape shape;

    FieldObject(PApplet sketch, Position pos) {
        super(sketch);

    }

    public boolean detectCollision(FieldObject other) {
        return false;
    }
}
