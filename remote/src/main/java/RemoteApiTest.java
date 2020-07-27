import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import java.io.IOException;

// [START example]
public class RemoteApiTest {

    public static void main(String[] args) throws IOException {
        String serverString = args[0];
        RemoteApiOptions options;
        if (serverString.equals("localhost")) {
            options = new RemoteApiOptions().server(serverString,
                    8080).useDevelopmentServerCredential();
        } else {
            options = new RemoteApiOptions().server(serverString,
                    443).useApplicationDefaultCredential();
        }
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        try {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            System.out.println("Key of new entity is " + ds.put(new Entity("Hello Remote API!")));
        } finally {
            installer.uninstall();
        }
    }
}
