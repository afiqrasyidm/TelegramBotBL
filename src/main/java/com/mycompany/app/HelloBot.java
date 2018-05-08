package com.mycompany.app;

import org.telegram.abilitybots.api.objects.Ability;

import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.text.ParseException;
import java.util.function.Predicate;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import com.mycompany.app.Tables.*;


import java.sql.*;

public class HelloBot extends BaseBot {
    private Statement stmt;

    public HelloBot() throws Exception {
        super();
        //stmt = super.ConnecttoDB();
        //super.openDBConnection();
    }

    public Ability playWithMe() {
        String playMessage = "Play with me!";

        return Ability
                .builder()
                .name("play")
                .info("Do you want to play with me?")
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(ctx -> silent.forceReply(playMessage, ctx.chatId()))
                // The signature of a reply is -> (Consumer<Update> action, Predicate<Update>... conditions)
                // So, we  first declare the action that takes an update (NOT A MESSAGECONTEXT) like the action above
                // The reason of that is that a reply can be so versatile depending on the message, context becomes an inefficient wrapping
                .reply(upd -> {
                            // Prints to console
                            System.out.println("I'm in a reply!");
                            // Sends message
                            silent.send("It's been nice playing with you!", upd.getMessage().getChatId());
                        },
                    // Now we start declaring conditions, MESSAGE is a member of the enum Flag class
                    // That class contains out-of-the-box predicates for your replies!
                    // MESSAGE means that the update must have a message
                    // This is imported statically, Flag.MESSAGE
                    MESSAGE,
                    // REPLY means that the update must be a reply, Flag.REPLY
                    REPLY,
                    // A new predicate user-defined
                    // The reply must be to the bot
                    isReplyToBot(),
                    // If we process similar logic in other abilities, then we have to make this reply specific to this message
                    // The reply is to the playMessage
                    isReplyToMessage(playMessage)
                )
                // You can add more replies by calling .reply(...)
                .build();
    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
    }

    public Ability sayHelloWorld() {
        return Ability
                .builder()
                .name("hello")
                .info("says hello world!")
                .input(0)
                .locality(USER)
                .privacy(ADMIN)
                .action(ctx -> silent.send("Hello world!", ctx.chatId()))
                .post(ctx -> silent.send("Bye world!", ctx.chatId()))
                .build();
    }

    //untuk percobaan query db jadi tulis /start <argument apapun>
    public Ability testingDB() {
        return Ability
                .builder()
                .name("start")
                .info("says hello world!")
                .input(0)
                .locality(USER)
                .privacy(ADMIN)
                .action(ctx -> {
                    //    String rst = "";
                    if (ctx.arguments().length == 0) {
                        silent.send("start", ctx.chatId());
                    } else {
                        //open db connection (it is a must before a query)
                        super.openDBConnection();
                        User user = Tables.USER.findFirst("id = ?", 1);
                        silent.send(" " + user.get("username"), ctx.chatId());
                        //close db
                        super.closeDBConnection();
                    }
                })
                .build();
    }

    public Ability setRemote() {
      // arg 1 = tanggal
      // arg 2 = alasan
        return Ability
                .builder()
                .name("remote")
                .info("Set status menjadi remote")
                .input(2)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String tanggal = ctx.firstArg();
                    String alasan = ctx.secondArg();
                    if (dateIsValid(tanggal)) {
                        java.sql.Date newDate = changeDateFormat(tanggal);

                        openDBConnection();

                        String username = ctx.user().username();
                        User user = Tables.USER.findFirst("username = ?", username);

                        History record = new History();
                        record.set("id", user.get("id"))
                                .set("status", "remote")
                                .set("tanggal", newDate)
                                .set("reason", alasan);
                        record.saveIt();

                        closeDBConnection();
                    } else {
                        // salah tanggal
                        silent.send("Format tanggal salah", ctx.chatId());
                    }
                })
                .build();
    }

    //blm
    public Ability setCuti() {
      // arg 1 = tanggal
        return Ability
                .builder()
                .name("cuti")
                .info("Set status menjadi cuti")
                .input(1)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String tanggal = ctx.firstArg();
                    if (dateIsValid(tanggal)) {
                        java.sql.Date newDate = changeDateFormat(tanggal);

                        openDBConnection();

                        String username = ctx.user().username();
                        User user = Tables.USER.findFirst("username = ?", username);

                        History record = new History();
                        record.set("id", user.get("id"))
                                .set("status", "cuti")
                                .set("tanggal", newDate);
                        record.saveIt();

                        closeDBConnection();
                    } else {
                        // salah tanggal
                        silent.send("Format tanggal salah", ctx.chatId());
                    }
                })
                .build();
    }

    //blm
    public Ability setSakit() {
      // arg 1 = tanggal
        return Ability
                .builder()
                .name("sakit")
                .info("Set status menjadi sakit")
                .input(1)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String tanggal = ctx.firstArg();
                    if (dateIsValid(tanggal)) {
                        java.sql.Date newDate = changeDateFormat(tanggal);

                        openDBConnection();

                        String username = ctx.user().username();
                        User user = Tables.USER.findFirst("username = ?", username);

                        History record = new History();
                        record.set("id", user.get("id"))
                                .set("status", "sakit")
                                .set("tanggal", newDate);
                        record.saveIt();

                        closeDBConnection();
                    } else {
                        // salah tanggal
                        silent.send("Format tanggal salah", ctx.chatId());
                    }
                })
                .build();
    }

    private boolean dateIsValid(String tanggal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date inputDate = dateFormat.parse(tanggal);
            Date currentDate = new Date();

            return inputDate.compareTo(currentDate) >= 0;
        } catch (ParseException e) {
            // salah format
            return false;
        }
    }

    private java.sql.Date changeDateFormat(String oldTanggal) {
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date oldDate = oldDateFormat.parse(oldTanggal);

            String date = newDateFormat.format(oldDate);
            Date newDate = newDateFormat.parse(date);

            java.sql.Date sqlDate = new java.sql.Date(newDate.getTime());
            return sqlDate;
        } catch (ParseException e) {
            return null;
        }
    }

    private void setStatus(String tanggal, String alasan, SilentSender silent) {

    }
}
