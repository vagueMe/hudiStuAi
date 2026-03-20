package com.hudi.springai.more.dt;

import java.util.Map;

/**
 * @author hudi
 * @date 20 3月 2026 16:51
 */
public class AiJob {

    public record Job(JobType jobType, Map<String, String> keyInfos) {};

    public enum JobType {
        CANCEL,
        QUERY,
        OTHER,
    }
}
