package org.firstinspires.ftc.teamcode.sirius.outtake.mechanism;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.LinearFunction;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeMap;
import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeMap;
import org.firstinspires.ftc.teamcode.sirius.util.PIDControllerFurat;

public class OuttakeShooter {

    // motors
    public CRServo outtakeRight;
    public CRServo outtakeLeft;
    DcMotorEx outtakeEncoder;


    // localizer (passed in)
    private final PinPointLocalizer pinPointLocalizer;

    // autospeed bounds
    public static double speedfar = 3400.0;
    public static double speedclose = 2300.0;

    // goal
    public static SparkFunOTOS.Pose2D goalPosition = new SparkFunOTOS.Pose2D(1500, 3000, 0);

    // distance mapping
    public static double dClose = 1000.0;  // distance where speedclose
    public static double dFar   = 4000.0;  // distance where speedfar (TUNE!)

    // PIDF (dashboard tunable if you want)
    public static double fly_kP = 0.006;
    public static double fly_kI = 0.0;
    public static double fly_kD = 0.0;
    public static double fly_kF = 0.00013;

    public static double fly_ctrlHz = 30.0;

    // floor + overspeed behavior
    public static double fly_Fs = 0.08;
    public static double fly_minRpmForPID = 1000.0;

    public static double fly_deadbandRpm = 75.0;
    public static double fly_coastPower = 0.0; // 0 = coast when overspeed

    // rpm measurement
    private int lastPos = 0;
    private long lastTimeNs = 0;

    // rate limit
    private long lastCtrlUpdateNs = 0;

    // public debug
    public double rpmNow = 0.0;
    public double tps = 0.0;
    public double targetRpm = 0.0;
    public double power = 0.0;
    public double distTp = 0.0;

    private final PIDControllerFurat launcherSpeedController =
            new PIDControllerFurat(0.0003, 0, 0, 0.002);

    public OuttakeShooter(HardwareMap hardwareMap, PinPointLocalizer pinPointLocalizer) {
        this.pinPointLocalizer = pinPointLocalizer;

        outtakeRight = hardwareMap.get(CRServo.class, OuttakeMap.outtakeRight);

        outtakeLeft = hardwareMap.get(CRServo.class, OuttakeMap.outtakeLeft);

        outtakeEncoder = hardwareMap.get(DcMotorEx.class, "rearLeft");
        outtakeEncoder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtakeEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeEncoder.setDirection(DcMotorSimple.Direction.REVERSE);

        long now = System.nanoTime();
        lastCtrlUpdateNs = now;
    }

    /** Call this every loop (TeleOp and Auto). */
    public void update() {

        // ---- 1) distance in TP frame (your "distTp is good" frame) ----
        SparkFunOTOS.Pose2D raw = pinPointLocalizer.getCurrentPosition();
        SparkFunOTOS.Pose2D tp = new SparkFunOTOS.Pose2D(-raw.y, raw.x, raw.h);

        distTp = pinPointLocalizer.getDistanceFromTwoPoints(tp, goalPosition);

        // clamp distance and map to target rpm
        double distClamped = Range.clip(distTp, dClose, dFar);
        targetRpm = LinearFunction.getOutput(speedclose, speedfar, dClose, dFar, distClamped);

        // ---- 2) controller setup ----
        launcherSpeedController.setPIDF(fly_kP, fly_kI, fly_kD, fly_kF);
        launcherSpeedController.setFreq(fly_ctrlHz);
        launcherSpeedController.setTargetPosition(targetRpm, false);

        // ---- 3) control rate limit ----
        long nowNs = System.nanoTime();
        long periodNs = (long) (1e9 / Math.max(1.0, fly_ctrlHz));
        if (nowNs - lastCtrlUpdateNs < periodNs) {
            telemetry(raw, tp, distClamped);
            return;
        }
        lastCtrlUpdateNs = nowNs;

        // ---- 4) measure rpm ----
        tps = Math.abs(getTicksPerSecond(outtakeEncoder));
        rpmNow = (tps / 28.0) * 60.0;

        // ---- 5) compute power (allows rpm to drop when target drops) ----
        if (targetRpm < fly_minRpmForPID) {
            power = fly_Fs;
        } else if (rpmNow > targetRpm + fly_deadbandRpm) {
            power = fly_coastPower; // coast when overspeed
        } else {
            double pidOut = launcherSpeedController.calculatePower(rpmNow);
            power = Math.max(0.0, pidOut) + fly_Fs;
            power = Range.clip(power, 0.0, 1.0);
        }

        // ---- 6) apply ----
        outtakeLeft.setPower(power);
        outtakeRight.setPower(power);

        telemetry(raw, tp, distClamped);
    }

    /** Optional: set a fixed rpm target (disables autospeed mapping for that moment). */
    public void setTargetRpm(double rpm, boolean resetI) {
        targetRpm = rpm;
        launcherSpeedController.setTargetPosition(rpm, resetI);
    }

    private void telemetry(SparkFunOTOS.Pose2D raw, SparkFunOTOS.Pose2D tp, double distClamped) {
        Robot.dash.addData("fly_distTp", distTp);
        Robot.dash.addData("fly_distClamped", distClamped);
        Robot.dash.addData("fly_targetRpm", targetRpm);
        Robot.dash.addData("fly_rpmNow", rpmNow);
        Robot.dash.addData("fly_power", power);
        Robot.dash.addData("fly_overspeed", rpmNow - targetRpm);

        double distRaw = pinPointLocalizer.getDistanceFromTwoPoints(raw, goalPosition);
        Robot.dash.addData("fly_distRaw", distRaw);

        Robot.dash.addData("rawX", raw.x);
        Robot.dash.addData("rawY", raw.y);
        Robot.dash.addData("tpX", tp.x);
        Robot.dash.addData("tpY", tp.y);

        Robot.dash.update();
    }

    private double getTicksPerSecond(DcMotorEx m) {
        int pos = m.getCurrentPosition();
        long now = System.nanoTime();

        if (lastTimeNs == 0) {
            lastTimeNs = now;
            lastPos = pos;
            return 0.0;
        }

        double dt = (now - lastTimeNs) / 1e9;
        int dPos = pos - lastPos;

        lastTimeNs = now;
        lastPos = pos;

        if (dt <= 0) return 0.0;
        return dPos / dt;
    }
}
