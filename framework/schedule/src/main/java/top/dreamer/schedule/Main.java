package top.dreamer.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 22:39
 * @description: 测试Quart
 */
public class Main {
    static class task1 extends HScheduleTaskAbstract {
        @Override
        public void executeScheduledTask() {
            System.out.println("task1 running......");
        }
    }
    static class task2 extends HScheduleTaskAbstract {
        @Override
        public void executeScheduledTask() {
            System.out.println("task2 running......");
        }
    }

    static class task3 extends HScheduleTaskAbstract {
        @Override
        public void executeScheduledTask() {
            System.out.println("task3 running......");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        task1 task1 = new task1();
        task2 task2 = new task2();
        task3 task3 = new task3();

        task1.scheduleTask("g1", 0, 2);
        task2.scheduleTask("g1", 1, 2);
        task3.scheduleTask("g2", 0, 3);

        Thread.sleep(12000);

        HScheduleTaskAbstract.stopTask("g1");

    }
}
