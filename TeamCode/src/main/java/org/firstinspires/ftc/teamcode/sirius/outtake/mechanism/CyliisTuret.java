package org.firstinspires.ftc.teamcode.sirius.outtake.mechanism;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.LinearFunction;
import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeMap;
import org.firstinspires.ftc.teamcode.sirius.util.PIDController;
import org.firstinspires.ftc.teamcode.sirius.util.PIDControllerFurat;
import org.firstinspires.ftc.teamcode.sirius.util.PIDFController;
import org.opencv.core.Mat;

public class CyliisTuret {

    private static final double SERVO_MAX_ANGLE = Math.toRadians(360);

    public Servo hoodServo;

    CRServo outtakeRight;
    CRServo outtakeLeft;

    ServoImplEx turretServoLeft;
    ServoImplEx turretServoRight;

    public PinPointLocalizer pinPointLocalizer;
    public DcMotorEx outtakeEncoder;

    PIDController LaucnherSpeedController = new PIDController(0.004,0, 0.00001);

    // Change these from Dashboard
    public static double goalX = 1100;
    public static double goalY = -3000;

    public static double speedfar = 3500.0;
    public static double speedclose = 1400.0;

    public static double turretTargetAngle;

    public static boolean turretLeftReversed = false;
    public static boolean turretRightReversed = false;

    public static double servoOffset = 0.005;

    double left;
    double right;

    int lastPos;
    long lastTimeNs = 0;
    public static SparkFunOTOS.Pose2D goalPosition = new SparkFunOTOS.Pose2D(-2900, 1300, 0);
    public double targetAngle;
    public double tps;
    public double rpmNow;
    SparkFunOTOS.Pose2D raw;
    double dist;
    public double targetRpm;
    double power;

    public CyliisTuret(HardwareMap hardwareMap) {

        turretServoLeft = hardwareMap.get(ServoImplEx.class, OuttakeMap.turretLeft);
        turretServoRight = hardwareMap.get(ServoImplEx.class, OuttakeMap.turretRight);

        turretServoLeft.setPwmRange(new PwmControl.PwmRange(500, 2500));
        turretServoRight.setPwmRange(new PwmControl.PwmRange(500, 2500));

        if (turretLeftReversed) {
            turretServoLeft.setDirection(Servo.Direction.REVERSE);
        }

        if (turretRightReversed) {
            turretServoRight.setDirection(Servo.Direction.REVERSE);
        }

        hoodServo = hardwareMap.get(Servo.class, OuttakeMap.hood);

        pinPointLocalizer = new PinPointLocalizer(hardwareMap);

        outtakeRight = hardwareMap.get(CRServo.class, OuttakeMap.outtakeRight);
        outtakeLeft = hardwareMap.get(CRServo.class, OuttakeMap.outtakeLeft);

        outtakeEncoder = hardwareMap.get(DcMotorEx.class, "rearLeft");
        outtakeEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeEncoder.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private static double normalizeRadians(double angle) {
        angle %= (2.0 * Math.PI);

        if (angle < 0) {
            angle += (2.0 * Math.PI);
        }

        return angle;
    }

    private void updateTargetAngle() {
        double robotX = pinPointLocalizer.getCurrentPosition().x;
        double robotY = -pinPointLocalizer.getCurrentPosition().y;

        turretTargetAngle = Math.atan2(
                goalX - robotX,
                goalY - robotY
        );
    }

    private void updateServosPosition() {
        double robotHeading = pinPointLocalizer.getCurrentPosition().h;

        double relativeAngle = turretTargetAngle - robotHeading;
        relativeAngle = normalizeRadians(relativeAngle);

        double targetPosition = relativeAngle / SERVO_MAX_ANGLE;

        left = targetPosition - servoOffset;
        right = targetPosition + servoOffset;

        left = Math.max(0.0007, left);
        left = Math.min(1.0 - 0.0007, left);

        right = Math.max(0.0007, right);
        right = Math.min(1.0 - 0.0007, right);

        turretServoLeft.setPosition(left);
        turretServoRight.setPosition(left);
    }

    private SparkFunOTOS.Pose2D getGoalPosition() {
        return new SparkFunOTOS.Pose2D(Math.abs(goalX), Math.abs(goalY), 0);
    }

    public void updateFlyWheel() {
        raw = pinPointLocalizer.getCurrentPosition();
        SparkFunOTOS.Pose2D tp  = new SparkFunOTOS.Pose2D(-raw.y, raw.x, raw.h);

        dist = pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition);


        // use your mapping, BUT clamp distance so it actually moves between close/far
        double dClose = 200.0; // distance where you want speedclose
        double dFar   = Math.hypot(goalPosition.x, goalPosition.y); // your "far" reference

        // clamp to range to avoid target going crazy / not changing


        targetRpm = LinearFunction.getOutput(speedclose, speedfar, dClose, dFar, pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition));
        LaucnherSpeedController.setTargetPosition(targetRpm);

//        targetAngle = LinearFunction.getOutput(0.61, 0.26, speedclose, speedfar, rpmNow);
        targetAngle = LinearFunction.getOutput(0.7, 0.4, 100, dFar, pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition));



        // ---- 3) measure rpm ----
        tps = Math.abs(outtakeEncoder.getVelocity());
        rpmNow = (tps / 28) * 60.0;


        power = LaucnherSpeedController.calculatePower(rpmNow);
        outtakeRight.setPower(power  + 0.6 * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));
        outtakeLeft.setPower(power  + 0.6 * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));


        double distTp  = pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition);

        Robot.dash.addData("distTp", distTp);
        hoodServo.setPosition(targetAngle);
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

        if (dt <= 0) {
            return 0;
        }

        return dPos / dt;
    }

    public void update() {
        pinPointLocalizer.Update();
        updateTargetAngle();
        updateServosPosition();
        updateFlyWheel();

        Robot.dash.addData("turret target angle rad", turretTargetAngle);
        Robot.dash.addData("turret left servo", left);
        Robot.dash.addData("turret right servo", right);
        Robot.dash.addData("target angle", targetAngle);
        Robot.dash.addData("target rpm", targetRpm);
        Robot.dash.addData("raw", dist);
        Robot.dash.addData("rpm", rpmNow);
        Robot.dash.addData("power", power);
        Robot.dash.addData("x", goalX -pinPointLocalizer.getCurrentPosition().x);
        Robot.dash.addData("y", goalY + pinPointLocalizer.getCurrentPosition().y);
        Robot.dash.update();
    }
}