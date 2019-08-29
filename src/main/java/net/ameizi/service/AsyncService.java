package net.ameizi.service;

import lombok.extern.slf4j.Slf4j;
import net.ameizi.vo.Employee;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AsyncService {

    /**
     * 异步方法
     *
     * @return
     */
    @Async
    public CompletableFuture<Employee> processAsync() {
        log.info("Start processing request");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Employee employee = new Employee();
        employee.setName("乔布斯");
        log.info("Completed processing request");
        return CompletableFuture.completedFuture(employee);
    }

    /**
     * 同步方法
     *
     * @return
     */
    public Employee processSync() {
        log.info("Start processing request");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Employee employee = new Employee();
        employee.setName("乔布斯");
        log.info("Completed processing request");
        return employee;
    }


}
