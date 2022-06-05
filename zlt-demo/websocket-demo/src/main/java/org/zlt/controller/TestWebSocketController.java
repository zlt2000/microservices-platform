package org.zlt.controller;

import com.central.oauth2.common.config.WcAuthConfigurator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author zlt
 * @date 2022/5/8
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/test", configurator = WcAuthConfigurator.class)
public class TestWebSocketController {
    @OnOpen
    public void onOpen(Session session) throws IOException {
        session.getBasicRemote().sendText("TestWebSocketController-ok");
    }
}
