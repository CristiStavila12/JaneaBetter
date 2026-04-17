package org.firstinspires.ftc.teamcode.sirius.Wrappers;

import java.util.ArrayList;
import java.util.Queue;

/* loaded from: classes7.dex */
public class AsyncScheduler extends Scheduler {
    public ArrayList<Queue<Task>> asyncQueues = new ArrayList<>();

    public AsyncScheduler() {
        this.asyncQueues.add(this.tasks);
    }

    public AsyncScheduler AddAnotherAsyncScheduler(AsyncScheduler scheduler) {
        for (int i = 0; i < scheduler.asyncQueues.size(); i++) {
            this.asyncQueues.get(i).addAll(scheduler.asyncQueues.get(i));
        }
        return this;
    }

    @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Scheduler
    public AsyncScheduler addTask(Task t) {
        this.asyncQueues.get(0).add(t);
        return this;
    }

    @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Scheduler
    public AsyncScheduler waitSeconds(final double sec) {
        addTask(new Task() { // from class: org.firstinspires.ftc.teamcode.sirius.Wrappers.AsyncScheduler.1
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

    public AsyncScheduler DoingTaskForSeconds(final double sec, final Task task) {
        addTask(new Task() { // from class: org.firstinspires.ftc.teamcode.sirius.Wrappers.AsyncScheduler.2
            private long track;
            private long wait;

            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public void Actions() {
                this.wait = (long) (sec * 1000.0d);
                this.track = -1L;
                task.Actions();
            }

            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public boolean Conditions() {
                if (!task.Conditions()) {
                    this.track = -1L;
                    return false;
                } else if (this.track == -1) {
                    this.track = System.currentTimeMillis();
                    return false;
                } else {
                    boolean r = System.currentTimeMillis() - this.track >= this.wait;
                    if (r) {
                        this.track = -1L;
                    }
                    return r;
                }
            }
        });
        return this;
    }

    public AsyncScheduler DoingTaskUntilSeconds(final double sec, final Task task) {
        addTask(new Task() { // from class: org.firstinspires.ftc.teamcode.sirius.Wrappers.AsyncScheduler.3
            private long track;
            private long wait;

            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public void Actions() {
                this.wait = (long) (sec * 1000.0d);
                this.track = -1L;
                task.Actions();
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
                return r || task.Conditions();
            }
        });
        return this;
    }

    public AsyncScheduler waitUntilAllAsyncDone() {
        addTask(new Task() { // from class: org.firstinspires.ftc.teamcode.sirius.Wrappers.AsyncScheduler.4
            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public void Actions() {
            }

            @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Task
            public boolean Conditions() {
                return AsyncScheduler.this.asyncQueues.size() == 1;
            }
        });
        return this;
    }

    public void clearAsyncQueues() {
        for (int i = 1; i < this.asyncQueues.size(); i++) {
            Queue<Task> q = this.asyncQueues.get(i);
            Task t = q.peek();
            if (t != null) {
                t.RanOnce = false;
            }
            q.clear();
        }
        while (this.asyncQueues.size() > 1) {
            this.asyncQueues.remove(this.asyncQueues.size() - 1);
        }
    }

    @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Scheduler
    public void clear() {
        super.clear();
        clearAsyncQueues();
    }

    @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Scheduler
    public void update() {
        if (IsSchedulerDone()) {
            return;
        }
        for (int i = 0; i < this.asyncQueues.size(); i++) {
            Queue<Task> q = this.asyncQueues.get(i);
            if (!q.isEmpty()) {
                Task t = q.peek();
                if (t.Run()) {
                    q.poll();
                }
            }
        }
        for (int i2 = this.asyncQueues.size() - 1; i2 >= 1; i2--) {
            if (this.asyncQueues.get(i2).isEmpty()) {
                this.asyncQueues.remove(i2);
            }
        }
    }

    @Override // org.firstinspires.ftc.teamcode.sirius.Wrappers.Scheduler
    public boolean IsSchedulerDone() {
        return this.tasks.isEmpty() && this.asyncQueues.size() == 1;
    }
}
