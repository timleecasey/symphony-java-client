/*
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.client.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.impl.PresenceException;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.Presence;
import org.symphonyoss.symphony.pod.model.PresenceList;
import org.symphonyoss.symphony.pod.model.UserPresence;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class PresenceService implements PresenceListener {

    private final SymphonyClient symClient;
    private PresenceList presenceList;
    private PresenceWorker presenceWorker;
    private final Set<PresenceListener> presenceListeners = ConcurrentHashMap.newKeySet();
    private final Logger logger = LoggerFactory.getLogger(PresenceService.class);


    public PresenceService(SymphonyClient symClient) {

        this.symClient = symClient;

        try {
            presenceList = getAllUserPresence();
        } catch (PresenceException e) {
            logger.error("Unable to obtain presence list...please check connections...", e);
        }

    }


    public PresenceList getAllUserPresence() throws PresenceException {


        return symClient.getPresenceClient().getAllUserPresence();

    }

    public Presence getUserPresence(Long userId) throws PresenceException {

        return symClient.getPresenceClient().getUserPresence(userId);

    }

    public Presence getUserPresence(String email) throws PresenceException {

        if (email == null)
            throw new NullPointerException("Email was not provided..");

        SymUser user;
        try {
            user = symClient.getUsersClient().getUserFromEmail(email);
        } catch (UsersClientException e) {
            logger.error("Failed to obtain userID from email", e);
            throw new PresenceException("Failed to obtain user from email: " + email, e);
        }

        return (user != null) ?
                symClient.getPresenceClient().getUserPresence(user.getId()) : null;


    }

    /**
     * Please use {@link #addPresenceListener(PresenceListener)}
     *
     * @param presenceListener
     */
    public void registerPresenceListener(PresenceListener presenceListener) {

        addPresenceListener(presenceListener);

    }

    public void addPresenceListener(PresenceListener presenceListener) {

        if (presenceWorker == null) {
            logger.debug("Starting presence worker thread..");
            presenceWorker = new PresenceWorker(symClient, this, presenceList);
            new Thread(presenceWorker).start();
        }

        presenceListeners.add(presenceListener);

    }

    public void removePresenceListener(PresenceListener presenceListener) {

        presenceListeners.remove(presenceListener);

        if (presenceListeners.isEmpty()) {
            presenceWorker.shutdown();
            presenceWorker = null;

            logger.debug("Killing presence worker thread..");
        }


    }

    @Override
    public void onUserPresence(UserPresence userPresence) {

        for (PresenceListener listener : presenceListeners) {
            listener.onUserPresence(userPresence);

        }


    }


    public void shutdown() {

        if (presenceWorker != null) {
            presenceWorker.shutdown();
            presenceWorker = null;
        }
    }

}
