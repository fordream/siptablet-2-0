package com.shopivr.service.utils;

import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipCallSession.InvState;

public  class TACAllParser {

	public TACAllParser() {
	}

	public void execute(SipCallSession callSession, boolean isShopmodeEnable) {
		int callState = callSession.getCallState();

		switch (callState) {
		case InvState.INCOMING: {
			inComing(callSession);
		}
			break;
		case InvState.CONNECTING: {
			connecting(callSession);
		}
			break;
		case InvState.EARLY: {
			if (callSession.isIncoming()) {
				inComing(callSession);
			} else {
				outgoing(callSession);
			}
		}
			break;
		case InvState.CALLING: {
			outgoing(callSession);
		}
			break;
		case InvState.CONFIRMED: {
			talking(callSession);
		}
			break;
		case InvState.DISCONNECTED: {
			disconnect(callSession);
		}
			break;
		}
	}

	public  void inComing(SipCallSession callSession){};

	public  void disconnect(SipCallSession callSession){};

	public  void talking(SipCallSession callSession){};

	public  void outgoing(SipCallSession callSession){};

	public  void connecting(SipCallSession callSession){};

}
