package io.smallrye.reactive.messaging.mqtt;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ConsumptionBean {

  private List<Integer> list = new ArrayList<>();

  @Incoming("data")
  @Outgoing("sink")
  public Message<Integer> process(MqttMessage input) {
    String s = new String(input.getPayload());
    return Message.of(Integer.valueOf(s) + 1);
  }

  @Incoming("sink")
  public void sink(int val) {
    list.add(val);
  }

  @Produces
  public Config myConfig() {
    String prefix = "smallrye.messaging.source.data.";
    Map<String, String> config = new HashMap<>();
    config.put(prefix + "topic", "data");
    config.put(prefix + "type", Mqtt.class.getName());
    config.put(prefix + "host", System.getProperty("mqtt-host"));
    config.put(prefix + "port", System.getProperty("mqtt-port"));
    if (System.getProperty("mqtt-user") != null) {
      config.put(prefix + "username", System.getProperty("mqtt-user"));
      config.put(prefix + "password", System.getProperty("mqtt-pwd"));
    }
    return new MyConfig(config);
  }


  public List<Integer> getResults() {
    return list;
  }
}