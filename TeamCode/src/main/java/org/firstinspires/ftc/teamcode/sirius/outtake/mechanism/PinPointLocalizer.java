package org.firstinspires.ftc.teamcode.sirius.outtake.mechanism;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.PinPoint;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeMap;
import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeMap;

/* loaded from: classes8.dex */
public class PinPointLocalizer {
    private static SparkFunOTOS.Pose2D lastPose;
    public static PinPoint pinPoint;
    private static ElapsedTime time;
    private static SparkFunOTOS.Pose2D velocity;
    IMU imu;
    private long imuUpdate = 0;
    public static double X = -81.0;
    public static double Y = -40.616;
    public static double yawScalar = 1.0019d;
    public static PinPoint.EncoderDirection xPod = PinPoint.EncoderDirection.REVERSED;
    public static PinPoint.EncoderDirection yPod = PinPoint.EncoderDirection.REVERSED;

    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.RuntimeException, java.lang.String, com.qualcomm.hardware.sparkfun.SparkFunOTOS$Pose2D] */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.lang.RuntimeException, java.lang.String, com.qualcomm.hardware.sparkfun.SparkFunOTOS$Pose2D] */



    public static SparkFunOTOS.Pose2D Div(SparkFunOTOS.Pose2D pose, double d) {
        return new SparkFunOTOS.Pose2D(pose.x / d, pose.y / d, pose.h / d);
    }

        public PinPointLocalizer(HardwareMap hm) {
            time = new ElapsedTime();
            this.imu = hm.get(IMU.class, OuttakeMap.IMU);
            this.imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD, RevHubOrientationOnRobot.UsbFacingDirection.RIGHT)));
            this.imu.resetYaw();
            pinPoint = hm.get(PinPoint.class, OuttakeMap.pinPoint);
            pinPoint.setOffsets(X, Y, DistanceUnit.MM);
            pinPoint.setEncoderResolution(PinPoint.GoBildaOdometryPods.goBILDA_4_BAR_POD);
            pinPoint.setEncoderDirections(xPod, yPod);
        }

    public double normalizeRadians(double raw) {
        while (raw > 3.141592653589793d) {
            raw -= 6.283185307179586d;
        }
        while (raw < -3.141592653589793d) {
            raw += 6.283185307179586d;
        }
        return raw;
    }

    public double cwDistance(double from, double to) {
        double dist = normalizeRadians(to) - normalizeRadians(from);
        return dist < 0.0d ? dist + 6.283185307179586d : dist;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.RuntimeException, java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.lang.RuntimeException, java.lang.String] */
    public double getDistanceFromTwoPoints(SparkFunOTOS.Pose2D p1, SparkFunOTOS.Pose2D p2) {
        if(p1 == null) p1 = new SparkFunOTOS.Pose2D();
        if(p2 == null) p2 = new SparkFunOTOS.Pose2D();
        return Math.sqrt(((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y)));
    }

    public double getAngleDifference(double h1, double h2) {
        double h12 = normalizeRadians(h1);
        double h22 = normalizeRadians(h2);
        return Math.min(Math.abs(h12 - h22), 6.283185307179586d - Math.abs(h12 - h22));
    }

    public void Update() {
        pinPoint.update();

        /*if(1000.f / (System.currentTimeMillis() - imuUpdate) >= 10) {
            double h = RobotInitializers.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            if(
                    getAngleDifference(0, h) <= Math.toRadians(0.03) &&
                            getAngleDifference(Math.PI, h) <= Math.toRadians(0.03) &&
                            getAngleDifference(-Math.PI, h) <= Math.toRadians(0.03) &&
                            getAngleDifference(Math.PI / 2, h) <= Math.toRadians(0.03) &&
                            getAngleDifference(-Math.PI / 2, h) <= Math.toRadians(0.03)
            ){
                while (h < 0) h += 2 * Math.PI;
                while (h > 2 * Math.PI) h -= Math.PI * 2;

                pinPoint.setPosition(new Pose2D(DistanceUnit.MM, getCurrentPosition().x, getCurrentPosition().y, AngleUnit.RADIANS, h));
                imuUpdate = System.currentTimeMillis();
            }
        }*/

       if(lastPose == null) lastPose = getCurrentPosition();
       if (time == null) time = new ElapsedTime();

       double dt = time.seconds();
       if (dt <= 0) dt = 1e-6;
       velocity = new SparkFunOTOS.Pose2D(
               (getCurrentPosition().x - lastPose.x)/dt,
               (getCurrentPosition().y - lastPose.y)/dt,
               pinPoint.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS)
       );
    }

    public void Reset() {
        pinPoint.recalibrateIMU();
        pinPoint.resetPosAndIMU();
    }

    public SparkFunOTOS.Pose2D getCurrentPosition() {
        double h = pinPoint.getHeading();
        return new SparkFunOTOS.Pose2D(pinPoint.getPosX(), pinPoint.getPosY(), normalizeRadians(h));
    }

    public SparkFunOTOS.Pose2D getVelocity() {
        return velocity;
    }

    public void setPosition(SparkFunOTOS.Pose2D humanTake) {
        pinPoint.setPosition(new Pose2D(DistanceUnit.MM, humanTake.x, humanTake.y, AngleUnit.RADIANS, humanTake.h));
        pinPoint.update();
    }
}
