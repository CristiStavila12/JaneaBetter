package org.firstinspires.ftc.teamcode.sirius.Wrappers;

import com.acmerobotics.dashboard.config.Config;

@Config
/* loaded from: classes7.dex */
public class LinearFunction {
    public static double getOutput(double pstart, double pend, double estart, double eend, double t) {
        if (eend - estart == 0.0d) {
            return 0.0d;
        }
        return (((t - estart) * (pend - pstart)) / (eend - estart)) + pstart;
    }
}