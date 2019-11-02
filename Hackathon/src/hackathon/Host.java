package hackathon;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class Host
{
    private static DoublyLinkedList<Squirrel> squirrels;
    private static DoublyLinkedList<Socket> clients;
    private static DoublyLinkedList<Nut> nuts; //haha nuts
    
    public static void main(String[] args)
    {
        squirrels = new DoublyLinkedList<>();
        nuts = new DoublyLinkedList<>();
        clients = new DoublyLinkedList<>();

        ServerSocket server = null;
        while(null == server)
        {
            try
            {
                server = new ServerSocket(Constants.PORT);
            }
            catch(IOException e)
            {
    
            }
        }

        

        while(!Thread.interrupted())
        {
            try
            {
                Socket client = server.accept();
                client.setTcpNoDelay(true);
                clients.add(client);    
            }
            catch(IOException e)
            {

            }
        }

        try
        {
            server.close();
        }
        catch(IOException e)
        {

        }
    }

    /**
     * Moves players in set intervals of 40 times a second and sends updated locations to all clients.
     */
    private static void doRounds()
    {
        new Thread(()->
        {
            long lastUpdate = System.currentTimeMillis();
            int updateTime = 1000 / 40;
            while(true)
            {
                // Do player movements/updates
                for(Squirrel s : squirrels)// For each squirrel in list
                {
                    
                    
                    
                }

                //Send updates to players
                //get packet data and calculate cordinets?
                byte[] data = getBytes();
                for(Socket client : clients)
                {
                    update(client, data);
                }

                //Only update 40 times a second
                long timeTaken = System.currentTimeMillis() - lastUpdate;
                lastUpdate = System.currentTimeMillis(); 
                if(timeTaken < 40)
                {
                    try
                    {
                        Thread.sleep(40 - timeTaken);

                    }
                    catch(InterruptedException e)
                    {
                    }
                }
            }
        }).start();
    }

    private static byte[] getBytes()
    {
        
        int at = 0;
        byte[] data = new byte[8 + 16 * squirrels.size() + 8 * nuts.size()];
        
        ByteHelp.toBytes(16 * squirrels.size(), at, data);
        at += 4;
        ByteHelp.toBytes(8 * nuts.size(), at, data);
        at += 4;

        for(Squirrel s : squirrels)
        {
            byte[] sData = s.getBytes();
            for(int i = 0; i < 16; i++)
            {
                data[at] = sData[i];
                at++;
            }
        }

        for(Nut n : nuts)
        {
            ByteHelp.toBytes(n.getX(), at, data);
            at += 4;
            ByteHelp.toBytes(n.getY(), at, data);
            at += 4;
        }

        return data;
    }

    private static void update(Socket client, byte[] data)
    {
        new Thread(()->
        {
            try{
                OutputStream out = client.getOutputStream();
                out.write(data);
            }
            catch(IOException e)
            {

            }
            
        }).start();
    }

    private void handleClient(Socket client)
    { 
        new Thread(()->
        {
            InputStream in = null;
            while(null == in)
            {
                try
                {
                    in = client.getInputStream();
                }
                catch(IOException e)
                {
        
                }
            }

            while(!client.isClosed())
            {
                try
                {
                    if(in.available() >= 4)
                    {

                    }
                    else
                    {
                        try
                        {
                            Thread.sleep(10);
                        }
                        catch(InterruptedException e)
                        {
    
                        }
                    }
                }
                catch(IOException e)
                {

                }
  
            }

            clients.remove(client);
        }).start();
    }

    private void nutGeneration()
    {
        new Thread(()->
        {
            Random rand = new Random();

            while(nuts.size() < 100){
                for (int i = 0; i < 1000; i++) {
                    for (int j = 0; j < 1000; j++) {
                        if (rand.nextInt() % 200 < 10) {
                            Nut n = new Nut(i, j);
                            nuts.add(n);
                        }
                    }
                }
            }
        }).start();
    }
}