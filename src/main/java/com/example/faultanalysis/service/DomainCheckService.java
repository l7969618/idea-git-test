package com.example.faultanalysis.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;

@Service
public class DomainCheckService {
    public DomainCheckResult checkDomain(String domainInput) {
        if (domainInput == null || domainInput.trim().isEmpty()) {
            return new DomainCheckResult("SKIPPED", "未提供域名，跳过检测。");
        }
        String normalized = domainInput.trim();
        String host = extractHost(normalized);
        try {
            InetAddress address = InetAddress.getByName(host);
            boolean port80 = canConnect(host, 80);
            boolean port443 = canConnect(host, 443);
            String message = String.format("解析地址: %s, 端口80:%s, 端口443:%s",
                    address.getHostAddress(), port80 ? "可用" : "不可用", port443 ? "可用" : "不可用");
            return new DomainCheckResult("OK", message);
        } catch (Exception ex) {
            return new DomainCheckResult("FAILED", "检测失败: " + ex.getMessage());
        }
    }

    private String extractHost(String input) {
        if (input.startsWith("http://") || input.startsWith("https://")) {
            try {
                URI uri = new URI(input);
                if (uri.getHost() != null) {
                    return uri.getHost();
                }
            } catch (URISyntaxException ignored) {
                return input;
            }
        }
        return input;
    }

    private boolean canConnect(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1500);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static class DomainCheckResult {
        private final String status;
        private final String message;

        public DomainCheckResult(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
