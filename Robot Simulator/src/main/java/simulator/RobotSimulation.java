package simulator;

import java.util.ArrayList;
import processing.core.*;


public class RobotSimulation extends PApplet {
    private Field roboticsField;
    private Robot robot;
    public static int FPS = 60;
    int angle = 0;

    public void settings() {
        size(1000, 1000);
    }

    public void setup() {
        frameRate(RobotSimulation.FPS);

        this.roboticsField = Field.Default(this);
        this.robot = Robot.Default(this);
        this.roboticsField.addObject(robot);

        this.robot.move(new PVector(100, 100), 50, false);
        this.robot.move(new PVector(100, 50), 50, false);
        this.robot.move(new PVector(50, 100), 150, false);
        this.robot.move(new PVector(-100, -100), 450, false);
        this.robot.move(new PVector(400, 200), 50, false);
        this.robot.move(new PVector(-100, -300), 150, false);
        this.robot.move(new PVector(300, 300), 250, false);
        this.robot.move(new PVector(-100, -100), 50, false);


    }

    public void draw() {
        this.roboticsField.draw();
    }

    public Field getField() {
        return roboticsField;
    }
    public static void main(String[] args){
		String[] processingArgs = {"RobotSimulation"};
        RobotSimulation simulation = new RobotSimulation();
        
		PApplet.runSketch(processingArgs, simulation);
	}
}

class Shapes {
    public static PShape DefaultRobot(PApplet sketch) {
        int width = 100;
        int height = 100;

        PShape robotShape = sketch.createShape(PShape.RECT, 0, 0, width, height);
        robotShape.setFill(sketch.color(209, 56, 29));
        robotShape.setStroke(sketch.color(209, 209, 209));
        robotShape.setStrokeWeight(4);

        return robotShape;
    }

    public static PShape DefaultField(PApplet sketch) {
        int width = 900;
        int height = 900;

        PShape field = sketch.createShape(PShape.RECT, 0, 0, width, height);
        field.setFill(sketch.color(62, 91, 199));
        field.setStroke(sketch.color(33, 33, 33));
        field.setStrokeWeight(10);

        return field;
    }

}


abstract class Drawable {
    PApplet sketch;
    PShape shape;

    Drawable(PApplet sketch, PShape shape) {
        this.shape = shape;
        this.sketch = sketch;
    }

    abstract public void draw();
}

class FieldObject extends Drawable {
    PVector currentPos;
    ArrayList<Movement> movements= new ArrayList<>();

    FieldObject(PApplet sketch, PShape shape, PVector pos) {
        super(sketch, shape);
        this.currentPos = new PVector(pos.x, pos.y);
        this.setupObject();
    }

    public void setupObject() {
        this.sketch.shapeMode(PConstants.CENTER);
        this.shape.translate(this.currentPos.x, this.currentPos.y);    
    }

    public void move(PVector vector, double velocity, boolean rotateFirst) {
        this.addMovement(new Movement(vector, velocity));

        if (this.movements.size() == 1) {
            this.movements.get(0).setStartVector(this.currentPos.copy());
        }
    }

    public void addMovement(Movement movement) {
        this.movements.add(movement);
    }

    public Movement getCurrMovement() {
        if (this.movements.size() != 0) {
            return this.movements.get(0);
        }
        return null;
    }

    public Movement getNextMovement() {
        this.movements.remove(0);
        return this.getCurrMovement();
    }

    public void step() {
        Movement currMovement = getCurrMovement();

        if (currMovement == null) {
            return;
        }

        if (currMovement.isComplete()) {
            Movement nextMovement = this.getNextMovement();
            nextMovement.setStartVector(currMovement.endVector);
            currMovement = nextMovement;
        }

        if (currMovement != null) {
            PVector diff = currMovement.getDiff();
            int frames = currMovement.getFramesToMove();
            frames = currMovement.getFramesToMove();
            float changeInX = diff.x/frames;
            float changeInY = diff.y/frames;

            this.currentPos.add(new PVector(changeInX, changeInY));
            this.shape.translate(changeInX, changeInY);

            currMovement.nextFrame();
        }
    }

    public void draw() {
        this.step();
        this.sketch.shape(this.shape);
    }

}


class Robot extends FieldObject{
    double acceleration;
    double maxVelocity;
    double velocity = 0;
    double angle = 0;

    boolean isMoving = false;


    Robot(PApplet sketch, PShape robotShape, double acceleration, double maxVelocity) {
        super(sketch, robotShape, new PVector(50, 50));
        this.acceleration = acceleration;
        this.maxVelocity = maxVelocity;
    }

    public static Robot Default(PApplet sketch) {
        return new Robot(sketch, Shapes.DefaultRobot(sketch), 1, 5);
    }

    

}


class Field extends FieldObject{
    ArrayList<FieldObject> fieldObjects = new ArrayList<>();

    Field(PApplet sketch, PShape fieldShape, PVector pos) {
        super(sketch, fieldShape, pos);
    }

    public static Field Default(PApplet sketch) {
        return new Field(sketch, Shapes.DefaultField(sketch), new PVector(50, 50));
    }

    public void addObject(FieldObject fieldObj) {
        this.fieldObjects.add(fieldObj);
    }

    public void draw() {
        super.draw();
        for (FieldObject obj : this.fieldObjects) {
            obj.draw();
        }
    }
}

