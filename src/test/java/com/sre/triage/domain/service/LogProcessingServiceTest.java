package com.sre.triage.domain.service;

import com.sre.triage.infrastructure.service.LogProcessingServiceImpl;
import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogProcessingServiceTest {

    private static final Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static boolean isValidUUID(String candidate) {
        return candidate != null && UUID_REGEX.matcher(candidate).matches();
    }

}
