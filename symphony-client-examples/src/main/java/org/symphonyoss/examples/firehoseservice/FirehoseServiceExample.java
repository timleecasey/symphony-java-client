/*
 *
 *  *
 *  * Copyright 2016 The Symphony Software Foundation
 *  *
 *  * Licensed to The Symphony Software Foundation (SSF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.symphonyoss.examples.firehoseservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.FirehoseService;
import org.symphonyoss.client.services.MessageListener;
import org.symphonyoss.exceptions.AuthorizationException;
import org.symphonyoss.exceptions.InitException;
import org.symphonyoss.exceptions.SymException;
import org.symphonyoss.symphony.clients.AuthorizationClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.MemberInfo;
import org.symphonyoss.symphony.pod.model.MembershipList;


/**
 * EXPERIMENTAL Example of firehose service.
 * This might be pulled away in future releases..
 * <p>
 * This service will have the ability to listen to all POD messages.
 * <p>
 * <p>
 * <p>
 * REQUIRED VM Arguments or System Properties:
 * <p>
 * -Dsessionauth.url=https://pod_fqdn:port/sessionauth
 * -Dkeyauth.url=https://pod_fqdn:port/keyauth
 * -Dsymphony.agent.pod.url=https://agent_fqdn:port/pod
 * -Dsymphony.agent.agent.url=https://agent_fqdn:port/agent
 * -Dcerts.dir=/dev/certs/
 * -Dkeystore.password=(Pass)
 * -Dtruststore.file=/dev/certs/server.truststore
 * -Dtruststore.password=(Pass)
 * -Dbot.user=bot.user1
 * -Dbot.domain=@domain.com
 * -Duser.call.home=frank.tarsillo@markit.com
 * <p>
 * <p>
 * <p>
 * <p>
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class FirehoseServiceExample implements MessageListener {


    private final Logger logger = LoggerFactory.getLogger(FirehoseServiceExample.class);

    private SymphonyClient symClient;

    public FirehoseServiceExample() {


        init();


    }

    public static void main(String[] args) {

        new FirehoseServiceExample();

    }

    public void init() {


        try {

            //Create a basic client instance.
            symClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC);

            logger.debug("{} {}", System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            //Init the Symphony authorization client, which requires both the key and session URL's.  In most cases,
            //the same fqdn but different URLs.
            AuthorizationClient authClient = new AuthorizationClient(
                    System.getProperty("sessionauth.url"),
                    System.getProperty("keyauth.url"));


            //Set the local keystores that hold the server CA and client certificates
            authClient.setKeystores(
                    System.getProperty("truststore.file"),
                    System.getProperty("truststore.password"),
                    System.getProperty("certs.dir") + System.getProperty("bot.user") + ".p12",
                    System.getProperty("keystore.password"));

            //Create a SymAuth which holds both key and session tokens.  This will call the external service.
            SymAuth symAuth = authClient.authenticate();


            //With a valid SymAuth we can now init our client.
            symClient.init(
                    symAuth,
                    System.getProperty("bot.user") + System.getProperty("bot.domain"),
                    System.getProperty("symphony.agent.agent.url"),
                    System.getProperty("symphony.agent.pod.url")
            );

            FirehoseService firehoseService = new FirehoseService(symClient);

            firehoseService.addMessageListener(this);






        } catch (AuthorizationException ae) {

            logger.error(ae.getMessage(), ae);

        } catch (InitException e) {
            logger.error("error", e);
        }

    }



    //Chat sessions callback method.
//    @Override
//    public void onChatMessage(SymMessage message) {
//        if (message == null)
//            return;
//
//        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
//                message.getTimestamp(),
//                message.getFromUserId(),
//                message.getMessage(),
//                message.getMessageType());
//
//        Chat chat = symClient.getChatService().getChatByStream(message.getStreamId());
//
//        if(chat!=null)
//            logger.debug("New message is related to chat with users: {}", remoteUsersString(chat.getRemoteUsers()));
//
//
//
//
//    }
//
//    @Override
//    public void onNewChat(Chat chat) {
//
//        chat.addListener(this);
//
//        logger.debug("New chat session detected on stream {} with {}", chat.getStream().getId(), remoteUsersString(chat.getRemoteUsers()));
//
//
//    }
//
//    @Override
//    public void onRemovedChat(Chat chat) {
//
//    }
//
//    private  String remoteUsersString(Set<SymUser> symUsers){
//
//        String output = "";
//        for(SymUser symUser: symUsers){
//            output += "[" + symUser.getId() + ":" + symUser.getDisplayName() + "] ";
//
//        }
//
//        return output;
//    }

    @Override
    public void onMessage(SymMessage message) {

        if (message == null)
            return;
        String toUsers ="";

        try {
            MembershipList membershipList = symClient.getRoomMembershipClient().getRoomMembership(message.getStreamId());

            for(MemberInfo memberInfo: membershipList){
                if(!memberInfo.getId().equals(message.getFromUserId()))
                    toUsers+= "[" + memberInfo.getId() + "] ";
            }

        } catch (SymException e) {
            e.printStackTrace();
        }

        logger.debug("TS: {}\nFrom ID: {}\nTo: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                toUsers,
                message.getMessage(),
                message.getMessageType());

    }
}
