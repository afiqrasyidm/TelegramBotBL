package com.mycompany.app;

import com.google.common.annotations.VisibleForTesting;
import org.glassfish.hk2.api.Visibility;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Map;
import java.util.function.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.mycompany.app.CalendarQuickstart;

import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class HelloBot extends AbilityBot {
  public static String BOT_TOKEN = "599164260:AAGJcr3NdXBs8TsSng4_b_w_gHwkptD13II";
  public static String BOT_USERNAME = "afiqbotbot";

  public HelloBot() {
    super(BOT_TOKEN, BOT_USERNAME);
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

      public Ability getToken() {


          return Ability
                    .builder()
                    .name("start")
                    .info("says hello world!")
                    .input(0)
                    .locality(USER)
                    .privacy(ADMIN)
                    .action(ctx ->       {

                        if(ctx.arguments().length == 0 ){

                              silent.send("start", ctx.chatId());
                        }
                        else{
                                silent.send(ctx.secondArg(), ctx.chatId());
                        }

                     })
                    .build();
      }

      public Ability getEvents() throws IOException, GeneralSecurityException {


          return Ability
                    .builder()
                    .name("events")
                    .info("tell events")
                    .input(0)
                    .locality(ALL)
                    .privacy(PUBLIC)
                    .action(ctx ->
                      {
                         try {
                           CalendarQuickstart cal = new CalendarQuickstart();

                            silent.send(cal.getEvents(), ctx.chatId());
                          }
                          catch (IOException e ) {
                              e.printStackTrace(); // Or something more intelligent
                          }
                          catch (GeneralSecurityException e) {
                              e.printStackTrace(); // Or something more intelligent
                          }
                      })
                    .build();

      }
  @Override
   public int creatorId() {
     return 470958982;
   }




}
