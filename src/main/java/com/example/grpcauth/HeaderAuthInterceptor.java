package com.example.grpcauth;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@GrpcGlobalServerInterceptor
public class HeaderAuthInterceptor implements ServerInterceptor {

    public static final Metadata.Key<String> REQUEST_ID_KEY = 
        Metadata.Key.of("x-request-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        String requestId = headers.get(REQUEST_ID_KEY);

        if (requestId == null || requestId.trim().isEmpty()) {
            // 如果 x-request-id 不存在或為空，則拒絕請求
            System.out.println("Request rejected. Missing x-request-id header.");
            call.close(Status.UNAUTHENTICATED.withDescription("Missing x-request-id header"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }

        System.out.println("Request validated with x-request-id: " + requestId);
        // 驗證通過，繼續處理請求
        return next.startCall(call, headers);
    }
}
