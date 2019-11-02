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

        ServerSocket server = new ServerSocket(Constants.PORT);


    }

    /**
     * Moves players in set intervals of 40 times a second and sends updated locations to all clients.
     */
    Paint.update();
    
    private static doRounds()
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

                //Only update 40 times a second
                long timeTaken = System.currentTimeMillis() - lastUpdate;
                lastUpdate = System.currentTimeMillis(); 
                if(timeTaken < 40)
                {
                    Thread.sleep(40 - timeTaken);
                }
            }
        }).start();
    }

    private static update(Socket client)
    {
        new Thread(()->
        {

        }).start();
    }
}