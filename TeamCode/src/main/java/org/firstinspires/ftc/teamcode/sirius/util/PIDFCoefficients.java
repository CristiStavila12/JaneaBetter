package org.firstinspires.ftc.teamcode.sirius.util;

public class PIDFCoefficients {
    public double p, i, d, f;

    public PIDFCoefficients() {
        this(0, 0, 0, 0);
    }

    public PIDFCoefficients(double p, double i, double d, double f) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
    }
}
