public class PendulumObj {
    //local variables of the pendulum object
    private double length, gravity, angle, initialAngle, timePeriod, angularVelocity;

    public PendulumObj(){}

    public PendulumObj(double len, double grav, double ang, double initAng){
        length = len;
        gravity = grav;
        angle = ang;
        initialAngle = initAng;
    }

    //getter methods for the pendulum object
    public double getLength(){
        return length;
    }

    public double getGravity(){
        return gravity;
    }

    public double getAngle(){
        return angle;
    }

    public double getInitialAngle(){return initialAngle;}

    public double getTimePeriod() {
        return timePeriod;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    //setter methods for the pendulum object
    public void setLength(double len){
        length = len;
    }

    public void setGravity(double grav){
        gravity = grav;
    }

    public void setAngle(double ang){
        angle = ang;
    }

    public void setInitialAngle(double ang){
        initialAngle = ang;
    }

    public void setTimePeriod(double timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
}