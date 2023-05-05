package com.tata.flux.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
 
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tata.flux.wpms.WordPressService;

import jakarta.annotation.PostConstruct;
import lombok.Data; 
 
@Data
@Component
public class FluxScheduled {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final WordPressService service;

  @Scheduled(cron = "0 * * * * ?")
  public void executeTask() {
    scheduler.schedule(this::triggerPullWpData, 0, TimeUnit.MILLISECONDS);
  }

  @PostConstruct
  private void triggerPullWpData()
  {
    service.triggerPullWpData().subscribe();
  }
}

