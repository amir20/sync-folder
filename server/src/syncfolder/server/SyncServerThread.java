package syncfolder.server;

import syncfolder.common.FileItem;
import syncfolder.common.FileRequest;
import syncfolder.common.FileResponse;
import syncfolder.common.Request;
import syncfolder.common.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: Amir
 * Date: Jun 24, 2008
 * Time: 1:15:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class SyncServerThread implements Runnable
{
    final private Socket socket;
    final private SyncServer parent;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public SyncServerThread( SyncServer parent, Socket socket ) throws IOException
    {
        this.socket = socket;
        this.parent = parent;
        ois = new ObjectInputStream(socket.getInputStream());
        oos = new ObjectOutputStream(socket.getOutputStream());
    }

    public void run()
    {
        try
        {
            while (socket.isConnected() && !socket.isClosed())
            {
                Object o = ois.readObject();
                if (o instanceof Request)
                {
                    handleRequest((Request) o);
                }
                else if (o instanceof FileRequest)
                {
                    handleFileRequest((FileRequest) o);
                }
            }

        }
        catch (IOException e)
        {
            try
            {
                ois.close();
                oos.close();
                socket.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void handleFileRequest( FileRequest fileRequest ) throws IOException
    {
        for (FileItem fi : fileRequest.getFiles())
        {
            FileResponse response = new FileResponse(fileRequest.getServerFolder(), fi);
            oos.writeObject(response);
            oos.flush();
            oos.reset();
        }
    }

    private void handleRequest( Request request ) throws IOException
    {
        Response response = new Response(parent.getFolderFromRequest(request));
        response.setRequest(request);
        oos.writeObject(response);
        oos.flush();
        oos.reset();
    }
}
