package com.hsae.d531mc.bluetooth.music.observer;

import android.app.Activity;
import android.os.Message;

public class ActivitySubjecter extends Activity implements ISubject{

	@Override
	public boolean attach(IObserver inObserver) {
		return ObserverAdapter.getInstance().register(this, inObserver);
	}

	@Override
	public boolean detach(IObserver inObserver) {
		return ObserverAdapter.getInstance().unregister(this, inObserver);
	}

	@Override
	public void notify(Message inMessage, int... flag) {
		 ObserverAdapter.getInstance().notify(this, inMessage, flag);
		
	}

}
