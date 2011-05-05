package syncfolder.common;

import java.io.File;
import java.io.Serializable;

public class FileItem implements Serializable
{
    String path;
    long lastModified;
    long length;

    public FileItem( File parent, File file )
    {
        path = file.getPath().replaceAll("[/\\\\]", "/").replaceAll(parent.getAbsolutePath().replaceAll("[/\\\\]", "/"), "");
        lastModified = file.lastModified();
        length = file.length();
    }

    public FileItem( File parent, FileItem file )
    {
        this(parent, new File(parent, file.getPath()));
    }

    public boolean equals( Object o )
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        FileItem fileItem = (FileItem) o;

        if (path != null ? !path.equals(fileItem.path) : fileItem.path != null)
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (path != null ? path.hashCode() : 0);
        result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
        result = 31 * result + (int) (length ^ (length >>> 32));
        return result;
    }

    public String toString()
    {
        return String.format("[FileItem] %s, lastmodified: %d, length: %d", path, lastModified, length);
    }

    public String getPath()
    {
        return path.replaceAll("[/]", "\\" + File.separator);
    }

    public long getLastModified()
    {
        return lastModified;
    }

    public long getLength()
    {
        return length;
    }
}