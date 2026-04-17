package org.firstinspires.ftc.teamcode.sirius.intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.sirius.intake.mechanism.Active;

public class IntakeSubsystem {
    public Active active;

    public IntakeSubsystem(HardwareMap hardwareMap){
        active = new Active(hardwareMap);
    }

    public void goToIdle() {
        active.stop();
    }
    public void goToCollect(){
        active.intake();
    }
}
