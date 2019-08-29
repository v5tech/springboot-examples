package net.ameizi.controller;

import lombok.extern.slf4j.Slf4j;
import net.ameizi.service.AsyncService;
import net.ameizi.vo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Callable
 * WebAsyncTask
 * DeferredResult
 * ListenableFuture
 * ListenableFutureTask
 * CompletableFuture
 * CompletionStage
 * ResponseBodyEmitter
 * SseEmitter
 * StreamingResponseBody
 */
@Slf4j
@RestController
@RequestMapping("/async")
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    /**
     * 返回CompletableFuture 调用的异步方法
     *
     * @return
     */
    @GetMapping("/completable")
    public CompletableFuture<Employee> async() {
        log.info("Request received");
        CompletableFuture<Employee> completableFuture = asyncService.processAsync();
        log.info("Servlet thread released");
        return completableFuture;
    }

    /**
     * 同步转异步
     *
     * @return
     */
    @GetMapping("/sync2async")
    public CompletableFuture<Employee> sync2async() {
        log.info("Request received");
        CompletableFuture<Employee> completableFuture = CompletableFuture.supplyAsync(() -> asyncService.processSync());
        log.info("Servlet thread released");
        return completableFuture;
    }

    /**
     * 返回Callable 方法内部其实调用的是同步方法
     *
     * @return
     */
    @GetMapping("/callable")
    public Callable<Employee> callable() {
        log.info("Request received");
        Callable<Employee> callable = () -> asyncService.processSync();
        log.info("Servlet thread released");
        return callable;
    }

    /**
     * 返回Future 方法内部其实调用的是同步方法
     *
     * @return
     */
    @GetMapping("/future")
    public Future<Employee> future() {
        log.info("Request received");
        AsyncResult<Employee> asyncResult = new AsyncResult<>(asyncService.processSync());
        log.info("Servlet thread released");
        return asyncResult;
    }

    /**
     * 使用DeferredResult异步返回
     *
     * @return
     */
    @GetMapping("/deferred")
    public DeferredResult<Employee> deferred() {
        log.info("Request received");
        DeferredResult<Employee> deferredResult = new DeferredResult<>();
        asyncService.processAsync().whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
        // ForkJoinPool.commonPool().submit(()->deferredResult.setResult(asyncService.processSync()));
        log.info("Servlet thread released");
        return deferredResult;
    }

    /**
     * 异步任务
     *
     * @return
     */
    @GetMapping("/webtask")
    public WebAsyncTask<Employee> webAsyncTask() {
        log.info("Request received");
        WebAsyncTask<Employee> asyncTask = new WebAsyncTask<>(10 * 1000, () -> asyncService.processSync());
        log.info("Servlet thread released");
        return asyncTask;
    }

    /**
     * ListenableFutureTask 方法内部其实调用的是同步方法
     *
     * @return
     */
    @GetMapping("/listentask")
    public ListenableFutureTask<Employee> listenableFutureTask() {
        log.info("Request received");
        ListenableFutureTask<Employee> task = new ListenableFutureTask<>(() -> asyncService.processSync());
        log.info("Servlet thread released");
        return task;
    }

    @GetMapping("/streamingbody")
    public StreamingResponseBody responseBody() {
        log.info("Request received");
        return outputStream -> {
            Employee employee = asyncService.processSync();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(employee);
            objectOutputStream.flush();
            objectOutputStream.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            outputStream.write(bytes);
            outputStream.flush();
            byteArrayOutputStream.close();
        };
    }


    /**
     * 同步调用
     *
     * @return
     */
    @GetMapping("/sync")
    public Employee sync() {
        log.info("Request received");
        Employee employee = asyncService.processSync();
        log.info("Servlet thread released");
        return employee;
    }


}
