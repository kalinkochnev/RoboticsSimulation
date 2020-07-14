package simulator;

import java.util.ArrayList;
import java.util.function.Function;

import processing.core.*;

public class RobotSimulation extends PApplet {
    private Field roboticsField;
    private Robot robot;
    public static int FPS = 60;
    int angle = 0;

    int cols, rows;
    int videoScale = 100;

    public void settings() {
        size(1000, 1000);
        cols = 1000/videoScale;
        rows = 1000/videoScale;
    }

    public void setup() {
        frameRate(RobotSimulation.FPS);

        this.roboticsField = Field.Default(this);
        this.robot = Robot.Default(this);

        this.roboticsField.addObject(robot);

        this.robot.moveRobot(new PVector(500-50-50+3, -950+100+50), 300);

        float circleRadius = (1000-(2*50)-(2*10)-75);
        

        this.robot.circle(circleRadius, (float)(2*Math.PI*circleRadius)/2);
        this.robot.moveRobot(new PVector(400, 400), 100);
        this.robot.moveRobot(new PVector(-400, 400), 200);
        this.robot.moveRobot(new PVector(-400, -400), 100);
        this.robot.moveRobot(new PVector(400, -400), 200);

        this.robot.moveRobot(new PVector(0, 800), 500);
        this.robot.moveRobot(new PVector(0, -800), 500);

        this.robot.moveRobot(new PVector(-400, 0), 500);
        this.robot.moveRobot(new PVector(800, 0), 500);
        this.robot.moveRobot(new PVector(-800, 0), 500);

        this.robot.moveRobot(new PVector(400, 400), 500);
    }

    private void drawGrid() {
        for (int i = 0; i < cols; i++) {
            // Begin loop for rows
            for (int j = 0; j < rows; j++) {

                // Scaling up to draw a rectangle at (x,y)
                int x = i * videoScale;
                int y = j * videoScale;
                noFill();
                stroke(2);
                // For every column and row, a rectangle is drawn at an (x,y) location scaled
                // and sized by videoScale.
                rect(x, y, videoScale, videoScale);
            }
        }
    }

    public void draw() {
        // Draw grid on screen
        // Begin loop for columns
        this.roboticsField.draw();
        drawGrid();
    }

    public Field getField() {
        return roboticsField;
    }

    public static void main(String[] args) {
        String[] processingArgs = { "RobotSimulation" };
        RobotSimulation simulation = new RobotSimulation();

        PApplet.runSketch(processingArgs, simulation);
    }
}

class Shapes {
    public static PShape DefaultRobot(PApplet sketch) {
        int width = 100;
        int height = 100;

        PShape robotShape = sketch.createShape(PShape.RECT, 0, 0, width, height);
        
        // PShape robotShape = sketch.createShape(PShape.ELLIPSE, 0, 0, width, height);
        robotShape.setFill(sketch.color(209, 56, 29));
        robotShape.setStroke(sketch.color(209, 209, 209));
        //robotShape.setStrokeWeight(4);

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
    MoveExecutor mExecutor;

    FieldObject(PApplet sketch, PShape shape, PVector pos) {
        super(sketch, shape);
        this.currentPos = new PVector(pos.x, pos.y);
        this.mExecutor = new MoveExecutor(this);
        this.setupObject();
    }

    public void setupObject() {
        this.sketch.shapeMode(PConstants.CENTER);
        this.shape.translate(this.currentPos.x, this.currentPos.y);
    }

    public void move(PVector vector) {
        this.shape.translate(vector.x, vector.y);
        this.currentPos.add(vector);
    }

    public void draw() {
        if (this.mExecutor.hasMovement()) {
            this.mExecutor.step();
        }
        this.sketch.shape(this.shape);
    }

}

class Robot extends FieldObject {
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

    public void moveRobot(double xDir, double yDir, float velocity) {
        this.moveRobot(new PVector((float)xDir, (float)yDir), velocity);
    }

    public void moveRobot(PVector vector, float velocity) {
        // The formula to find the number of frames needed is (distance in pixels/velocity in pixels/sec) = seconds needed
        // seconds needed * FPS to get the total number of frames for the movement
        float framesToTravel = (vector.mag()/velocity)*RobotSimulation.FPS;
        Function<Float, Float> xFunc = (Float time) -> {
            return vector.x/framesToTravel;
        };

        Function<Float, Float> yFunc = (Float time) -> {
            return vector.y/framesToTravel;//new Float(Math.tan(vector.heading()) * time);
        };

        this.mExecutor.addMovement(new Movement(xFunc, yFunc, new float[] { 0,  framesToTravel}));
    }

    public void circle(double radius, float velocity) {
        // I have no idea if the velocity is actually working in pixels/second. I think it does

        // The formula to find the number of frames needed is (distance in pixels/velocity in pixels/sec) = seconds needed
        // seconds needed * FPS to get the total number of frames for the movement
        float framesToTravelAngular = (float)((2*Math.PI*radius)/velocity)*RobotSimulation.FPS;
        
        // Apparently a framesToTravelXY is needed or else the radius of the circle does not come out right
        // And for whatever reason you have to multiply by 2 again... Haven't figured out why but it works!!!
        double framesToTravelXY = (float)((4*radius)/velocity)*RobotSimulation.FPS;

        Function<Float, Float> xFunc = (Float time) -> {
            // Based on how far along in the animation the shape is currentFrameToDegrees calculated what angle to
            // Evaluate the function for
            double currentFrameToDegrees = 360*time/framesToTravelAngular;
            double xDist = radius*Math.cos(Math.toRadians(currentFrameToDegrees));
            return (float)(2*xDist/framesToTravelXY);
        };

        Function<Float, Float> yFunc = (Float time) -> {
            // Based on how far along in the animation the shape is currentFrameToDegrees calculated what angle to
            // Evaluate the function for
            double currentFrameToDegrees = 360*time/framesToTravelAngular;
            double yDist = radius*Math.sin(Math.toRadians(currentFrameToDegrees));
            return (float)(2*yDist/framesToTravelXY);
        };
        this.mExecutor.addMovement(new Movement(xFunc, yFunc, new float[] { 0, framesToTravelAngular}));
    }

    public void sin(double amplitude, double period, double periodLen) {
        Function<Float, Float> xFunc = (Float time) -> {
            return (float)(period*periodLen);
        };

        Function<Float, Float> yFunc = (Float time) -> {
            return (float)(amplitude*Math.sin(Math.toRadians(time)*period));
        };
        this.mExecutor.addMovement(new Movement(xFunc, yFunc, new float[] { 0, 360}));
    }

}

class Field extends FieldObject {
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
