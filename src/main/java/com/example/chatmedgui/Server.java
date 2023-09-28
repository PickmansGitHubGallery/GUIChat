package com.example.chatmedgui;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private static ConcurrentHashMap<String, User> connectedUsers = new ConcurrentHashMap<>();
    private static ArrayList<Message> chatHistory =  new ArrayList<>();
    private static ExecutorService executor = Executors.newFixedThreadPool(20); // Change the pool size as needed
    private static String fejl = "";

    public String activeUsers(){
        String activeUsername = "Liste over brugere: " + "\n";
        for (Map.Entry<String, User> entry : connectedUsers.entrySet()){
            activeUsername += entry.getValue().getUsername() + "\n";
        }

        return activeUsername;
    }

    public void addUser(String SID, Socket clientSocket) {
        User user = new User("", clientSocket);
        connectedUsers.put(SID, user);
    }
    public void sendPrivateMessageToUser(String SID, String message, String recieverSID) {
        User reciever = connectedUsers.get(recieverSID);
        User sender = connectedUsers.get(SID);
        if (reciever != null) {
            reciever.sendMessage("PM fra " +sender.getUsername()+ ": " + message);
            sendCopyOfPrivateMessageToOwnChat(SID,message,recieverSID);

        }
    }
    public void sendCopyOfPrivateMessageToOwnChat(String SID, String message,String recieverSID)
    {
        User reciever = connectedUsers.get(recieverSID);
        User sender = connectedUsers.get(SID);
        if (reciever != null) {
            sender.sendMessage("PM til " + reciever.getUsername() + ": " + message);
        }

    }
    public void sendBroadcastMessage(String name, String message) {
        for (Map.Entry<String, User> entry : connectedUsers.entrySet()) {
            User user = entry.getValue();
            user.sendMessage(name+": " +message);
        }
    }

    public void serverBroadcastMessage(String name, String message){
        for(Map.Entry<String,User> entry : connectedUsers.entrySet()){
            User user = entry.getValue();
            user.sendMessage(name + " " + message);
        }
    }

    public void updateName(String SID, String name)
    {
        connectedUsers.get(SID).setUsername(name);
    }
    public boolean isUsernameTaken(String username) {
        for (Map.Entry<String, User> entry : connectedUsers.entrySet()) {
            User user = entry.getValue();
            if (user.getUsername().equals(username)) {
                return true; // Username er taget
            }
        }
        return false; // Username er ledigt
    }
    public void sendRecentHistory(User user)
    {
        ArrayList<Message> past5Messages = new ArrayList<>();

        if(chatHistory.size()> 5)
        {

            //for at få de sidste 5 beskeder tager vi den besked på plads size()-5 og går 1 op per omgang i for loop.
            for (int i = 0 ; i<5; i++)
            {
                past5Messages.add(chatHistory.get(chatHistory.size()-5+i));
            }
        }
        else past5Messages =  chatHistory;
        for(int i = 0; i<past5Messages.size(); i++)
        {
            user.sendMessage(past5Messages.get(i).getUser().getUsername()+ " : " + past5Messages.get(i).getMsg());
        }

    }
    private void readInput(String input, PrintWriter out) {
        System.out.println(input);
        String status = input.substring(0, 3);
        String SID = input.substring(3,7);
        if (status.equals("100"))
        {
            String name = input.substring(7);
            if (isUsernameTaken(name) == false) {
                updateName(SID, name);
                out.println("199"+name);
                serverBroadcastMessage(name, "har tilsluttet sig chatten");
                sendRecentHistory(connectedUsers.get(SID));
                out.flush();
            }
            else{
                out.println("099" + fejl); // Brugernavn er taget Client skal vælge et andet
                out.flush();
            }
        }
        if (status.equals("200")) {
            String msg = input.substring(7);
            sendBroadcastMessage(connectedUsers.get(SID).getUsername(),msg);
            Message m1 = new Message();
            m1.setUser(connectedUsers.get(SID));
            m1.setMsg(msg);
            m1.setDate(new Date());
            chatHistory.add(m1);
        }
        if (status.equals("300")) {
            String splitInput = getFirstWordUsingSplit(input)[0];
            String msg = getFirstWordUsingSplit(input)[1];

            String recieverName = splitInput.substring(7);
            String recieverSID = findSIDforUser(recieverName);

            if(recieverSID.equals("fejl")) {
                User u1 = connectedUsers.get(SID);
                u1.sendMessage("#FEJL Bruger: " + recieverName + " Eksisterer ikke.");
            }
            else
            {
                sendPrivateMessageToUser(SID,msg,recieverSID);
            }
        }
        if (status.equals("400")) {
            serverBroadcastMessage(connectedUsers.get(SID).getUsername(),"har forladt chatten");
            connectedUsers.remove(SID);
        }
        if(status.equals("500")){
            connectedUsers.get(SID).sendMessage(activeUsers());

        }
    }
    public static String findFreeSID()
    {
        String SID = "";
        for (int i = 1000; i <= 9999; i++)
        {
            if (connectedUsers.containsKey(i+"") == false) {
                SID = i + "";
                break;
            }
        }return SID;
    }

    public String findSIDforUser(String username) {
        for (Map.Entry<String, User> entry : connectedUsers.entrySet()) {
            User user = entry.getValue();
            if (user.getUsername().equals(username)) {
                return entry.getKey(); // returner SID for bruger med username
            }
        }
        return "fejl";
    }


    public String[] getFirstWordUsingSplit(String input) {
        String[] tokens = input.split(" ", 2);
        return tokens;
    }


    public static void main(String[] args) {
        Server theServer = new Server();

        int portNumber = 1992;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String SID = findFreeSID();
                theServer.addUser(SID,clientSocket);

                executor.submit(() -> {
                    try (
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                    )
                    {
                        out.println("999"+SID);
                        out.println("Kommandoer:"+"\n"+ "!navn navn for a vælge et brugernavn"+"\n"+ "For listen over aktive brugere skriv !brugere."+"\n"+ "For at skrive en privatbesked skriv @brugernavn (din besked).");
                        String inputLine = "";
                        while (true) {
                            inputLine = in.readLine();
                            theServer.readInput(inputLine, out);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                            //System.out.println("Closed connection from " + clientSocket.getInetAddress());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
