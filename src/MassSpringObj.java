public class MassSpringObj {
    private double springConstant, displacement, length, timePeriod, mass, amplitude, angularVelocity;

    public MassSpringObj() {

    }

    public MassSpringObj(double springConstant, double displacement, double length, double mass, double amplitude) {
        this.springConstant = springConstant;
        this.displacement = displacement;
        this.length = length;
        this.mass = mass;
        this.amplitude = amplitude;
    }

    //getter methods
    public double getSpringConstant() {
        return springConstant;
    }

    public double getAngularVelocity() {
        return angularVelocity;
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

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
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

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }
}
