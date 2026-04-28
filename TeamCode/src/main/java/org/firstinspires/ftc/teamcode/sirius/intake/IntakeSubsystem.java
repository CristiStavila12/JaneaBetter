package org.firstinspires.ftc.teamcode.sirius.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.sirius.intake.mechanism.Active;
import org.firstinspires.ftc.teamcode.sirius.intake.mechanism.Spindex;

public class IntakeSubsystem {
    public Active active;
    public Spindex spindex;

    private double lastSpindexPosition = 0.0;
    private double negativeRotations = 0.0;
    private boolean negativeCounterStarted = false;

    public IntakeSubsystem(HardwareMap hardwareMap){
        active = new Active(hardwareMap);
        spindex = new Spindex(hardwareMap);
    }

    public void goToIdle() {
        active.stop();
    }

    public void goToCollect(){
        active.intake();
    }

    public void updateNegativeRotationCounter() {
        double currentPosition = spindex.getCurrentPosition();

        if (!negativeCounterStarted) {
            lastSpindexPosition = currentPosition;
            negativeCounterStarted = true;
            return;
        }

        double change = currentPosition - lastSpindexPosition;

        if (change < 0) {
            negativeRotations += -change;
        }

        lastSpindexPosition = currentPosition;
    }

    public double getNegativeRotations() {
        return negativeRotations;
    }

    public void resetNegativeRotations() {
        negativeRotations = 0.0;
        lastSpindexPosition = spindex.getCurrentPosition();
        negativeCounterStarted = true;
    }
}