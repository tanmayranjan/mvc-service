package managers;

import org.sunbird.kafka.client.KafkaClient;

public class EventProducer {
    KafkaClient kafkaclientobj = new KafkaClient();
    public  void writeToKafka(String event,String topic) {
        try {
            kafkaclientobj.send(event,topic);

        }
        catch (Exception e) {
            System.out.println("Exception while writing to kafka topic" + e);
        }
    }
}
