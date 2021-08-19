package cn.wecuit.robot.eventHandle


import cn.wecuit.mybatis.entity.MyBatis
import cn.wecuit.robot.data.Storage
import cn.wecuit.robot.data.mapper.DictMapper
import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.nextEvent
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.content
import java.util.*

/**
 * @Author jiyec
 * @Date  2021/5/14 9:44
 * @Version 1.0
 **/
object StudyEventKt {
    suspend fun handle(event: MessageEvent){
        val subjectId = event.subject.id
        val senderId = event.sender.id
        val timeout = 30_000L
        val msg = event.message.content.replace("  ", " ")

        val key = msg.substring(msg.indexOf(" ") + 1)
        if(key.isEmpty()){
            event.subject.sendMessage("主人，我不知道该学习什么啊╥﹏╥...")
            return;
        }

        Storage.addIgnore("$subjectId,$senderId", 0)

        try {
            event.subject.sendMessage("请阁下在" + (timeout / 1000) + "秒内发送回答")
            val newEvent: MessageEvent = nextEvent<MessageEvent>(timeout) {
                it.sender.id == senderId
            }
            // 消息序列化
            val content = newEvent.message.serializeToMiraiCode()

            val openSession = MyBatis.getSqlSessionFactory().openSession()
            val mapper = openSession.getMapper(DictMapper::class.java)
            mapper.addItem(key, content)
            openSession.commit()

            newEvent.subject.sendMessage(PlainText("你的回答是：\n") + content.deserializeMiraiCode())

        } catch (e: TimeoutCancellationException) {
            event.subject.sendMessage(At(event.sender.id) + "阁下超时了呢T﹏T")
        }finally{
            Storage.delIgnore("$subjectId,$senderId")
        }
    }


}