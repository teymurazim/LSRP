package socs.network.networking;

import ch.qos.logback.classic.net.SimpleSocketServer;
import ch.qos.logback.core.net.server.Client;
import com.sun.istack.internal.NotNull;
import socs.network.events.Event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class Server
{
    private int portNum;
    private ClientHandler[] clients;
    public Event<Integer, String> msgReceivedEvent;

    public Server(int portNum)
    {
        this.portNum = portNum;
        clients = new ClientHandler[4];
        msgReceivedEvent = new Event();
        listen();
    }

    private void listen()
    {
            new Thread(new Runnable() {
                public void run() {
                    try
                    {
                        while (true)
                        {
                            ServerSocket server = new ServerSocket(portNum);
                            Socket client = server.accept();

                            int index = getAvailableIndex();

                            if(index == -1)
                            {
                                System.err.println("ERROR! Connections Capacity Reached!");
                                break;
                            }

                            ClientHandler clientHandler = new ClientHandler(client, index);
                            new Thread(clientHandler).start();
                        }
                    }
                    catch(Exception e)
                    {
                        System.err.println("FATAL_ERROR! Server thread interrupted\n" + e.getStackTrace());
                    }
            }
        }).start();
    }

    /**
     *
     * @return the index of the available entry in clients array, -1 on failure
     */
    private int getAvailableIndex()
    {
        for(int i = 0; i < clients.length; i++)
        {
            if(clients[i] == null)
            {
                return i;
            }
        }

        return -1;
    }


    //TODO add client
    public void attach(String processIP, short processPort)
    {
        try
        {
            Socket socket = new Socket();
            InetAddress inetAddress = InetAddress.getByName("localHost");
            SocketAddress bindingPoint = new InetSocketAddress(inetAddress, portNum);
            socket.bind(bindingPoint);
        }
        catch (Exception e)
        {
            System.err.println("ERROR! Failed to attache to " + processIP + " " + processPort + "\n" + e.getStackTrace());
        }

    }

    public void send(String msg, int index)
    {
        clients[index].send(msg);
    }
}


class ClientHandler implements Runnable
{
    private Socket client;
    private int index;
    private DataInputStream fromClient = null;
    private DataOutputStream toClient = null;
    public Event<Integer, String> msgReceived;

    ClientHandler(@NotNull Socket client, int index)
    {
        this.client = client;
        this.index = index;

        try
        {
             DataInputStream fromClient = new DataInputStream(client.getInputStream());
             DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
        }
        catch (Exception e)
        {
            System.err.println("ERROR! Failed to initialize Client Handler streams\n " + e.getStackTrace());
        }
    }

    private String recv() throws IOException
    {
       return fromClient.readUTF();
    }

    public void send(String msg)
    {
        try
        {
            toClient.writeUTF(msg);
        }
        catch(Exception e)
        {
            System.err.println("ERROR! Failed to send the message\n" + e.getStackTrace());
        }
    }

    public void run()
    {
        try
        {
            while(true)
            {
                String msg = recv();
                msgReceived.invoke(index, msg);
            }
        }
        catch (Exception e)
        {
            System.err.println("ERROR!: Client interrupted\n" + e.getStackTrace());
        }
    }

    public int getIndex()
    {
        return index;
    }
}
