package org.firstinspires.ftc.teamcode.sirius.outtake.mechanism;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.LinearFunction;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeMap;
import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeMap;
import org.firstinspires.ftc.teamcode.sirius.util.PIDController;

@Config
public class TuretOdometry {

    public double distNormalizedContinuos;
    public Servo hoodServo;
    CRServo outtakeRight;
    public DcMotorEx turretEncoder;
    public PinPointLocalizer pinPointLocalizer;
    public double targetGlobalHeading;
    public double ticksPerRev;
    CRServo outtakeLeft;
    public CRServo turretServoLeft;
    public CRServo turretServoRight;
    DcMotorEx outtakeEncoder;
    public static double gearRatio = 40/20 * 30/120;
    public static double speedfar = 4100.0;
    public static double speedclose = 1700.0;
    public static SparkFunOTOS.Pose2D goalPosition = new SparkFunOTOS.Pose2D(-1500, 3120, 0);
    public static double maxContinuosLimit = 2*Math.PI;
    public static double offset = 0;
    public double rpmNow;
    public double tps;
    public double dx;
    public double dy;
    public double ShouldHaveTurretHeading;
    int lastPos;
    long lastTimeNs = 0;
    long lastFlyCtrlUpdateNs = 0;
    long lastFlyTelemUpdateNs = 0;
    public double targetAngle;
    public double robotHeading;
    public double currentTurretRel;
    public PIDController turretController = new PIDController(0.67, 0.0, 0.01);
    public PIDController LaucnherSpeedController =
            new PIDController(0.00013, 0, 0.0000006);
    public OuttakeShooter outtakeShooter;
    public double errorRad;

    public TuretOdometry(HardwareMap hardwareMap) {

        turretServoLeft = hardwareMap.get(CRServo.class, OuttakeMap.turretLeft);
        turretServoRight = hardwareMap.get(CRServo.class, OuttakeMap.turretRight);
        hoodServo = hardwareMap.get(Servo.class, OuttakeMap.hood);
        pinPointLocalizer = new PinPointLocalizer(hardwareMap);

        outtakeRight = hardwareMap.get(CRServo.class, OuttakeMap.outtakeRight);

        outtakeLeft = hardwareMap.get(CRServo.class, OuttakeMap.outtakeLeft);

        outtakeEncoder = hardwareMap.get(DcMotorEx.class, "rearLeft");
        outtakeEncoder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtakeEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeEncoder.setDirection(DcMotorSimple.Direction.REVERSE);

        turretEncoder = hardwareMap.get(DcMotorEx.class, "frontLeft");
        turretEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretEncoder.setDirection(DcMotorSimple.Direction.REVERSE);


        long now = System.nanoTime();
        lastFlyCtrlUpdateNs = now;
        lastFlyTelemUpdateNs = now;
    }

    public static double offsetTicks = 5685;

    public double fromEncoderToRads() {
        double ticks = this.turretEncoder.getCurrentPosition() + offsetTicks;
        this.ticksPerRev = gearRatio * 8192.0;
        return ((2.0 * ticks) * Math.PI) / ticksPerRev;
    }

    private double LiniarizedTargetAngle(double targetAngle, double currContinuosAngle) {
        double bestdifference = 1e9;
        double bestcontinuosAngle = 0;
        distNormalizedContinuos = this.pinPointLocalizer.cwDistance(2 * Math.PI - offset, targetAngle);
        while (distNormalizedContinuos <= maxContinuosLimit) {
            if (bestdifference > Math.abs(distNormalizedContinuos - currContinuosAngle)) {
                bestdifference = Math.abs(distNormalizedContinuos - currContinuosAngle);
                bestcontinuosAngle = distNormalizedContinuos;
            }
            this.distNormalizedContinuos += 2 * Math.PI;
        }
        if (bestcontinuosAngle == 1e9) return 1e9;
        return currContinuosAngle - bestcontinuosAngle;
    }


    public void updateFlyWheel() {

        // ---- 1) compute target rpm from distance (close -> low, far -> high) ----
        SparkFunOTOS.Pose2D raw = pinPointLocalizer.getCurrentPosition();
        SparkFunOTOS.Pose2D tp  = new SparkFunOTOS.Pose2D(-raw.y, raw.x, raw.h);

        double dist = pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition);


        // use your mapping, BUT clamp distance so it actually moves between close/far
        double dClose = 200.0; // distance where you want speedclose
        double dFar   = Math.hypot(goalPosition.x, goalPosition.y); // your "far" reference

        // clamp to range to avoid target going crazy / not changing


        double targetRpm = LinearFunction.getOutput(speedclose, speedfar, dClose, dFar, pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition));
        LaucnherSpeedController.setTargetPosition(targetRpm);

//        targetAngle = LinearFunction.getOutput(0.61, 0.26, speedclose, speedfar, rpmNow);
        targetAngle = LinearFunction.getOutput(0.84, 0.24, 100, dFar, pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition));



        // ---- 3) measure rpm ----
        tps = Math.abs(getTicksPerSecond(outtakeEncoder));
        rpmNow = (tps / 8192.0) * 60.0;

        // ---- 4) compute power with overspeed drop so rpm can go DOWN ----
        double power;

        power = LaucnherSpeedController.calculatePower(rpmNow);

        // ---- 5) apply power (both motors if you want) ----
        outtakeRight.setPower(power  + 0.42 * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));
        outtakeLeft.setPower(power  + 0.42 * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));
//        motor.setPower(power  + 1 * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));
//        turretEncoder.setPower(power  + 1 * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));
        // ---- 6) telemetry ----

        Robot.dash.addData("rawPower", power);
        Robot.dash.addData("powerFF", power  + 0.25 * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));
        Robot.dash.update();



        double distRaw = pinPointLocalizer.getDistanceFromTwoPoints(raw, goalPosition);
        double distTp  = pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition);

        Robot.dash.addData("distTp", distTp);


    }



    public void updateFacingDirection() {
        SparkFunOTOS.Pose2D robotPose = pinPointLocalizer.getCurrentPosition();
        robotPose = new SparkFunOTOS.Pose2D(-robotPose.y, robotPose.x, robotPose.h);

        robotHeading = robotPose.h;
        dx = goalPosition.x - robotPose.x;
        dy = goalPosition.y - robotPose.y;



        targetGlobalHeading = Math.atan2(dy, dx);
        double diagonalToBot  = Math.hypot(dx, dy);
        double diagonalToHood = Math.hypot(diagonalToBot, 800.0);

// hood angle in RADIANS
        double hoodAngleRad = Math.asin(800.0 / diagonalToHood);

// convert to DEGREES (servos are easier to map in degrees)
        double hoodAngleDeg = Math.toDegrees(hoodAngleRad);

// map angle -> servo position
// 45° -> 0.27
// 90° -> 0.60   (adjust if needed)
        double hoodPos = Range.clip(
                (hoodAngleDeg - 45.0) / (90.0 - 45.0) * (0.60 - 0.27) + 0.27,
                0.15,
                0.9
        );

        ShouldHaveTurretHeading = targetGlobalHeading - robotHeading;
        currentTurretRel = fromEncoderToRads()/10;
        errorRad = LiniarizedTargetAngle(ShouldHaveTurretHeading, currentTurretRel);
        if (errorRad != 1e9) {
            double cmd = -turretController.calculatePower(errorRad);
            turretServoLeft.setPower(-cmd + Math.signum(errorRad) * 0.061);
            turretServoRight.setPower(-cmd + Math.signum(errorRad) * 0.061);
        } else {
            turretServoLeft.setPower(0.0);
            turretServoRight.setPower(0.0);
        }
        hoodServo.setPosition(targetAngle);
        updateFlyWheel();

    }



    double getTicksPerSecond(DcMotorEx flyWheelEncoder) {
        int pos = flyWheelEncoder.getCurrentPosition();
        long now = System.nanoTime();
        if (lastTimeNs == 0) {
            lastTimeNs = now;
            lastPos = pos;
            return 0;
        }
        double dt = (now - lastTimeNs) / 1e9;
        int dPos = pos - lastPos;

        lastTimeNs = now;
        lastPos = pos;

        if (dt <= 0) return 0;
        return dPos / dt;
    }
}
