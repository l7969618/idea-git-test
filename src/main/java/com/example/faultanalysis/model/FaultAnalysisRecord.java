package com.example.faultanalysis.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "fault_analysis_record")
public class FaultAnalysisRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String terminalIp;

    private String location;

    private String deviceModel;

    private String deviceVersion;

    private String iccid;

    private String domain;

    private String domainCheckStatus;

    @Lob
    private String domainCheckMessage;

    private String qrcodeFilename;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTerminalIp() {
        return terminalIp;
    }

    public void setTerminalIp(String terminalIp) {
        this.terminalIp = terminalIp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomainCheckStatus() {
        return domainCheckStatus;
    }

    public void setDomainCheckStatus(String domainCheckStatus) {
        this.domainCheckStatus = domainCheckStatus;
    }

    public String getDomainCheckMessage() {
        return domainCheckMessage;
    }

    public void setDomainCheckMessage(String domainCheckMessage) {
        this.domainCheckMessage = domainCheckMessage;
    }

    public String getQrcodeFilename() {
        return qrcodeFilename;
    }

    public void setQrcodeFilename(String qrcodeFilename) {
        this.qrcodeFilename = qrcodeFilename;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
