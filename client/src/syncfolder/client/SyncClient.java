package syncfolder.client;

import com.growl.Growl;
import syncfolder.common.FileItem;
import syncfolder.common.FileRequest;
import syncfolder.common.FileResponse;
import syncfolder.common.Request;
import syncfolder.common.Response;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SyncClient implements Closeable
{
    private static final int port = 30123;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String serverFolder;
    private File clientFolder;
    private Set<String> includes;
    private Set<String> excludes;
    private boolean enableGrowl;
    private Growl growl;

    public SyncClient( InetAddress ip ) throws IOException
    {
        socket = new Socket(ip, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        includes = new HashSet<String>();
        excludes = new HashSet<String>();
        growl = new Growl("SyncFolder", new String[]{"SyncFolder"}, new String[]{"SyncFolder"});
    }


    public void setServerFolder( String folder )
    {
        serverFolder = folder;
    }

    public void setClientFolder( File folder )
    {
        clientFolder = folder;
    }

    public void enableGrowl()
    {
        enableGrowl = true;
    }

    public void disableGrowl()
    {
        enableGrowl = false;
    }


    public Response getResponse( Request.Modes mode ) throws IOException, ClassNotFoundException
    {

        Request request = new Request(clientFolder);
        request.setMode(mode);
        request.setServerFolderName(serverFolder);
        for (String r : excludes)
        {
            request.addExclude(r);
        }
        for (String r : includes)
        {
            request.addInclude(r);
        }
        oos.writeObject(request);
        oos.flush();
        return (Response) ois.readObject();
    }

    public Response getResponse() throws IOException, ClassNotFoundException
    {

        return getResponse(Request.Modes.UPDATE);
    }

    public Collection<FileResponse> getFileResponses( FileRequest fileRequest ) throws IOException, ClassNotFoundException
    {
        Collection<FileResponse> fileResponses = new ArrayList<FileResponse>();
        oos.writeObject(fileRequest);
        oos.flush();

        for (int i = 0; i < fileRequest.getFilesSize(); i++)
        {
            FileResponse response = (FileResponse) ois.readObject();
            fileResponses.add(response);
        }
        return fileResponses;
    }

    public void getFileResponsesAndSaveToDisk( FileRequest fileRequest ) throws IOException, ClassNotFoundException
    {
        oos.writeObject(fileRequest);
        oos.flush();
        for (int i = 0; i < fileRequest.getFilesSize(); i++)
        {
            FileResponse response = (FileResponse) ois.readObject();
            saveFileResponse(response);
        }
        if (fileRequest.getFilesSize() > 0)
        {
            growl("New Files Updated", "Updated " + fileRequest.getFilesSize() + " successfully.");
        }
    }

    public void saveFiles( Collection<FileResponse> files ) throws IOException
    {
        for (FileResponse file : files)
        {
            saveFileResponse(file);
        }
        if (files.size() > 0)
        {
            growl("New Files Updated", "Updated " + files.size() + " successfully.");
        }
    }

    public void saveFileResponse( FileResponse file ) throws IOException
    {
        File target = new File(clientFolder, file.getFileItem().getPath());
        target.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(target);
        FileChannel channel = fos.getChannel();
        channel.write(file.asByteBuffer());
        channel.close();
        fos.close();
        growl("New File Updated", target.getName() + " was updated successfully.");
    }

    private void growl( String title, String message )
    {
        if (enableGrowl)
        {
            try
            {
                growl.notifyGrowlOf("SyncFolder", title, message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException
    {
        oos.close();
        ois.close();
        socket.close();
    }

    public void addExclude( String rule )
    {
        excludes.add(rule);
    }

    public void addInclude( String rule )
    {
        includes.add(rule);
    }


    public static void main( String... args ) throws IOException, ClassNotFoundException
    {
        SyncClient client = new SyncClient(InetAddress.getAllByName("localhost")[0]);
        client.setClientFolder(new File("clientFolder"));
        client.setServerFolder("serverFolder");
        Response response = client.getResponse(Request.Modes.CREATE);
        for (FileItem fi : response.getFiles())
        {
            System.out.println(fi);
        }

        FileRequest fileRequest = new FileRequest(response);
        Collection<FileResponse> fileResponses = client.getFileResponses(fileRequest);
        for (FileResponse fr : fileResponses)
        {
            System.out.println(fr.getFileItem());
        }
        client.saveFiles(fileResponses);
        client.close();
    }
}
