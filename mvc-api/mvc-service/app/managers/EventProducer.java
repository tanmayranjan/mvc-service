package managers;

import org.sunbird.kafka.client.KafkaClient;

public class EventProducer {
    public static void writeToKafka(String event,String topic) {
        KafkaClient kafkaclientobj = new KafkaClient();
        try {
            kafkaclientobj.send(event,topic);

        }
        catch (Exception e) {
            System.out.println("Exception while writing to kafka topic" + e);
        }
    }
}
