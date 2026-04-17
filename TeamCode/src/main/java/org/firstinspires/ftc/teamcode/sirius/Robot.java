package org.firstinspires.ftc.teamcode.sirius;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.AsyncScheduler;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.ColorRangeSensorWraper;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.Colors;
import org.firstinspires.ftc.teamcode.sirius.Wrappers.Task;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.sirius.util.States;


public class Robot {
    public static Telemetry dash;
    public IntakeSubsystem intake;
    ColorRangeSensorWraper colorRangeSensor;

    public States.CollectBallState collectBallStateCS = States.CollectBallState.IDLE;

    public Robot(HardwareMap hardwareMap){
        dash = FtcDashboard.getInstance().getTelemetry();
        colorRangeSensor = new ColorRangeSensorWraper("intakeColorSensor", hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
    }

    public void toggleIntake() {
        if (collectBallStateCS == States.CollectBallState.IDLE) {
            intake.goToCollect();
            collectBallStateCS = States.CollectBallState.SUCK;
        } else if (collectBallStateCS == States.CollectBallState.SUCK) {
            intake.goToIdle();
            collectBallStateCS = States.CollectBallState.IDLE;
        }
    }
}
