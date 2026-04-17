package org.firstinspires.ftc.teamcode.sirius.Wrappers;

/* loaded from: classes7.dex */
public abstract class Task {
    public boolean RanOnce = false;

    public abstract void Actions();

    public abstract boolean Conditions();

    public final boolean Run() {
        if (!this.RanOnce) {
            this.RanOnce = true;
            Actions();
        }
        if (Conditions()) {
            this.RanOnce = false;
            return true;
        }
        return false;
    }
}
