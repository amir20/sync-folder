package syncfolder.common;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Response implements Serializable
{
    protected List<FileItem> files;
    protected File serverFolder;

    private Response()
    {
        files = new ArrayList<FileItem>();
    }


    public Response( File folder )
    {
        this();
        serverFolder = folder;
        addFiles(folder);
    }

    public void addFiles( File folder )
    {
        for (File f : folder.listFiles())
        {
            if (f.isDirectory())
            {
                addFiles(f);
            }
            else
            {
                files.add(new FileItem(serverFolder, f));
            }
        }
    }

    public void setRequest( Request request )
    {
        if (request.getMode() == Request.Modes.UPDATE)
        {
            for (FileItem clientFile : request.files)
            {
                if (files.contains(clientFile))
                {
                    FileItem serverFile = files.get(files.indexOf(clientFile));
                    if (serverFile.lastModified <= clientFile.lastModified && serverFile.length == clientFile.length)
                    {
                        files.remove(serverFile);
                    }
                }
            }
        }


        if (request.getIncludes().size() > 0)
        {
            Set<FileItem> toKeep = new HashSet<FileItem>();
            for (Pattern p : request.getIncludes())
            {
                for (FileItem file : files)
                {
                    Matcher m = p.matcher(file.path);
                    if (m.find())
                    {
                        toKeep.add(file);
                    }
                }
            }
            files.retainAll(toKeep);
        }
        if (request.getExcludes().size() > 0)
        {
            Set<FileItem> toRemove = new HashSet<FileItem>();
            for (Pattern p : request.getExcludes())
            {
                for (FileItem file : files)
                {
                    Matcher m = p.matcher(file.path);
                    if (m.find())
                    {
                        toRemove.add(file);
                    }
                }
            }
            files.removeAll(toRemove);
        }
    }

    public File getServerFolder()
    {
        return serverFolder;
    }

    public void setServerFolder( File serverFolder )
    {
        this.serverFolder = serverFolder;
    }

    public List<FileItem> getFiles()
    {
        return files;
    }
}
