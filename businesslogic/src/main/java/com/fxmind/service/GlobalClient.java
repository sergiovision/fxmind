package com.fxmind.service;

import com.fxmind.global.FXMindMQL;
/**
 *
 * interface that instantiates and gives reference to remote GlobalService from any client location on network
 * * Created by sergeizhuravlev on 5/8/14.
 */
public interface GlobalClient {

    public FXMindMQL.Iface connectFXMindMQLClient();
    public void closeFXMindMQLClient();
}
