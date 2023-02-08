package com.impl;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ComDomainTest {

    // http://www.nirsoft.net/whois_servers_list.html

    private final static String WHO ="cnn.com";
    private final static String WHOIS_HOST = "whois.verisign-grs.com";
    private final static int WHOIS_PORT = 43;

    @Test
    void justAnExample() {

        try (Socket socket = new Socket(WHOIS_HOST, WHOIS_PORT)) {
            OutputStream out = socket.getOutputStream();
            out.write((WHO + "\r\n").getBytes());

            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    if (line.contains("Registry Expiry Date")) {
                        line = line.substring(line.indexOf(':') + 1).trim();
                        System.out.println("---> " + line);
                        break; // don't need to read any more input
                    }
                }

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                if(line != null){
                    final LocalDateTime dt = LocalDateTime.parse(line, formatter);
                    System.out.println("---> " + dt);
                    System.out.println("---> Not available " + WHO);

                } else {
                    System.out.println("---> Available " + WHO);
                }
            }

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
