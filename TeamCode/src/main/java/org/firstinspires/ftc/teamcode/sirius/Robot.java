package org.firstinspires.ftc.teamcode.sirius;

import static org.firstinspires.ftc.teamcode.sirius.testing.SpindexTest.pos;

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
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeSettings;
import org.firstinspires.ftc.teamcode.sirius.intake.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.sirius.util.States;


public class Robot {
    public static Telemetry dash;
    public IntakeSubsystem intake;
    ColorRangeSensorWraper colorRangeSensor;

    public States.CollectBallState collectBallStateCS = States.CollectBallState.IDLE;
    public States.SorterCapacity sorterCapacityCS = States.SorterCapacity.EMPTY;
    public States.FirstSorterState firstSorterCS = States.FirstSorterState.EMPTY;
    public States.SecondSorterState secondSorterCS = States.SecondSorterState.EMPTY;
    public States.ThirdSorterState thirdSorterCS = States.ThirdSorterState.EMPTY;
    public States.SorterState sorterStateCS = States.SorterState.COLLECT1;
    public Robot(HardwareMap hardwareMap){
        dash = FtcDashboard.getInstance().getTelemetry();
        colorRangeSensor = new ColorRangeSensorWraper("intakeColorSensor", hardwareMap);
        intake = new IntakeSubsystem(hardwareMap);
    }

    public void autoIntake(){
        if(collectBallStateCS == States.CollectBallState.SUCK){
            if (sorterCapacityCS == States.SorterCapacity.EMPTY){
                if (firstSorterCS == States.FirstSorterState.EMPTY && sorterStateCS == States.SorterState.COLLECT1) {
                    if (colorRangeSensor.getColorSeenBySensor() == Colors.ColorType.GREEN) {
                        intake.spindex.goToCollect2();
                            sorterStateCS = States.SorterState.COLLECT2;
                            firstSorterCS = States.FirstSorterState.GREEN;

                    }
                    if (colorRangeSensor.getColorSeenBySensor() == Colors.ColorType.PURPLE) {
                        intake.spindex.goToCollect2();
                        sorterStateCS = States.SorterState.COLLECT2;
                        firstSorterCS = States.FirstSorterState.PURPLE;
                    }

                }
                if (firstSorterCS != States.FirstSorterState.EMPTY && secondSorterCS == States.SecondSorterState.EMPTY && sorterStateCS == States.SorterState.COLLECT2 && Math.abs(intake.spindex.getCurrentPosition() -intake.spindex.pos) < 0.05) {
                    if (colorRangeSensor.getColorSeenBySensor() == Colors.ColorType.GREEN) {
                        secondSorterCS = States.SecondSorterState.GREEN;
                        intake.spindex.goToCollect3();
                        sorterStateCS = States.SorterState.COLLECT3;
                    }
                    if (colorRangeSensor.getColorSeenBySensor() == Colors.ColorType.PURPLE) {
                        secondSorterCS = States.SecondSorterState.PURPLE;
                        intake.spindex.goToCollect3();
                        sorterStateCS = States.SorterState.COLLECT3;
                    }

                }
                if (secondSorterCS != States.SecondSorterState.EMPTY && thirdSorterCS == States.ThirdSorterState.EMPTY && sorterStateCS == States.SorterState.COLLECT3 && Math.abs(intake.spindex.getCurrentPosition() -intake.spindex.pos) < 0.05) {
                    if (colorRangeSensor.getColorSeenBySensor() == Colors.ColorType.GREEN) {
                        thirdSorterCS = States.ThirdSorterState.GREEN;
                        intake.spindex.goToScore1();
                        sorterStateCS = States.SorterState.SCORE1;
                        sorterCapacityCS = States.SorterCapacity.FULL;
                    }
                    if (colorRangeSensor.getColorSeenBySensor() == Colors.ColorType.PURPLE) {
                        thirdSorterCS = States.ThirdSorterState.PURPLE;
                        intake.spindex.goToScore1();
                        sorterStateCS = States.SorterState.SCORE1;
                        sorterCapacityCS = States.SorterCapacity.FULL;
                    }

                }

            }
        }
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

    public void update(){
        intake.spindex.power = intake.spindex.spindexPid.calculatePower(intake.spindex.getCurrentPosition() - intake.spindex.pos);
        intake.spindex.spindex.setPower(intake.spindex.power);
    }

    public void init(){
        intake.spindex.goToCollect1();
    }
}
