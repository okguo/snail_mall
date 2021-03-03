package com.okguo.snailmall.search.thread;

import org.elasticsearch.threadpool.FixedExecutorBuilder;

import java.util.concurrent.*;

/**
 * @Description:
 * @Author: Guoyongfu
 * @Date: 2021/03/02 17:04
 */
public class ThreadTest {

    public static ExecutorService service = new ThreadPoolExecutor(10,
            20,
            30L,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public void thread(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("start----");

//        service = Executors.newCachedThreadPool();
//        service = Executors.newFixedThreadPool(10);
//        service = Executors.newScheduledThreadPool(10);
        service = Executors.newSingleThreadExecutor();
        System.out.println("end----");


    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("start------");
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程id："+Thread.currentThread().getId());
//            System.out.println(10 / 2);
//        }, service);

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程id：" + Thread.currentThread().getId());
//            return 10 / 0;
//        }, service).whenComplete((res,exception)->{
//            System.out.println("异步完成,结果是：" + res + ";异常是:" + exception);
//        }).exceptionally(throwable -> {
//            return 10;
//        });

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程id：" + Thread.currentThread().getId());
            return 10 / 2;
        }, service).handle((res, exception) -> {
            return res;
        });


        System.out.println("---------end---结果："+future.get());


    }

    public static class Thread01 extends Thread {
        public void run() {
            System.out.println(10 / 2);
        }
    }

    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println(10 / 2);
        }
    }

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            return 10 / 2;
        }
    }


}
