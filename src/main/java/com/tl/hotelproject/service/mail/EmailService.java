package com.tl.hotelproject.service.mail;

import java.util.Map;

public interface EmailService {
    String readFile(String filename) throws Exception;
    void send(String to, String content, String subject, Map<String, Object> body, String filename);

    String initContent(Map<String, Object> body, String filename);
}
