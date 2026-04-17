package org.firstinspires.ftc.teamcode.sirius.Wrappers;

import org.firstinspires.ftc.teamcode.sirius.ChassisController;
//import org.firstinspires.ftc.teamcode.sirius.Robot;

import java.util.LinkedList;
import java.util.Queue;

/* loaded from: classes7.dex */
public class Scheduler {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    ChassisController Chasis;
//    Robot robot;
    public Queue<Task> tasks = new LinkedList();

    public Scheduler addTask(Task t) {
        this.tasks.add(t);
        return this;
    }

    public boolean IsSchedulerDone() {
        return this.tasks.isEmpty();
    }

    public Scheduler waitForStill() {
        addTask(new Task() { // from class: org.firstinspires.ftc.teamcode.sirius.Wrappers.Scheduler.1
            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public void Actions() {
            }

            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public boolean Conditions() {
                return true;
            }
        });
        return this;
    }

    public Scheduler waitSeconds(final double sec) {
        addTask(new Task() { // from class: org.firstinspires.ftc.teamcode.sirius.Wrappers.Scheduler.2
            private long track;
            private long wait;

            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public void Actions() {
                this.wait = (long) (sec * 1000.0d);
                this.track = -1L;
            }

            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public boolean Conditions() {
                if (this.track == -1) {
                    this.track = System.currentTimeMillis();
                    return false;
                }
                boolean r = System.currentTimeMillis() - this.track >= this.wait;
                if (r) {
                    this.track = -1L;
                }
                return r;
            }
        });
        return this;
    }

    public Scheduler AddAnotherScheduler(Scheduler scheduler) {
        this.tasks.addAll(scheduler.tasks);
        return this;
    }

    public void clear() {
        Task t = this.tasks.peek();
        if (t != null) {
            t.RanOnce = false;
        }
        this.tasks.clear();
    }

    public void update() {
        if (IsSchedulerDone()) {
            return;
        }
        Task t = this.tasks.peek();
        if (t == null) {
            throw new AssertionError();
        }
        boolean result = t.Run();
        if (result) {
            this.tasks.poll();
        }
    }
}
