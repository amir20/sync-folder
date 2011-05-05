package syncfolder.common;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class Request implements Serializable
{
    public enum Modes implements Serializable
    {
        UPDATE, CREATE
    }

    protected Set<FileItem> files;
    protected Modes mode;
    protected String serverFolderName;
    protected File clientFolder;
    protected Set<Pattern> includes;
    protected Set<Pattern> excludes;

    public Request()
    {
        mode = Modes.CREATE;
        files = new HashSet<FileItem>();
        includes = new HashSet<Pattern>();
        excludes = new HashSet<Pattern>();
    }

    public Request( File folder )
    {
        this();
        mode = Modes.UPDATE;
        clientFolder = folder;
        addFiles(folder);
    }

    private void addFiles( File folder )
    {
        for( File f : folder.listFiles() )
        {
            if( f.isDirectory() )
            {
                addFiles(f);
            }
            else
            {
                files.add(new FileItem(clientFolder, f));
            }
        }
    }

    public void setMode( Modes mode )
    {
        this.mode = mode;
    }

    public Set<FileItem> getFiles()
    {
        return files;
    }

    public Modes getMode()
    {
        return mode;
    }

    public Set<Pattern> getExcludes()
    {
        return excludes;
    }

    public Set<Pattern> getIncludes()
    {
        return includes;
    }

    public void addExclude(String rule)
    {
        excludes.add(Pattern.compile(rule));
    }

    public void addInclude(String rule)
    {
        includes.add(Pattern.compile(rule));
    }

    public void setServerFolderName( String serverFolderName )
    {
        this.serverFolderName = serverFolderName;
    }

    public String getServerFolderName()
    {
        return serverFolderName;
    }

    public static void main( String... args )
    {
        Request r = new Request(new File("clientFolder"));
        for( FileItem s : r.getFiles() )
        {
            System.out.println(s);
        }

        Response response = new Response(new File("serverFolder"));
        response.setRequest(r);

        for (FileItem fi : response.getFiles())
        {
            System.out.println(fi);
        }
    }
}
