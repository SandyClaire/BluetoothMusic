package com.hsae.d531mc.bluetooth.music.observer;

import android.os.Message;

/**
 * Observer Pattern Utils Class
 * @author wangda
 *
 */
public interface ISubject {
    public final static int FLAG_RUN_SYNC = 0;
    public final static int FLAG_RUN_MAIN_THREAD = 1;

    public boolean attach(IObserver inObserver);

    public boolean detach(IObserver inObserver);

    public void notify(Message inMessage, int... flag);
}
