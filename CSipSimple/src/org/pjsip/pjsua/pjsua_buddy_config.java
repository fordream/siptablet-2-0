/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public class pjsua_buddy_config {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public pjsua_buddy_config(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(pjsua_buddy_config obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsuaJNI.delete_pjsua_buddy_config(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setUri(pj_str_t value) {
    pjsuaJNI.pjsua_buddy_config_uri_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getUri() {
    long cPtr = pjsuaJNI.pjsua_buddy_config_uri_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setSubscribe(int value) {
    pjsuaJNI.pjsua_buddy_config_subscribe_set(swigCPtr, this, value);
  }

  public int getSubscribe() {
    return pjsuaJNI.pjsua_buddy_config_subscribe_get(swigCPtr, this);
  }

  public void setUser_data(byte[] value) {
    pjsuaJNI.pjsua_buddy_config_user_data_set(swigCPtr, this, value);
  }

  public byte[] getUser_data() {
	return pjsuaJNI.pjsua_buddy_config_user_data_get(swigCPtr, this);
}

  public pjsua_buddy_config() {
    this(pjsuaJNI.new_pjsua_buddy_config(), true);
  }

}
