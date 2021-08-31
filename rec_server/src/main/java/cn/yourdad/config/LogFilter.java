package cn.yourdad.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.config
 * @description: 日志过滤
 * @author: wzj
 * @create: 2020-10-17 14:52
 **/

public class LogFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLoggerName().contains("requestLog")){
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
