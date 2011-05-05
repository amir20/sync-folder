package syncfolder.client;

import java.io.File;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Amir
 * Date: Jun 24, 2008
 * Time: 5:13:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainThread
{
    public static void main( String... args ) throws UnknownHostException
    {
        String host = args[0];
        String serverFolderName = args[1];
        File homeFolder = new File(args[2]);
        SyncClientThread thread = new SyncClientThread(host, serverFolderName, homeFolder);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(thread, 0, 1, TimeUnit.SECONDS);

    }
}
