package net.yura.domination.android.push;

import java.util.logging.Level;
import java.util.logging.Logger;
import android.content.Context;
import com.google.android.gcm.GCMRegistrar;
import net.yura.domination.R;
import net.yura.lobby.client.AndroidLobbyClient;
import net.yura.lobby.client.Connection;

public class ADMServerUtilities implements AndroidLobbyClient {

    static final Logger logger = Logger.getLogger(ADMServerUtilities.class.getName());

    public static void setup() {
        try {
            Context context = net.yura.android.AndroidMeApp.getContext();

            GCMRegistrar.checkDevice(context);
            GCMRegistrar.checkManifest(context);
            final String regId = GCMRegistrar.getRegistrationId(context);
            if (regId.equals("")) {
                GCMRegistrar.register(context, context.getString(R.string.app_id));
            }
            else {
                if (GCMRegistrar.isRegisteredOnServer(context)) {
                    logger.info("Already registered");
                }
                else {
                    ADMServerUtilities.registerOnLobbyServer(context, regId);

                    // TODO if we FAIL at registering on our server then call
                    // GCMRegistrar.unregister(context);
                    // currently can not tell
                }
            }
        }
        catch (UnsupportedOperationException th) {
            logger.log(Level.INFO, "gmc unsupported", th);
        }
    }

    /**
     * @see ADMIntentService#onUnregistered(Context, String)
     */
    public static void delete() {
        Context context = net.yura.android.AndroidMeApp.getContext();
        GCMRegistrar.unregister(context);
    }





    public static void registerOnLobbyServer(Context context, String registrationId) {
        Connection con = PushActivity.getLobbyConnection();
        if (con != null) {
            con.addAndroidEventListener(new ADMServerUtilities(context));
            con.androidRegister(registrationId);
        }
    }

    public static void unregisterOnLobbyServer(Context context, String registrationId) {
        Connection con = PushActivity.getLobbyConnection();
        if (con != null) {
            con.addAndroidEventListener(new ADMServerUtilities(context));
            con.androidUnregister(registrationId);
        }
    }



    private Context context;
    public ADMServerUtilities(Context context) {
        this.context = context;
    }

    @Override
    public void registerDone() {
        GCMRegistrar.setRegisteredOnServer(context, true);
    }

    @Override
    public void unregisterDone() {
        GCMRegistrar.setRegisteredOnServer(context, false);
    }

}
