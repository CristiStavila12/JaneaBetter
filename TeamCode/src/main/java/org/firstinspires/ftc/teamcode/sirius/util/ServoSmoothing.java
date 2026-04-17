package org.firstinspires.ftc.teamcode.sirius.util;

/* loaded from: classes7.dex */
public class ServoSmoothing {
    public static double servoSmoothing(double currPos, double targetPos) {
        double smoothedPos = (0.07d * targetPos) + (0.93d * currPos);
        return smoothedPos;
    }
}
