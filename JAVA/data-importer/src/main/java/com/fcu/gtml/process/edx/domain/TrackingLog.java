package com.fcu.gtml.process.edx.domain;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fcu.gtml.utils.JsonParser;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackingLog extends TheData {
    //private DateTimeFormatter dtf = DateTimeFormat.forPattern("YYYY-MM-DD'T'hh:mm:ss.sTZD");
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @JsonProperty("username")
    private String userName;
    @JsonProperty("event_type")
    private String eventType;
    private String ip;
    private String agent;
    private String host;
    private String session;
    @JsonIgnore
    private Object event;
    @JsonProperty("event_source")
    private String eventSource;
    //private Context context;
    @JsonIgnore
    private Object context;
    private String time;
    private String page;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getEvent() {
        return event.toString();
    }

    @JsonSetter("event")
    public void setEvent(Object event) {
        this.event = event;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

//    public Context getContext() {
//        return context;
//    }
//
//    public void setContext(Context context) {
//        this.context = context;
//    }

    public String getContext() {
        return context.toString();
    }

    @JsonSetter("context")
    public void setContext(Object context) {
        this.context = context;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Date getTime() {
        //2014-09-12T06:59:20.049365+00:00
//        System.out.println("time-----------"+time);
        return new DateTime(time, DateTimeZone.UTC).toDate();
                //.toString("yyyy-MM-dd HH:mm:ss.SSS");
        //System.out.println("time::" + DateTime.parse(time, dtf).toDate());
        //return DateTime.parse(time, dtf).toString();
        //return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    @JsonProperty("time")
//    public String getNowTime() {
//        return new DateTime(time).toString("yyyy-MM-dd HH:mm:ss.SSS");
//    }
//    public Event getEventObj() {
//        Event jNodes = null;
////        System.out.println("this.event:"+StringUtils.isNotEmpty(this.event));
//        try {
//            if (StringUtils.isNotEmpty(this.event)) {
////                System.out.println("this.event1111:"+event);
//                jNodes = JsonParser.parse(this.event, Event.class);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("parser error", e);
//        }
//        return jNodes;
//    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Event extends TheData {
        private String id;
        private String code;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Context extends TheData {
        @JsonProperty("username")
        private String userName;
        @JsonProperty("user_id")
        private int userId;
        private String ip;
        @JsonProperty("org_id")
        private String orgId;
        private String agent;
        private String host;
        private String session;
        @JsonProperty("course_id")
        private String courseId;
        private String path;
        public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }
        public int getUserId() {
            return userId;
        }
        public void setUserId(int userId) {
            this.userId = userId;
        }
        public String getIp() {
            return ip;
        }
        public void setIp(String ip) {
            this.ip = ip;
        }
        public String getOrgId() {
            return orgId;
        }
        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }
        public String getAgent() {
            return agent;
        }
        public void setAgent(String agent) {
            this.agent = agent;
        }
        public String getHost() {
            return host;
        }
        public void setHost(String host) {
            this.host = host;
        }
        public String getSession() {
            return session;
        }
        public void setSession(String session) {
            this.session = session;
        }
        public String getCourseId() {
            return courseId;
        }
        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
    }
    
}
