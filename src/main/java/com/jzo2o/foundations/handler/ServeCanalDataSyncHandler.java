package com.jzo2o.foundations.handler;

import com.jzo2o.canal.listeners.AbstractCanalRabbitMqMsgListener;
import com.jzo2o.es.core.ElasticSearchTemplate;
import com.jzo2o.foundations.constants.IndexConstants;
import com.jzo2o.foundations.model.domain.ServeSync;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.execchain.TunnelRefusedException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

//实现canal同步数据
@Component
@Slf4j
public class ServeCanalDataSyncHandler extends AbstractCanalRabbitMqMsgListener<ServeSync> {
    @Resource
    private ElasticSearchTemplate elasticSearchTemplate;

    //监听mq
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "canal-mq-jzo2o-foundations"),
            exchange = @Exchange(name = "exchange.canal-jzo2o", type = ExchangeTypes.TOPIC),
            key = "canal-mq-jzo2o-foundations"
    ),concurrency = "1")//指定消费线程个数为1
    public void onMessage(Message message) throws Exception {
        parseMsg(message);
    }
    //向es中保存数据，解析到binlog中的新增，更新消息执行此方法
    @Override
    public void batchSave(List<ServeSync> data) {
        Boolean serve_aggregation = elasticSearchTemplate.opsForDoc().batchInsert(IndexConstants.SERVE, data);
        //如果执行失败，要抛出异常，给mq回nack
        if(!serve_aggregation){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e ){
                throw new RuntimeException(e);
            }
            throw new RuntimeException("同步失败");
        }
    }
    //将es中的文档进行删除，解析到binlog中的delete信息，消息执行此方法。
    @Override
    public void batchDelete(List<Long> ids) {
        Boolean serve_aggregation = elasticSearchTemplate.opsForDoc().batchDelete(IndexConstants.SERVE, ids);
        if(!serve_aggregation){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e ){
                throw new RuntimeException(e);
            }
            throw new RuntimeException("同步失败");
        }
    }
}
