// com.example.web_summaryy.scheduler.DailyScheduler

package com.example.web_summaryy.scheduler;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.web_summaryy.service.UpdateService;

import java.util.concurrent.CompletableFuture;

@Component
public class DailyScheduler {

    private final  UpdateService updateService;
    private final boolean runOnStartup;

    public DailyScheduler(UpdateService updateService,
                          @Value("${app.runOnStartup:false}") boolean runOnStartup) {
        this.updateService = updateService;
        this.runOnStartup = runOnStartup;
    }

    @Scheduled(cron = "${scheduler.cron}", zone = "${scheduler.zone:Asia/Yekaterinburg}")
    public void fetchAndSave() {
        updateService.syncPositions();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runOnceAfterStartup() {
        if (runOnStartup) {
            CompletableFuture.runAsync(this::fetchAndSave);
        }
    }
}
