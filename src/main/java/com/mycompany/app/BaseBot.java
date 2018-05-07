package com.mycompany.app;

import java.util.Date;

import com.mycompany.app.jobs.BroadcastJob;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;

import io.github.cdimascio.dotenv.Dotenv;

public class BaseBot extends AbilityBot {
    private static Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMalformed().ignoreIfMissing().load();
    public Scheduler scheduler;

    public BaseBot() throws Exception {
        super(validToken(), validUsername());
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
    }

    public JobDetail registerJob(Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass).withIdentity("broadcastJob").build();
    }

    public Trigger registerTrigger(Date date) {
        return TriggerBuilder.newTrigger().withIdentity("broadcastTrigger").startAt(date).build();
    }

    public void queueJob(JobDetail job, Trigger trigger) throws Exception {
        scheduler.scheduleJob(job, trigger);
    }

    public static String validToken() {
        String botToken = System.getenv().get("BOT_TOKEN");
        if (botToken == null) {
            return dotenv.get("BOT_TOKEN");
        }
        return botToken;
    }

    private static String validUsername() {
        String botUsername = System.getenv().get("BOT_USERNAME");
        if (botUsername == null) {
            return dotenv.get("BOT_USERNAME");
        }
        return botUsername;
    }

    @Override
    public int creatorId() {
        String creatorId = System.getenv().get("CREATOR_ID");
        if (creatorId == null) {
            return Integer.parseInt(dotenv.get("CREATOR_ID"));
        }
        return Integer.parseInt(creatorId);
    }
}