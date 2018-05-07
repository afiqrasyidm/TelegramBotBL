package com.mycompany.app;

import org.telegram.abilitybots.api.bot.AbilityBot;
import io.github.cdimascio.dotenv.Dotenv;
import org.javalite.activejdbc.Base;
import org.postgresql.Driver;

public class BaseBot extends AbilityBot {
    private static Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMalformed().ignoreIfMissing().load();

    public BaseBot() {
        super(validToken(), validUsername());
    }

    public void openDBConnection(){
        Base.open("org.postgresql.Driver", validHost(), validUser(), validPassword());
    }

    public void closeDBConnection(){
        Base.close();
    }

    private static String validToken() {
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
}