package syncfolder.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;



public class FileResponse implements Serializable
{
    protected FileItem fileItem;
    protected byte[] bytes;

    public FileResponse( File folder, FileItem file )
    {
        fileItem = new FileItem(folder, file);
        readBytes(new File(folder, fileItem.getPath()));
    }

    private void readBytes( File f )
    {
        try
        {
            FileInputStream fis = new FileInputStream(f);
            FileChannel channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(fis.available());
            channel.read(buffer);
            bytes = new byte[buffer.capacity()];
            buffer.rewind();
            buffer.get(bytes);
            channel.close();
            fis.close();
        }
        catch( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

    public ByteBuffer asByteBuffer()
    {
        return ByteBuffer.wrap(bytes);
    }

    public FileItem getFileItem()
    {
        return fileItem;
    }

    public static void main( String... args ) throws IOException, ClassNotFoundException
    {
        /*FileResponse response = new FileResponse(new File("test.pdf"));
        FileOutputStream fos = new FileOutputStream(new File("test.pdf.ser"));
        ObjectOutputStream outputStream = new ObjectOutputStream(fos);
        outputStream.writeObject(response);
        outputStream.close();*/

        FileInputStream fis = new FileInputStream(new File("test.pdf.ser"));
        ObjectInputStream ois = new ObjectInputStream(fis);
        FileResponse response = (FileResponse) ois.readObject();
        FileOutputStream fos = new FileOutputStream("test2.pdf");
        FileChannel channel = fos.getChannel();
        channel.write(response.asByteBuffer());
        channel.close();
        fos.close();
    }

}
