package cn.emay.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

public class RocketMqTest {

}

class Producer {
	public static void start() {
		DefaultMQProducer producer = new DefaultMQProducer("Producer");
		producer.setNamesrvAddr("100.100.10.95:9876");
		try {
			producer.start();

			Message msg = new Message("PushTopic", "push", "1", "Just for test.".getBytes());

			SendResult result = producer.send(msg);
			System.out.println("id:" + result.getMsgId() + " result:" + result.getSendStatus());

			msg = new Message("PushTopic", "push", "2", "Just for test.".getBytes());

			result = producer.send(msg);
			System.out.println("id:" + result.getMsgId() + " result:" + result.getSendStatus());

			msg = new Message("PullTopic", "pull", "1", "Just for test.".getBytes());

			result = producer.send(msg);
			System.out.println("id:" + result.getMsgId() + " result:" + result.getSendStatus());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			producer.shutdown();
		}
	}
}

class Consumer {
	public static void start() {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("PushConsumer");
		consumer.setNamesrvAddr("127.0.0.1:9876");
		try {
			// 订阅PushTopic下Tag为push的消息
			consumer.subscribe("PushTopic", "push");
			// 程序第一次启动从消息队列头取数据
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
			consumer.registerMessageListener(new MessageListenerConcurrently() {
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext Context) {
					Message msg = list.get(0);
					System.out.println(msg.toString());
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			consumer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
