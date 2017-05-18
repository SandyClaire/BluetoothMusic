package com.hsae.d531mc.bluetooth.music.observer;

import android.os.Message;

/**
 * Observer Pattern Utils Class
 * @author wangda
 *
 */
public interface IObserver {

    abstract public void listen(Message inMessage);
}
