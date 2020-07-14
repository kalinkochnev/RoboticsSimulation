package simulator;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

public class Practice {
    public static void main(String[] args) {
        RobotSimulation.main(args);
    }

    public static SimpleRobot setupRobot(PApplet sketch, PShape shape) {
        SimpleRobot myRobot = new SimpleRobot(sketch, shape);

        myRobot.setStartPos(500, 500);

        myRobot.finishSetup();
        return myRobot;
    }

    public static void robotMovements(SimpleRobot robot) {
        robot.moveForward(200, 2.0);
        robot.moveLeft(200, 2.0);
        robot.moveBackward(200, 2.0);
        robot.moveRight(200, 2.0);
        robot.moveAngle((float)(200*Math.sqrt(2)), 45.0, 100);
    }


}

class SimpleRobot {
    Robot robot;

    PVector startPos;
    PApplet sketch;
    PShape shape;

    SimpleRobot(PApplet sketch, PShape shape) {
        this.sketch = sketch;
        this.shape = shape;
    }

    public void setStartPos(float x, float y) {
        this.startPos = new PVector(x, y);
    }

    public void finishSetup() {
        this.robot = new Robot(sketch, shape, startPos);
    }


    public Robot getRobot() {
        return this.robot;
    }

    public void moveForward(float distance, float velocity) {
        this.robot.moveRobot(distance, 0, velocity);
    }

    public void moveForward(float distance, double seconds) {
        this.moveForward(distance, (float)(distance/seconds));
    }

    public void moveBackward(float distance, float velocity) {
        this.robot.moveRobot(-distance, 0, velocity);
    }

    public void moveBackward(float distance, double seconds) {
        this.moveBackward(distance, (float)(distance/seconds));
    }

    public void moveRight(float distance, float velocity) {
        this.robot.moveRobot(0, -distance, velocity);
    }

    public void moveRight(float distance, double seconds) {
        this.moveRight(distance, (float)(distance/seconds));
    }

    public void moveLeft(float distance, float velocity) {
        this.robot.moveRobot(0, distance, velocity);
    }

    public void moveLeft(float distance, double seconds) {
        this.moveLeft(distance, (float)(distance/seconds));
    }

    public void moveAngle(float distance, double angle, float velocity) {
        double angleRad = Math.toRadians(angle);
        this.robot.moveRobot(distance*(float)Math.cos(angleRad), distance*(float)Math.sin(angleRad), velocity);
    }

    public void moveAngle(float distance, double angle, double seconds) {
        this.moveAngle(distance, angle, (float)(distance/seconds));
    }
}