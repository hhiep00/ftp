//package com.example.demoftp;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.net.ftp.FTPFile;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.expression.Expression;
//import org.springframework.integration.annotation.InboundChannelAdapter;
//import org.springframework.integration.annotation.Poller;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.core.MessageSource;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.integration.dsl.IntegrationFlows;
//import org.springframework.integration.dsl.MessageChannels;
//import org.springframework.integration.dsl.Pollers;
//import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
//import org.springframework.integration.file.filters.CompositeFileListFilter;
//import org.springframework.integration.file.filters.RegexPatternFileListFilter;
//import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
//import org.springframework.integration.file.remote.handler.FileTransferringMessageHandler;
//import org.springframework.integration.file.remote.session.CachingSessionFactory;
//import org.springframework.integration.file.remote.session.SessionFactory;
//import org.springframework.integration.ftp.filters.FtpPersistentAcceptOnceFileListFilter;
//import org.springframework.integration.ftp.filters.FtpSimplePatternFileListFilter;
//import org.springframework.integration.ftp.gateway.FtpOutboundGateway;
//import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
//import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizingMessageSource;
//import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
//import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
//import org.springframework.integration.metadata.SimpleMetadataStore;
//import org.springframework.integration.scheduling.PollerMetadata;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.scheduling.support.PeriodicTrigger;
//
//import java.io.File;
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//@Slf4j
//public class FtpConfig {
//
//    @Bean
//    public DefaultFtpSessionFactory ftpSessionFactory() {
//        DefaultFtpSessionFactory sf = new DefaultFtpSessionFactory();
//        sf.setHost("192.168.101.200");
//        sf.setPort(21);
//        sf.setUsername("ftptmg");
//        sf.setPassword("123456");
//        return sf;
//    }
//
//    @Bean
//    public FtpInboundFileSynchronizer ftpInboundFileSynchronizer() {
//        FtpInboundFileSynchronizer fileSynchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
//        fileSynchronizer.setDeleteRemoteFiles(false);
//        fileSynchronizer.setRemoteDirectory("/ReceiveRec");
//        fileSynchronizer.setFilter(ftpFileListFilter());
//        return fileSynchronizer;
//    }
//
//    /*@Bean
//    public FtpInboundFileSynchronizer ftpInboundFileSynchronizerSend() {
//        FtpInboundFileSynchronizer fileSynchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
//        fileSynchronizer.setDeleteRemoteFiles(false);
//        fileSynchronizer.setRemoteDirectory("/Send");
//        fileSynchronizer.setFilter(ftpFileListFilter());
//        return fileSynchronizer;
//    }*/
//
//    @Bean
//    public CompositeFileListFilter<FTPFile> ftpFileListFilter() {
//        CompositeFileListFilter<FTPFile> fileListFilter = new CompositeFileListFilter<>();
//        fileListFilter.addFilter(new FtpPersistentAcceptOnceFileListFilter(new SimpleMetadataStore(), "ftpFiles"));
//        return fileListFilter;
//    }
//
//    @Bean
//    public MessageSource<File> ftpMessageSource() {
//        FtpInboundFileSynchronizingMessageSource source =
//                new FtpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizer());
////        source.setLocalDirectory(new File("/home/datdv/CSD/ReceiveRec"));
//        source.setLocalDirectory(new File("/var/vsdclient/ReceiveRec"));
//        source.setAutoCreateLocalDirectory(true);
//        source.setLocalFilter(new AcceptOnceFileListFilter<>());
//        source.setMaxFetchSize(100);
//
//        System.out.println("Current working directory: " + new File(".").getAbsolutePath());
//        return source;
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "ftpUploadChannel")
//    public MessageHandler ftpMessageHandler(SessionFactory<FTPFile> ftpSessionFactory) {
//        FtpRemoteFileTemplate template = new FtpRemoteFileTemplate(ftpSessionFactory);
//        FileTransferringMessageHandler<FTPFile> handler = new FileTransferringMessageHandler<>(template);
//        handler.setRemoteDirectoryExpressionString("headers['remote-directory']");
//        return handler;
//    }
//
//    /*@Bean
//    public MessageSource<File> ftpMessageSourceSend() {
//        FtpInboundFileSynchronizingMessageSource source =
//                new FtpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizerSend());
//        source.setLocalDirectory(new File("D:\\var\\vsd\\ftp\\Send"));
//        source.setAutoCreateLocalDirectory(true);
//        source.setLocalFilter(new AcceptOnceFileListFilter<>());
//        source.setMaxFetchSize(100);
//        return source;
//    }*/
//
//    @Bean(name = PollerMetadata.DEFAULT_POLLER)
//    public PollerMetadata poller() {
//        PollerMetadata poller = new PollerMetadata();
//        poller.setTrigger(new PeriodicTrigger(100, TimeUnit.MILLISECONDS)); // Poll every 100 MILLISECONDS
//        return poller;
//    }
//
//    @Bean
//    public IntegrationFlow ftpIntegrationFlow() {
//        return IntegrationFlows.from(ftpMessageSource(),
//                        e -> e.poller(poller()))
//                .handle(message -> {
//                    // process the file
//                    File file = (File) message.getPayload();
//                    log.info("New file received: " + file.getAbsolutePath());
//                })
//                .get();
//    }
//
//    @Bean
//    public MessageChannel ftpUploadChannel() {
//        return MessageChannels.direct().get();
//    }
//
//    @Bean
//    public IntegrationFlow ftpUploadFlow() {
//        return IntegrationFlows.from("ftpUploadChannel")
//                .handle("ftpMessageHandler", "handleMessage")
//                .get();
//    }
//
//    /*@Bean
//    public IntegrationFlow ftpIntegrationFlowSend() {
//        return IntegrationFlows.from(ftpMessageSourceSend(),
//                        e -> e.poller(Pollers.fixedRate(5000, TimeUnit.MILLISECONDS)))
//                .handle(message -> {
//                    // process the file
//                    File file = (File) message.getPayload();
//                    System.out.println("New file received: " + file.getAbsolutePath());
//                })
//                .get();
//    }*/
//}