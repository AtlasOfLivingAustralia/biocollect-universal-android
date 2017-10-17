package fragments.offline_species.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class FetchAndSaveSpeciesService extends Service {
    Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        handler = new Handler(Looper.getMainLooper());
        new Thread()
        {
            public void run()
            {
                processRequest();
            }
        }.start();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    private void processRequest(){
        try {
            Thread.sleep(5000);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FetchAndSaveSpeciesService.this, "work finished", Toast.LENGTH_SHORT).show();
                }
            });
            //
            stopSelf();
        } catch (InterruptedException e) {
            // Restore interrupt status.
            //Toast.makeText(this, "interrupted", Toast.LENGTH_SHORT).show();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "on create", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
