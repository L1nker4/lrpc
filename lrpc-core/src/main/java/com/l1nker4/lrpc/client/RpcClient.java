package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.entity.RpcRequest;

/**
 * RPC请求客户端
 *
 * @author l1nker4
 */
public interface RpcClient {

    Object sendRequest(RpcRequest request) throws Exception;
}
