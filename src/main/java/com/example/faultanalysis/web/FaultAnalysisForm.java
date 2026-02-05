package com.example.faultanalysis.web;

import javax.validation.constraints.NotBlank;

public class FaultAnalysisForm {
    @NotBlank(message = "终端IP不能为空")
    private String terminalIp;

    private String location;

    @NotBlank(message = "设备型号不能为空")
    private String deviceModel;

    @NotBlank(message = "设备版本不能为空")
    private String deviceVersion;

    private String iccid;

    private String domain;

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
}
