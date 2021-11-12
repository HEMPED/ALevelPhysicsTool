public class MassSpringObj {
    private double springConstant, gravity, displacement, length, timePeriod,
            angularVelocity, acceleration, mass, amplitude;

    public MassSpringObj() {

    }
    public MassSpringObj(double springConstant, double gravity, double displacement, double length) {
        this.springConstant = springConstant;
        this.gravity = gravity;
        this.displacement = displacement;
        this.length = length;
    }

    //getter methods
    public double getSpringConstant() {
        return springConstant;
    }

    public double getGravity() {
        return gravity;
    }

    public double getDisplacement() {
        return displacement;
    }

    public double getLength() {
        return length;
    }

    public double getTimePeriod() {
        return timePeriod;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getMass() {
        return mass;
    }

    public double getAmplitude() {
        return amplitude;
    }

    //setter methods
    public void setSpringConstant(double springConstant) {
        this.springConstant = springConstant;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public void setDisplacement(double displacement) {
        this.displacement = displacement;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setTimePeriod(double timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }
}
