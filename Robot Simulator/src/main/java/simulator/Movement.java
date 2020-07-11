package simulator;

import java.util.ArrayList;
import java.util.function.Function;

import processing.core.PApplet;
import processing.core.PVector;



class Movement {
    Function<Float, Float> xFunc;
    Function<Float, Float> yFunc;
    float[] domain;
    float currFrame = 0;
    float startFrame;

    PVector originalPos;
    FieldObject fieldObject;

    Movement(Function<Float, Float> xFunc, Function<Float, Float> yFunc, float[] domain) {
        this.xFunc = xFunc;
        this.yFunc = yFunc;
        this.domain = domain;
    }

    public PVector getMoveVector() {
        PVector v1 = new PVector(xFunc.apply(domain[0]).floatValue(), yFunc.apply(domain[0]).floatValue());
        PVector v2 = new PVector(xFunc.apply(domain[1]).floatValue(), yFunc.apply(domain[1]).floatValue());
        return v2.copy().sub(v1);
    }

    private void evalNextPos() {
        currFrame++;
        float xMove = xFunc.apply(currFrame);// - this.fieldObject.currentPos.x;//);
        //Needs to be -yFunc because coord plane is weird in processing
        float yMove = -yFunc.apply(currFrame);// - this.fieldObject.currentPos.y;///RobotSimulation.FPS);

        this.fieldObject.move(new PVector(xMove, yMove));
        PApplet.println(currFrame, xMove, yMove, this.fieldObject.currentPos);
    }

    public boolean isComplete() {
        return this.currFrame >= this.domain[1];
    }

    public void startMove(FieldObject obj) {
        this.fieldObject = obj;
        this.originalPos = obj.currentPos.copy();
        this.startFrame = obj.sketch.frameCount;
    }

    public void step() {
        if (!isComplete()) {
            this.evalNextPos();
        }

    }
}

class MoveExecutor   {
    private ArrayList<Movement> movements = new ArrayList<>();
    FieldObject fieldObj;

    MoveExecutor(FieldObject obj) {
        this.fieldObj = obj;
    }

    public boolean hasMovement() {
        return movements.size() != 0;
    }

    public Movement getNextMovement(boolean remove) {
        if (this.movements.size() == 1) {
            return null;
        }

        if (remove) {
            this.movements.remove(0);
            return this.movements.get(0);
        }
        return this.movements.get(1);
    }

    public Movement currentMovement() {
        if (!this.hasMovement()) {
            return null;
        }

        Movement current = this.movements.get(0);
        if (current.isComplete()) {
            PApplet.println("Changed movement!");
            current = this.getNextMovement(true);
            if (current != null) {
                current.startMove(this.fieldObj);
            } else {
                this.movements.remove(0);
            }
        }
        return current;
    }

    public void addMovement(Movement movement) {
        movement.startMove(this.fieldObj);
        this.movements.add(movement);
    }

    public void step() {
        Movement currMovement = this.currentMovement();
        if (currMovement != null) {
            currMovement.step();
        }
    }

}