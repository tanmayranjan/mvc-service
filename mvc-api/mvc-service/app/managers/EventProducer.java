package managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.kafka.client.KafkaClient;

public class EventProducer {
   static Logger logger = LoggerFactory.getLogger(EventProducer.class);
    public static void writeToKafka(String event,String topic) {
        KafkaClient kafkaclientobj = new KafkaClient();
        try {
           logger.info(" sending an event to kafka topic where Event is " + event);
            kafkaclientobj.send(event,topic);
            logger.info(" sent an event to kafka topic ");
        }
        catch (Exception e) {
            logger.info("Failure while sending an event to kafka topic " + e + "\nEvent is" + event);
        }
    }
}
