import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

/*********************************************
 *
 * Created By Abul Syed
 *
 * Functionality:
 * 1. Connect to server? YES
 * 2. Receive messages from other users? YES
 * 3. Respond to other users? YES
 *
 * Protocols: 12 protocols used:
 * NICK, USER, JOIN, PRIVMSG, NOTICE, TIME, TOPIC,
 * ADMIN, PING, PONG, KICK, QUIT
 *
 * Creativity:
 * 1. My bot kicks a user on the 3rd time they send a
 * banned word
 * 2. Bot sends a quote based on user input
 * 3. I implemented a dice game where the bot asks user
 * to guess a number from a 6 sided dice
 * 
 *********************************************/

public class IrcBot {

    private static String nick;
    private static String username;
    private static String realName;
    private static PrintWriter output;
    private static Scanner input;


    public static void main(String[] args) throws IOException {

        // Server to connect to
        String server = "chat.freenode.net";

        // Channel the bot will join
        String channel = "sybot";

        // User details
        nick = "sybot";
        username = "sybot";
        realName = "sy bot";

        // Socket connecting to server
        Socket socket = new Socket(server, 6667);

        // Way to output text to be sent to server
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new Scanner(socket.getInputStream());

        // Creating and registering user
        write("NICK", nick);
        write("USER", username + " 0 * :" + realName);
        // Joining channel
        write("JOIN", "#" + channel);

        write("PRIVMSG", "#" + channel+ " :Hi i am a bot created by Abul Syed!");

        // Notice message to user specified
        write("NOTICE", "legend56 hi");

        // Specifies current time in console
        write("TIME", "");

        // Changes topic of channel #sybot, only works if assigned as a channel operator
        write("TOPIC", "#" + channel+ " yoo");

        // Returns information about administrator of the server
        write("ADMIN", "");

        write("PRIVMSG", "#" + channel+ " :For a quote, type q1, q2 ... q5! Or if you want to play a dice game, type dice");

        write("PRIVMSG", "#" + channel+ " :Other commands include !time, !spam, !quit, !kick");


        String str = "";
        int warning = 1;
        String time2 ="";

        // Generating a random number from 1-6 to be used for dice number guessing game
        Random dice = new Random();
        int diceNum;
        diceNum = 1 + dice.nextInt(6);
        String diceNumber = Integer.toString(diceNum);
        System.out.println(diceNumber);

        // Receiving input from server and responding depending on what is received
        while (input.hasNext()){
            String serverMsg = input.nextLine();
            System.out.println(serverMsg);
            // Ping command - Need to respond to PING from the server to avoid being disconnected
            if (serverMsg.startsWith("PING")){
                String ping = serverMsg.split(" ", 2)[1];
                // Responding with PONG
                write("PONG", ping);
            }
            // Here i store the server time in a variable
            // TIME command had numeric reply of 391
            if (serverMsg.contains("391")) {
                String myString = serverMsg;
                String delimter = " :";
                String [] words = myString.split(delimter);

                time2 = "Local time is: " + words[words.length-1];
            }
            // Time we be outputted on chat if !time is sent by user
            if (serverMsg.contains("!time")){
                write("PRIVMSG",  "#" + channel + " :" + time2);
            }
            // Bot will respond to anyone that types "Hello" or "hello"
            // Bot responds with Hello + name of user who said hello
            if (serverMsg.contains("Hello") || serverMsg.contains("hello")){
                String name = serverMsg;
                str = "";
                int endOfName = 0;

                // Here i am extracting the users name who said Hello
                // The users name is between : and !, here i locate position of "!"
                for (int i=0; i<serverMsg.length(); i++){
                    if (serverMsg.charAt(i) == '!'){
                        endOfName = i;
                    }
                }
                // Extracting name
                str = name.substring(1,endOfName);
                // Bot hello response
                write("PRIVMSG #" + channel+ " :Hello " + str + " i'm a bot!d", "");
            }
            // Here i kick anyone who repeatedly uses a foul word 3 times
            // You must be a channel operator for this to work
            if (serverMsg.contains("fuck")){
                write("PRIVMSG #" + channel+ " :Hello " + str + ", Please refrain from using such language. You are on warning " + warning + " you will be kicked on your 3rd!", "");
                warning++;
                if (warning == 4){
                    write("KICK", "#" + channel + " " + str);
                }
            }
            // Bot can easily spam, conditional can be increased to flood chat
            if (serverMsg.contains("!spam")){
                for(int i=0; i<10; i++){
                    write("PRIVMSG #" + channel +" :Spamming!", "");
                }
            }
            // Disconnects the bot from the server if someone says quit bot
            if (serverMsg.contains("!quit")){
                write("QUIT", "BYE FOR NOW");
            }
            // Using KICK command
            // Will kick the user specified
            if (serverMsg.contains("!kick")){
                write("KICK", "#" + channel+" legend56");
            }


            // Quotes based on what is typed
            if (serverMsg.endsWith("q1")){
                write("PRIVMSG #" + channel, ":Be yourself; everyone else is already taken.");
            }else if (serverMsg.endsWith("q2")){
                write("PRIVMSG #" + channel, ":In three words I can sum up everything I've learned about life: it goes on.");
            }else if (serverMsg.endsWith("q3")){
                write("PRIVMSG #" + channel, ":Darkness cannot drive out darkness: only light can do that. Hate cannot drive out hate: only love can do that.");
            }else if (serverMsg.endsWith("q4")){
                write("PRIVMSG #" + channel, ":We accept the love we think we deserve.");
            }else if (serverMsg.endsWith("q5")){
                write("PRIVMSG #" + channel, ":To live is the rarest thing in the world. Most people exist, that is all.");
            }else {

            }


            // Dice game
            // Where user has a chance to guess a 6 sided dice number
            String diceNum2 = Integer.toString(diceNum);

            if (serverMsg.endsWith("dice")){
                write("PRIVMSG #" + channel, ":Welcome to the dice game");
                write("PRIVMSG #" + channel, ":I will roll a 6 sided dice and you have to guess my number");
                write("PRIVMSG #" + channel, ":What's your guess?");

                while (!serverMsg.endsWith(diceNum2)) {
                    serverMsg = input.nextLine();
                    if (serverMsg.endsWith(diceNum2)) {
                        write("PRIVMSG #" + channel, ":Well done, you guessed correctly");
                    } else {
                        write("PRIVMSG #" + channel, ":Unlucky, try again");
                    }
                }
            }
        }

        input.close();
        output.close();
        socket.close();

    }

    private static void write(String cmd, String msg) {
        String fullMsg = cmd + " " + msg;
        System.out.println(fullMsg);
        output.print(fullMsg + "\r\n");
        output.flush();
    }


}
