package org.firstinspires.ftc.teamcode.sirius.util;

import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDFController {

    public static class PIDFCoefficients {
        public double p;
        public double i;
        public double d;
        public double f;

        public PIDFCoefficients(double p, double i, double d, double f) {
            this.p = p;
            this.i = i;
            this.d = d;
            this.f = f;
        }
    }

    private PIDFCoefficients coefficients;

    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime frequencyTimer = new ElapsedTime();

    private double targetPosition = 0.0;

    public double error = 0.0;
    public double lastError = 0.0;

    private double integralSum = 0.0;
    private double lastOutput = 0.0;

    public double maxOutput = 1.0;
    public double minOutput = -1.0;

    public double maxIntegralSum = 1.0;

    public double kS = 0.0;

    public double frequency = 50.0;

    public PIDFController(double p, double i, double d, double f) {
        this.coefficients = new PIDFCoefficients(p, i, d, f);
        timer.reset();
        frequencyTimer.reset();
    }

    public PIDFController(double p, double i, double d) {
        this(p, i, d, 0.0);
    }

    public PIDFController(PIDFCoefficients coefficients) {
        this.coefficients = coefficients;
        timer.reset();
        frequencyTimer.reset();
    }

    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
        reset();
    }

    public void setTargetPosition(double targetPosition, boolean resetIntegral) {
        this.targetPosition = targetPosition;

        if (resetIntegral) {
            integralSum = 0.0;
        }
    }

    public double getTargetPosition() {
        return targetPosition;
    }

    public double calculate(double currentPosition) {
        return calculate(currentPosition, null);
    }

    public double calculate(double currentPosition, Double externalDerivative) {

        if (frequencyTimer.seconds() < 1.0 / frequency) {
            return lastOutput;
        }

        frequencyTimer.reset();

        double dt = timer.seconds();

        if (dt <= 0.0) {
            dt = 1e-6;
        }

        error = targetPosition - currentPosition;

        double proportional = error;

        integralSum += error * dt;

        integralSum = clamp(
                integralSum,
                -maxIntegralSum,
                maxIntegralSum
        );

        double derivative;

        if (externalDerivative != null) {
            derivative = externalDerivative;
        } else {
            derivative = (error - lastError) / dt;
        }

        double pid =
                coefficients.p * proportional +
                        coefficients.i * integralSum +
                        coefficients.d * derivative;

        double feedforward = coefficients.f * targetPosition;

        double staticFriction = 0.0;

        if (Math.abs(error) > 0.001) {
            staticFriction = kS * Math.signum(error);
        }

        double output = pid + feedforward + staticFriction;

        output = clamp(output, minOutput, maxOutput);

        if (Math.abs(output) >= maxOutput && Math.signum(output) == Math.signum(error)) {
            integralSum -= error * dt;
        }

        lastError = error;
        lastOutput = output;

        timer.reset();

        return output;
    }

    public void reset() {
        error = 0.0;
        lastError = 0.0;
        integralSum = 0.0;
        lastOutput = 0.0;
        timer.reset();
        frequencyTimer.reset();
    }

    public void setPIDF(double p, double i, double d, double f) {
        coefficients.p = p;
        coefficients.i = i;
        coefficients.d = d;
        coefficients.f = f;
    }

    public void setPID(double p, double i, double d) {
        coefficients.p = p;
        coefficients.i = i;
        coefficients.d = d;
    }

    public void setPidCoefficients(PIDCoefficients pidCoefficients) {
        coefficients.p = pidCoefficients.p;
        coefficients.i = pidCoefficients.i;
        coefficients.d = pidCoefficients.d;
    }

    public PIDFCoefficients getCoefficients() {
        return coefficients;
    }

    public void setKF(double f) {
        coefficients.f = f;
    }

    public void setKS(double kS) {
        this.kS = kS;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setOutputLimits(double minOutput, double maxOutput) {
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
    }

    public void setMaxIntegralSum(double maxIntegralSum) {
        this.maxIntegralSum = Math.abs(maxIntegralSum);
    }

    public double getLastOutput() {
        return lastOutput;
    }

    public double getIntegralSum() {
        return integralSum;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}