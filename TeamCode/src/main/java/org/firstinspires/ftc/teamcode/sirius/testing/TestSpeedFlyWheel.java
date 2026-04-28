package org.firstinspires.ftc.teamcode.sirius.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.teamcode.sirius.ChassisController;
import org.firstinspires.ftc.teamcode.sirius.Robot;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeMap;
import org.firstinspires.ftc.teamcode.sirius.outtake.OuttakeMap;
import org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.TuretOdometry;
import org.firstinspires.ftc.teamcode.sirius.util.PIDController;
import org.firstinspires.ftc.teamcode.sirius.util.StickyGamepad;

@TeleOp
@Config
public class TestSpeedFlyWheel extends LinearOpMode {
    public static StickyGamepad stickyGamepad;
    CRServo outtakeRight;
    CRServo outtakeLeft;
    DcMotorEx outtakeEncoder;
    Robot robot;

    public static double speed = 0.0;
    public static double lastspeed = 0.0;



    // Dashboard editable feedforward

    // ✅ Control update rate (how often you compute/apply new power)
    public static double ctrlHz = 30.0;

    // ✅ Telemetry update rate (how often dashboard values refresh)
    public static double telemHz = 10.0;

    int lastPos;
    long lastTimeNs = 0;

    // rate limiters
    long lastCtrlUpdateNs = 0;
    long lastTelemUpdateNs = 0;

    // debug counters
    long lastLoopCountTimeNs = 0;
    int loopCount = 0;
    double loopHz = 0.0;
    double ctrlHzActual = 0.0;

    double lastPower = 0.0;
    double lastRpmNow = 0.0;
    public double tps;
    public double rpmNow;
    public double power;

    public static double kf;
    org.firstinspires.ftc.teamcode.sirius.outtake.mechanism.TuretOdometry turetOdometry;
    long lastFlyCtrlUpdateNs = 0;
    public static final long FLYWHEEL_UPDATE_PERIOD_NS = 33_333_333L; // 30 Hz

    public static PIDCoefficients coefficients = new PIDCoefficients(0,0,0);
    public PIDController LaucnherSpeedController = new PIDController(0.0, 0, 0);

    @Override
    public void runOpMode() throws InterruptedException {
        // ✅ Make dashboard telemetry send faster (prevents "1 Hz looking" updates)
        long now = System.nanoTime();

        // ---- RATE LIMIT TO 30 Hz ----
        if (now - lastFlyCtrlUpdateNs < FLYWHEEL_UPDATE_PERIOD_NS) {
            return;
        }
        lastFlyCtrlUpdateNs = now;
        FtcDashboard.getInstance().setTelemetryTransmissionInterval(40); // ms (try 25-50)

        robot = new Robot(hardwareMap);
        outtakeRight = hardwareMap.get(CRServo.class, OuttakeMap.outtakeRight);

        outtakeLeft = hardwareMap.get(CRServo.class, OuttakeMap.outtakeLeft);

        outtakeEncoder = hardwareMap.get(DcMotorEx.class, "rearLeft");

        stickyGamepad = new StickyGamepad(this.gamepad1, this);



        robot.init();

        waitForStart();


        lastCtrlUpdateNs = now;
        lastTelemUpdateNs = now;
        lastLoopCountTimeNs = now;

        while (opModeIsActive()) {
            tps = Math.abs(getTicksPerSecond(outtakeEncoder));
            rpmNow = (tps / 28.0) * 60.0;


            if(speed != lastspeed){
                lastspeed = speed;
                LaucnherSpeedController.setTargetPosition(speed);
            }

            LaucnherSpeedController.setPidCoefficients(coefficients);
            power = LaucnherSpeedController.calculatePower(rpmNow);
            outtakeLeft.setPower(power + kf * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));
            outtakeRight.setPower(power + kf * Math.signum(LaucnherSpeedController.getTargetPosition() - rpmNow));

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
