public class PendulumObj {
    //local variables of the pendulum object
    private double length, velocity, gravity, angle, dt, initialAngle, initialVelocity;

    public PendulumObj(){}

    public PendulumObj(double len, double vel, double initVel, double grav, double ang, double initAng, double DT){
        length = len;
        velocity = vel;
        gravity = grav;
        angle = ang;
        dt = DT;
        initialAngle = initAng;
        initialVelocity = initVel;
    }

    //getter methods for the pendulum object
    public double getLength(){
        return length;
    }

    public double getVelocity(){
        return velocity;
    }

    public double getGravity(){
        return gravity;
    }

    public double getAngle(){
        return angle;
    }

    public double getDt(){
        return dt;
    }

    public double getInitialVelocity(){
        return initialVelocity;
    }

    public double getInitialAngle(){return initialAngle;}

    //setter methods for the pendulum object
    public void setLength(double len){
        length = len;
    }

    public void setVelocity(double vel){
        velocity = vel;
    }

    public void setGravity(double grav){
        gravity = grav;
    }

    public void setAngle(double ang){
        angle = ang;
    }

    public void setDt(double DT){
        dt = DT;
    }

    public void setInitialAngle(double ang){
        initialAngle = ang;}

    public void setInitialVelocity(double velocity){
        initialVelocity = velocity;
    }
}