package simulator;

import java.util.function.Function;

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

class NewMovement {
    Function<Float, Float> xFunc;
    Function<Float, Float> yFunc;
    float[] domain;
    float currFrame = 0;

    PVector originalPos;

    NewMovement(Function<Float, Float> xFunc, Function<Float, Float> yFunc, float[] domain) {
        this.xFunc = xFunc;
        this.yFunc = yFunc;
        this.domain = domain;
    }

    public PVector getMoveVector() {
        PVector v1 = new PVector(xFunc.apply(domain[0]).floatValue(), yFunc.apply(domain[0]).floatValue());
        PVector v2 = new PVector(xFunc.apply(domain[1]).floatValue(), yFunc.apply(domain[1]).floatValue());
        return v2.copy().sub(v1);
    }

    private FieldObject evalNextPos(FieldObject fieldObj) {
        currFrame++;
        float xMove = xFunc.apply(currFrame);
        float yMove = yFunc.apply(currFrame);

        fieldObj.move(new PVector(xMove, yMove));
        return fieldObj;
    }

    private boolean isComplete(PVector originalPos, PVector currentPos) {
        PVector positionDiff = currentPos.copy().sub(originalPos);
        // This might be bugged if there are movements in the opposite direction, we will see
        return positionDiff.mag() >= this.getMoveVector().mag();
    }

    public void startMove(FieldObject obj) {
        this.originalPos = obj.currentPos.copy();
    }

    private void step(FieldObject obj) {
        if (!isComplete(this.originalPos, obj.currentPos)) {
            this.evalNextPos(obj);
        }

    }
}