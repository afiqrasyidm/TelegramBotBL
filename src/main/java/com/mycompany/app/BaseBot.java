package com.mycompany.app;

import java.util.Date;

import com.mycompany.app.jobs.BroadcastJob;

import org.apache.commons.lang3.time.DateUtils;
import org.javalite.activejdbc.Base;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;

public class BaseBot extends AbilityBot {
    private static Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMalformed().ignoreIfMissing().load();
    public Scheduler scheduler;

    public BaseBot() throws Exception {
        super(validToken(), validUsername());
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
    }

    public void openDBConnection() {
        Base.open("org.postgresql.Driver", validHost(), validUser(), validPassword());
    }

    public void closeDBConnection() {
        Base.close();
    }

    public JobDetail registerJob(Class<? extends Job> jobClass, String identifier) {
        return JobBuilder.newJob(jobClass).withIdentity(identifier, "job").build();
    }

    public Trigger registerTrigger(Date date, String identifier) {
        return TriggerBuilder.newTrigger().withIdentity(identifier, "trigger").startAt(date).build();
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

    private static String validHost() {
        String dbHost = System.getenv().get("DB_HOST");
        if (dbHost == null) {
            return dotenv.get("DB_HOST");
        }
        return dbHost;
    }

    private static String validUser() {
        String dbUser = System.getenv().get("DB_USER");
        if (dbUser == null) {
            return dotenv.get("DB_USER");
        }
        return dbUser;
    }

    private static String validPassword(){
        String dbPass = System.getenv().get("DB_PASSWORD");
        if (dbPass == null) {
            return dotenv.get("DB_PASSWORD");
        }
        return dbPass;
    }

    @Override
    public int creatorId() {
        String creatorId = System.getenv().get("CREATOR_ID");
        if (creatorId == null) {
            return Integer.parseInt(dotenv.get("CREATOR_ID"));
        }
        return Integer.parseInt(creatorId);
    }

    public Statement ConnecttoDB() throws Exception {

        Statement stmt = null;

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
            e.printStackTrace();

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(validHost(), validUser(), validPassword());

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

        }

        if (connection != null) {
            try {
                stmt = connection.createStatement();
            }

            catch (Exception e) {

                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();

            }
            /*
             * String sql; sql = "SELECT * FROM hackaton.user"; ResultSet rs =
             * stmt.executeQuery(sql);
             *
             * while(rs.next()){ //Retrieve by column name String username =
             * rs.getString("username");
             *
             * //Display values
             *
             * System.out.print("Username: " + username); }
             */
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        return stmt;
    }

}
