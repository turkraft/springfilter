package com.turkraft.springfilter.mongodb;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

@Configuration
public class Config implements InitializingBean, DisposableBean {

  private static final String CONNECTION_STRING = "mongodb://%s:%d";

  private MongodExecutable mongodExecutable;
  private MongoTemplate mongoTemplate;

  @Override
  public void afterPropertiesSet() throws Exception {

    String ip = "localhost";
    int port = 27017;

    MongodConfig mongodConfig = MongodConfig.builder().version(Version.Main.PRODUCTION)
        .net(new Net(ip, port, Network.localhostIsIPv6())).build();

    MongodStarter starter = MongodStarter.getDefaultInstance();
    mongodExecutable = starter.prepare(mongodConfig);
    mongodExecutable.start();
    mongoTemplate =
        new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, port)), "test");

  }

  @Override
  public void destroy() throws Exception {
    mongodExecutable.stop();
  }

  @Bean
  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

}
