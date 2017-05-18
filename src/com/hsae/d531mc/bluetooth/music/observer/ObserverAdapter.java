package com.hsae.d531mc.bluetooth.music.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Observer Pattern Utils Class
 * @author wangda
 *
 */
public class ObserverAdapter {
    private HashMap<ISubject, ArrayList<IObserver>> mSubjectObserverMap;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mHandler.removeMessages(0);
    }

    private class ObserverHandler extends Handler {
        public ObserverHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            Object[] objects = (Object[]) msg.obj;
            IObserver observer = (IObserver) objects[0];
            Message message = (Message) objects[1];
            observer.listen(message);
        }
    }

    private static class SafeInstanceHolder {
        public static ObserverAdapter sInstance = new ObserverAdapter();
    }

    public static synchronized ObserverAdapter getInstance() {
        return SafeInstanceHolder.sInstance;
    }

    private ObserverHandler mHandler = null;

    private ObserverAdapter() {
        mSubjectObserverMap = new HashMap<ISubject, ArrayList<IObserver>>();
        mHandler = new ObserverHandler(Looper.getMainLooper());
    }

    public synchronized boolean register(ISubject inSubject, IObserver inObserver) {
        boolean ret = false;
        if(mSubjectObserverMap.containsKey(inSubject)) {
            ArrayList<IObserver> list = mSubjectObserverMap.get(inSubject);
            if(!list.contains(inObserver)) {
                list.add(inObserver);
                ret = true;
            }
        } else {
            ArrayList<IObserver> list = new ArrayList<IObserver>();
            list.add(inObserver);
            mSubjectObserverMap.put(inSubject, list);
            ret = true;
        }
        return ret;
    }

    public synchronized boolean unregister(ISubject inSubject, IObserver inObserver) {
        if(mSubjectObserverMap.containsKey(inSubject)) {
            ArrayList<IObserver> list = mSubjectObserverMap.get(inSubject);
            list.remove(inObserver);
            if(list.size() <= 0) {
                mSubjectObserverMap.remove(inSubject);
            }
        }
        return true;
    }

    public synchronized void clear() {
        // should avoid avoid ()null pointer issue.
        for(Iterator<ISubject> it = mSubjectObserverMap.keySet().iterator(); it.hasNext();) {
            ISubject subject = (ISubject) it.next();
            ArrayList<IObserver> list = mSubjectObserverMap.get(subject);
            if(list == null)
                continue;
            list.clear();
        }
        mSubjectObserverMap.clear();
    }

    public synchronized void clear(Object inSubject) {
        if(mSubjectObserverMap.containsKey(inSubject)) {
            ArrayList<IObserver> list = mSubjectObserverMap.get(inSubject);
            list.clear();
            mSubjectObserverMap.remove(inSubject);
        }
    }

    public void notify(Message inMessage, int... flag) {
        // should avoid null pointer issue.
        for(Iterator<ISubject> it = mSubjectObserverMap.keySet().iterator(); it.hasNext();) {
            ISubject subject = (ISubject) it.next();
            notify(subject, inMessage, flag);
        }
    }

    public void notify(ISubject inSubject, Message inMessage, int... flag) {
        ArrayList<IObserver> list = mSubjectObserverMap.get(inSubject);
        if(list == null)
            return;
        for(Iterator<IObserver> it = list.iterator(); it.hasNext();) {
            IObserver observer = (IObserver) it.next();

            if(observer != null) {
                boolean asyncMode = false;
                if(flag.length > 0 && flag[0] == ISubject.FLAG_RUN_MAIN_THREAD) {
                    asyncMode = true;
                }
                if(asyncMode) {
                    Object[] objects = new Object[] { observer, inMessage };
                    mHandler.sendMessage(mHandler.obtainMessage(0, objects));
                } else {
                    observer.listen(inMessage);
                }
            }
        }
    }

    public void notify(IObserver observer, Message inMessage, int... flag) {
        boolean asyncMode = false;
        if(flag.length > 0 && flag[0] == ISubject.FLAG_RUN_MAIN_THREAD) {
            asyncMode = true;
        }
        if(asyncMode) {
            Object[] objects = new Object[] { observer, inMessage };
            mHandler.sendMessage(mHandler.obtainMessage(0, objects));
        } else {
            observer.listen(inMessage);
        }
    }
}
