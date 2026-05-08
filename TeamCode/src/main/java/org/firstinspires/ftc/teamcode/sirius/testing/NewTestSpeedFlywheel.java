package org.firstinspires.ftc.teamcode.sirius.testing;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeMap;
import org.firstinspires.ftc.teamcode.sirius.util.PIDFController;
import org.firstinspires.ftc.teamcode.sirius.util.StickyGamepad;

@TeleOp
@Config
public class NewTestSpeedFlywheel extends LinearOpMode {
    CRServo outtakeRight;
    CRServo outtakeLeft;
    DcMotorEx outtakeEncoder;
    Robot robot;
    public double tps;
    public double rpmNow;
    public double power;
    int lastPos;
    long lastTimeNs = 0;

    public static double speed = 0.0;
    public static double lastspeed = 0.0;
    public static double kP = 0;
    public static double kI = 0;
    public static double kD = 0;
    public static double kF = 0;
    PIDFController flywheelPID = new PIDFController(kP, kI, kD, kF);


    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap);
        outtakeRight = hardwareMap.get(CRServo.class, OuttakeMap.outtakeRight);

        outtakeLeft = hardwareMap.get(CRServo.class, OuttakeMap.outtakeLeft);

        outtakeEncoder = hardwareMap.get(DcMotorEx.class, "rearLeft");
        outtakeEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeEncoder.setDirection(DcMotorSimple.Direction.REVERSE);


        robot.init();

        waitForStart();
        while (opModeIsActive()) {
            tps = Math.abs(getTicksPerSecond(outtakeEncoder));
            rpmNow = (tps / 28.0) * 60.0;


            if(speed != lastspeed){
                lastspeed = speed;
                flywheelPID.setTargetPosition(speed);
            }

            flywheelPID.setPIDF(kP, kI, kD, kF);
            power = flywheelPID.calculate(rpmNow);
            outtakeLeft.setPower(power);
            outtakeRight.setPower(power);

            // ---- loop rate measurement ----


            // --------------------------------------------------

            // --------- TELEMETRY RATE LIMIT (nanoTime) ----------

            Robot.dash.addData("speedTarget", speed);
            Robot.dash.addData("rpm", rpmNow);
            Robot.dash.addData("tps", tps);
            Robot.dash.addData("power", power);


            // optional (can be expensive, so only at telemHz)
            Robot.dash.addData("motorVelTicks", outtakeEncoder.getVelocity());

            Robot.dash.update();

            // ---------------------------------------------------
        }
    }

    double getTicksPerSecond(DcMotorEx motor) {
        int pos = motor.getCurrentPosition();
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

