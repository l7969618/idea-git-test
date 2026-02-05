package com.example.faultanalysis.web;

import com.example.faultanalysis.model.FaultAnalysisRecord;
import com.example.faultanalysis.repository.FaultAnalysisRecordRepository;
import com.example.faultanalysis.service.DomainCheckService;
import com.example.faultanalysis.service.DomainCheckService.DomainCheckResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fault-analysis")
public class FaultAnalysisController {
    private static final Path UPLOAD_DIR = Paths.get("uploads");

    private final FaultAnalysisRecordRepository repository;
    private final DomainCheckService domainCheckService;

    public FaultAnalysisController(FaultAnalysisRecordRepository repository, DomainCheckService domainCheckService) {
        this.repository = repository;
        this.domainCheckService = domainCheckService;
    }

    @GetMapping
    public String showForm(Model model, HttpServletRequest request) {
        if (!model.containsAttribute("form")) {
            FaultAnalysisForm form = new FaultAnalysisForm();
            form.setTerminalIp(resolveClientIp(request));
            model.addAttribute("form", form);
        }
        model.addAttribute("records", latestRecords());
        return "fault-analysis";
    }

    @GetMapping("/device-info")
    @ResponseBody
    public Map<String, String> deviceInfo(HttpServletRequest request) {
        Map<String, String> info = new HashMap<>();
        info.put("terminalIp", resolveClientIp(request));
        info.put("location", "未知");
        info.put("deviceModel", "未知型号");
        info.put("deviceVersion", "未知版本");
        return info;
    }

    @PostMapping
    public String submitForm(@Valid @ModelAttribute("form") FaultAnalysisForm form,
                             BindingResult bindingResult,
                             MultipartFile qrcode,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/fault-analysis";
        }

        FaultAnalysisRecord record = new FaultAnalysisRecord();
        record.setTerminalIp(form.getTerminalIp());
        record.setLocation(form.getLocation());
        record.setDeviceModel(form.getDeviceModel());
        record.setDeviceVersion(form.getDeviceVersion());
        record.setIccid(form.getIccid());
        record.setDomain(form.getDomain());

        DomainCheckResult result = domainCheckService.checkDomain(form.getDomain());
        record.setDomainCheckStatus(result.getStatus());
        record.setDomainCheckMessage(result.getMessage());

        if (qrcode != null && !qrcode.isEmpty()) {
            try {
                Files.createDirectories(UPLOAD_DIR);
                Path target = UPLOAD_DIR.resolve(System.currentTimeMillis() + "_" + qrcode.getOriginalFilename());
                Files.copy(qrcode.getInputStream(), target);
                record.setQrcodeFilename(target.toString());
            } catch (IOException ex) {
                record.setQrcodeFilename("上传失败: " + ex.getMessage());
            }
        }

        repository.save(record);
        redirectAttributes.addFlashAttribute("successMessage", "记录已保存，ID: " + record.getId());
        return "redirect:/fault-analysis";
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.trim().isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private List<FaultAnalysisRecord> latestRecords() {
        List<FaultAnalysisRecord> records = repository.findAll();
        records.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return records.size() > 10 ? records.subList(0, 10) : records;
    }
}
