package syncfolder.client;

import syncfolder.common.FileRequest;
import syncfolder.common.Request;
import syncfolder.common.Response;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SyncClientThread implements Runnable
{
    private InetAddress host;
    private String serverFolder;
    private File folder;

    public SyncClientThread( String host, String serverFolder, File folder ) throws UnknownHostException
    {
        this.host = InetAddress.getByName(host);
        this.serverFolder = serverFolder;
        this.folder = folder;
    }

    public void run()
    {
        try
        {
            SyncClient client = new SyncClient(host);
            client.setClientFolder(folder);
            client.setServerFolder(serverFolder);
            //client.addInclude("\\.mp3$");
            client.enableGrowl();
            Response response = client.getResponse(Request.Modes.UPDATE);
            FileRequest fileRequest = new FileRequest(response);
            client.getFileResponsesAndSaveToDisk(fileRequest);
            System.out.println(String.format("Fetched %d updated files.", fileRequest.getFilesSize()));
            client.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
