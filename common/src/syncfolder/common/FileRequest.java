package syncfolder.common;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;



public class FileRequest implements Serializable
{
    protected Collection<FileItem> files;
    protected File serverFolder;

    public FileRequest()
    {
        files = new ArrayList<FileItem>();
    }

    public FileRequest( Response response )
    {
        this();
        serverFolder = response.serverFolder;
        files.addAll(response.files);
    }

    public void addFile( FileItem fi )
    {
        files.add(fi);
    }

    public int getFilesSize()
    {
        return files.size();
    }

    public Collection<FileItem> getFiles()
    {
        return files;
    }

    public File getServerFolder()
    {
        return serverFolder;
    }
}
