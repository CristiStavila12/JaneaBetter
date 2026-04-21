package org.firstinspires.ftc.teamcode.sirius.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.sirius.intake.mechanism.Active;
import org.firstinspires.ftc.teamcode.sirius.intake.mechanism.Spindex;

public class IntakeSubsystem {
    public Active active;
    public Spindex spindex;

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
}
