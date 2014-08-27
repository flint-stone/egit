package edu.uiuc.cs.cs425.xgrep;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class handles user inputs, load server configuration file, create
 * GrepSender thread for every server.
 * @author wwang84
 * @version 1.3
 */
public class Client {

    /**
     * The entry point on client side. End users input their Xgrep command and
     * can see the result
     *
     * @param String[] args
     * @return void.
     */
    public static void main(String[] args) {

        // 1. Load server config file
        if(args.length<1){
            System.out.println("Please indicate your server config file");
            return;
        }
        List<ClientWorker> connection;
        try {
            connection = LoadServer(args[0]);
        } catch (FileNotFoundException e2) {
            System.out.println("Server config file cannot be found. Please run it again.");
            return;
        } catch (IOException e2) {
            System.out.println("Error occurs when reading files. Please run it again.");
            return;
        }
        String input = "";
        if(args.length==2){
            input=args[1];
            startClient(input,connection);
            return;
        }
       
        // 2. welcome message
        System.out.println("***************Welcome to Xgrep****************");
        System.out.println("Xgrep is a command-line utility for searching ");
        System.out.println("log files for lines matching a regular expres-");
        System.out.println("sion. It supports all the arguments in grep. ");
        System.out.println("Example:");
        System.out.println("Xgrep -key timestamp -value details [argument] ");
        System.out.println("***********************************************");

        // 3. Read user's input and create threads
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));
           
            try {
                input = br.readLine();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            startClient(input,connection);
           
        }
    }

    private static void startClient(String input,List<ClientWorker> connection ){
       
        // System.out.println(input);
                    String[] commandLine = CommandParser.parseXgrepToGrep(input);
                    if (commandLine == null) {
                        System.out.println("Illegal input. Please use Xgrep as ");
                        System.out
                                .println("Example: Xgrep -key timestamp -value details [argument] ");
                        return;
                    }
                    // start thread for every server machine, and record the execution
                    // time
                    if (connection != null && connection.size() != 0) {
                        ExecutorService taskExecutor = Executors
                                .newFixedThreadPool(connection.size());
                        long startTime = System.currentTimeMillis();
                        for (int i = 0; i < connection.size(); i++) {
                            connection.get(i).setCommandLine(commandLine);
                            taskExecutor.execute(connection.get(i));
                        }
                        taskExecutor.shutdown();
                        try {
                            taskExecutor.awaitTermination(Long.MAX_VALUE,
                                    TimeUnit.NANOSECONDS);
                            long time = System.currentTimeMillis() - startTime;
                            System.out.println("Execution time for \"" + input + "\": "
                                    + time + " ms");
                        } catch (InterruptedException e) {
                            System.out.println("Thread is interrupted.");
                            e.printStackTrace();
                        }
                    }
    }
    /**
     * This method loads the configuration file which include the connection
     * information for each server.
     *
     * @param fileName
     *            : Server configuration file name
     * @return List<ClientProxy>: A list of threads handle sending and receiving
     *         messages.
     */
    public static List<ClientWorker> LoadServer(String fileName) throws FileNotFoundException, IOException{
        List<ClientWorker> connection = new LinkedList<ClientWorker>();
       
        // FileReader reads text files in the default encoding.
        FileReader fileReader = new FileReader(fileName);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            connection.add(new ClientWorker(line));
            // System.out.println(line);
        }
        bufferedReader.close();
       
        return connection;
    }
}
