package com.shopivr.service.utils;

import com.csipsimple.api.SipCallSession;
import com.csipsimple.api.SipCallSession.InvState;

public class SipCallSessionParse {

	public SipCallSessionParse(SipCallSession callSession) {
		int callState = callSession.getCallState();
		switch (callState) {
		case InvState.INCOMING: {
			incoming(callSession);
		}
		case InvState.CONNECTING: {
			connecting(callSession);
		}
		case InvState.EARLY: {
			early(callSession);
			if (callSession.isIncoming()) {
				incoming(callSession);
			} else {
				outgoing(callSession);
			}
		}
		case InvState.CALLING: {
			calling(callSession);
		}
		case InvState.CONFIRMED: {
			confirmed(callSession);
		}
		case InvState.DISCONNECTED: {
			disconnected(callSession);
		}
		default: {
			mdefault(callSession);
		}
		}
	}

	public void outgoing(SipCallSession callSession) {

	}

	public void mdefault(SipCallSession callSession) {

	}

	public void disconnected(SipCallSession callSession) {

	}

	public void confirmed(SipCallSession callSession) {

	}

	public void calling(SipCallSession callSession) {

	}

	public void early(SipCallSession callSession) {

	}

	public void connecting(SipCallSession callSession) {

	}

	public void incoming(SipCallSession callSession) {

	}
}