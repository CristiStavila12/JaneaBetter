package org.firstinspires.ftc.teamcode.sirius.util;

import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

/* loaded from: classes7.dex */
public class PIDControllerFurat {

    /** PIDF coefficients (F is feedforward) */
    public static class PIDFCoefficients {
        public double p, i, d, f;

        public PIDFCoefficients(double p, double i, double d, double f) {
            this.p = p;
            this.i = i;
            this.d = d;
            this.f = f;
        }
    }

    public double Isum;
    public int clamp;
    public double error;

    private final ElapsedTime et;
    private final ElapsedTime time;

    public double freq;
    public double kS;

    public double lastError;
    private double lastReturn;

    public double maxActuatorOutput;

    public PIDFCoefficients pidCoefficients;
    private double targetPosition;

    public PIDControllerFurat(PIDFCoefficients pidcoef) {
        this.kS = 0.0;
        this.targetPosition = 0.0;
        this.Isum = 0.0;

        this.et = new ElapsedTime();
        this.time = new ElapsedTime();

        this.clamp = 1;
        this.freq = 30.0;

        this.lastReturn = 0.0d;

        this.pidCoefficients = pidcoef;

        this.error = 0.0d;
        this.lastError = 0.0d;

        this.maxActuatorOutput = 1.0d;
    }

    public PIDControllerFurat(double p, double i, double d) {
        this(new PIDFCoefficients(p, i, d, 0.0));
    }

    public PIDControllerFurat(double p, double i, double d, double f) {
        this(new PIDFCoefficients(p, i, d, f));
    }

    public void setPidCoefficients(PIDFCoefficients coeff) {
        this.pidCoefficients = coeff;
    }

    // Backwards-compatible: FTC SDK PIDCoefficients (keeps current F)
    public void setPidCoefficients(PIDCoefficients coeff) {
        this.pidCoefficients.p = coeff.p;
        this.pidCoefficients.i = coeff.i;
        this.pidCoefficients.d = coeff.d;
    }

    public void setPIDF(double p, double i, double d, double f) {
        this.pidCoefficients.p = p;
        this.pidCoefficients.i = i;
        this.pidCoefficients.d = d;
        this.pidCoefficients.f = f;
    }

    public void setKF(double kF) {
        this.pidCoefficients.f = kF;
    }

    public void setFreq(double f) {
        this.freq = f;
    }

    public double calculatePower(double currentPosition) {
        return calculatePower(currentPosition, null);
    }

    public double calculatePower(double currentPosition, Double d) {
        // rate limit
        if (this.time.seconds() < 1.0d / this.freq) {
            return this.lastReturn;
        }
        this.time.reset();

        this.error = this.targetPosition - currentPosition;

        double dt = this.et.seconds();
        if (dt <= 0) dt = 1e-6;

        double P = this.error;

        double D;
        if (d != null) {
            D = d.doubleValue();
        } else {
            D = (this.error - this.lastError) / dt;
        }

        this.Isum += this.error * dt;

        // PID (no I yet)
        double r = (this.pidCoefficients.p * P) + (this.pidCoefficients.d * D);

        // anti-windup
        if (Math.abs(r) >= this.maxActuatorOutput && this.error * r > 0.0d) {
            this.clamp = 0;
            this.Isum = 0.0d;
        } else {
            this.clamp = 1;
        }

        // add I
        double r2 = r + (this.pidCoefficients.i * this.Isum);

        // ✅ FIXED FEEDFORWARD:
        // proportional to target, but *direction-aware* and 0 at target.
        // This makes kF actually help instead of fighting or always pushing.
        double FF = (this.pidCoefficients.f * Math.signum(this.error) + this.targetPosition );

        // combine + static friction comp
        double out = r2 + FF - (this.kS * Math.signum(this.error));

        // ✅ clamp output so huge kF can't explode and look "dead"
        if (out > this.maxActuatorOutput) out = this.maxActuatorOutput;
        if (out < -this.maxActuatorOutput) out = -this.maxActuatorOutput;

        this.et.reset();
        this.lastError = this.error;
        this.lastReturn = out;
        return this.lastReturn;
    }

    public void setTargetPosition(double pos, boolean resetIsum) {
        this.targetPosition = pos;
        if (resetIsum) {
            this.Isum = 0.0d;
        }
    }

    public void setTargetPosition(double pos) {
        setTargetPosition(pos, true);
    }

    public double getTargetPosition() {
        return this.targetPosition;
    }

    public void setMaxActuatorOutput(double mao) {
        this.maxActuatorOutput = mao;
    }

    public PIDFCoefficients getCoeff() {
        return this.pidCoefficients;
    }
}
