package com.mycompany.app.Broadcaster;

import com.mycompany.app.Messages.Message;

public interface ISpreader {
	void eagerBroadcast(final Message msg);
}
