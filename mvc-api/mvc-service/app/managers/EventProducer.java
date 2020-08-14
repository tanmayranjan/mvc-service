package managers;

import org.sunbird.kafka.client.KafkaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class EventProducer {
    static Logger logger = LoggerFactory.getLogger(EventProducer.class);
    public static void writeToKafka(String event,String topic) {
        KafkaClient kafkaclientobj = new KafkaClient();
        try {
            logger.info(" an event to kafka topic "  + "\nEvent is" + event);
            kafkaclientobj.send(event,topic);

        }
        catch (Exception e) {
            logger.info("Failure while sending an event to kafka topic " + e + "\nEvent is" + event);
        }
    }
}
