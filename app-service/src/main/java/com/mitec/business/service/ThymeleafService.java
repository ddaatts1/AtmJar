package com.mitec.business.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.mitec.business.model.Job;
import com.mitec.business.model.Kpsc;
import com.mitec.business.model.ReplacementAccessory;
import com.mitec.business.repository.KpscRepository;
import com.mitec.business.repository.ReplacementAccessoryRepository;

@Service
public class ThymeleafService {
	private static final String MAIL_TEMPLATE_BASE_NAME = "mailMessages";
    private static final String MAIL_TEMPLATE_PREFIX = "/static/mails/";
    private static final String MAIL_TEMPLATE_SUFFIX = ".html";
    private static final String UTF_8 = "UTF-8";

    private static TemplateEngine templateEngine;

    static {
        templateEngine = emailTemplateEngine();
    }

    @Autowired
    private KpscRepository kpscRepository;
    
    @Autowired
    private ReplacementAccessoryRepository replacementAccessoryRepository;
    
    private static TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(htmlTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
        return templateEngine;
    }

    private static ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(MAIL_TEMPLATE_BASE_NAME);
        return messageSource;
    }

    private static ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(MAIL_TEMPLATE_PREFIX);
        templateResolver.setSuffix(MAIL_TEMPLATE_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(UTF_8);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    public String getCheckOutContent(Job job, boolean isCustomer, String workDetail) {
        final Context context = new Context();
        
        List<Kpsc> kpscs = kpscRepository.getByJobId(job.getId());
    	List<ReplacementAccessory> replacementAccessories = replacementAccessoryRepository.getByJobId(job.getId());
    	
        if (isCustomer) {
        	job.setNote(null);
        }
    	
        context.setVariable("job", job);
        context.setVariable("serialNumber", job.getAtm().getSerialNumber().toUpperCase());
        context.setVariable("checkInTime", formatTime(job.getCheckInTime()));
        context.setVariable("completeTime", formatTime(job.getCompleteTime()));
        context.setVariable("kpscs", kpscs);
        context.setVariable("replacementAccessories", replacementAccessories);
        context.setVariable("workDetail", workDetail);
        
        if (job.isMaintenance() && !job.isKpsc()) {
        	context.setVariable("isMaintenanceOnly", true);
        }else {
        	context.setVariable("isMaintenanceOnly", false);
        }

        return templateEngine.process("check-out-template", context);
    }
    
    private String formatTime(LocalDateTime time) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd-MM-yyyy");
    	return time.format(formatter);
    }
}
