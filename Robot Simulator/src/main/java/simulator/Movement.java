package simulator;

import processing.core.PVector;

public class Movement {
    PVector endVector;
    PVector startVector;
    double velocity;
    int framesToMove;

    Movement(PVector moveVector, double velocity) {
        this.endVector = moveVector;
        this.velocity = velocity;
    }

    public void setStartVector(PVector vector) {
        this.startVector = vector;
        this.endVector.add(vector);
        this.framesToMove = this.getFramesToMove();
    }

    public PVector getDiff() {
        return endVector.copy().sub(startVector);
    }

    public int getFramesToMove() {
        return (int)Math.ceil(this.getDiff().mag()/(this.velocity))*RobotSimulation.FPS;
    }

    public boolean isComplete() {
        return framesToMove == 0;
    }

    public void nextFrame() {
        this.framesToMove--;
    }

    
}
