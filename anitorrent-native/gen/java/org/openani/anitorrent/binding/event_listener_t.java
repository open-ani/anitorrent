/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the Apache-2.0 license, which can be found at the following link.
 *
 * https://github.com/open-ani/mediamp/blob/main/LICENSE
 */

//@formatter:off
/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.openani.anitorrent.binding;

public class event_listener_t {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected event_listener_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(event_listener_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(event_listener_t obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings({"deprecation", "removal"})
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        anitorrentJNI.delete_event_listener_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    anitorrentJNI.event_listener_t_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    anitorrentJNI.event_listener_t_change_ownership(this, swigCPtr, true);
  }

  public void on_checked(long handle_id) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_checked(swigCPtr, this, handle_id); else anitorrentJNI.event_listener_t_on_checkedSwigExplicitevent_listener_t(swigCPtr, this, handle_id);
  }

  public void on_metadata_received(long handle_id) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_metadata_received(swigCPtr, this, handle_id); else anitorrentJNI.event_listener_t_on_metadata_receivedSwigExplicitevent_listener_t(swigCPtr, this, handle_id);
  }

  public void on_torrent_added(long handle_id) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_torrent_added(swigCPtr, this, handle_id); else anitorrentJNI.event_listener_t_on_torrent_addedSwigExplicitevent_listener_t(swigCPtr, this, handle_id);
  }

  public void on_save_resume_data(long handle_id, torrent_resume_data_t data) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_save_resume_data(swigCPtr, this, handle_id, torrent_resume_data_t.getCPtr(data), data); else anitorrentJNI.event_listener_t_on_save_resume_dataSwigExplicitevent_listener_t(swigCPtr, this, handle_id, torrent_resume_data_t.getCPtr(data), data);
  }

  public void on_torrent_state_changed(long handle_id, torrent_state_t state) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_torrent_state_changed(swigCPtr, this, handle_id, state.swigValue()); else anitorrentJNI.event_listener_t_on_torrent_state_changedSwigExplicitevent_listener_t(swigCPtr, this, handle_id, state.swigValue());
  }

  public void on_block_downloading(long handle_id, int piece_index, int block_index) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_block_downloading(swigCPtr, this, handle_id, piece_index, block_index); else anitorrentJNI.event_listener_t_on_block_downloadingSwigExplicitevent_listener_t(swigCPtr, this, handle_id, piece_index, block_index);
  }

  public void on_piece_finished(long handle_id, int piece_index) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_piece_finished(swigCPtr, this, handle_id, piece_index); else anitorrentJNI.event_listener_t_on_piece_finishedSwigExplicitevent_listener_t(swigCPtr, this, handle_id, piece_index);
  }

  public void on_status_update(long handle_id, torrent_stats_t stats) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_status_update(swigCPtr, this, handle_id, torrent_stats_t.getCPtr(stats), stats); else anitorrentJNI.event_listener_t_on_status_updateSwigExplicitevent_listener_t(swigCPtr, this, handle_id, torrent_stats_t.getCPtr(stats), stats);
  }

  public void on_file_completed(long handle_id, int file_index) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_file_completed(swigCPtr, this, handle_id, file_index); else anitorrentJNI.event_listener_t_on_file_completedSwigExplicitevent_listener_t(swigCPtr, this, handle_id, file_index);
  }

  public void on_torrent_removed(long handle_id, String torrent_name) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_torrent_removed(swigCPtr, this, handle_id, torrent_name); else anitorrentJNI.event_listener_t_on_torrent_removedSwigExplicitevent_listener_t(swigCPtr, this, handle_id, torrent_name);
  }

  public void on_session_stats(long handle_id, session_stats_t stats) {
    if (getClass() == event_listener_t.class) anitorrentJNI.event_listener_t_on_session_stats(swigCPtr, this, handle_id, session_stats_t.getCPtr(stats), stats); else anitorrentJNI.event_listener_t_on_session_statsSwigExplicitevent_listener_t(swigCPtr, this, handle_id, session_stats_t.getCPtr(stats), stats);
  }

  public event_listener_t() {
    this(anitorrentJNI.new_event_listener_t(), true);
    anitorrentJNI.event_listener_t_director_connect(this, swigCPtr, true, true);
  }

}

//@formatter:on