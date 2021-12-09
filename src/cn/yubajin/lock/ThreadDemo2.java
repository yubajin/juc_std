package cn.yubajin.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 创建资源类，及其属性和方法
class ShareResource{
    // AA 1 BB 2  CC 3
    private int flag = 1;

    private Lock lock = new ReentrantLock();

    Condition c1 = lock.newCondition();
    Condition c2 = lock.newCondition();
    Condition c3 = lock.newCondition();

    public void print5(int loop) throws InterruptedException {
        lock.lock();
        try {
            // 判断
            while (flag != 1){
                c1.await();
            }
            // 干活
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + " :: " + i + "; 轮数: " + loop);
            }
            // 通知
            flag = 2;
            c2.signal();
        }finally {
            lock.unlock();
        }
    }

    public void print10(int loop) throws InterruptedException {
        lock.lock();
        try {
            // 判断
            while (flag != 2) {
                c2.await();
            }
            // 干活
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + " :: " + i + "; 轮数: " + loop);
            }
            // 通知
            flag = 3;
            c3.signal();
        }finally {
            lock.unlock();
        }
    }

    public void print15(int loop) throws InterruptedException {
        lock.lock();
        try {
            // 判断
            while (flag != 3) {
                c3.await();
            }
            // 干活
            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName() + " :: " + i + "; 轮数: " + loop);
            }
            // 通知
            flag = 1;
            c1.signal();
        }finally {
            lock.unlock();
        }
    }

}


/***
 * 定制化线程执行顺序
 * AA 打印5次 BB 打印10次  CC 打印15次
 *
 * 进行10轮
 */

public class ThreadDemo2{
    public static void main(String[] args) {
        // 创建线程，以及调用资源类方法
        ShareResource shareResource = new ShareResource();
        new Thread(()->{
            for (int i = 1; i <=10; i++) {
                try {
                    shareResource.print5(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AA").start();

        new Thread(()->{
            for (int i = 1; i <=10; i++) {
                try {
                    shareResource.print10(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BB").start();

        new Thread(()->{
            for (int i = 1; i <=10; i++) {
                try {
                    shareResource.print15(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"CC").start();
    }
}
