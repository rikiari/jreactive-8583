package com.github.kpavlov.jreactive8583;

import com.github.kpavlov.jreactive8583.netty.pipeline.CompositeIsoMessageHandler;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractIso8583ConnectorTest<M extends IsoMessage> {

    private AbstractIso8583Connector subject;

    @Mock
    private ConnectorConfiguration config;
    @Mock
    private MessageFactory<M> messageFactory;
    @Mock
    private IsoMessageListener<IsoMessage> listener;

    private CompositeIsoMessageHandler<M> compositeIsoMessageHandler;
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private IsoMessage message;

    @Before
    public void setUp() throws Exception {
        subject = new AbstractIso8583Connector(config, messageFactory) {
            @Override
            protected AbstractBootstrap createBootstrap() {
                throw new UnsupportedOperationException("Method is not implemented: .createBootstrap");
            }
        };
        //noinspection unchecked

        compositeIsoMessageHandler = (CompositeIsoMessageHandler<M>) subject.messageHandler;
    }

    @Test
    public void addMessageListener() throws Exception {
        //given
        IsoMessageListener listener = mock(IsoMessageListener.class);
        when(listener.applies(message)).thenReturn(true);

        //when
        subject.addMessageListener(listener);
        compositeIsoMessageHandler.channelRead(ctx, message);

        // then
        verify(listener).onMessage(ctx, message);
    }

    @Test
    public void removeMessageListener() throws Exception {
        //given
        subject.addMessageListener(listener);
        IsoMessageListener listener = mock(IsoMessageListener.class);

        //when
        subject.removeMessageListener(listener);
        compositeIsoMessageHandler.channelRead(ctx, message);

        // then
        verifyZeroInteractions(listener);
    }

}