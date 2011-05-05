package syncfolder.client;

import com.growl.Growl;

/**
 * Created by IntelliJ IDEA.
 * User: Amir
 * Date: Jun 27, 2008
 * Time: 1:50:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestGrowl
{
    public static void main( String... args ) throws Exception
    {
        Growl growl = new Growl("SyncFolder", new String[]{"SyncFolder"}, new String[]{"SyncFolder"});
        growl.register();
        growl.notifyGrowlOf("SyncFolder", "File updated", "This file has been successfully updated");
    }
}
