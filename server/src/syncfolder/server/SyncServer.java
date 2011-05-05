package syncfolder.server;

import syncfolder.common.Request;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SyncServer implements Runnable
{
    private ServerSocket serverSocket;
    private Map<String, File> folders;
    private boolean shutdown = false;

    public SyncServer()
    {
        folders = new HashMap<String, File>();
        try
        {
            serverSocket = new ServerSocket(30123);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public SyncServer( String name, File folder )
    {
        this();
        addFolder(name, folder);
    }

    public void run()
    {
        System.out.println("Running server...");
        try
        {
            while (!shutdown)
            {
                Socket socket = serverSocket.accept();
                System.out.println("Accepting connection from " + socket.getInetAddress());
                new Thread(new SyncServerThread(this, socket)).start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public File getFolderFromRequest( Request request )
    {
        return folders.get(request.getServerFolderName());
    }

    public void addFolder( String name, File folder )
    {
        folders.put(name, folder);
    }

    public void shutdown() throws IOException
    {
        shutdown = true;
        serverSocket.close();
    }

    public static void main( String... args )
    {
        new Thread(new SyncServer(args[0], new File(args[0]))).start();
    }
}
